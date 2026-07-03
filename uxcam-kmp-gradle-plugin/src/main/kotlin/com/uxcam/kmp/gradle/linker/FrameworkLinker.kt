package com.uxcam.kmp.gradle.linker

import com.uxcam.kmp.gradle.UXCamCocoa
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import java.io.File

/**
 * Deliver-and-link for non-CocoaPods consumers (embedAndSign / direct framework / SPM). Puts the
 * native UXCam `UXCam.xcframework` on each Apple framework binary's linker search path so the
 * consumer's iOS framework resolves the native symbols the wrapper's cinterop references — the gap
 * Sentry's plugin leaves to a manual SPM install.
 *
 * `UXCam.xcframework` is static, so we inject:
 *  - `-F<slice>` — the framework search path (the missing piece behind `ld: framework 'UXCam' not found`),
 *  - `-force_load <slice archive>` — loads ALL SDK members, matching the `-ObjC` the podspec applies
 *    on the CocoaPods path; plain `-framework UXCam` would drop the category-only members that
 *    carry touch capture (gestures would go unrecorded),
 *  - the system frameworks/libraries the static SDK needs ([UXCamCocoa.SYSTEM_FRAMEWORKS]/[SYSTEM_LIBRARIES]).
 *
 * No runtime embed is wired because the SDK is static — its code is baked into the consumer's
 * framework at link time.
 *
 * **Static consumer frameworks are merged instead of linked.** A static Kotlin framework is an
 * archive: its Gradle-side "link" runs `libtool`, not `ld`, so linker options are ignored and the
 * native UXCam symbols stay undefined until the consumer's Xcode *app* link — which this plugin
 * has no reach into (embedAndSign consumers would hit `Undefined symbol: _OBJC_CLASS_$_UXCam`).
 * For those, [StaticFrameworkPrelinker] prelinks the whole native SDK into one object and appends
 * it to the produced archive after the link, making the framework self-contained with exactly one
 * UXCam copy. Consumers whose app ALSO provides UXCam (Podfile/SPM) should turn this off via
 * `uxcamKmp { linker { mergeStaticFrameworks.set(false) } }` — otherwise the app link would see
 * the class symbols twice. Test executables, which ARE linked here at Gradle time, always get the
 * native SDK via linker options regardless of the framework packaging.
 */
internal object FrameworkLinker {

    private val logger = Logging.getLogger(FrameworkLinker::class.java)

