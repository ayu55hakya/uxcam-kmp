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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.uxcam.kmp.UXCam
import com.uxcam.kmp.uxcamOcclude
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private enum class Screen { Home, UserApis, CustomEvents, Occlusion, FullScreenOcclusion, SessionControls, Animation }

@Composable
fun App() {
    MaterialTheme {
        var screen by remember { mutableStateOf(Screen.Home) }
        when (screen) {
            Screen.Home -> HomeScreen(
                onOpenUserApis = { screen = Screen.UserApis },
                onOpenCustomEvents = { screen = Screen.CustomEvents },
                onOpenOcclusion = { screen = Screen.Occlusion },
                onOpenFullScreenOcclusion = { screen = Screen.FullScreenOcclusion },
                onOpenSessionControls = { screen = Screen.SessionControls },
                onOpenAnimation = { screen = Screen.Animation },
            )
            Screen.UserApis -> UserApisScreen(onBack = { screen = Screen.Home })
            Screen.CustomEvents -> CustomEventsScreen(onBack = { screen = Screen.Home })
            Screen.Occlusion -> OcclusionScreen(onBack = { screen = Screen.Home })
            Screen.FullScreenOcclusion -> FullScreenOcclusionScreen(onBack = { screen = Screen.Home })
            Screen.SessionControls -> SessionControlsScreen(onBack = { screen = Screen.Home })
            Screen.Animation -> AnimationScreen(onBack = { screen = Screen.Home })
        }
    }
}

@Composable
private fun HomeScreen(
    onOpenUserApis: () -> Unit,
    onOpenCustomEvents: () -> Unit,
    onOpenOcclusion: () -> Unit,
    onOpenFullScreenOcclusion: () -> Unit,
    onOpenSessionControls: () -> Unit,
    onOpenAnimation: () -> Unit,
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
        Button(onClick = onOpenUserApis) {
            Text("User APIs")
        }
        Button(onClick = onOpenCustomEvents) {
            Text("Custom Events")
        }
        Button(onClick = onOpenOcclusion) {
            Text("View Occlusion")
        }
        Button(onClick = onOpenFullScreenOcclusion) {
            Text("Full-Screen Occlusion")
        }
        Button(onClick = onOpenSessionControls) {
            Text("Session Controls")
        }
        Button(onClick = onOpenAnimation) {
            Text("Animation")
        }
    }
}

@Composable
private fun UserApisScreen(onBack: () -> Unit) {
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
                UXCam.setUserIdentity("user_12345")
                lastCall = "setUserIdentity(\"user_12345\")"
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

@Composable
private fun CustomEventsScreen(onBack: () -> Unit) {
    var eventName by remember { mutableStateOf("") }
    var propertyKey by remember { mutableStateOf("") }
    var propertyValue by remember { mutableStateOf("") }
    var lastCall by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { UXCam.tagScreenName("Custom Events") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Custom Events", style = MaterialTheme.typography.headlineMedium)

        EventButton("Event without properties") {
            UXCam.logEvent("event_no_props")
            lastCall = "logEvent(\"event_no_props\")"
        }
        EventButton("Event with map properties") {
            UXCam.logEvent("event_with_map", mapOf("source" to "sample", "count" to 3))
            lastCall = "logEvent(\"event_with_map\", {source, count})"
        }
        EventButton("Event with empty map") {
            UXCam.logEvent("event_empty_map", emptyMap())
            lastCall = "logEvent(\"event_empty_map\", {})"
        }
        EventButton("Event with null map") {
            UXCam.logEvent("event_null_map", null)
            lastCall = "logEvent(\"event_null_map\", null)"
        }
        EventButton("Event with JSON properties") {
            UXCam.logEventWithJson("event_with_json", "{\"source\":\"sample\",\"count\":3}")
            lastCall = "logEventWithJson(\"event_with_json\", {...})"
        }
        EventButton("Event with empty JSON") {
            UXCam.logEventWithJson("event_empty_json", "{}")
            lastCall = "logEventWithJson(\"event_empty_json\", \"{}\")"
        }
        EventButton("Event with null JSON") {
            UXCam.logEventWithJson("event_null_json", null)
            lastCall = "logEventWithJson(\"event_null_json\", null)"
        }

        Text("Custom property event", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event name") },
            singleLine = true,
        )
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
            enabled = eventName.isNotBlank() && propertyKey.isNotBlank(),
            onClick = {
                UXCam.logEvent(eventName, mapOf(propertyKey to propertyValue))
                lastCall = "logEvent(\"$eventName\", {$propertyKey=$propertyValue})"
            },
        ) {
            Text("Log custom event")
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

@Composable
private fun FullScreenOcclusionScreen(onBack: () -> Unit) {
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

@Composable
private fun SessionControlsScreen(onBack: () -> Unit) {
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

@Composable
private fun AnimationScreen(onBack: () -> Unit) {
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

@Composable
private fun EventButton(label: String, onClick: () -> Unit) {
    Button(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Text(label)
    }
}

@Composable
private fun OcclusionScreen(onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var dialogText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { UXCam.tagScreenName("Occlusion") }

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
