package com.example.testapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private enum class Screen { Home, UserApis, CustomEvents, Occlusion, FullScreenOcclusion, SessionControls, Animation, Crash }

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
                onOpenCrash = { screen = Screen.Crash },
            )
            Screen.UserApis -> UserApisScreen(onBack = { screen = Screen.Home })
            Screen.CustomEvents -> CustomEventsScreen(onBack = { screen = Screen.Home })
            Screen.Occlusion -> OcclusionScreen(onBack = { screen = Screen.Home })
            Screen.FullScreenOcclusion -> FullScreenOcclusionScreen(onBack = { screen = Screen.Home })
            Screen.SessionControls -> SessionControlsScreen(onBack = { screen = Screen.Home })
            Screen.Animation -> AnimationScreen(onBack = { screen = Screen.Home })
            Screen.Crash -> CrashScreen(onBack = { screen = Screen.Home })
        }
    }
}
