package com.uxcam.kmp.gradle

/**
 * Default versions and native-SDK coordinates the plugin installs. Hand-maintained — keep in sync
 * with the rest of the build:
 *  - [UXCAM_KMP]   → `version` of the `:uxcam` module and this plugin.
 *  - [UXCAM_COCOA] → the `pod("UXCam")` version in uxcam/build.gradle.kts: the native iOS UXCam SDK
 *                    version the wrapper's cinterop was built against. The deliver-and-link path
 *                    downloads exactly this `UXCam.xcframework`.
 *  - [UXCAM_COCOA_SHA256] → SHA-256 of the [UXCAM_COCOA] `UXCam.xcframework.zip`, taken from the
 *                    uxcam-ios-sdk SPM `Package.swift` checksum for the same tag. Version-specific:
 *                    if a consumer overrides the Cocoa version they must also supply a checksum (or
 *                    point `linker.frameworkPath` at a local copy).
 *  - [MIN_IOS_DEPLOYMENT_TARGET] → lowest iOS deployment target the pod accepts.
 *  - [MIN_KOTLIN] → Kotlin version the `:uxcam` klib was compiled with. Older Kotlin/Native
 *                    distributions lack platform libraries the klib references.
 */
internal object Versions {
    const val UXCAM_KMP = "0.0.3"
    const val UXCAM_COCOA = "3.8.3"
    const val UXCAM_COCOA_SHA256 = "8708caa4dd24beeec91ffcf6e781ecc9ff0a733353435459cd137e7ea85184a8"
    const val MIN_IOS_DEPLOYMENT_TARGET = "12.0"
    const val MIN_KOTLIN = "2.2.21"
}

/**
 * Native UXCam iOS SDK distribution facts. `UXCam.xcframework` is a **static** framework: linking
 * `-F<slice>` at build time bakes its object code into the consumer's framework (no runtime embed),
 * but — being static — it does NOT pull its own dependencies, so the consumer's link must also
 * reference the system frameworks and libraries below. (Source: uxcam-ios-sdk `Package.swift`
 * `UXCamWrapper` target `linkerSettings`.)
 */
internal object UXCamCocoa {
    /** GitHub-hosted XCFramework zip for a given SDK version. */
    fun zipUrl(version: String) =
        "https://raw.githubusercontent.com/uxcam/uxcam-ios-sdk/$version/UXCam.xcframework.zip"

    const val FRAMEWORK_NAME = "UXCam"

    /** System frameworks the static UXCam SDK requires at link time. */
    val SYSTEM_FRAMEWORKS = listOf(
        "AVFoundation", "CoreGraphics", "CoreMedia", "CoreVideo", "CoreTelephony",
        "MobileCoreServices", "QuartzCore", "Security", "SystemConfiguration", "WebKit",
    )

    /** System libraries the static UXCam SDK requires at link time (passed as `-l<name>`). */
    val SYSTEM_LIBRARIES = listOf("z", "iconv", "c++")
}
