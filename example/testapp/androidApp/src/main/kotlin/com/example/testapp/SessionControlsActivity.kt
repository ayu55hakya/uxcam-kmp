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

        findViewById<Button>(R.id.allowShortBreak).setOnClickListener {
            UXCam.allowShortBreakForAnotherApp(20_000)
            report("allowShortBreakForAnotherApp(20000) -> picker")
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }
        findViewById<Button>(R.id.cancelSession).setOnClickListener {
            UXCam.cancelCurrentSession(); report("cancelCurrentSession()")
        }
        findViewById<Button>(R.id.pause).setOnClickListener {
            UXCam.pauseScreenRecording(); report("pauseScreenRecording()")
        }
        findViewById<Button>(R.id.resume).setOnClickListener {
            UXCam.resumeScreenRecording(); report("resumeScreenRecording()")
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
