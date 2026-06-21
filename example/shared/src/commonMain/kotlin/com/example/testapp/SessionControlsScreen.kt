package com.example.testapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
internal fun SessionControlsScreen(onBack: () -> Unit) {
    var lastCall by remember { mutableStateOf<String?>(null) }

    // Opening the picker sends the user to another app; allow a short break so the
    // session continues, then end the break on return (mirrors the android-sdk demo).
    val openImagePicker = rememberImagePicker(onResult = {
        UXCam.allowShortBreakForAnotherApp(false)
        lastCall = "allowShortBreakForAnotherApp(false) [returned]"
    })

    LaunchedEffect(Unit) { UXCam.tagScreenName("Session Controls") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Session Controls", style = MaterialTheme.typography.headlineMedium)

        EventButton("Allow short break for another app") {
            UXCam.allowShortBreakForAnotherApp(20_000)
            lastCall = "allowShortBreakForAnotherApp(20000) → open image picker"
            openImagePicker()
        }
        EventButton("Cancel current session") {
            UXCam.cancelCurrentSession()
            lastCall = "cancelCurrentSession()"
        }
        EventButton("Pause screen recording") {
            UXCam.pauseScreenRecording()
            lastCall = "pauseScreenRecording()"
        }
        EventButton("Resume screen recording") {
            UXCam.resumeScreenRecording()
            lastCall = "resumeScreenRecording()"
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
