package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCamKMP

class OptInOutActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opt_in_out)
        UXCamKMP.tagScreenName("Opt In / Out")

        findViewById<Button>(R.id.optInOverall).setOnClickListener {
            UXCamKMP.optInOverall(); report("optInOverall()")
        }
        findViewById<Button>(R.id.optOutOverall).setOnClickListener {
            UXCamKMP.optOutOverall(); report("optOutOverall()")
        }
        findViewById<Button>(R.id.optInOverallStatus).setOnClickListener {
            report("optInOverallStatus() = ${UXCamKMP.optInOverallStatus()}")
        }
        findViewById<Button>(R.id.optInVideo).setOnClickListener {
            UXCamKMP.optIntoVideoRecording(); report("optIntoVideoRecording()")
        }
        findViewById<Button>(R.id.optOutVideo).setOnClickListener {
            UXCamKMP.optOutOfVideoRecording(); report("optOutOfVideoRecording()")
        }
        findViewById<Button>(R.id.optInVideoStatus).setOnClickListener {
            report("optInVideoRecordingStatus() = ${UXCamKMP.optInVideoRecordingStatus()}")
        }
    }
}
