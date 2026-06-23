package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

class OptInOutActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opt_in_out)
        UXCam.tagScreenName("Opt In / Out")

        findViewById<Button>(R.id.optInOverall).setOnClickListener {
            UXCam.optInOverall(); report("optInOverall()")
        }
        findViewById<Button>(R.id.optOutOverall).setOnClickListener {
            UXCam.optOutOverall(); report("optOutOverall()")
        }
        findViewById<Button>(R.id.optInOverallStatus).setOnClickListener {
            report("optInOverallStatus() = ${UXCam.optInOverallStatus()}")
        }
        findViewById<Button>(R.id.optInVideo).setOnClickListener {
            UXCam.optIntoVideoRecording(); report("optIntoVideoRecording()")
        }
        findViewById<Button>(R.id.optOutVideo).setOnClickListener {
            UXCam.optOutOfVideoRecording(); report("optOutOfVideoRecording()")
        }
        findViewById<Button>(R.id.optInVideoStatus).setOnClickListener {
            report("optInVideoRecordingStatus() = ${UXCam.optInVideoRecordingStatus()}")
        }
    }
}
