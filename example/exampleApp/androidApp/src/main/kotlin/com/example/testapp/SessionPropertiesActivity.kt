package com.example.testapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCamKMP

class SessionPropertiesActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_properties)
        UXCamKMP.tagScreenName("Session Properties")

        findViewById<Button>(R.id.propString).setOnClickListener {
            UXCamKMP.setSessionProperty("tier", "gold")
            report("setSessionProperty(\"tier\", \"gold\")")
        }
        findViewById<Button>(R.id.propInt).setOnClickListener {
            UXCamKMP.setSessionProperty("cart_items", 3)
            report("setSessionProperty(\"cart_items\", 3)")
        }
        findViewById<Button>(R.id.propFloat).setOnClickListener {
            UXCamKMP.setSessionProperty("cart_total", 49.99f)
            report("setSessionProperty(\"cart_total\", 49.99f)")
        }
        findViewById<Button>(R.id.propBool).setOnClickListener {
            UXCamKMP.setSessionProperty("checkout_started", true)
            report("setSessionProperty(\"checkout_started\", true)")
        }
        findViewById<Button>(R.id.favorite).setOnClickListener {
            UXCamKMP.markSessionAsFavorite()
            report("markSessionAsFavorite()")
        }

        val key = findViewById<EditText>(R.id.propKey)
        val value = findViewById<EditText>(R.id.propValue)
        findViewById<Button>(R.id.setCustomProp).setOnClickListener {
            UXCamKMP.setSessionProperty(key.text.toString(), value.text.toString())
            report("setSessionProperty(\"${key.text}\", \"${value.text}\")")
        }
    }
}
