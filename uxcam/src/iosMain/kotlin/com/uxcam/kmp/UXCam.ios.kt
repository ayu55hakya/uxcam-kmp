package com.uxcam.kmp

/**
 * iOS implementation — STUB. The structure is in place (the targets compile into
 * UXCamKMP.framework), but bodies are not wired to the native iOS UXCam SDK yet.
 * Android is the focus; these throw so accidental iOS use fails loudly.
 */
private const val NOT_YET = "UXCam KMP: iOS implementation not wired up yet"

actual object UXCam {

    // Lifecycle & session
    actual fun startWithConfiguration(config: UXConfig): Unit = TODO(NOT_YET)
    actual fun startNewSession(): Unit = TODO(NOT_YET)
    actual fun stopSessionAndUploadData(): Unit = TODO(NOT_YET)
    actual fun stopSessionAndUploadData(onSessionStopped: () -> Unit): Unit = TODO(NOT_YET)
    actual fun cancelCurrentSession(): Unit = TODO(NOT_YET)

    // Events
    actual fun logEvent(eventName: String): Unit = TODO(NOT_YET)
    actual fun logEvent(eventName: String, properties: Map<String, Any?>?): Unit = TODO(NOT_YET)
    actual fun logEventWithJson(eventName: String, json: String?): Unit = TODO(NOT_YET)

    // Bug & exception reporting
    actual fun reportBugEvent(eventName: String): Unit = TODO(NOT_YET)
    actual fun reportBugEvent(eventName: String, properties: Map<String, Any?>?): Unit = TODO(NOT_YET)
    actual fun reportBugEventWithJson(eventName: String, json: String?): Unit = TODO(NOT_YET)
    actual fun reportExceptionEvent(throwable: Throwable): Unit = TODO(NOT_YET)
    actual fun reportExceptionEvent(throwable: Throwable, properties: Map<String, Any?>?): Unit = TODO(NOT_YET)

    // User identity & properties
    actual fun setUserIdentity(userIdentity: String): Unit = TODO(NOT_YET)
    actual fun setUserProperty(propertyName: String, value: String): Unit = TODO(NOT_YET)
    actual fun setUserProperty(propertyName: String, value: Int): Unit = TODO(NOT_YET)
    actual fun setUserProperty(propertyName: String, value: Float): Unit = TODO(NOT_YET)
    actual fun setUserProperty(propertyName: String, value: Boolean): Unit = TODO(NOT_YET)
    actual fun setPushNotificationToken(token: String): Unit = TODO(NOT_YET)

    // Session properties
    actual fun setSessionProperty(propertyName: String, value: String): Unit = TODO(NOT_YET)
    actual fun setSessionProperty(propertyName: String, value: Int): Unit = TODO(NOT_YET)
    actual fun setSessionProperty(propertyName: String, value: Float): Unit = TODO(NOT_YET)
    actual fun setSessionProperty(propertyName: String, value: Boolean): Unit = TODO(NOT_YET)
    actual fun markSessionAsFavorite(): Unit = TODO(NOT_YET)

    // Screen tagging & ignore lists
    actual fun tagScreenName(screenName: String): Unit = TODO(NOT_YET)
    actual fun setAutomaticScreenNameTagging(enable: Boolean): Unit = TODO(NOT_YET)
    actual fun setImprovedScreenCaptureEnabled(enable: Boolean): Unit = TODO(NOT_YET)
    actual fun addScreenNameToIgnore(screenName: String): Unit = TODO(NOT_YET)
    actual fun addScreenNamesToIgnore(screenNames: List<String>): Unit = TODO(NOT_YET)
    actual fun removeScreenNameToIgnore(screenName: String): Unit = TODO(NOT_YET)
    actual fun removeScreenNamesToIgnore(screenNames: List<String>): Unit = TODO(NOT_YET)
    actual fun removeAllScreenNamesToIgnore(): Unit = TODO(NOT_YET)
    actual fun screenNamesBeingIgnored(): List<String> = TODO(NOT_YET)

    // Occlusion
    actual fun occludeSensitiveScreen(hide: Boolean): Unit = TODO(NOT_YET)
    actual fun occludeSensitiveScreen(hide: Boolean, withoutGesture: Boolean): Unit = TODO(NOT_YET)
    actual fun occludeAllTextFields(occludeAll: Boolean): Unit = TODO(NOT_YET)
    actual fun applyOverlayOcclusion(withoutGesture: Boolean): Unit = TODO(NOT_YET)
    actual fun applyBlurOcclusion(blurRadius: Int, withoutGesture: Boolean): Unit = TODO(NOT_YET)
    actual fun removeOcclusion(): Unit = TODO(NOT_YET)

    // Recording control
    actual fun pauseScreenRecording(): Unit = TODO(NOT_YET)
    actual fun resumeScreenRecording(): Unit = TODO(NOT_YET)
    actual fun isRecording(): Boolean = TODO(NOT_YET)
    actual fun allowShortBreakForAnotherApp(): Unit = TODO(NOT_YET)
    actual fun allowShortBreakForAnotherApp(continueSession: Boolean): Unit = TODO(NOT_YET)
    actual fun allowShortBreakForAnotherApp(millis: Int): Unit = TODO(NOT_YET)
    actual fun resumeShortBreakForAnotherApp(): Unit = TODO(NOT_YET)

    // Opt in / out
    actual fun optInOverall(): Unit = TODO(NOT_YET)
    actual fun optOutOverall(): Unit = TODO(NOT_YET)
    actual fun optInOverallStatus(): Boolean = TODO(NOT_YET)
    actual fun optIntoVideoRecording(): Unit = TODO(NOT_YET)
    actual fun optOutOfVideoRecording(): Unit = TODO(NOT_YET)
    actual fun optInVideoRecordingStatus(): Boolean = TODO(NOT_YET)

    // Multi-session
    actual fun getMultiSessionRecord(): Boolean = TODO(NOT_YET)
    actual fun setMultiSessionRecord(enable: Boolean): Unit = TODO(NOT_YET)

    // Crash handling
    actual fun disableCrashHandling(disabled: Boolean): Unit = TODO(NOT_YET)

    // Verification
    actual fun addVerificationListener(onSuccess: () -> Unit, onFailure: (errorMessage: String) -> Unit): Unit = TODO(NOT_YET)

    // URLs, uploads & status
    actual fun urlForCurrentSession(): String? = TODO(NOT_YET)
    actual fun urlForCurrentUser(): String? = TODO(NOT_YET)
    actual fun deletePendingUploads(): Unit = TODO(NOT_YET)
    actual fun pendingSessionCount(): Int = TODO(NOT_YET)
    actual fun pendingUploads(onResult: (count: Int) -> Unit): Unit = TODO(NOT_YET)
    actual fun getSdkVersionInfo(): String = TODO(NOT_YET)
}
