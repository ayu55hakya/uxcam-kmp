package com.uxcam.kmp.gradle.linker

import com.uxcam.kmp.gradle.UXCamCocoa
import com.uxcam.kmp.gradle.Versions
import org.gradle.api.GradleException
import org.gradle.api.logging.Logging
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Makes a STATIC consumer framework self-contained by merging the native UXCam SDK into it right
 * after the Kotlin/Native link.
 *
 * A static Kotlin framework is an `ar` archive produced by `libtool`, not `ld` — the
 * `-F`/`-framework` options the dynamic path injects are silently ignored, so the native UXCam
 * symbols the wrapper's cinterop references stay undefined until the consumer's Xcode *app* link,
 * which this plugin has no reach into. embedAndSign consumers then hit
 * `Undefined symbol: _OBJC_CLASS_$_UXCam`. Instead of instructing them to provision UXCam
 * app-side, we bake the SDK into the framework archive.
 *
 * The merge must reproduce the `-ObjC` force-load semantics the SDK gets on the CocoaPods path:
 * UXCam keeps critical code in *category-only* members (`UIWindow+TouchWindow.o` is the touch
 * capture behind gesture tracking) that define no referenced symbol, so ordinary archive
 * member-on-demand loading silently drops them. Force-load is rebuilt in three steps:
 *
 *  1. `lipo -thin` the UXCam slice archive down to the link's architecture (the simulator slice
 *     is a fat arm64+x86_64 binary), then extract its members.
 *  2. `ld -r -all_load` prelinks all NON-Swift members into one relocatable object (the
 *     "carrier"): a single object is loaded wholesale — categories included — as soon as anything
 *     in it is referenced, and the Kotlin objects reference `_OBJC_CLASS_$_UXCam`. Swift members
 *     must NOT go through `ld -r` (it corrupts Swift class metadata — the app link fails with
 *     `null objc class data`), so they stay untouched archive members and the carrier instead
 *     records an undefined reference (`-u`) to one anchor symbol per Swift member, force-loading
 *     each of them the moment the carrier loads. `ld -r` preserves the `LC_LINKER_OPTION`
 *     autolink hints, so the app link still pulls the system frameworks UXCam needs.
 *  3. A tiny assembled shim adds autolink hints for the system *libraries* the SDK needs but does
 *     not autolink itself ([UXCamCocoa.SYSTEM_LIBRARIES] — on the CocoaPods path the podspec
 *     provides these; here nothing else would). `libtool -static` then appends the carrier and the
 *     Swift members to the Kotlin framework archive.
 *
 * The result resolves with a single UXCam copy and zero consumer-side configuration. Runs inside
 * the link task's `doLast` (before anything downstream — embedAndSign, XCFramework assembly —
 * copies the binary) via plain [ProcessBuilder], keeping the plugin configuration-cache safe.
 */
internal object StaticFrameworkPrelinker {

    private val logger = Logging.getLogger(StaticFrameworkPrelinker::class.java)

    /** Carrier member name — also the marker that the merge already happened. */
    internal const val PRELINKED_MEMBER = "UXCam-prelinked.o"

    // xcrun SDK name per ld platform, to resolve the SDK version `-platform_version` wants.
    private val SDK_BY_LD_PLATFORM = mapOf(
        "ios" to "iphoneos",
        "ios-simulator" to "iphonesimulator",
    )

