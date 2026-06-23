package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

class SessionControlsActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_controls)
        UXCam.tagScreenName("Session Controls")

        // Lifecycle
        findViewById<Button>(R.id.startNewSession).setOnClickListener {
            UXCam.startNewSession(); report("startNewSession()")
        }
        findViewById<Button>(R.id.stopAndUpload).setOnClickListener {
            UXCam.stopSessionAndUploadData(); report("stopSessionAndUploadData()")
        }
        findViewById<Button>(R.id.stopWithListener).setOnClickListener {
            UXCam.stopSessionAndUploadData { report("onSessionStopped() callback") }
            report("stopSessionAndUploadData(listener)")
        }
        findViewById<Button>(R.id.cancelSession).setOnClickListener {
            UXCam.cancelCurrentSession(); report("cancelCurrentSession()")
        }

        // Recording control
        findViewById<Button>(R.id.pause).setOnClickListener {
            UXCam.pauseScreenRecording(); report("pauseScreenRecording()")
        }
        findViewById<Button>(R.id.resume).setOnClickListener {
            UXCam.resumeScreenRecording(); report("resumeScreenRecording()")
        }
        findViewById<Button>(R.id.isRecording).setOnClickListener {
            report("isRecording() = ${UXCam.isRecording()}")
        }

        // Short break
        findViewById<Button>(R.id.allowShortBreak).setOnClickListener {
            UXCam.allowShortBreakForAnotherApp(20_000)
            report("allowShortBreakForAnotherApp(20000) -> picker")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
        findViewById<Button>(R.id.allowShortBreakDefault).setOnClickListener {
            UXCam.allowShortBreakForAnotherApp(); report("allowShortBreakForAnotherApp()")
        }
        findViewById<Button>(R.id.resumeShortBreak).setOnClickListener {
            UXCam.resumeShortBreakForAnotherApp(); report("resumeShortBreakForAnotherApp()")
        }

        // Multi-session
        findViewById<Button>(R.id.multiSessionGet).setOnClickListener {
            report("getMultiSessionRecord() = ${UXCam.getMultiSessionRecord()}")
        }
        findViewById<Button>(R.id.multiSessionOn).setOnClickListener {
            UXCam.setMultiSessionRecord(true); report("setMultiSessionRecord(true)")
        }
        findViewById<Button>(R.id.multiSessionOff).setOnClickListener {
            UXCam.setMultiSessionRecord(false); report("setMultiSessionRecord(false)")
        }
    }

    @Deprecated("startActivityForResult — mirrors the android-sdk demo's image-picker flow")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            UXCam.allowShortBreakForAnotherApp(false)
            report("allowShortBreakForAnotherApp(false) [returned]")
        }
    }

    companion object {
        private const val PICK_IMAGE = 1
    }
}
