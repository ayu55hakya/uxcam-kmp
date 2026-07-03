package com.example.template

import com.example.template.UxcamSetup.APP_KEY
import com.uxcam.kmp.UXCamKMP
import com.uxcam.kmp.UXConfig

/**
 * The single place to configure UXCam for this app.
 *
 * Lives in `commonMain`, so Android and iOS start UXCam from the exact same app key, the
 * same config, and the same code path. Each platform host (MainActivity / MainViewController)
 * calls [start] once before showing the UI. To point the app at a different UXCam account,
 * change [APP_KEY] here — nowhere else.
 */
object UxcamSetup {

    /** Replace with the app key from your UXCam dashboard. This is the only place it lives. */
    private const val APP_KEY = "n5ctt823s8qihkk-us"

    private var started = false

    fun start() {
        if (started) return
        started = true
        UXCamKMP.startWithConfiguration(
            UXConfig(
                appKey = APP_KEY,
                enableIntegrationLogging = true,
                enableAutomaticScreenNameTagging = true
            ),
        )

        UXCamKMP.optIntoVideoRecording()

        UXCamKMP.addVerificationListener(
            onSuccess = { println("UXCam: verification success") },
            onFailure = { message -> println("UXCam: verification failure — $message") },
        )
    }
}
