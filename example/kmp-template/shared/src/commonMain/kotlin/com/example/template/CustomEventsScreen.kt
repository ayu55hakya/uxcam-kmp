package com.example.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.uxcam.kmp.UXCamKMP

/** Event logging with various payload shapes — mirrors CustomEventsView. */
@Composable
internal fun CustomEventsScreen(onBack: () -> Unit) =
    DemoScaffold("Custom Events", "Custom Events", onBack) { report ->
        var eventName by remember { mutableStateOf("") }
        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }

        SectionHeader("Map payloads")
        DemoButton("Event without properties") {
            UXCamKMP.logEvent("event_no_props"); report("logEvent(\"event_no_props\")")
        }
        DemoButton("Event with map properties") {
            UXCamKMP.logEvent("event_with_map", mapOf("source" to "sample", "count" to 3))
            report("logEvent(\"event_with_map\", {source, count})")
        }
        DemoButton("Event with empty map") {
            UXCamKMP.logEvent("event_empty_map", emptyMap()); report("logEvent(\"event_empty_map\", {})")
        }
        DemoButton("Event with null map") {
            UXCamKMP.logEvent("event_null_map", null); report("logEvent(\"event_null_map\", null)")
        }

        SectionHeader("JSON payloads")
        DemoButton("Event with JSON properties") {
            UXCamKMP.logEventWithJson("event_with_json", "{\"source\":\"sample\",\"count\":3}")
            report("logEventWithJson(\"event_with_json\", json)")
        }
        DemoButton("Event with empty JSON") {
            UXCamKMP.logEventWithJson("event_empty_json", "{}"); report("logEventWithJson(\"event_empty_json\", \"{}\")")
        }
        DemoButton("Event with null JSON") {
            UXCamKMP.logEventWithJson("event_null_json", null); report("logEventWithJson(\"event_null_json\", null)")
        }

        SectionHeader("Custom event")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = eventName, onValueChange = { eventName = it },
            label = { Text("Event name") }, singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = key, onValueChange = { key = it },
            label = { Text("Property key") }, singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value, onValueChange = { value = it },
            label = { Text("Property value") }, singleLine = true,
        )
        DemoButton("Log custom event") {
            UXCamKMP.logEvent(eventName, mapOf(key to value))
            report("logEvent(\"$eventName\", {$key: $value})")
        }
    }
