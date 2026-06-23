package com.example.starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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

/**
 * Shared scaffold for the UXCam demo screens. Tags the screen in UXCam on first
 * composition, lays out a scrollable column, and shows a "last call" panel at the bottom —
 * mirroring the iOS sample's `Form` + `DemoButton` + `ResultPanel` pattern.
 *
 * [content] receives a `report` callback; call it from a button to echo the last UXCam
 * call onto the screen.
 */
@Composable
internal fun DemoScaffold(
    title: String,
    screenTag: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.(report: (String) -> Unit) -> Unit,
) {
    var result by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { UXCam.tagScreenName(screenTag) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        content { result = it }
        if (result.isNotEmpty()) ResultPanel(result)
        TextButton(onClick = onBack) { Text("Back") }
    }
}

/** A section header inside a demo screen. */
@Composable
internal fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp),
    )
}

/** Full-width button used by the action screens. */
@Composable
internal fun DemoButton(label: String, onClick: () -> Unit) {
    Button(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Text(label)
    }
}

/** Footer panel showing the last action / returned value. */
@Composable
internal fun ResultPanel(text: String) {
    Text(
        text = "Last call: $text",
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    )
}
