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
import com.uxcam.kmp.UXCam

@Composable
internal fun UserApisScreen(onBack: () -> Unit) {
    var propertyKey by remember { mutableStateOf("") }
    var propertyValue by remember { mutableStateOf("") }
    var lastCall by remember { mutableStateOf<String?>(null) }

    // Tag this screen in the UXCam timeline as soon as it's shown.
    LaunchedEffect(Unit) { UXCam.tagScreenName("User APIs") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("User APIs", style = MaterialTheme.typography.headlineMedium)

        // 1. User identity
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                UXCam.setUserIdentity("user_123456")
                lastCall = "setUserIdentity(\"user_123456\")"
            },
        ) {
            Text("Set user identity")
        }

        // 2. Predefined user property
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                UXCam.setUserProperty("plan", "premium")
                lastCall = "setUserProperty(\"plan\", \"premium\")"
            },
        ) {
            Text("Set predefined property (plan = premium)")
        }

        // 3. Custom key/value user property
        Text("Custom property", style = MaterialTheme.typography.titleMedium)
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
            enabled = propertyKey.isNotBlank(),
            onClick = {
                UXCam.setUserProperty(propertyKey, propertyValue)
                lastCall = "setUserProperty(\"$propertyKey\", \"$propertyValue\")"
            },
        ) {
            Text("Set custom property")
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
