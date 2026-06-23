package com.example.starter

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uxcam.kmp.UXCam

/** Menu hub, grouped like the iOS sample's HomeView. Each row opens a UXCam use-case screen. */
private val sections: List<Pair<String, List<Pair<String, Screen>>>> = listOf(
    "User & session data" to listOf(
        "User APIs" to Screen.UserApis,
        "Session Properties" to Screen.SessionProperties,
        "Custom Events" to Screen.CustomEvents,
        "Bug & Exception Reporting" to Screen.BugReporting,
    ),
    "Privacy & occlusion" to listOf(
        "Occlusion" to Screen.Occlusion,
        "Full-Screen Occlusion" to Screen.FullScreenOcclusion,
        "Opt In / Out" to Screen.OptInOut,
    ),
    "Recording & diagnostics" to listOf(
        "Screen Tagging" to Screen.ScreenTagging,
        "Session Controls" to Screen.SessionControls,
        "Status & Misc" to Screen.Status,
        "Crash" to Screen.Crash,
    ),
)

@Composable
internal fun HomeScreen(onOpen: (Screen) -> Unit) {
    LaunchedEffect(Unit) { UXCam.tagScreenName("Home") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("UXCam KMP Sample", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Running on ${getPlatform().name}",
            style = MaterialTheme.typography.bodyMedium,
        )

        sections.forEach { (header, items) ->
            SectionHeader(header)
            items.forEach { (label, target) ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpen(target) },
                ) {
                    Text(label)
                }
            }
        }
    }
}