    /**
     * @param frameworkDir the produced `<name>.framework` directory (the archive sits inside it,
     *   named after the framework).
     * @param uxcamArchive the static library inside the matching `UXCam.xcframework` slice.
     * @param arch the Apple arch of this link (`arm64` / `x86_64`).
     * @param ldPlatform `ld -platform_version` platform name (`ios` / `ios-simulator`) — `ld -r`
     *   refuses to combine the SDK's objects without an explicit platform, because not every
     *   archive member carries a build version load command.
     */
    fun mergeInto(frameworkDir: File, uxcamArchive: File, arch: String, ldPlatform: String) {
        val binary = File(frameworkDir, frameworkDir.name.removeSuffix(".framework"))
        if (!binary.isFile) {
            throw GradleException(
                "UXCam: expected the static framework binary at ${binary.absolutePath} but it " +
                    "does not exist — cannot merge the native UXCam SDK."
            )
        }
        if (!uxcamArchive.isFile) {
            throw GradleException(
                "UXCam: native UXCam SDK archive not found at ${uxcamArchive.absolutePath}. " +
                    "If you overrode uxcamKmp.linker.frameworkPath, check it points at a valid " +
                    "UXCam.xcframework."
            )
        }

        // The link task rewrites the binary whenever it runs (and doLast is skipped when it's
        // UP-TO-DATE), so a second merge shouldn't happen — but guard anyway: merging twice would
        // duplicate every UXCam symbol.
        if (run(null, "xcrun", "ar", "t", binary.absolutePath).lineSequence().any { it == PRELINKED_MEMBER }) {
            logger.info("UXCam: ${binary.name} already contains $PRELINKED_MEMBER — skipping merge.")
            return
        }

        val work = Files.createTempDirectory("uxcam-prelink").toFile()
        try {
            val thin = thinFor(uxcamArchive, arch, work)
            val members = extractMembers(thin, work)
            val (swiftMembers, objcMembers) = members.partition { isSwiftMember(it, work) }

            val sdk = SDK_BY_LD_PLATFORM.getValue(ldPlatform)
            val sdkVersion = run(null, "xcrun", "--sdk", sdk, "--show-sdk-version").trim()
            val autolinkShim = assembleAutolinkShim(work, arch, ldPlatform)
            val anchorFlags = swiftMembers.mapNotNull { anchorSymbol(it, work) }
                .flatMap { listOf("-u", it) }

            val prelinked = File(work, PRELINKED_MEMBER)
            run(
                work,
                "xcrun", "ld", "-r", "-arch", arch,
                "-platform_version", ldPlatform, Versions.MIN_IOS_DEPLOYMENT_TARGET, sdkVersion,
                "-all_load", *objcMembers.map { it.name }.toTypedArray(),
                autolinkShim.name,
                *anchorFlags.toTypedArray(),
                "-o", prelinked.name,
            )
            patchCarrierObject(prelinked)

            val merged = File(work, binary.name)
            run(
                work,
                "xcrun", "libtool", "-static", "-o", merged.name,
                binary.absolutePath, prelinked.name, *swiftMembers.map { it.name }.toTypedArray(),
            )
            Files.move(merged.toPath(), binary.toPath(), StandardCopyOption.REPLACE_EXISTING)
            logger.lifecycle(
                "UXCam: merged the native UXCam SDK ($arch, ${objcMembers.size}+${swiftMembers.size} " +
                    "members) into static framework '${binary.name}'."
            )
        } finally {
            work.deleteRecursively()
        }
    }

    /** Thins a fat archive down to [arch]; returns [archive] unchanged when it is already thin. */
    private fun thinFor(archive: File, arch: String, work: File): File {
        val archs = run(null, "xcrun", "lipo", "-archs", archive.absolutePath).trim().split(Regex("\\s+"))
        if (archs.size <= 1) return archive
        val thin = File(work, "UXCam-$arch.a")
        run(null, "xcrun", "lipo", archive.absolutePath, "-thin", arch, "-output", thin.absolutePath)
        return thin
    }

