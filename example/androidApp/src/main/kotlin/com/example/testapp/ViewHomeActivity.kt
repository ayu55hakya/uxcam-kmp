package com.example.testapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

/** XML home — a menu that opens each feature screen (mirrors the Compose HomeScreen). */
class ViewHomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_home)
        UXCam.tagScreenName("XML View Home")

        open(R.id.userApisButton, UserApisActivity::class.java)
        open(R.id.sessionPropertiesButton, SessionPropertiesActivity::class.java)
        open(R.id.customEventsButton, CustomEventsActivity::class.java)
        open(R.id.bugReportingButton, BugReportingActivity::class.java)
        open(R.id.occlusionButton, OcclusionActivity::class.java)
        open(R.id.fullScreenOcclusionButton, FullScreenOcclusionActivity::class.java)
        open(R.id.screenTaggingButton, ScreenTaggingActivity::class.java)
        open(R.id.optInOutButton, OptInOutActivity::class.java)
        open(R.id.sessionControlsButton, SessionControlsActivity::class.java)
        open(R.id.statusButton, StatusActivity::class.java)
        open(R.id.crashButton, CrashActivity::class.java)
    }

    private fun open(buttonId: Int, target: Class<out Activity>) {
        findViewById<Button>(buttonId).setOnClickListener {
            startActivity(Intent(this, target))
        }
    }
}
