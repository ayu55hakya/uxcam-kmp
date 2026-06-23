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
internal fun FullScreenOcclusionScreen(onBack: () -> Unit) {
    var lastCall by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { UXCam.tagScreenName("Full-Screen Occlusion") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Full-Screen Occlusion", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Occludes the entire screen in the recording (the live screen is unchanged). " +
                "Apply one, then check the recording.",
            style = MaterialTheme.typography.bodyMedium,
        )

        EventButton("Apply overlay occlusion") {
            UXCam.applyOverlayOcclusion()
            lastCall = "applyOverlayOcclusion()"
        }
        EventButton("Apply blur occlusion") {
            UXCam.applyBlurOcclusion(blurRadius = 15)
            lastCall = "applyBlurOcclusion(blurRadius = 15)"
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
