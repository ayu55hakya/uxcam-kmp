package com.example.template

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCamKMP

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
            UXCamKMP.allowShortBreakForAnotherApp(false)
            report("allowShortBreakForAnotherApp(continueSession = false) [returned]")
        })

        SectionHeader("Lifecycle")
        DemoButton("Start new session") {
            UXCamKMP.startNewSession(); report("startNewSession()")
        }
        DemoButton("Stop and upload") {
            UXCamKMP.stopSessionAndUploadData(); report("stopSessionAndUploadData()")
        }
        DemoButton("Stop with callback") {
            UXCamKMP.stopSessionAndUploadData { report("onSessionStopped() callback") }
            report("stopSessionAndUploadData(callback)")
        }
        DemoButton("Cancel current session") {
            UXCamKMP.cancelCurrentSession(); report("cancelCurrentSession()")
        }

        SectionHeader("Recording control")
        DemoButton("Pause recording") {
            UXCamKMP.pauseScreenRecording(); report("pauseScreenRecording()")
        }
        DemoButton("Resume recording") {
            UXCamKMP.resumeScreenRecording(); report("resumeScreenRecording()")
        }
        DemoButton("Check if recording") {
            report("isRecording() = ${UXCamKMP.isRecording()}")
        }

        SectionHeader("Short break for another app")
        DemoButton("Allow short break (20s) + pick image") {
            UXCamKMP.allowShortBreakForAnotherApp(20_000)
            report("allowShortBreakForAnotherApp(20000) → open image picker")
            openImagePicker()
        }
        DemoButton("Allow short break (default)") {
            UXCamKMP.allowShortBreakForAnotherApp(); report("allowShortBreakForAnotherApp()")
        }
        DemoButton("Resume short break") {
            UXCamKMP.resumeShortBreakForAnotherApp(); report("resumeShortBreakForAnotherApp()")
        }

        SectionHeader("Multi-session")
        DemoButton("Get multi-session status") {
            report("getMultiSessionRecord() = ${UXCamKMP.getMultiSessionRecord()}")
        }
        DemoButton("Enable multi-session") {
            UXCamKMP.setMultiSessionRecord(true); report("setMultiSessionRecord(true)")
        }
        DemoButton("Disable multi-session") {
            UXCamKMP.setMultiSessionRecord(false); report("setMultiSessionRecord(false)")
        }
    }
