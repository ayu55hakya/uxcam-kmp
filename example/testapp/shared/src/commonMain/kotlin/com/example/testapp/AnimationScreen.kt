package com.example.testapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.ui.unit.dp
import com.uxcam.kmp.UXCam
import com.uxcam.kmp.uxcamOcclude
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun AnimationScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { UXCam.tagScreenName("Animation") }
    // Toggle visibility on a loop so the occluded text continuously slides in and out
    // (mirrors the android-sdk "moving composables" demo).
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000)
            visible = false
            delay(1000)
            visible = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Animation", style = MaterialTheme.typography.headlineMedium)
        Text(
            "The occluded text slides in and out every second. uxcamOcclude re-registers " +
                "its bounds on each layout pass, so occlusion tracks the moving composable " +
                "in the recording.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            MovingOccludedText(visible)
        }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}

// Extracted so AnimatedVisibility resolves to the scope-free overload (not the
// ColumnScope/BoxScope extension that the enclosing layouts would otherwise select).
@Composable
private fun MovingOccludedText(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(tween(700)) { fullWidth -> -fullWidth },
        exit = slideOutHorizontally(tween(700)) { fullWidth -> fullWidth },
    ) {
        Text(
            text = "Occlude me!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(24.dp)
                .uxcamOcclude("moving_text"),
        )
    }
}