    /** Extracts every object member of [archive] into [work] and returns them in archive order. */
    private fun extractMembers(archive: File, work: File): List<File> {
        val names = run(null, "xcrun", "ar", "t", archive.absolutePath)
            .lineSequence().map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("__.SYMDEF") }
            .toList()
        if (names.toSet().size != names.size) {
            throw GradleException(
                "UXCam: ${archive.name} contains duplicate member names — extraction would lose " +
                    "objects. This UXCam SDK build is unexpected; please report it."
            )
        }
        run(work, "xcrun", "ar", "x", archive.absolutePath)
        return names.map { File(work, it) }
    }

    /**
     * A member is Swift-compiled when it carries `__swift5_*` metadata sections. Those members
     * must be kept out of `ld -r` (see class docs).
     */
    private fun isSwiftMember(member: File, work: File): Boolean =
        run(work, "xcrun", "llvm-objdump", "--section-headers", member.name)
            .lineSequence()
            .any { line ->
                val tokens = line.trim().split(Regex("\\s+"))
                tokens.size > 2 && tokens[1].startsWith("__swift5")
            }

    /**
     * First defined external symbol of [member] — an undefined `-u` reference to it in the carrier
     * force-loads the member at the app link. Null (with a warning) when the member exports
     * nothing; it then only loads if something references it, like any plain archive member.
     */
    private fun anchorSymbol(member: File, work: File): String? {
        val anchor = run(work, "xcrun", "llvm-objdump", "--syms", member.name)
            .lineSequence()
            .map { it.trim().split(Regex("\\s+")) }
            .firstOrNull { it.size >= 4 && it[1] == "g" && it[2] != "*UND*" }
            ?.last()
        if (anchor == null) {
            logger.warn(
                "UXCam: Swift member ${member.name} exports no symbol to anchor a force-load on; " +
                    "it will only be linked into the app if referenced."
            )
        }
        return anchor
    }

    /**
     * Assembles an object whose only content is `LC_LINKER_OPTION` autolink hints for the system
     * libraries and frameworks the static SDK needs at the app link ([UXCamCocoa]). Passed through
     * `ld -r` these merge into the carrier, replacing what the podspec's `libraries`/`frameworks`
     * fields provide on the CocoaPods path.
     */
    private fun assembleAutolinkShim(work: File, arch: String, ldPlatform: String): File {
        val source = File(work, "uxcam-autolink.s")
        source.writeText(
            buildString {
                UXCamCocoa.SYSTEM_LIBRARIES.forEach { appendLine(".linker_option \"-l$it\"") }
                UXCamCocoa.SYSTEM_FRAMEWORKS.forEach { appendLine(".linker_option \"-framework\", \"$it\"") }
            }
        )
        val target = "$arch-apple-ios${Versions.MIN_IOS_DEPLOYMENT_TARGET}" +
            if (ldPlatform == "ios-simulator") "-simulator" else ""
        val shim = File(work, "uxcam-autolink.o")
        run(work, "xcrun", "clang", "-c", source.name, "-target", target, "-o", shim.name)
        return shim
    }

    private const val MH_MAGIC_64 = -0x1120531 // 0xFEEDFACF as Int
    private const val LC_LINKER_OPTIMIZATION_HINT = 0x2E
    private const val LC_SEGMENT_64 = 0x19
    private const val CLASSREFS_SECTION = "__objc_classrefs"

    /** Neutral name the carrier's classref slots hide under (must fit Mach-O's 16-byte field). */
    internal const val RENAMED_CLASSREFS_SECTION = "__uxc_clsrefs"

    /**
     * Two byte-level fixups that make the `ld -r` carrier safe for the consumer's app link. Both
     * address the same failure mode: `ld -r` output cannot carry valid Linker Optimization Hints
     * (it merges the per-member LOH tables but their instruction offsets go stale), and ld-prime's
     * instruction-rewriting optimizations rely on those hints to rewrite ADRP/ADD/LDR sequences as
     * a unit. Applied to the carrier, they rewrite the wrong instructions (stale hints) or only
     * part of a sequence (no hints). Observed effect: with `-dead_strip` and a modern deployment
     * target, the app link ELIMINATED `__objc_classrefs` and repointed the carrier's classref
     * loads directly at class objects — but left the dependent `ldr` in place, so UXCam loaded
     * garbage "Class" pointers and crashed (unrecognized selector → EXC_BAD_ACCESS at ~0x3 in
     * `objc_class::demangledName` while formatting the error).
     *
     *  1. Zero the `LC_LINKER_OPTIMIZATION_HINT` payload — ld-prime dropped the
     *     `-ignore_optimization_hints` flag, so the command's `datasize` is zeroed by hand. Hints
     *     are purely advisory; the cost is a missed micro-optimization on UXCam's code.
     *  2. Rename the carrier's `__objc_classrefs` section to [RENAMED_CLASSREFS_SECTION] so the
     *     classrefs-elimination pass never considers its slots. The slots are ordinary
     *     dyld-rebased Class pointers; the canonical name only opts them into linker/runtime
     *     optimizations (and runtime classref remapping, which statically linked classes never
     *     need). Should a future linker grow the same elimination for `__objc_superrefs`, rename
     *     it here the same way.
     */
    private fun patchCarrierObject(obj: File) {
        RandomAccessFile(obj, "rw").use { raf ->
            val header = ByteArray(32)
            raf.readFully(header)
            val head = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
            if (head.getInt(0) != MH_MAGIC_64) {
                throw GradleException(
                    "UXCam: ${obj.name} is not a thin 64-bit Mach-O object " +
                        "(magic 0x${Integer.toHexString(head.getInt(0))}) — cannot patch the carrier."
                )
            }
            val ncmds = head.getInt(16)
            var offset = 32L
            repeat(ncmds) {
                raf.seek(offset)
                val cmdHeader = ByteArray(8)
                raf.readFully(cmdHeader)
                val cmd = ByteBuffer.wrap(cmdHeader).order(ByteOrder.LITTLE_ENDIAN)
                when (cmd.getInt(0)) {
                    LC_LINKER_OPTIMIZATION_HINT -> {
                        raf.seek(offset + 12) // linkedit_data_command.datasize
                        raf.write(ByteArray(4))
                        logger.info("UXCam: dropped stale linker optimization hints from ${obj.name}.")
                    }
                    LC_SEGMENT_64 -> {
                        // segment_command_64 header is 72 bytes; then nsects × 80-byte section_64
                        // entries, each starting with sectname[16].
                        raf.seek(offset + 64) // nsects field
                        val tail = ByteArray(4)
                        raf.readFully(tail)
                        val nsects = ByteBuffer.wrap(tail).order(ByteOrder.LITTLE_ENDIAN).getInt(0)
                        for (i in 0 until nsects) {
                            val sectOffset = offset + 72 + i * 80L
                            raf.seek(sectOffset)
                            val name = ByteArray(16)
                            raf.readFully(name)
                            if (String(name).trimEnd('\u0000') == CLASSREFS_SECTION) {
                                raf.seek(sectOffset)
                                raf.write(RENAMED_CLASSREFS_SECTION.toByteArray().copyOf(16))
                                logger.info("UXCam: renamed ${obj.name} $CLASSREFS_SECTION → $RENAMED_CLASSREFS_SECTION.")
                            }
                        }
                    }
                }
                offset += cmd.getInt(4)
            }
        }
    }

    /** Runs [command] (in [workingDir] when given), returning its combined output; throws on failure. */
    private fun run(workingDir: File?, vararg command: String): String {
        val process = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .apply { workingDir?.let { directory(it) } }
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val exit = process.waitFor()
        if (exit != 0) {
            throw GradleException("UXCam: '${command.joinToString(" ")}' failed (exit $exit):\n$output")
        }
        return output
    }
}
