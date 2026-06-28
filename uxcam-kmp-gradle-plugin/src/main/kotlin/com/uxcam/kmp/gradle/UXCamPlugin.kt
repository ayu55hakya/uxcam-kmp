package com.uxcam.kmp.gradle

import com.uxcam.kmp.gradle.linker.FrameworkLinker
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.konan.target.HostManager
import org.slf4j.LoggerFactory

internal const val UXCAM_EXTENSION_NAME = "uxcamKmp"
internal const val KOTLIN_EXTENSION_NAME = "kotlin"

private const val WRAPPER_GROUP = "com.uxcam.kmp"
private const val WRAPPER_ARTIFACT = "uxcam"
private const val UXCAM_POD_NAME = "UXCam"

/**
 * Sentry-style convenience plugin (`com.uxcam.kmp.gradle`) for Kotlin-source consumers of the
 * UXCam KMP wrapper. Applying it to a KMP shared module auto-wires the boilerplate a consumer would
 * otherwise write by hand:
 *  - adds `com.uxcam.kmp:uxcam` to the commonMain source set,
 *  - verifies the consumer's Kotlin version can resolve the wrapper klib,
 *  - links the native UXCam iOS SDK so the consumer's framework resolves the native symbols the
 *    wrapper's cinterop references, across BOTH iOS delivery paths:
 *      • Kotlin-CocoaPods consumers → adds `pod("UXCam")` + a deployment-target floor,
 *      • embedAndSign / direct-framework / SPM consumers → deliver-and-link: downloads the native
 *        `UXCam.xcframework` and injects the `-F`/`-framework`/`-l` linker options ([FrameworkLinker]).
 */
@Suppress("unused")
class UXCamPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(UXCAM_EXTENSION_NAME, UXCamExtension::class.java, project)
        project.afterEvaluate { executeConfiguration(it) }
    }

    /** Visible for testing so the logic can be exercised without a real Apple host. */
    internal fun executeConfiguration(
        project: Project,
        hostIsMac: Boolean = HostManager.hostIsMac,
    ) {
        val extension = project.extensions.getByType(UXCamExtension::class.java)
        val autoInstall = extension.autoInstall
        if (!autoInstall.enabled.get()) {
            logger.info("uxcamKmp.autoInstall disabled — skipping all auto-installation.")
            return
        }

        if (autoInstall.verifyKotlinVersion.get()) {
            project.verifyKotlinVersion()
        }

        if (autoInstall.commonMain.enabled.get()) {
            project.installUXCamForKmp(autoInstall.commonMain)
        }

        // iOS native linking. The Kotlin-CocoaPods path and the deliver-and-link path are mutually
        // exclusive: CocoaPods already delivers the native framework to the linker, so we only
        // deliver-and-link when the consumer is NOT using the Kotlin CocoaPods plugin.
        val hasCocoapodsPlugin =
            project.plugins.findPlugin(KotlinCocoapodsPlugin::class.java) != null
        when {
            hasCocoapodsPlugin && autoInstall.cocoapods.enabled.get() && hostIsMac ->
                project.installUXCamForCocoapods(autoInstall.cocoapods)

            hasCocoapodsPlugin ->
                logger.info("Kotlin CocoaPods detected but CocoaPods auto-install is off or host is not a Mac.")

            extension.linker.enabled.get() ->
                FrameworkLinker.link(
                    project = project,
                    cocoaVersion = extension.linker.cocoaVersion.get(),
                    cocoaSha256 = extension.linker.cocoaSha256.get(),
                    frameworkPathOverride = extension.linker.frameworkPath.orNull,
                )

            else ->
                logger.info("uxcamKmp.linker disabled — skipping native UXCam framework linking.")
        }
    }

    companion object {
        internal val logger by lazy { LoggerFactory.getLogger(UXCamPlugin::class.java) }
    }
}

/** Adds `com.uxcam.kmp:uxcam` to the commonMain source set (no-op if the KMP plugin is absent). */
internal fun Project.installUXCamForKmp(commonMain: SourceSetAutoInstallExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        UXCamPlugin.logger.info("Kotlin Multiplatform plugin not found — skipping UXCam wrapper installation.")
        return
    }

    val commonMainSourceSet = kmpExtension.sourceSets.find { it.name.contains("common") }
    if (commonMainSourceSet == null) {
        UXCamPlugin.logger.info("No commonMain source set found — skipping UXCam wrapper installation.")
        return
    }

    val version = commonMain.uxcamKmpVersion.get()
    commonMainSourceSet.dependencies { api("$WRAPPER_GROUP:$WRAPPER_ARTIFACT:$version") }
}

/**
 * Adds `pod("UXCam")` to the Kotlin CocoaPods configuration, unless the consumer already declared
 * it (their declaration always wins), and guarantees a high-enough iOS deployment target so the
 * synthetic Podfile can resolve the pod.
 */
internal fun Project.installUXCamForCocoapods(cocoapods: CocoapodsAutoInstallExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension || kmpExtension.targets.isEmpty() || !HostManager.hostIsMac) {
        UXCamPlugin.logger.info("Skipping UXCam pod installation.")
        return
    }

    (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { pods ->
        if (pods.pods.findByName(UXCAM_POD_NAME) == null) {
            pods.pod(UXCAM_POD_NAME) {
                version = cocoapods.uxcamCocoaVersion.get()
                moduleName = UXCAM_POD_NAME
            }
        }

        val required = cocoapods.iosDeploymentTarget.get()
        val current = pods.ios.deploymentTarget
        if (current == null || compareVersions(current, required) < 0) {
            pods.ios.deploymentTarget = required
            UXCamPlugin.logger.info("Set cocoapods.ios.deploymentTarget to $required for UXCam.")
        }
    }
}

/**
 * Fails the build with an actionable message when the consumer's Kotlin version is older than
 * [Versions.MIN_KOTLIN] — the version the `:uxcam` klib was built with.
 */
internal fun Project.verifyKotlinVersion() {
    val kotlinVersion = runCatching { getKotlinPluginVersion() }.getOrNull() ?: return
    if (compareVersions(kotlinVersion, Versions.MIN_KOTLIN) < 0) {
        throw GradleException(
            """
            UXCam KMP requires Kotlin ${Versions.MIN_KOTLIN} or newer, but this project uses $kotlinVersion.
            The com.uxcam.kmp:uxcam library was compiled with Kotlin ${Versions.MIN_KOTLIN} and depends on
            Kotlin/Native platform libraries that older versions don't provide.

            Fix: set the Kotlin version to ${Versions.MIN_KOTLIN} or newer (e.g. in gradle/libs.versions.toml).
            To bypass this check: uxcamKmp { autoInstall { verifyKotlinVersion.set(false) } }
            """.trimIndent()
        )
    }
}

/**
 * Compares two dot-separated version strings numerically (`"2.2.21"` vs `"2.2.0"`). Any pre-release
 * suffix after `-` is ignored, missing components are treated as `0`. Negative if [a] < [b].
 */
internal fun compareVersions(a: String, b: String): Int {
    fun parts(v: String) = v.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
    val aParts = parts(a)
    val bParts = parts(b)
    for (i in 0 until maxOf(aParts.size, bParts.size)) {
        val cmp = aParts.getOrElse(i) { 0 }.compareTo(bParts.getOrElse(i) { 0 })
        if (cmp != 0) return cmp
    }
    return 0
}
