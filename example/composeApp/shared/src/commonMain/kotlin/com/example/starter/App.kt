package com.example.starter

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

internal enum class Screen {
    Home, UserApis, SessionProperties, CustomEvents, BugReporting,
    Occlusion, FullScreenOcclusion, OptInOut,
    ScreenTagging, SessionControls, Status, Crash,
}

/**
 * The whole app UI — a menu of UXCam use cases. Each screen exercises a slice of the
 * UXCam KMP wrapper (screen tagging, events, user/session APIs, occlusion, opt in/out,
 * crashes, status) and runs identically on Android and iOS from this single commonMain
 * source. Ported from the example app's View/SwiftUI sample.
 */
@Composable
fun App() {
    MaterialTheme {
        var screen by remember { mutableStateOf(Screen.Home) }
        val back = { screen = Screen.Home }
        when (screen) {
            Screen.Home -> HomeScreen(onOpen = { screen = it })
            Screen.UserApis -> UserApisScreen(back)
            Screen.SessionProperties -> SessionPropertiesScreen(back)
            Screen.CustomEvents -> CustomEventsScreen(back)
            Screen.BugReporting -> BugReportingScreen(back)
            Screen.Occlusion -> OcclusionScreen(back)
            Screen.FullScreenOcclusion -> FullScreenOcclusionScreen(back)
            Screen.OptInOut -> OptInOutScreen(back)
            Screen.ScreenTagging -> ScreenTaggingScreen(back)
            Screen.SessionControls -> SessionControlsScreen(back)
            Screen.Status -> StatusScreen(back)
            Screen.Crash -> CrashScreen(back)
        }
    }
}
