package com.uxcam.kmp

import androidx.compose.ui.Modifier

// iOS occlusion is not wired up yet (see UXCam.ios.kt). No-op so shared UI compiles
// and runs on iOS without occluding.
actual fun Modifier.uxcamOcclude(identifier: String, isInDialog: Boolean): Modifier = this
