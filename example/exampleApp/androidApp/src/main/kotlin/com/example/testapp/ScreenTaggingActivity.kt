package com.example.testapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCamKMP

class ScreenTaggingActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_tagging)
        UXCamKMP.tagScreenName("Screen Tagging")

        val screenName = findViewById<EditText>(R.id.screenName)
        findViewById<Button>(R.id.tagScreen).setOnClickListener {
            val name = screenName.text.toString().ifBlank { "CustomScreen" }
            UXCamKMP.tagScreenName(name); report("tagScreenName(\"$name\")")
        }
        findViewById<Button>(R.id.autoTagOn).setOnClickListener {
            UXCamKMP.setAutomaticScreenNameTagging(true); report("setAutomaticScreenNameTagging(true)")
        }
        findViewById<Button>(R.id.autoTagOff).setOnClickListener {
            UXCamKMP.setAutomaticScreenNameTagging(false); report("setAutomaticScreenNameTagging(false)")
        }
        findViewById<Button>(R.id.improvedCaptureOn).setOnClickListener {
            UXCamKMP.setImprovedScreenCaptureEnabled(true); report("setImprovedScreenCaptureEnabled(true)")
        }
        findViewById<Button>(R.id.improvedCaptureOff).setOnClickListener {
            UXCamKMP.setImprovedScreenCaptureEnabled(false); report("setImprovedScreenCaptureEnabled(false)")
        }

        val ignoreName = findViewById<EditText>(R.id.ignoreName)
        findViewById<Button>(R.id.addIgnore).setOnClickListener {
            val name = ignoreName.text.toString().ifBlank { "Settings" }
            UXCamKMP.addScreenNameToIgnore(name); report("addScreenNameToIgnore(\"$name\")")
        }
        findViewById<Button>(R.id.addIgnoreList).setOnClickListener {
            UXCamKMP.addScreenNamesToIgnore(listOf("Login", "Payment"))
            report("addScreenNamesToIgnore([Login, Payment])")
        }
        findViewById<Button>(R.id.removeIgnore).setOnClickListener {
            val name = ignoreName.text.toString().ifBlank { "Settings" }
            UXCamKMP.removeScreenNameToIgnore(name); report("removeScreenNameToIgnore(\"$name\")")
        }
        findViewById<Button>(R.id.removeIgnoreList).setOnClickListener {
            UXCamKMP.removeScreenNamesToIgnore(listOf("Login", "Payment"))
            report("removeScreenNamesToIgnore([Login, Payment])")
        }
        findViewById<Button>(R.id.removeAllIgnore).setOnClickListener {
            UXCamKMP.removeAllScreenNamesToIgnore(); report("removeAllScreenNamesToIgnore()")
        }
        findViewById<Button>(R.id.listIgnored).setOnClickListener {
            report("screenNamesBeingIgnored() = ${UXCamKMP.screenNamesBeingIgnored()}")
        }
    }
}
