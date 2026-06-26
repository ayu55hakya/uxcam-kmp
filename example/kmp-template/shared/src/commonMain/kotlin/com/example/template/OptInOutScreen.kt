package com.example.template

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCamKMP

/** Recording consent (overall + video) and status checks — mirrors OptInOutView. */
@Composable
internal fun OptInOutScreen(onBack: () -> Unit) = DemoScaffold("Opt In / Out", "Opt In / Out", onBack) { report ->
    SectionHeader("Overall")
    DemoButton("Opt in overall") {
        UXCamKMP.optInOverall(); report("optInOverall()")
    }
    DemoButton("Opt out overall") {
        UXCamKMP.optOutOverall(); report("optOutOverall()")
    }
    DemoButton("Check opt-in status") {
        report("optInOverallStatus() = ${UXCamKMP.optInOverallStatus()}")
    }

    SectionHeader("Video recording")
    DemoButton("Opt in to video recording") {
        UXCamKMP.optIntoVideoRecording(); report("optIntoVideoRecording()")
    }
    DemoButton("Opt out of video recording") {
        UXCamKMP.optOutOfVideoRecording(); report("optOutOfVideoRecording()")
    }
    DemoButton("Check video opt-in status") {
        report("optInVideoRecordingStatus() = ${UXCamKMP.optInVideoRecordingStatus()}")
    }
}
