package com.example.testapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeScreen(
    onOpenUserApis: () -> Unit,
    onOpenCustomEvents: () -> Unit,
    onOpenOcclusion: () -> Unit,
    onOpenFullScreenOcclusion: () -> Unit,
    onOpenSessionControls: () -> Unit,
    onOpenAnimation: () -> Unit,
    onOpenCrash: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("UXCam KMP Sample", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onOpenUserApis) { Text("User APIs") }
        Button(onClick = onOpenCustomEvents) { Text("Custom Events") }
        Button(onClick = onOpenOcclusion) { Text("View Occlusion") }
        Button(onClick = onOpenFullScreenOcclusion) { Text("Full-Screen Occlusion") }
        Button(onClick = onOpenSessionControls) { Text("Session Controls") }
        Button(onClick = onOpenAnimation) { Text("Animation") }
        Button(onClick = onOpenCrash) { Text("Crash") }
    }
}
