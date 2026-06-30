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
 *  - `-framework UXCam` — explicit, in case the cinterop reference isn't propagated to the consumer,
 *  - the system frameworks/libraries the static SDK needs ([UXCamCocoa.SYSTEM_FRAMEWORKS]/[SYSTEM_LIBRARIES]).
 *
 * No runtime embed is wired because the SDK is static — its code is baked into the consumer's
 * framework at link time.
 *
 * **Static consumer frameworks are skipped.** A static Kotlin framework is an archive: the native
 * UXCam symbols it references stay undefined and are resolved at the consumer's Xcode *app* link,
 * not at the Gradle framework link. Our Gradle-side `-F` never reaches that app link, and injecting
 * it would double-provision UXCam when the app already supplies it via Podfile/SPM (the common
 * embedAndSign shape). So we stand down for static frameworks and leave the native SDK to the app
 * link — no manual `linker { enabled.set(false) }` needed. Test executables, which ARE linked here
 * at Gradle time, always get the native SDK regardless of the framework packaging.
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
            if (slice == null) {
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
                // Static framework → UXCam resolves at the consumer's app link, which our Gradle-side
                // `-F` can't reach; injecting would risk double-provisioning. Stand down and instruct.
                if (fw.isStatic) {
                    logger.lifecycle(
                        "UXCam: '${target.name}:${fw.name}' is a static framework — the native UXCam SDK " +
                            "is resolved at your Xcode app link, not here. Provide it at app link via your " +
                            "app's Podfile (pod 'UXCam'), SPM, or the app target's FRAMEWORK_SEARCH_PATHS. " +
                            "Skipping deliver-and-link to avoid double-provisioning."
                    )
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
        add("-framework"); add(UXCamCocoa.FRAMEWORK_NAME)
        UXCamCocoa.SYSTEM_FRAMEWORKS.forEach { add("-framework"); add(it) }
        UXCamCocoa.SYSTEM_LIBRARIES.forEach { add("-l$it") }
    }
}
