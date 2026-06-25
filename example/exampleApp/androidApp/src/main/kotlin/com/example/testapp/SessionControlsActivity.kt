package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCamKMP

class SessionControlsActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_controls)
        UXCamKMP.tagScreenName("Session Controls")

        // Lifecycle
        findViewById<Button>(R.id.startNewSession).setOnClickListener {
            UXCamKMP.startNewSession(); report("startNewSession()")
        }
        findViewById<Button>(R.id.stopAndUpload).setOnClickListener {
            UXCamKMP.stopSessionAndUploadData(); report("stopSessionAndUploadData()")
        }
        findViewById<Button>(R.id.stopWithListener).setOnClickListener {
            UXCamKMP.stopSessionAndUploadData { report("onSessionStopped() callback") }
            report("stopSessionAndUploadData(listener)")
        }
        findViewById<Button>(R.id.cancelSession).setOnClickListener {
            UXCamKMP.cancelCurrentSession(); report("cancelCurrentSession()")
        }

        // Recording control
        findViewById<Button>(R.id.pause).setOnClickListener {
            UXCamKMP.pauseScreenRecording(); report("pauseScreenRecording()")
        }
        findViewById<Button>(R.id.resume).setOnClickListener {
            UXCamKMP.resumeScreenRecording(); report("resumeScreenRecording()")
        }
        findViewById<Button>(R.id.isRecording).setOnClickListener {
            report("isRecording() = ${UXCamKMP.isRecording()}")
        }

        // Short break
        findViewById<Button>(R.id.allowShortBreak).setOnClickListener {
            UXCamKMP.allowShortBreakForAnotherApp(20_000)
            report("allowShortBreakForAnotherApp(20000) -> picker")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
        findViewById<Button>(R.id.allowShortBreakDefault).setOnClickListener {
            UXCamKMP.allowShortBreakForAnotherApp(); report("allowShortBreakForAnotherApp()")
        }
        findViewById<Button>(R.id.resumeShortBreak).setOnClickListener {
            UXCamKMP.resumeShortBreakForAnotherApp(); report("resumeShortBreakForAnotherApp()")
        }

        // Multi-session
        findViewById<Button>(R.id.multiSessionGet).setOnClickListener {
            report("getMultiSessionRecord() = ${UXCamKMP.getMultiSessionRecord()}")
        }
        findViewById<Button>(R.id.multiSessionOn).setOnClickListener {
            UXCamKMP.setMultiSessionRecord(true); report("setMultiSessionRecord(true)")
        }
        findViewById<Button>(R.id.multiSessionOff).setOnClickListener {
            UXCamKMP.setMultiSessionRecord(false); report("setMultiSessionRecord(false)")
        }
    }

    @Deprecated("startActivityForResult — mirrors the android-sdk demo's image-picker flow")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            UXCamKMP.allowShortBreakForAnotherApp(false)
            report("allowShortBreakForAnotherApp(false) [returned]")
        }
    }

    companion object {
        private const val PICK_IMAGE = 1
    }
}
