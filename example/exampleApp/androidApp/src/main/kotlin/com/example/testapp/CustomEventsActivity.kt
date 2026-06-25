package com.example.testapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.uxcam.kmp.UXCamKMP

class CustomEventsActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_events)
        UXCamKMP.tagScreenName("Custom Events")

        findViewById<Button>(R.id.eventNoProps).setOnClickListener {
            UXCamKMP.logEvent("event_no_props"); report("logEvent(no props)")
        }
        findViewById<Button>(R.id.eventMap).setOnClickListener {
            UXCamKMP.logEvent("event_with_map", mapOf("source" to "sample", "count" to 3))
            report("logEvent(map)")
        }
        findViewById<Button>(R.id.eventEmptyMap).setOnClickListener {
            UXCamKMP.logEvent("event_empty_map", emptyMap()); report("logEvent(empty map)")
        }
        findViewById<Button>(R.id.eventNullMap).setOnClickListener {
            UXCamKMP.logEvent("event_null_map", null); report("logEvent(null map)")
        }
        findViewById<Button>(R.id.eventJson).setOnClickListener {
            UXCamKMP.logEventWithJson("event_with_json", "{\"source\":\"sample\",\"count\":3}")
            report("logEventWithJson(json)")
        }
        findViewById<Button>(R.id.eventEmptyJson).setOnClickListener {
            UXCamKMP.logEventWithJson("event_empty_json", "{}"); report("logEventWithJson({})")
        }
        findViewById<Button>(R.id.eventNullJson).setOnClickListener {
            UXCamKMP.logEventWithJson("event_null_json", null); report("logEventWithJson(null)")
        }

        val eventName = findViewById<EditText>(R.id.eventName)
        val eventKey = findViewById<EditText>(R.id.eventKey)
        val eventValue = findViewById<EditText>(R.id.eventValue)
        findViewById<Button>(R.id.logCustomEvent).setOnClickListener {
            UXCamKMP.logEvent(
                eventName.text.toString(),
                mapOf(eventKey.text.toString() to eventValue.text.toString()),
            )
            report("logEvent(\"${eventName.text}\", {${eventKey.text}=${eventValue.text}})")
        }
    }
}
