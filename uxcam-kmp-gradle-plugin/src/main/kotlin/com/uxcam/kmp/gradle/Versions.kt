package com.uxcam.kmp.gradle

/**
 * Default versions the plugin installs.
 *
 * Hand-maintained — keep in sync with the rest of the build:
 *  - [UXCAM_KMP]   → `version` of the `:uxcam` module (uxcam/build.gradle.kts) and this plugin.
 *  - [UXCAM_COCOA] → the `pod("UXCam")` version in uxcam/build.gradle.kts (currently mirrored by
 *                    the `uxcam` ref's iOS counterpart). This is the native iOS UXCam SDK version
 *                    the wrapper's cinterop was built against.
 *
 * (Sentry's plugin generates these into a BuildConfig from Gradle properties; we keep constants to
 * avoid pulling in the buildConfig plugin. Revisit if drift becomes a problem.)
 */
internal object Versions {
    const val UXCAM_KMP = "0.0.3"
    const val UXCAM_COCOA = "3.8.3"
}
