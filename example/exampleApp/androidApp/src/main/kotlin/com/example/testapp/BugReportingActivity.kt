package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

class BugReportingActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bug_reporting)
        UXCam.tagScreenName("Bug & Exception Reporting")

        findViewById<Button>(R.id.bugNoProps).setOnClickListener {
            UXCam.reportBugEvent("bug_no_props"); report("reportBugEvent(no props)")
        }
        findViewById<Button>(R.id.bugMap).setOnClickListener {
            UXCam.reportBugEvent("bug_with_map", mapOf("screen" to "checkout", "code" to 500))
            report("reportBugEvent(map)")
        }
        findViewById<Button>(R.id.bugJson).setOnClickListener {
            UXCam.reportBugEventWithJson("bug_with_json", "{\"screen\":\"checkout\",\"code\":500}")
            report("reportBugEventWithJson(json)")
        }
        findViewById<Button>(R.id.exception).setOnClickListener {
            UXCam.reportExceptionEvent(IllegalStateException("Sample exception"))
            report("reportExceptionEvent(exception)")
        }
        findViewById<Button>(R.id.exceptionMap).setOnClickListener {
            UXCam.reportExceptionEvent(
                IllegalStateException("Sample exception"),
                mapOf("where" to "BugReportingActivity"),
            )
            report("reportExceptionEvent(exception, map)")
        }
    }
}
