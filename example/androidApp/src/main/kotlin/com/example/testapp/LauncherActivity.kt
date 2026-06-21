package com.example.testapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.uxcam.kmp.UXCam
import com.uxcam.kmp.UXConfig

/**
 * Launcher / chooser screen. Starts a single UXCam session for the whole app (before
 * either UI path), then lets you open the Compose or the XML (View) sample.
 */
class LauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UXCam.startWithConfiguration(
            UXConfig(appKey = "app_key", enableIntegrationLogging = true),
        )

        UXCam.addVerificationListener(
            onSuccess = {
                Log.d("uxcam","success")
                        },
            onFailure = { msg ->
                Log.d("uxcam","failure")
                        },
        )

        setContentView(R.layout.activity_launcher)
        findViewById<Button>(R.id.composeButton).setOnClickListener {
            startActivity(Intent(this, ComposeHomeActivity::class.java))
        }
        findViewById<Button>(R.id.xmlButton).setOnClickListener {
            startActivity(Intent(this, ViewHomeActivity::class.java))
        }
    }
}
