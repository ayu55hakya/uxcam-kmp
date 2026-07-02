@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.uxcam.kmp

import cocoapods.UXCam.UXCam_VerifyNotification
import cocoapods.UXCam.UXCam_VerifyNotification_StartedKey
import cocoapods.UXCam.UXCamBlurSetting
import cocoapods.UXCam.UXCamConfiguration
import cocoapods.UXCam.UXCamOcclusion
import cocoapods.UXCam.UXCamOccludeAllTextFields
import cocoapods.UXCam.UXCamOverlaySetting
import cocoapods.UXCam.UXCam as NativeUXCam
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.darwin.NSObjectProtocol

/**
 * iOS implementation — backed by the native iOS UXCam SDK (the `UXCam` pod, consumed
 * via the Kotlin CocoaPods plugin; cinterop package `cocoapods.UXCam`, aliased here as
 * [NativeUXCam] to avoid clashing with this `object UXCam`).
 *
 * A few common-API methods have no direct iOS equivalent — on iOS they are configured at
 * startup (via [UXCamConfiguration]) rather than toggled at runtime, or are handled
 * automatically. Those are documented no-ops below.
 */
private fun unsupported(name: String) {
    println("UXCam KMP (iOS): $name has no native iOS equivalent — no-op")
}

actual object UXCamKMP {

    // Retained so the verification observer isn't deallocated.
    private var verificationObserver: NSObjectProtocol? = null

    // --- Lifecycle & session ---
    actual fun startWithConfiguration(config: UXConfig) {
        val configuration = UXCamConfiguration(appKey = config.appKey)
        configuration.enableMultiSessionRecord = config.enableMultiSessionRecord
        configuration.enableCrashHandling = config.enableCrashHandling
        configuration.enableAutomaticScreenNameTagging = config.enableAutomaticScreenNameTagging
        configuration.enableIntegrationLogging = config.enableIntegrationLogging
        if (config.occludeAllTextFields) {
            configuration.occlusion = UXCamOcclusion(setting = UXCamOccludeAllTextFields())
        }
        NativeUXCam.startWithConfiguration(configuration)
    }

    actual fun startNewSession() = NativeUXCam.startNewSession()
    actual fun stopSessionAndUploadData() = NativeUXCam.stopSessionAndUploadData()
    actual fun stopSessionAndUploadData(onSessionStopped: () -> Unit) =
        NativeUXCam.stopSessionAndUploadData(onSessionStopped)
    actual fun cancelCurrentSession() = NativeUXCam.cancelCurrentSession()

    // --- Events ---
    actual fun logEvent(eventName: String) = NativeUXCam.logEvent(eventName)
    actual fun logEvent(eventName: String, properties: Map<String, Any?>?) =
        NativeUXCam.logEvent(eventName, withProperties = properties?.toNSDictionaryMap())
    actual fun logEventWithJson(eventName: String, json: String?) =
        NativeUXCam.logEvent(eventName, withProperties = json?.let { parseJsonToMap(it) })

    // --- Bug & exception reporting ---
    actual fun reportBugEvent(eventName: String) =
        NativeUXCam.reportBugEvent(eventName, properties = null)
    actual fun reportBugEvent(eventName: String, properties: Map<String, Any?>?) =
        NativeUXCam.reportBugEvent(eventName, properties = properties?.toNSDictionaryMap())
    actual fun reportBugEventWithJson(eventName: String, json: String?) =
        NativeUXCam.reportBugEvent(eventName, properties = json?.let { parseJsonToMap(it) })
    actual fun reportExceptionEvent(throwable: Throwable) =
        reportExceptionEvent(throwable, null)
    actual fun reportExceptionEvent(throwable: Throwable, properties: Map<String, Any?>?) =
        NativeUXCam.reportExceptionEvent(
            name = throwable::class.simpleName ?: "Throwable",
            reason = throwable.message ?: "",
            callStacks = emptyList<String>(),
            properties = properties?.toNSDictionaryMap(),
        )

    // --- User identity & properties ---
    actual fun setUserIdentity(userIdentity: String) = NativeUXCam.setUserIdentity(userIdentity)
    actual fun setUserProperty(propertyName: String, value: String) =
        NativeUXCam.setUserProperty(propertyName, value = value)
    actual fun setUserProperty(propertyName: String, value: Int) =
        NativeUXCam.setUserProperty(propertyName, value = NSNumber(int = value))
    actual fun setUserProperty(propertyName: String, value: Float) =
        NativeUXCam.setUserProperty(propertyName, value = NSNumber(float = value))
    actual fun setUserProperty(propertyName: String, value: Boolean) =
        NativeUXCam.setUserProperty(propertyName, value = NSNumber(bool = value))
    actual fun setPushNotificationToken(token: String) =
        NativeUXCam.setPushNotificationToken(token)

    // --- Session properties ---
    actual fun setSessionProperty(propertyName: String, value: String) =
        NativeUXCam.setSessionProperty(propertyName, value = value)
    actual fun setSessionProperty(propertyName: String, value: Int) =
        NativeUXCam.setSessionProperty(propertyName, value = NSNumber(int = value))
    actual fun setSessionProperty(propertyName: String, value: Float) =
        NativeUXCam.setSessionProperty(propertyName, value = NSNumber(float = value))
    actual fun setSessionProperty(propertyName: String, value: Boolean) =
        NativeUXCam.setSessionProperty(propertyName, value = NSNumber(bool = value))
    // iOS has no "favorite session" API.
    actual fun markSessionAsFavorite() = unsupported("markSessionAsFavorite")

    // --- Screen tagging & ignore lists ---
    actual fun tagScreenName(screenName: String) = NativeUXCam.tagScreenName(screenName)
    // On iOS these are configured on UXCamConfiguration at startup, not toggled at runtime.
    actual fun setAutomaticScreenNameTagging(enable: Boolean) = unsupported("setAutomaticScreenNameTagging")
    actual fun setImprovedScreenCaptureEnabled(enable: Boolean) = unsupported("setImprovedScreenCaptureEnabled")
    actual fun addScreenNameToIgnore(screenName: String) = NativeUXCam.addScreenNameToIgnore(screenName)
    actual fun addScreenNamesToIgnore(screenNames: List<String>) = NativeUXCam.addScreenNamesToIgnore(screenNames)
    actual fun removeScreenNameToIgnore(screenName: String) = NativeUXCam.removeScreenNameToIgnore(screenName)
    actual fun removeScreenNamesToIgnore(screenNames: List<String>) = NativeUXCam.removeScreenNamesToIgnore(screenNames)
    actual fun removeAllScreenNamesToIgnore() = NativeUXCam.removeAllScreenNamesToIgnore()
    actual fun screenNamesBeingIgnored(): List<String> =
        NativeUXCam.screenNamesBeingIgnored().filterIsInstance<String>()

    // --- Occlusion (screen-level) ---
    actual fun occludeSensitiveScreen(hide: Boolean) = NativeUXCam.occludeSensitiveScreen(hide)
    actual fun occludeSensitiveScreen(hide: Boolean, withoutGesture: Boolean) =
        NativeUXCam.occludeSensitiveScreen(hide, hideGestures = withoutGesture)
    actual fun occludeAllTextFields(occludeAll: Boolean) = NativeUXCam.occludeAllTextFields(occludeAll)
    actual fun applyOverlayOcclusion(overlayOcclusion: KMPUXCamOverlay) {
        val setting = UXCamOverlaySetting(color = UIColor.redColor())
        setting.hideGestures = overlayOcclusion.hideGestures
        if (overlayOcclusion.screens.isNullOrEmpty()) {
            NativeUXCam.applyOcclusion(setting)
        } else {
            NativeUXCam.applyOcclusion(setting, toScreens = overlayOcclusion.screens)
        }
    }
    actual fun applyBlurOcclusion(blurOcclusion: KMPUXCamBlur) {
        val setting = UXCamBlurSetting(radius = blurOcclusion.blurRadius)
        setting.hideGestures = blurOcclusion.hideGestures
        if (blurOcclusion.screens.isNullOrEmpty()) {
            NativeUXCam.applyOcclusion(setting)
        } else {
            NativeUXCam.applyOcclusion(setting, toScreens = blurOcclusion.screens)
        }
    }
    actual fun removeOcclusion() = NativeUXCam.removeOcclusion()

    // --- Recording control ---
    actual fun pauseScreenRecording() = NativeUXCam.pauseScreenRecording()
    actual fun resumeScreenRecording() = NativeUXCam.resumeScreenRecording()
    actual fun isRecording(): Boolean = NativeUXCam.isRecording()
    actual fun allowShortBreakForAnotherApp() = NativeUXCam.allowShortBreakForAnotherApp(true)
    actual fun allowShortBreakForAnotherApp(continueSession: Boolean) =
        NativeUXCam.allowShortBreakForAnotherApp(continueSession)
    actual fun allowShortBreakForAnotherApp(millis: Int) {
        NativeUXCam.setAllowShortBreakMaxDuration(millis.toDouble())
        NativeUXCam.allowShortBreakForAnotherApp(true)
    }
    // iOS resumes the session automatically when the app returns.
    actual fun resumeShortBreakForAnotherApp() = unsupported("resumeShortBreakForAnotherApp")

    // --- Opt in / out --- (iOS calls the per-frame recording "schematic recording")
    actual fun optInOverall() = NativeUXCam.optInOverall()
    actual fun optOutOverall() = NativeUXCam.optOutOverall()
    actual fun optInOverallStatus(): Boolean = NativeUXCam.optInOverallStatus()
    actual fun optIntoVideoRecording() = NativeUXCam.optIntoSchematicRecordings()
    actual fun optOutOfVideoRecording() = NativeUXCam.optOutOfSchematicRecordings()
    actual fun optInVideoRecordingStatus(): Boolean = NativeUXCam.optInSchematicRecordingStatus()

    // --- Multi-session ---
    actual fun getMultiSessionRecord(): Boolean = NativeUXCam.getMultiSessionRecord()
    actual fun setMultiSessionRecord(enable: Boolean) = NativeUXCam.setMultiSessionRecord(enable)

    // --- Crash handling ---
    actual fun disableCrashHandling(disabled: Boolean) = NativeUXCam.disableCrashHandling(disabled)

    // --- Verification --- (iOS reports verification via an NSNotification, not a callback)
    actual fun addVerificationListener(onSuccess: () -> Unit, onFailure: (errorMessage: String) -> Unit) {
        verificationObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UXCam_VerifyNotification,
            `object` = null,
            queue = null,
        ) { notification: NSNotification? ->
            val started = (notification?.userInfo?.get(UXCam_VerifyNotification_StartedKey) as? NSNumber)
                ?.boolValue ?: false
            if (started) onSuccess() else onFailure("UXCam verification failed")
        }
    }

    // --- URLs, uploads & status ---
    actual fun urlForCurrentSession(): String? = NativeUXCam.urlForCurrentSession()
    actual fun urlForCurrentUser(): String? = NativeUXCam.urlForCurrentUser()
    actual fun deletePendingUploads() = NativeUXCam.deletePendingUploads()
    actual fun pendingSessionCount(): Int = NativeUXCam.pendingUploads().toInt()
    actual fun pendingUploads(onResult: (count: Int) -> Unit) = onResult(NativeUXCam.pendingUploads().toInt())
    actual fun getSdkVersionInfo(): String = "UXCam iOS (native SDK)"

    // --- iOS-only: per-view occlusion (UIView has no common type) ---

    fun occludeSensitiveView(view: UIView) = NativeUXCam.occludeSensitiveView(view)
    fun occludeSensitiveViewWithoutGesture(view: UIView) = NativeUXCam.occludeSensitiveViewWithoutGesture(view)
    fun unOccludeSensitiveView(view: UIView) = NativeUXCam.unOccludeSensitiveView(view)
}

/** Bridges a Kotlin map to the `NSDictionary<NSString*, id>` the iOS SDK expects. */
@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.toNSDictionaryMap(): Map<Any?, *> = this as Map<Any?, *>

/** Parses a JSON object string into a property map via NSJSONSerialization; null on failure. */
private fun parseJsonToMap(json: String): Map<Any?, *>? {
    val data = (json as NSString).dataUsingEncoding(NSUTF8StringEncoding) as? NSData ?: return null
    @Suppress("UNCHECKED_CAST")
    return NSJSONSerialization.JSONObjectWithData(data, options = 0u, error = null) as? Map<Any?, *>
}