    /**
     * @param frameworkPathOverride absolute path to a `UXCam.xcframework` the consumer supplies
     *   themselves (skips the download). When null, the framework is downloaded+verified by a
     *   [DownloadUXCamCocoaFramework] task whose output is the on-demand cache dir.
     */
    fun link(
        project: Project,
        cocoaVersion: String,
        cocoaSha256: String,
        frameworkPathOverride: String?,
        mergeStaticFrameworks: Boolean,
    ) {
        val kmp = project.extensions.findByName("kotlin") as? KotlinMultiplatformExtension ?: run {
            logger.info("Kotlin Multiplatform plugin not found — skipping UXCam Cocoa linking.")
            return
        }

        val appleTargets = kmp.targets.filterIsInstance<KotlinNativeTarget>()
            .filter { it.konanTarget.family.isAppleFamily }
        if (appleTargets.isEmpty()) {
            logger.info("No Apple targets detected — skipping UXCam Cocoa framework linking.")
            return
        }

        // Resolve the framework root. The cache dir is computed eagerly; the download task that
        // populates it is registered lazily (ensureDownloadTask) — only when a binary actually
        // consumes it. A consumer whose frameworks are all static and who has no test binaries
        // never triggers a download, because static frameworks resolve UXCam at the app link.
        val cacheDir = File(project.gradle.gradleUserHomeDir, "caches/uxcam-cocoa/$cocoaVersion")
        val frameworkRoot: File = if (frameworkPathOverride != null) {
            logger.info("Using consumer-supplied UXCam.xcframework at $frameworkPathOverride.")
            File(frameworkPathOverride)
        } else {
            File(cacheDir, "${UXCamCocoaArtifact.FRAMEWORK}.xcframework")
        }

        // Lazily registered on first use; stays null when the consumer supplied their own framework.
        var downloadTaskName: String? = null
        fun ensureDownloadTask(): String? {
            if (frameworkPathOverride != null) return null
            downloadTaskName?.let { return it }
            return project.tasks.register(
                "downloadUXCamCocoaFramework",
                DownloadUXCamCocoaFramework::class.java,
            ) {
                it.zipUrl.set(UXCamCocoa.zipUrl(cocoaVersion))
                it.sha256.set(cocoaSha256)
                it.destinationDir.set(cacheDir)
                it.description = "Downloads and verifies the native UXCam $cocoaVersion XCFramework."
                it.group = "uxcam"
            }.name.also { downloadTaskName = it }
        }

        appleTargets.forEach { target ->
            val slice = FrameworkArchitectures.sliceFor(target.name)
            val arch = FrameworkArchitectures.archFor(target.name)
            val ldPlatform = FrameworkArchitectures.ldPlatformFor(target.name)
            if (slice == null || arch == null || ldPlatform == null) {
                logger.warn(
                    "UXCam ships no framework slice for Apple target '${target.name}'; " +
                        "skipping native linking for it. Supported: ${FrameworkArchitectures.supportedTargets}."
                )
                return@forEach
            }
            // Same static SDK as the CocoaPods path, so it autolinks the Swift compatibility libs
            // too — add the toolchain's Swift static-lib search path (see [SwiftRuntimeLibraries]).
            val opts = linkerOpts(File(frameworkRoot, slice)) +
                SwiftRuntimeLibraries.linkerOpts(project, target.name)

            fun wire(binaryName: String, linkTaskName: String, apply: () -> Unit) {
                apply()
                ensureDownloadTask()?.let { dt ->
                    project.tasks.named(linkTaskName).configure { it.dependsOn(dt) }
                }
                logger.info("Linked native UXCam framework to ${target.name}:$binaryName")
            }

            target.binaries.withType(Framework::class.java).configureEach { fw ->
                // Static framework → the Gradle-side "link" is a libtool archive step that ignores
                // linker options, so instead of injecting `-F` we merge the native SDK into the
                // produced archive right after the link (see [StaticFrameworkPrelinker]).
                if (fw.isStatic) {
                    if (!mergeStaticFrameworks) {
                        logger.lifecycle(
                            "UXCam: '${target.name}:${fw.name}' is a static framework and " +
                                "uxcamKmp.linker.mergeStaticFrameworks is disabled — provide the native " +
                                "UXCam SDK at your Xcode app link via your app's Podfile (pod 'UXCam'), " +
                                "SPM, or the app target's FRAMEWORK_SEARCH_PATHS."
                        )
                        return@configureEach
                    }
                    // Captured as plain File/String values so the doLast action stays
                    // configuration-cache safe.
                    val frameworkDir = fw.outputFile
                    val sliceArchive = File(
                        frameworkRoot,
                        "$slice/${UXCamCocoaArtifact.FRAMEWORK}.framework/${UXCamCocoaArtifact.FRAMEWORK}",
                    )
                    wire(fw.name, fw.linkTaskName) {
                        project.tasks.named(fw.linkTaskName).configure { linkTask ->
                            linkTask.doLast {
                                StaticFrameworkPrelinker.mergeInto(frameworkDir, sliceArchive, arch, ldPlatform)
                            }
                        }
                    }
                    return@configureEach
                }
                wire(fw.name, fw.linkTaskName) { fw.linkerOpts(opts) }
            }
            target.binaries.withType(TestExecutable::class.java).configureEach { test ->
                // Test executables ARE linked here at Gradle time, so they need the native SDK on the
                // search path regardless of how the consumer's framework is packaged.
                wire(test.name, test.linkTaskName) { test.linkerOpts(opts) }
            }
        }
    }

    /** Link flags for a static framework rooted at [sliceDir] (one xcframework slice). */
    private fun linkerOpts(sliceDir: File): List<String> = buildList {
        add("-F${sliceDir.absolutePath}")
        // Force-load rather than `-framework UXCam`: the SDK keeps critical code in category-only
        // members (`UIWindow+TouchWindow.o` is the touch capture behind gesture tracking) that
        // define no referenced symbol, so on-demand archive loading silently drops them and
        // gestures go unrecorded. This mirrors the `-ObjC` the UXCam podspec applies on the
        // CocoaPods path. ld resolves the arch slice from the fat archive itself.
        add("-force_load")
        add(sliceDir.resolve("${UXCamCocoa.FRAMEWORK_NAME}.framework/${UXCamCocoa.FRAMEWORK_NAME}").absolutePath)
        UXCamCocoa.SYSTEM_FRAMEWORKS.forEach { add("-framework"); add(it) }
        UXCamCocoa.SYSTEM_LIBRARIES.forEach { add("-l$it") }
    }
}
