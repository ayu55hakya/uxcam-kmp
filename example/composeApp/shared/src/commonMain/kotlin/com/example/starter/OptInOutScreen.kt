package com.example.starter

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCam

/** Recording consent (overall + video) and status checks — mirrors OptInOutView. */
@Composable
internal fun OptInOutScreen(onBack: () -> Unit) = DemoScaffold("Opt In / Out", "Opt In / Out", onBack) { report ->
    SectionHeader("Overall")
    DemoButton("Opt in overall") {
        UXCam.optInOverall(); report("optInOverall()")
    }
    DemoButton("Opt out overall") {
        UXCam.optOutOverall(); report("optOutOverall()")
    }
    DemoButton("Check opt-in status") {
        report("optInOverallStatus() = ${UXCam.optInOverallStatus()}")
    }

    SectionHeader("Video recording")
    DemoButton("Opt in to video recording") {
        UXCam.optIntoVideoRecording(); report("optIntoVideoRecording()")
    }
    DemoButton("Opt out of video recording") {
        UXCam.optOutOfVideoRecording(); report("optOutOfVideoRecording()")
    }
    DemoButton("Check video opt-in status") {
        report("optInVideoRecordingStatus() = ${UXCam.optInVideoRecordingStatus()}")
    }
}
