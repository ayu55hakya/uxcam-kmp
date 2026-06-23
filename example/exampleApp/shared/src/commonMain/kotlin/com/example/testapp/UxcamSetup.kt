package com.example.testapp

import com.uxcam.kmp.UXCam
import com.uxcam.kmp.UXConfig

/**
 * Single runtime source of the UXCam app key for the sample.
 *
 * Lives in `commonMain`, so the Android app and the iOS app start UXCam from the exact
 * same value and the same code path. The key is handed to the SDK at runtime via
 * [UXCam.startWithConfiguration] — it is never compiled into the `:uxcam` wrapper, which
 * stays key-free. Each platform's thin host (MainActivity / MainViewController) calls
 * [start] once before showing the shared UI.
 */
object UxcamSetup {
    private val appKey = "n5ctt823s8qihkk-us"
    private var started = false

    fun start() {
        if (started) return
        started = true
        UXCam.startWithConfiguration(
            UXConfig(appKey = appKey, enableIntegrationLogging = true),
        )
        UXCam.addVerificationListener(
            onSuccess = { println("UXCam: verification success") },
            onFailure = { message -> println("UXCam: verification failure — $message") },
        )
    }
}
