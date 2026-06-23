package com.example.starter

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCam

/** Bug events and exception reporting — mirrors BugReportingView. */
@Composable
internal fun BugReportingScreen(onBack: () -> Unit) =
    DemoScaffold("Bug & Exception Reporting", "Bug & Exception Reporting", onBack) { report ->
        SectionHeader("Bug events")
        DemoButton("Bug without properties") {
            UXCam.reportBugEvent("bug_no_props"); report("reportBugEvent(\"bug_no_props\")")
        }
        DemoButton("Bug with map properties") {
            UXCam.reportBugEvent("bug_with_map", mapOf("screen" to "checkout", "code" to 500))
            report("reportBugEvent(\"bug_with_map\", {screen, code})")
        }
        DemoButton("Bug with JSON properties") {
            UXCam.reportBugEventWithJson("bug_with_json", "{\"screen\":\"checkout\",\"code\":500}")
            report("reportBugEventWithJson(\"bug_with_json\", json)")
        }

        SectionHeader("Exceptions")
        DemoButton("Report exception") {
            UXCam.reportExceptionEvent(RuntimeException("Sample exception"))
            report("reportExceptionEvent(RuntimeException)")
        }
        DemoButton("Report exception with properties") {
            UXCam.reportExceptionEvent(
                RuntimeException("Sample exception"),
                mapOf("where" to "BugReportingScreen"),
            )
            report("reportExceptionEvent(…, {where})")
        }
    }
