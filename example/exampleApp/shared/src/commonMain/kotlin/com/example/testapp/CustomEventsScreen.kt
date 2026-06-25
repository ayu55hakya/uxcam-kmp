package com.example.testapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.uxcam.kmp.UXCamKMP

@Composable
internal fun CustomEventsScreen(onBack: () -> Unit) {
    var eventName by remember { mutableStateOf("") }
    var propertyKey by remember { mutableStateOf("") }
    var propertyValue by remember { mutableStateOf("") }
    var lastCall by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { UXCamKMP.tagScreenName("Custom Events") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Custom Events", style = MaterialTheme.typography.headlineMedium)

        EventButton("Event without properties") {
            UXCamKMP.logEvent("event_no_props")
            lastCall = "logEvent(\"event_no_props\")"
        }
        EventButton("Event with map properties") {
            UXCamKMP.logEvent("event_with_map", mapOf("source" to "sample", "count" to 3))
            lastCall = "logEvent(\"event_with_map\", {source, count})"
        }
        EventButton("Event with empty map") {
            UXCamKMP.logEvent("event_empty_map", emptyMap())
            lastCall = "logEvent(\"event_empty_map\", {})"
        }
        EventButton("Event with null map") {
            UXCamKMP.logEvent("event_null_map", null)
            lastCall = "logEvent(\"event_null_map\", null)"
        }
        EventButton("Event with JSON properties") {
            UXCamKMP.logEventWithJson("event_with_json", "{\"source\":\"sample\",\"count\":3}")
            lastCall = "logEventWithJson(\"event_with_json\", {...})"
        }
        EventButton("Event with empty JSON") {
            UXCamKMP.logEventWithJson("event_empty_json", "{}")
            lastCall = "logEventWithJson(\"event_empty_json\", \"{}\")"
        }
        EventButton("Event with null JSON") {
            UXCamKMP.logEventWithJson("event_null_json", null)
            lastCall = "logEventWithJson(\"event_null_json\", null)"
        }

        Text("Custom property event", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event name") },
            singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = propertyKey,
            onValueChange = { propertyKey = it },
            label = { Text("Property key") },
            singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = propertyValue,
            onValueChange = { propertyValue = it },
            label = { Text("Property value") },
            singleLine = true,
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = eventName.isNotBlank() && propertyKey.isNotBlank(),
            onClick = {
                UXCamKMP.logEvent(eventName, mapOf(propertyKey to propertyValue))
                lastCall = "logEvent(\"$eventName\", {$propertyKey=$propertyValue})"
            },
        ) {
            Text("Log custom event")
        }

        lastCall?.let {
            Text(
                text = "Called: $it",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
            )
        }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}
