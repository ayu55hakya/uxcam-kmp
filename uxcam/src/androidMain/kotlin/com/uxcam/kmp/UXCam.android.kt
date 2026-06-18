package com.uxcam.kmp

import org.json.JSONObject
import com.uxcam.UXCam as NativeUXCam
import com.uxcam.datamodel.UXConfig as NativeUXConfig

actual object UXCam {

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

    actual fun isRecording(): Boolean = NativeUXCam.isRecording()
}
