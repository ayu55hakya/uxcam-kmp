package com.uxcam.kmp

/**
 * Common, platform-neutral facade over the native UXCam SDKs. Mirrors the developer-facing
 * surface of the native API; each call binds to the platform `actual` at compile time
 * (Android → com.uxcam.UXCam, iOS → UXCam.framework).
 *
 * Cross-platform/internal hooks (Cordova/Appcelerator/Flutter, pluginType, getDelegate) are
 * intentionally not mirrored — this wrapper is itself the cross-platform layer. Listener-based
 * APIs are exposed as Kotlin lambdas. Per-view occlusion (android.view.View) has no common type,
 * so it lives only on the Android actual (see UXCam.android.kt).
 */
expect object UXCamKMP {

    // --- Lifecycle & session ---
    fun startWithConfiguration(config: UXConfig)
    fun startNewSession()
    fun stopSessionAndUploadData()
    fun stopSessionAndUploadData(onSessionStopped: () -> Unit)
    fun cancelCurrentSession()

    // --- Events ---
    fun logEvent(eventName: String)
    fun logEvent(eventName: String, properties: Map<String, Any?>?)
    /** Properties as a JSON string (parsed to the native JSONObject overload on Android). */
    fun logEventWithJson(eventName: String, json: String?)

    // --- Bug & exception reporting ---
    fun reportBugEvent(eventName: String)
    fun reportBugEvent(eventName: String, properties: Map<String, Any?>?)
    fun reportBugEventWithJson(eventName: String, json: String?)
    fun reportExceptionEvent(throwable: Throwable)
    fun reportExceptionEvent(throwable: Throwable, properties: Map<String, Any?>?)

    // --- User identity & properties ---
    fun setUserIdentity(userIdentity: String)
    fun setUserProperty(propertyName: String, value: String)
    fun setUserProperty(propertyName: String, value: Int)
    fun setUserProperty(propertyName: String, value: Float)
    fun setUserProperty(propertyName: String, value: Boolean)
    fun setPushNotificationToken(token: String)

    // --- Session properties ---
    fun setSessionProperty(propertyName: String, value: String)
    fun setSessionProperty(propertyName: String, value: Int)
    fun setSessionProperty(propertyName: String, value: Float)
    fun setSessionProperty(propertyName: String, value: Boolean)
    fun markSessionAsFavorite()

    // --- Screen tagging & ignore lists ---
    fun tagScreenName(screenName: String)
    fun setAutomaticScreenNameTagging(enable: Boolean)
    fun setImprovedScreenCaptureEnabled(enable: Boolean)
    fun addScreenNameToIgnore(screenName: String)
    fun addScreenNamesToIgnore(screenNames: List<String>)
    fun removeScreenNameToIgnore(screenName: String)
    fun removeScreenNamesToIgnore(screenNames: List<String>)
    fun removeAllScreenNamesToIgnore()
    fun screenNamesBeingIgnored(): List<String>

    // --- Occlusion (screen-level; per-view is Android-only) ---
    fun occludeSensitiveScreen(hide: Boolean)
    fun occludeSensitiveScreen(hide: Boolean, withoutGesture: Boolean)
    fun occludeAllTextFields(occludeAll: Boolean)
    fun applyOverlayOcclusion(withoutGesture: Boolean = false)
    fun applyBlurOcclusion(blurRadius: Int = 15, withoutGesture: Boolean = false)
    fun removeOcclusion()

    // --- Recording control ---
    fun pauseScreenRecording()
    fun resumeScreenRecording()
    fun isRecording(): Boolean
    fun allowShortBreakForAnotherApp()
    fun allowShortBreakForAnotherApp(continueSession: Boolean)
    fun allowShortBreakForAnotherApp(millis: Int)
    fun resumeShortBreakForAnotherApp()

    // --- Opt in / out ---
    fun optInOverall()
    fun optOutOverall()
    fun optInOverallStatus(): Boolean
    fun optIntoVideoRecording()
    fun optOutOfVideoRecording()
    fun optInVideoRecordingStatus(): Boolean

    // --- Multi-session ---
    fun getMultiSessionRecord(): Boolean
    fun setMultiSessionRecord(enable: Boolean)

    // --- Crash handling ---
    fun disableCrashHandling(disabled: Boolean)

    // --- Verification ---
    fun addVerificationListener(onSuccess: () -> Unit, onFailure: (errorMessage: String) -> Unit)

    // --- URLs, uploads & status ---
    fun urlForCurrentSession(): String?
    fun urlForCurrentUser(): String?
    fun deletePendingUploads()
    fun pendingSessionCount(): Int
    fun pendingUploads(onResult: (count: Int) -> Unit)
    fun getSdkVersionInfo(): String
}
