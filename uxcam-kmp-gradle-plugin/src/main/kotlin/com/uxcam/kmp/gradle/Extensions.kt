package com.uxcam.kmp.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Root DSL extension, registered under the name `uxcamKmp`.
 *
 * ```kotlin
 * uxcamKmp {
 *     autoInstall {
 *         commonMain { uxcamKmpVersion.set("0.0.2") }
 *         cocoapods  { uxcamCocoaVersion.set("3.8.3") }
 *     }
 * }
 * ```
 */
@Suppress("UnnecessaryAbstractClass")
abstract class UXCamExtension @Inject constructor(project: Project) {
    val autoInstall: AutoInstallExtension =
        project.objects.newInstance(AutoInstallExtension::class.java, project)

    fun autoInstall(action: Action<AutoInstallExtension>) = action.execute(autoInstall)
}

/**
 * Controls auto-installation of the UXCam KMP wrapper dependency and the native UXCam pod.
 * Disabling [enabled] prevents the plugin from touching the build at all.
 */
@Suppress("UnnecessaryAbstractClass")
abstract class AutoInstallExtension @Inject constructor(project: Project) {
    /** Master switch for all auto-installation. Defaults to `true`. */
    val enabled: Property<Boolean> =
        project.objects.property(Boolean::class.java).convention(true)

    val commonMain: SourceSetAutoInstallExtension =
        project.objects.newInstance(SourceSetAutoInstallExtension::class.java, project)

    val cocoapods: CocoapodsAutoInstallExtension =
        project.objects.newInstance(CocoapodsAutoInstallExtension::class.java, project)

    fun commonMain(action: Action<SourceSetAutoInstallExtension>) = action.execute(commonMain)

    fun cocoapods(action: Action<CocoapodsAutoInstallExtension>) = action.execute(cocoapods)
}

/** Auto-installation of `com.uxcam.kmp:uxcam` into the commonMain source set. */
@Suppress("UnnecessaryAbstractClass")
abstract class SourceSetAutoInstallExtension @Inject constructor(project: Project) {
    /** Enable adding the wrapper dependency to commonMain. Defaults to `true`. */
    val enabled: Property<Boolean> =
        project.objects.property(Boolean::class.java).convention(true)

    /** Wrapper version to install. Defaults to [Versions.UXCAM_KMP]. */
    val uxcamKmpVersion: Property<String> =
        project.objects.property(String::class.java).convention(Versions.UXCAM_KMP)
}

/** Auto-installation of the native `UXCam` CocoaPods pod (Kotlin-CocoaPods users only). */
@Suppress("UnnecessaryAbstractClass")
abstract class CocoapodsAutoInstallExtension @Inject constructor(project: Project) {
    /**
     * Enable adding `pod("UXCam")`. Only takes effect when the Kotlin CocoaPods plugin is applied
     * and the host is a Mac. Defaults to `true`.
     */
    val enabled: Property<Boolean> =
        project.objects.property(Boolean::class.java).convention(true)

    /** Native iOS UXCam SDK version to install. Defaults to [Versions.UXCAM_COCOA]. */
    val uxcamCocoaVersion: Property<String> =
        project.objects.property(String::class.java).convention(Versions.UXCAM_COCOA)
}
