package com.example.testapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * iOS entry point for the shared Compose UI. The SwiftUI app hosts this view controller.
 * Starts UXCam from the single shared source before rendering, mirroring MainActivity.
 */
fun MainViewController(): UIViewController {
    UxcamSetup.start()
    return ComposeUIViewController { App() }
}
