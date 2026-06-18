package com.uxcam.kmp

/**
 * Common, platform-neutral facade over the native UXCam SDKs.
 *
 * Consumers call these from shared (commonMain) code; the compiler binds each
 * call to the platform `actual` (Android → com.uxcam.UXCam, iOS → UXCam.framework).
 * This replaces Flutter's MethodChannel + string dispatch: no serialization,
 * resolved at compile time.
 *
 * Note: starting a session needs a platform handle (an Android Activity/Context),
 * which has no meaning in commonMain. That entry point therefore lives in the
 * platform layer — see `UXCam.attach(...)` in the Android source set — and is
 * called by the consumer's native code before `startWithConfiguration`.
 */
expect object UXCam {
    fun startWithConfiguration(config: UXConfig)
    fun stopSessionAndUploadData()
    fun logEvent(eventName: String)
    fun tagScreenName(screenName: String)
    fun setUserIdentity(userIdentity: String)
    fun occludeSensitiveScreen(hide: Boolean)
    fun isRecording(): Boolean
}
