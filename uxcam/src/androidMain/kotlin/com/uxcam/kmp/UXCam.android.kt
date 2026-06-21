package com.uxcam.kmp

import android.view.View
import org.json.JSONObject
import com.uxcam.OnVerificationListener
import com.uxcam.UXCam as NativeUXCam
import com.uxcam.datamodel.UXConfig as NativeUXConfig
import com.uxcam.screenshot.model.UXCamBlur
import com.uxcam.screenshot.model.UXCamOcclusion as NativeOcclusion
import com.uxcam.screenshot.model.UXCamOverlay

actual object UXCam {

    // Tracks the active full-screen occlusion so removeOcclusion() can clear it.
    private var currentOcclusion: NativeOcclusion? = null

    // --- Lifecycle & session ---

    actual fun startWithConfiguration(config: UXConfig) {
        val nativeConfig = NativeUXConfig.Builder(config.appKey)
            .enableAutomaticScreenNameTagging(config.enableAutomaticScreenNameTagging)
            .enableMultiSessionRecord(config.enableMultiSessionRecord)
            .enableCrashHandling(config.enableCrashHandling)
            .enableIntegrationLogging(config.enableIntegrationLogging)
            .build()
        NativeUXCam.startWithConfiguration(nativeConfig)
        if (config.occludeAllTextFields) NativeUXCam.occludeAllTextFields(true)
    }

    actual fun startNewSession() = NativeUXCam.startNewSession()

    actual fun stopSessionAndUploadData() = NativeUXCam.stopSessionAndUploadData()

    actual fun stopSessionAndUploadData(onSessionStopped: () -> Unit) =
        NativeUXCam.stopSessionAndUploadData { onSessionStopped() }

    actual fun cancelCurrentSession() = NativeUXCam.cancelCurrentSession()

    // --- Events ---

    actual fun logEvent(eventName: String) = NativeUXCam.logEvent(eventName)

    actual fun logEvent(eventName: String, properties: Map<String, Any?>?) {
        // Static Map type selects the native Map overload (vs JSONObject); null hits the no-JSON path.
        NativeUXCam.logEvent(eventName, properties)
    }

    actual fun logEventWithJson(eventName: String, json: String?) {
        NativeUXCam.logEvent(eventName, json?.let(::JSONObject))
    }

    // --- Bug & exception reporting ---

    actual fun reportBugEvent(eventName: String) = NativeUXCam.reportBugEvent(eventName)

    actual fun reportBugEvent(eventName: String, properties: Map<String, Any?>?) {
        NativeUXCam.reportBugEvent(eventName, properties)
    }

    actual fun reportBugEventWithJson(eventName: String, json: String?) {
        NativeUXCam.reportBugEvent(eventName, json?.let(::JSONObject))
    }

    actual fun reportExceptionEvent(throwable: Throwable) =
        NativeUXCam.reportExceptionEvent(throwable)

    actual fun reportExceptionEvent(throwable: Throwable, properties: Map<String, Any?>?) {
        if (properties == null) NativeUXCam.reportExceptionEvent(throwable)
        else NativeUXCam.reportExceptionEvent(throwable, properties)
    }

    // --- User identity & properties ---

    actual fun setUserIdentity(userIdentity: String) = NativeUXCam.setUserIdentity(userIdentity)
    actual fun setUserProperty(propertyName: String, value: String) = NativeUXCam.setUserProperty(propertyName, value)
    actual fun setUserProperty(propertyName: String, value: Int) = NativeUXCam.setUserProperty(propertyName, value)
    actual fun setUserProperty(propertyName: String, value: Float) = NativeUXCam.setUserProperty(propertyName, value)
    actual fun setUserProperty(propertyName: String, value: Boolean) = NativeUXCam.setUserProperty(propertyName, value)
    actual fun setPushNotificationToken(token: String) = NativeUXCam.setPushNotificationToken(token)

    // --- Session properties ---

    actual fun setSessionProperty(propertyName: String, value: String) = NativeUXCam.setSessionProperty(propertyName, value)
    actual fun setSessionProperty(propertyName: String, value: Int) = NativeUXCam.setSessionProperty(propertyName, value)
    actual fun setSessionProperty(propertyName: String, value: Float) = NativeUXCam.setSessionProperty(propertyName, value)
    actual fun setSessionProperty(propertyName: String, value: Boolean) = NativeUXCam.setSessionProperty(propertyName, value)
    actual fun markSessionAsFavorite() = NativeUXCam.markSessionAsFavorite()

    // --- Screen tagging & ignore lists ---

    actual fun tagScreenName(screenName: String) = NativeUXCam.tagScreenName(screenName)
    actual fun setAutomaticScreenNameTagging(enable: Boolean) = NativeUXCam.setAutomaticScreenNameTagging(enable)
    actual fun setImprovedScreenCaptureEnabled(enable: Boolean) = NativeUXCam.setImprovedScreenCaptureEnabled(enable)
    actual fun addScreenNameToIgnore(screenName: String) = NativeUXCam.addScreenNameToIgnore(screenName)
    actual fun addScreenNamesToIgnore(screenNames: List<String>) = NativeUXCam.addScreenNamesToIgnore(screenNames)
    actual fun removeScreenNameToIgnore(screenName: String) = NativeUXCam.removeScreenNameToIgnore(screenName)
    actual fun removeScreenNamesToIgnore(screenNames: List<String>) = NativeUXCam.removeScreenNamesToIgnore(screenNames)
    actual fun removeAllScreenNamesToIgnore() = NativeUXCam.removeAllScreenNamesToIgnore()
    actual fun screenNamesBeingIgnored(): List<String> = NativeUXCam.screenNamesBeingIgnored()

    // --- Occlusion ---

    actual fun occludeSensitiveScreen(hide: Boolean) = NativeUXCam.occludeSensitiveScreen(hide)
    actual fun occludeSensitiveScreen(hide: Boolean, withoutGesture: Boolean) =
        NativeUXCam.occludeSensitiveScreen(hide, withoutGesture)
    actual fun occludeAllTextFields(occludeAll: Boolean) = NativeUXCam.occludeAllTextFields(occludeAll)

    actual fun applyOverlayOcclusion(withoutGesture: Boolean) {
        applyOcclusion(UXCamOverlay.Builder().withoutGesture(withoutGesture).build())
    }

    actual fun applyBlurOcclusion(blurRadius: Int, withoutGesture: Boolean) {
        applyOcclusion(UXCamBlur.Builder().blurRadius(blurRadius).withoutGesture(withoutGesture).build())
    }

    actual fun removeOcclusion() {
        currentOcclusion?.let { NativeUXCam.removeOcclusion(it) }
        currentOcclusion = null
    }

    private fun applyOcclusion(occlusion: NativeOcclusion) {
        removeOcclusion()
        NativeUXCam.applyOcclusion(occlusion)
        currentOcclusion = occlusion
    }

    // --- Recording control ---

    actual fun pauseScreenRecording() = NativeUXCam.pauseScreenRecording()
    actual fun resumeScreenRecording() = NativeUXCam.resumeScreenRecording()
    actual fun isRecording(): Boolean = NativeUXCam.isRecording()
    actual fun allowShortBreakForAnotherApp() = NativeUXCam.allowShortBreakForAnotherApp()
    actual fun allowShortBreakForAnotherApp(continueSession: Boolean) = NativeUXCam.allowShortBreakForAnotherApp(continueSession)
    actual fun allowShortBreakForAnotherApp(millis: Int) = NativeUXCam.allowShortBreakForAnotherApp(millis)
    actual fun resumeShortBreakForAnotherApp() = NativeUXCam.resumeShortBreakForAnotherApp()

    // --- Opt in / out ---

    actual fun optInOverall() = NativeUXCam.optInOverall()
    actual fun optOutOverall() = NativeUXCam.optOutOverall()
    actual fun optInOverallStatus(): Boolean = NativeUXCam.optInOverallStatus()
    actual fun optIntoVideoRecording() = NativeUXCam.optIntoVideoRecording()
    actual fun optOutOfVideoRecording() = NativeUXCam.optOutOfVideoRecording()
    actual fun optInVideoRecordingStatus(): Boolean = NativeUXCam.optInVideoRecordingStatus()

    // --- Multi-session ---

    actual fun getMultiSessionRecord(): Boolean = NativeUXCam.getMultiSessionRecord()
    actual fun setMultiSessionRecord(enable: Boolean) = NativeUXCam.setMultiSessionRecord(enable)

    // --- Crash handling ---

    actual fun disableCrashHandling(disabled: Boolean) = NativeUXCam.disableCrashHandling(disabled)

    // --- Verification ---

    actual fun addVerificationListener(onSuccess: () -> Unit, onFailure: (errorMessage: String) -> Unit) {
        NativeUXCam.addVerificationListener(object : OnVerificationListener {
            override fun onVerificationSuccess() = onSuccess()
            override fun onVerificationFailed(errorMessage: String?) = onFailure(errorMessage ?: "")
        })
    }

    // --- URLs, uploads & status ---

    actual fun urlForCurrentSession(): String? = NativeUXCam.urlForCurrentSession()
    actual fun urlForCurrentUser(): String? = NativeUXCam.urlForCurrentUser()
    actual fun deletePendingUploads() = NativeUXCam.deletePendingUploads()
    actual fun pendingSessionCount(): Int = NativeUXCam.pendingSessionCount()
    actual fun pendingUploads(onResult: (count: Int) -> Unit) =
        NativeUXCam.pendingUploads(NativeUXCam.OnPendingUploadsCallback { count -> onResult(count) })
    actual fun getSdkVersionInfo(): String = NativeUXCam.getSdkVersionInfo()

    // --- Android-only: per-view occlusion (android.view.View has no common type) ---

    fun occludeSensitiveView(view: View) = NativeUXCam.occludeSensitiveView(view)
    fun occludeSensitiveViewWithoutGesture(view: View) = NativeUXCam.occludeSensitiveViewWithoutGesture(view)
    fun unOccludeSensitiveView(view: View) = NativeUXCam.unOccludeSensitiveView(view)
}
