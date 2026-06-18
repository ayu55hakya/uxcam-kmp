package com.uxcam.kmp

/**
 * iOS implementation — STUB for now.
 *
 * The structure is in place (iosArm64 / iosSimulatorArm64 targets are declared and
 * this source set compiles into UXCamKMP.framework), but the bodies are not wired
 * to the native iOS UXCam SDK yet. That requires adding the iOS SDK via CocoaPods
 * or a cinterop def so `platform.UXCam.*` symbols are available here.
 *
 * Focus is Android first; these throw so accidental iOS use fails loudly.
 */
private const val NOT_YET = "UXCam KMP: iOS implementation not wired up yet"

actual object UXCam {
    actual fun startWithConfiguration(config: UXConfig): Unit = TODO(NOT_YET)
    actual fun stopSessionAndUploadData(): Unit = TODO(NOT_YET)
    actual fun logEvent(eventName: String): Unit = TODO(NOT_YET)
    actual fun logEvent(eventName: String, properties: Map<String, Any?>?): Unit = TODO(NOT_YET)
    actual fun logEventWithJson(eventName: String, json: String?): Unit = TODO(NOT_YET)
    actual fun tagScreenName(screenName: String): Unit = TODO(NOT_YET)
    actual fun setUserIdentity(userIdentity: String): Unit = TODO(NOT_YET)
    actual fun setUserProperty(propertyName: String, value: String): Unit = TODO(NOT_YET)
    actual fun occludeSensitiveScreen(hide: Boolean): Unit = TODO(NOT_YET)
    actual fun isRecording(): Boolean = TODO(NOT_YET)
}
