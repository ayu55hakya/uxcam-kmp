package com.example.testapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCam

class UserApisActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_apis)
        UXCam.tagScreenName("User APIs")

        findViewById<Button>(R.id.setIdentity).setOnClickListener {
            UXCam.setUserIdentity("user_12345")
            report("setUserIdentity(\"user_12345\")")
        }
        findViewById<Button>(R.id.setPredefinedProp).setOnClickListener {
            UXCam.setUserProperty("plan", "premium")
            report("setUserProperty(\"plan\", \"premium\")")
        }
        val propKey = findViewById<EditText>(R.id.propKey)
        val propValue = findViewById<EditText>(R.id.propValue)
        findViewById<Button>(R.id.setCustomProp).setOnClickListener {
            UXCam.setUserProperty(propKey.text.toString(), propValue.text.toString())
            report("setUserProperty(\"${propKey.text}\", \"${propValue.text}\")")
        }
    }
}
