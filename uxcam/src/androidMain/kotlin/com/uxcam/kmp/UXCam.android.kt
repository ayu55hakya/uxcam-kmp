package com.uxcam.kmp

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

    actual fun tagScreenName(screenName: String) = NativeUXCam.tagScreenName(screenName)

    actual fun setUserIdentity(userIdentity: String) = NativeUXCam.setUserIdentity(userIdentity)

    actual fun occludeSensitiveScreen(hide: Boolean) = NativeUXCam.occludeSensitiveScreen(hide)

    actual fun isRecording(): Boolean = NativeUXCam.isRecording()
}
