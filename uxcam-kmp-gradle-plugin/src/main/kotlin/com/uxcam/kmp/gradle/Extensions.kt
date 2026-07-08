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
 *         commonMain { uxcamKmpVersion.set("0.0.3") }
 *         cocoapods  { uxcamCocoaVersion.set("3.8.3") }   // Kotlin-CocoaPods consumers
 *     }
 *     linker {                                            // embedAndSign / SPM consumers
 *         // frameworkPath.set("/path/to/UXCam.xcframework")  // skip the download
 *     }
 * }
 * ```
 */
@Suppress("UnnecessaryAbstractClass")
abstract class UXCamExtension @Inject constructor(project: Project) {
    val autoInstall: AutoInstallExtension =
        project.objects.newInstance(AutoInstallExtension::class.java, project)

    /** Native-framework linking for non-CocoaPods (embedAndSign / direct framework / SPM) consumers. */
    val linker: LinkerExtension =
        project.objects.newInstance(LinkerExtension::class.java, project)

    fun autoInstall(action: Action<AutoInstallExtension>) = action.execute(autoInstall)
    fun linker(action: Action<LinkerExtension>) = action.execute(linker)
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

    /**
     * Fail the build with an actionable message when the consumer's Kotlin version is older than
     * the version the `:uxcam` klib was built with ([Versions.MIN_KOTLIN]). Defaults to `true`.
     */
    val verifyKotlinVersion: Property<Boolean> =
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

    /**
     * Also install `com.uxcam.kmp:uxcam-compose` (the `Modifier.uxcamOcclude` helpers) when a
     * Compose plugin is detected on the consumer. Non-Compose consumers never get the artifact
     * regardless of this flag. Defaults to `true`.
     */
    val composeHelpers: Property<Boolean> =
        project.objects.property(Boolean::class.java).convention(true)
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

    /**
     * Minimum iOS deployment target guaranteed in the Kotlin-CocoaPods configuration so the
     * synthetic Podfile can resolve the UXCam pod. Applied only when the consumer hasn't already
     * set an equal-or-higher value. Defaults to [Versions.MIN_IOS_DEPLOYMENT_TARGET].
     */
    val iosDeploymentTarget: Property<String> =
        project.objects.property(String::class.java).convention(Versions.MIN_IOS_DEPLOYMENT_TARGET)
}

/**
 * Deliver-and-link configuration for non-CocoaPods consumers. The plugin downloads the native
 * `UXCam.xcframework` ([cocoaVersion] / [cocoaSha256]), verifies it, and adds it to each Apple
 * framework's linker search path — unless [frameworkPath] points at a copy you already have.
 * Static frameworks can't take linker options, so for those the SDK is merged into the produced
 * archive instead ([mergeStaticFrameworks]).
 */
@Suppress("UnnecessaryAbstractClass")
abstract class LinkerExtension @Inject constructor(project: Project) {
    /** Enable native-framework linking for non-CocoaPods consumers. Defaults to `true`. */
    val enabled: Property<Boolean> =
        project.objects.property(Boolean::class.java).convention(true)

    /**
     * Merge the native UXCam SDK into STATIC consumer frameworks right after the Kotlin link,
     * making them self-contained — static archives can't carry the linker options the dynamic
     * path uses, and the plugin has no reach into the Xcode app link where those symbols would
     * otherwise resolve. Disable this only if your app supplies UXCam itself (Podfile / SPM);
     * leaving both in place would define the UXCam classes twice at the app link.
     * Defaults to `true`.
     */
    val mergeStaticFrameworks: Property<Boolean> =
        project.objects.property(Boolean::class.java).convention(true)

    /** Native UXCam SDK version to download and link. Defaults to [Versions.UXCAM_COCOA]. */
    val cocoaVersion: Property<String> =
        project.objects.property(String::class.java).convention(Versions.UXCAM_COCOA)

    /** SHA-256 of the `UXCam.xcframework.zip` for [cocoaVersion]. Defaults to [Versions.UXCAM_COCOA_SHA256]. */
    val cocoaSha256: Property<String> =
        project.objects.property(String::class.java).convention(Versions.UXCAM_COCOA_SHA256)

    /**
     * Absolute path to a `UXCam.xcframework` the consumer supplies (e.g. one SPM already resolved).
     * When set, the plugin skips the download and links this copy. Unset by default.
     */
    val frameworkPath: Property<String> =
        project.objects.property(String::class.java)
}
