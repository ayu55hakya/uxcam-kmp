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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uxcam.kmp.UXCamKMP

@Composable
internal fun CrashScreen(onBack: () -> Unit) {
    LaunchedEffect(Unit) { UXCamKMP.tagScreenName("Crash") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Crash (uncaught)", style = MaterialTheme.typography.headlineMedium)
        Text(
            "These crash the app to test UXCam's crash handler. The crash is captured and " +
                "uploaded with the session on the next launch.",
            style = MaterialTheme.typography.bodyMedium,
        )

        EventButton("ArithmeticException (10 / 0)") { Crashes.arithmeticException() }
        EventButton("RuntimeException") { Crashes.runtimeException() }
        EventButton("NullPointerException") { Crashes.nullPointerException() }
        EventButton("StackOverflowError") { Crashes.stackOverflow() }
        EventButton("OutOfMemoryError") { Crashes.outOfMemory() }

        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}
