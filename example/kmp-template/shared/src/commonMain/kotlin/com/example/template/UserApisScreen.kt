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

/** User identity and user properties — mirrors UserApisView / UserApisActivity. */
@Composable
internal fun UserApisScreen(onBack: () -> Unit) = DemoScaffold("User APIs", "User APIs", onBack) { report ->
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    SectionHeader("Predefined")
    DemoButton("Set user identity") {
        UXCamKMP.setUserIdentity("user_1234567"); report("setUserIdentity(\"user_123456\")")
    }
    DemoButton("Set property (plan = premium)") {
        UXCamKMP.setUserProperty("plan", "premium"); report("setUserProperty(\"plan\", \"premium\")")
    }
    DemoButton("Set int property (age = 30)") {
        UXCamKMP.setUserProperty("age", 30); report("setUserProperty(\"age\", 30)")
    }
    DemoButton("Set float property (rating = 4.5)") {
        UXCamKMP.setUserProperty("rating", 4.5f); report("setUserProperty(\"rating\", 4.5)")
    }
    DemoButton("Set bool property (subscribed = true)") {
        UXCamKMP.setUserProperty("subscribed", true); report("setUserProperty(\"subscribed\", true)")
    }
    DemoButton("Set push token") {
        UXCamKMP.setPushNotificationToken("sample_push_token"); report("setPushNotificationToken(…)")
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
        UXCamKMP.setUserProperty(key, value); report("setUserProperty(\"$key\", \"$value\")")
    }
}
