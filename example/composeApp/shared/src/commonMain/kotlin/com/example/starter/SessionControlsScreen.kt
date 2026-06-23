package com.example.starter

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCam

/**
 * Recording lifecycle, pause/resume, short break for another app (via the image picker),
 * and multi-session control — mirrors SessionControlsView.
 */
@Composable
internal fun SessionControlsScreen(onBack: () -> Unit) =
    DemoScaffold("Session Controls", "Session Controls", onBack) { report ->
        // Opening the picker sends the user to another app; end the short break (without
        // continuing) on return, matching the iOS sample's onChange handler.
        val openImagePicker = rememberImagePicker(onResult = {
            UXCam.allowShortBreakForAnotherApp(false)
            report("allowShortBreakForAnotherApp(continueSession = false) [returned]")
        })

        SectionHeader("Lifecycle")
        DemoButton("Start new session") {
            UXCam.startNewSession(); report("startNewSession()")
        }
        DemoButton("Stop and upload") {
            UXCam.stopSessionAndUploadData(); report("stopSessionAndUploadData()")
        }
        DemoButton("Stop with callback") {
            UXCam.stopSessionAndUploadData { report("onSessionStopped() callback") }
            report("stopSessionAndUploadData(callback)")
        }
        DemoButton("Cancel current session") {
            UXCam.cancelCurrentSession(); report("cancelCurrentSession()")
        }

        SectionHeader("Recording control")
        DemoButton("Pause recording") {
            UXCam.pauseScreenRecording(); report("pauseScreenRecording()")
        }
        DemoButton("Resume recording") {
            UXCam.resumeScreenRecording(); report("resumeScreenRecording()")
        }
        DemoButton("Check if recording") {
            report("isRecording() = ${UXCam.isRecording()}")
        }

        SectionHeader("Short break for another app")
        DemoButton("Allow short break (20s) + pick image") {
            UXCam.allowShortBreakForAnotherApp(20_000)
            report("allowShortBreakForAnotherApp(20000) → open image picker")
            openImagePicker()
        }
        DemoButton("Allow short break (default)") {
            UXCam.allowShortBreakForAnotherApp(); report("allowShortBreakForAnotherApp()")
        }
        DemoButton("Resume short break") {
            UXCam.resumeShortBreakForAnotherApp(); report("resumeShortBreakForAnotherApp()")
        }

        SectionHeader("Multi-session")
        DemoButton("Get multi-session status") {
            report("getMultiSessionRecord() = ${UXCam.getMultiSessionRecord()}")
        }
        DemoButton("Enable multi-session") {
            UXCam.setMultiSessionRecord(true); report("setMultiSessionRecord(true)")
        }
        DemoButton("Disable multi-session") {
            UXCam.setMultiSessionRecord(false); report("setMultiSessionRecord(false)")
        }
    }
