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

        // Resolve the framework root + (optionally) the task that populates it.
        val frameworkRoot: File
        val downloadTaskName: String?
        if (frameworkPathOverride != null) {
            frameworkRoot = File(frameworkPathOverride)
            downloadTaskName = null
            logger.info("Using consumer-supplied UXCam.xcframework at ${frameworkRoot.absolutePath}.")
        } else {
            val cacheDir = File(
                project.gradle.gradleUserHomeDir,
                "caches/uxcam-cocoa/$cocoaVersion",
            )
            frameworkRoot = File(cacheDir, "${UXCamCocoaArtifact.FRAMEWORK}.xcframework")
            val task = project.tasks.register(
                "downloadUXCamCocoaFramework",
                DownloadUXCamCocoaFramework::class.java,
            ) {
                it.zipUrl.set(UXCamCocoa.zipUrl(cocoaVersion))
                it.sha256.set(cocoaSha256)
                it.destinationDir.set(cacheDir)
                it.description = "Downloads and verifies the native UXCam $cocoaVersion XCFramework."
                it.group = "uxcam"
            }
            downloadTaskName = task.name
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
            val opts = linkerOpts(File(frameworkRoot, slice))

            fun wire(binaryName: String, linkTaskName: String, apply: () -> Unit) {
                apply()
                if (downloadTaskName != null) {
                    project.tasks.named(linkTaskName).configure { it.dependsOn(downloadTaskName) }
                }
                logger.info("Linked native UXCam framework to ${target.name}:$binaryName")
            }

            target.binaries.withType(Framework::class.java).configureEach { fw ->
                wire(fw.name, fw.linkTaskName) { fw.linkerOpts(opts) }
            }
            target.binaries.withType(TestExecutable::class.java).configureEach { test ->
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
