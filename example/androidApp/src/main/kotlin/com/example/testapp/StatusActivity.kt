package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

class StatusActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        UXCam.tagScreenName("Status & Misc")

        findViewById<Button>(R.id.sessionUrl).setOnClickListener {
            report("urlForCurrentSession() = ${UXCam.urlForCurrentSession()}")
        }
        findViewById<Button>(R.id.userUrl).setOnClickListener {
            report("urlForCurrentUser() = ${UXCam.urlForCurrentUser()}")
        }
        findViewById<Button>(R.id.sdkVersion).setOnClickListener {
            report("getSdkVersionInfo() = ${UXCam.getSdkVersionInfo()}")
        }
        findViewById<Button>(R.id.pendingCount).setOnClickListener {
            report("pendingSessionCount() = ${UXCam.pendingSessionCount()}")
        }
        findViewById<Button>(R.id.pendingUploads).setOnClickListener {
            UXCam.pendingUploads { count -> report("pendingUploads() callback = $count") }
            report("pendingUploads(callback) …")
        }
        findViewById<Button>(R.id.deletePending).setOnClickListener {
            UXCam.deletePendingUploads(); report("deletePendingUploads()")
        }
        findViewById<Button>(R.id.crashOn).setOnClickListener {
            UXCam.disableCrashHandling(true); report("disableCrashHandling(true)")
        }
        findViewById<Button>(R.id.crashOff).setOnClickListener {
            UXCam.disableCrashHandling(false); report("disableCrashHandling(false)")
        }
    }
}
