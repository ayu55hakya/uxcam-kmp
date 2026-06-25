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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.uxcam.kmp.UXCamKMP
import com.uxcam.kmp.uxcamOcclude

@Composable
internal fun OcclusionScreen(onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var dialogText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { UXCamKMP.tagScreenName("Occlusion") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Occlusion", style = MaterialTheme.typography.headlineMedium)

        // 1. A basic text node, occluded.
        Text("Visible text (recorded normally)")
        Text(
            text = "Secret text — occluded in the recording",
            modifier = Modifier.uxcamOcclude("basic_text"),
        )

        // 2. A password field, occluded.
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .uxcamOcclude("password_field"),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (occluded)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        // 3. A dialog containing a manually occluded field (isInDialog = true).
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showDialog = true },
        ) {
            Text("Open dialog")
        }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text("Dialog", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            // isInDialog = true: the dialog is its own window, so the
                            // occlusion bounds are computed relative to it.
                            .uxcamOcclude("dialog_field", isInDialog = true),
                        value = dialogText,
                        onValueChange = { dialogText = it },
                        label = { Text("Occluded field") },
                        singleLine = true,
                    )
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
