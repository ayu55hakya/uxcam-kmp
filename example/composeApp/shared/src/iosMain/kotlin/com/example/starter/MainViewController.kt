package com.example.starter

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** iOS entry point for the shared Compose UI. The SwiftUI app hosts this view controller. */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
