package com.example.starter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.uxcam.kmp.UXCam

/** Metadata on the current recording session — mirrors SessionPropertiesView. */
@Composable
internal fun SessionPropertiesScreen(onBack: () -> Unit) =
    DemoScaffold("Session Properties", "Session Properties", onBack) { report ->
        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }

        SectionHeader("Predefined")
        DemoButton("Set string property (tier = gold)") {
            UXCam.setSessionProperty("tier", "gold"); report("setSessionProperty(\"tier\", \"gold\")")
        }
        DemoButton("Set int property (cart_items = 3)") {
            UXCam.setSessionProperty("cart_items", 3); report("setSessionProperty(\"cart_items\", 3)")
        }
        DemoButton("Set float property (cart_total = 49.99)") {
            UXCam.setSessionProperty("cart_total", 49.99f); report("setSessionProperty(\"cart_total\", 49.99)")
        }
        DemoButton("Set bool property (checkout_started = true)") {
            UXCam.setSessionProperty("checkout_started", true)
            report("setSessionProperty(\"checkout_started\", true)")
        }
        DemoButton("Mark session as favorite") {
            UXCam.markSessionAsFavorite(); report("markSessionAsFavorite()")
        }

        SectionHeader("Custom property")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = key, onValueChange = { key = it },
            label = { Text("Key") }, singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value, onValueChange = { value = it },
            label = { Text("Value") }, singleLine = true,
        )
        DemoButton("Set custom property") {
            UXCam.setSessionProperty(key, value); report("setSessionProperty(\"$key\", \"$value\")")
        }
    }
