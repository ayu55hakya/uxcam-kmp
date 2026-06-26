package com.uxcam.kmp.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
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
 *  - adds `com.uxcam.kmp:uxcam` to the commonMain source set, and
 *  - for Kotlin-CocoaPods users on a Mac host, adds `pod("UXCam")` so the iOS framework links the
 *    native symbols the wrapper's cinterop references.
 *
 * Mirrors `io.sentry.kotlin.multiplatform.gradle`, trimmed to the CocoaPods path (SPM linker
 * auto-config is a future iteration).
 */
@Suppress("unused")
class UXCamPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(UXCAM_EXTENSION_NAME, UXCamExtension::class.java, project)
        project.afterEvaluate { executeConfiguration(it) }
    }

    /**
     * Visible for testing so the auto-install logic can be exercised without a real Apple host —
     * pass [hostIsMac] explicitly to cover the CocoaPods branch on CI.
     */
    internal fun executeConfiguration(
        project: Project,
        hostIsMac: Boolean = HostManager.hostIsMac
    ) {
        val extension = project.extensions.getByType(UXCamExtension::class.java)
        val autoInstall = extension.autoInstall
        if (!autoInstall.enabled.get()) {
            logger.info("uxcamKmp.autoInstall disabled — skipping all auto-installation.")
            return
        }

        if (autoInstall.commonMain.enabled.get()) {
            project.installUXCamForKmp(autoInstall.commonMain)
        }

        val hasCocoapodsPlugin =
            project.plugins.findPlugin(KotlinCocoapodsPlugin::class.java) != null
        if (hasCocoapodsPlugin && autoInstall.cocoapods.enabled.get() && hostIsMac) {
            project.installUXCamForCocoapods(autoInstall.cocoapods)
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
        UXCamPlugin.logger.info(
            "Kotlin Multiplatform plugin not found — skipping UXCam wrapper installation."
        )
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
 * it (their declaration always wins). Mirrors the `pod("UXCam")` a consumer would otherwise write
 * by hand so the iOS framework links the native symbols the wrapper's cinterop references.
 *
 * `linkOnly` is intentionally NOT set: it is ignored (with a warning) for static frameworks, which
 * is the common KMP iOS case, and a plain declaration links correctly for both static and dynamic.
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
    }
}
