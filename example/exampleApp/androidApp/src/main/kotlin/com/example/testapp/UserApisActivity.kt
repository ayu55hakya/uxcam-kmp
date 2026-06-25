package com.example.testapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCamKMP

class UserApisActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_apis)
        UXCamKMP.tagScreenName("User APIs")

        findViewById<Button>(R.id.setIdentity).setOnClickListener {
            UXCamKMP.setUserIdentity("user_123456")
            report("setUserIdentity(\"user_123456\")")
        }
        findViewById<Button>(R.id.propString).setOnClickListener {
            UXCamKMP.setUserProperty("plan", "premium")
            report("setUserProperty(\"plan\", \"premium\")")
        }
        findViewById<Button>(R.id.propInt).setOnClickListener {
            UXCamKMP.setUserProperty("age", 30)
            report("setUserProperty(\"age\", 30)")
        }
        findViewById<Button>(R.id.propFloat).setOnClickListener {
            UXCamKMP.setUserProperty("rating", 4.5f)
            report("setUserProperty(\"rating\", 4.5f)")
        }
        findViewById<Button>(R.id.propBool).setOnClickListener {
            UXCamKMP.setUserProperty("subscribed", true)
            report("setUserProperty(\"subscribed\", true)")
        }
        findViewById<Button>(R.id.pushToken).setOnClickListener {
            UXCamKMP.setPushNotificationToken("sample_push_token")
            report("setPushNotificationToken(\"sample_push_token\")")
        }

        val key = findViewById<EditText>(R.id.propKey)
        val value = findViewById<EditText>(R.id.propValue)
        findViewById<Button>(R.id.setCustomProp).setOnClickListener {
            UXCamKMP.setUserProperty(key.text.toString(), value.text.toString())
            report("setUserProperty(\"${key.text}\", \"${value.text}\")")
        }
    }
}
