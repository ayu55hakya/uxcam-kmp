package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCamKMP

class StatusActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        UXCamKMP.tagScreenName("Status & Misc")

        findViewById<Button>(R.id.sessionUrl).setOnClickListener {
            report("urlForCurrentSession() = ${UXCamKMP.urlForCurrentSession()}")
        }
        findViewById<Button>(R.id.userUrl).setOnClickListener {
            report("urlForCurrentUser() = ${UXCamKMP.urlForCurrentUser()}")
        }
        findViewById<Button>(R.id.sdkVersion).setOnClickListener {
            report("getSdkVersionInfo() = ${UXCamKMP.getSdkVersionInfo()}")
        }
        findViewById<Button>(R.id.pendingCount).setOnClickListener {
            report("pendingSessionCount() = ${UXCamKMP.pendingSessionCount()}")
        }
        findViewById<Button>(R.id.pendingUploads).setOnClickListener {
            UXCamKMP.pendingUploads { count -> report("pendingUploads() callback = $count") }
            report("pendingUploads(callback) …")
        }
        findViewById<Button>(R.id.deletePending).setOnClickListener {
            UXCamKMP.deletePendingUploads(); report("deletePendingUploads()")
        }
        findViewById<Button>(R.id.crashOn).setOnClickListener {
            UXCamKMP.disableCrashHandling(true); report("disableCrashHandling(true)")
        }
        findViewById<Button>(R.id.crashOff).setOnClickListener {
            UXCamKMP.disableCrashHandling(false); report("disableCrashHandling(false)")
        }
    }
}
