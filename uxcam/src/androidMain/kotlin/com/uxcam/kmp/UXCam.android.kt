package com.uxcam.kmp

import org.json.JSONObject
import com.uxcam.UXCam as NativeUXCam
import com.uxcam.datamodel.UXConfig as NativeUXConfig
import com.uxcam.screenshot.model.UXCamBlur
import com.uxcam.screenshot.model.UXCamOcclusion as NativeOcclusion
import com.uxcam.screenshot.model.UXCamOverlay

actual object UXCam {

    // Tracks the active full-screen occlusion so removeOcclusion() can clear it.
    // A small data object (not a Context/View), so safe to hold statically.
    private var currentOcclusion: NativeOcclusion? = null

    actual fun startWithConfiguration(config: UXConfig) {
        val nativeConfig = NativeUXConfig.Builder(config.appKey)
            .enableAutomaticScreenNameTagging(config.enableAutomaticScreenNameTagging)
            .enableMultiSessionRecord(config.enableMultiSessionRecord)
            .enableCrashHandling(config.enableCrashHandling)
            .enableIntegrationLogging(config.enableIntegrationLogging)
            .build()

        NativeUXCam.startWithConfiguration(nativeConfig)

        if (config.occludeAllTextFields) {
            NativeUXCam.occludeAllTextFields(true)
        }
    }

    actual fun stopSessionAndUploadData() = NativeUXCam.stopSessionAndUploadData()

    actual fun logEvent(eventName: String) = NativeUXCam.logEvent(eventName)

    actual fun logEvent(eventName: String, properties: Map<String, Any?>?) {
        // `properties`' static Map type picks the native Map overload (vs JSONObject),
        // so a null map correctly hits logEvent(String, Map) — the no-JSON path.
        NativeUXCam.logEvent(eventName, properties)
    }

    actual fun logEventWithJson(eventName: String, json: String?) {
        val params: JSONObject? = json?.let(::JSONObject)
        NativeUXCam.logEvent(eventName, params)
    }

    actual fun tagScreenName(screenName: String) = NativeUXCam.tagScreenName(screenName)

    actual fun setUserIdentity(userIdentity: String) = NativeUXCam.setUserIdentity(userIdentity)

    actual fun setUserProperty(propertyName: String, value: String) =
        NativeUXCam.setUserProperty(propertyName, value)

    actual fun occludeSensitiveScreen(hide: Boolean) = NativeUXCam.occludeSensitiveScreen(hide)

    actual fun applyOverlayOcclusion(withoutGesture: Boolean) {
        val overlay = UXCamOverlay.Builder().withoutGesture(withoutGesture).build()
        applyOcclusion(overlay)
    }

    actual fun applyBlurOcclusion(blurRadius: Int, withoutGesture: Boolean) {
        val blur = UXCamBlur.Builder().blurRadius(blurRadius).withoutGesture(withoutGesture).build()
        applyOcclusion(blur)
    }

    actual fun removeOcclusion() {
        currentOcclusion?.let { NativeUXCam.removeOcclusion(it) }
        currentOcclusion = null
    }

    // Replace any current full-screen occlusion so only one is active at a time.
    private fun applyOcclusion(occlusion: NativeOcclusion) {
        removeOcclusion()
        NativeUXCam.applyOcclusion(occlusion)
        currentOcclusion = occlusion
    }

    actual fun isRecording(): Boolean = NativeUXCam.isRecording()
}
