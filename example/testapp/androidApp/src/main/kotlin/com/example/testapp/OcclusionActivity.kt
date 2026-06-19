package com.example.testapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCam

class OcclusionActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occlusion)
        UXCam.tagScreenName("Occlusion")

        // Occlude these views in the recording.
        UXCam.occludeSensitiveView(findViewById(R.id.secretText))
        UXCam.occludeSensitiveView(findViewById(R.id.passwordField))

        findViewById<Button>(R.id.openDialog).setOnClickListener { showOccludedDialog() }
    }

    private fun showOccludedDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_occluded, null)
        UXCam.occludeSensitiveView(dialogView.findViewById<EditText>(R.id.dialogField))
        AlertDialog.Builder(this)
            .setTitle("Dialog")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }
}
