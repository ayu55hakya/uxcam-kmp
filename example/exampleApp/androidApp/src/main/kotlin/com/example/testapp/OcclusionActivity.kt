package com.example.testapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCamKMP

class OcclusionActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occlusion)
        UXCamKMP.tagScreenName("Occlusion")

        val secret = findViewById<android.view.View>(R.id.secretText)
        val password = findViewById<EditText>(R.id.passwordField)

        // Occlude these views in the recording on entry.
        UXCamKMP.occludeSensitiveView(secret)
        UXCamKMP.occludeSensitiveView(password)

        findViewById<Button>(R.id.unocclude).setOnClickListener {
            UXCamKMP.unOccludeSensitiveView(secret); report("unOccludeSensitiveView(password)")
        }
        findViewById<Button>(R.id.reocclude).setOnClickListener {
            UXCamKMP.occludeSensitiveView(password); report("occludeSensitiveView(password)")
        }
        findViewById<Button>(R.id.occludeWithoutGesture).setOnClickListener {
            UXCamKMP.occludeSensitiveViewWithoutGesture(password)
            report("occludeSensitiveViewWithoutGesture(password)")
        }

        findViewById<Button>(R.id.openDialog).setOnClickListener { showOccludedDialog() }

        // Screen-level occlusion
        findViewById<Button>(R.id.screenOn).setOnClickListener {
            UXCamKMP.occludeSensitiveScreen(true); report("occludeSensitiveScreen(true)")
        }
        findViewById<Button>(R.id.screenOff).setOnClickListener {
            UXCamKMP.occludeSensitiveScreen(false); report("occludeSensitiveScreen(false)")
        }
        findViewById<Button>(R.id.screenNoGesture).setOnClickListener {
            UXCamKMP.occludeSensitiveScreen(true, true); report("occludeSensitiveScreen(true, withoutGesture=true)")
        }

        // All text fields
        findViewById<Button>(R.id.allTextOn).setOnClickListener {
            UXCamKMP.occludeAllTextFields(true); report("occludeAllTextFields(true)")
        }
        findViewById<Button>(R.id.allTextOff).setOnClickListener {
            UXCamKMP.occludeAllTextFields(false); report("occludeAllTextFields(false)")
        }
    }

    private fun showOccludedDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_occluded, null)
        UXCamKMP.occludeSensitiveView(dialogView.findViewById<EditText>(R.id.dialogField))
        AlertDialog.Builder(this)
            .setTitle("Dialog")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }
}
