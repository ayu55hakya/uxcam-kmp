package com.example.starter

import androidx.compose.runtime.Composable

/**
 * Per-view occlusion that operates on a real native view, demoing
 * `UXCam.occludeSensitiveView(view)` / `occludeSensitiveViewWithoutGesture(view)` /
 * `unOccludeSensitiveView(view)`.
 *
 * These take a platform `View` (Android) / `UIView` (iOS), so they live on the wrapper's
 * platform actuals — not the common API. Each platform hosts a real native label inside
 * Compose (AndroidView / UIKitView) and occludes that exact instance. [report] echoes the
 * last call onto the screen.
 */
@Composable
expect fun NativeViewOcclusionSection(report: (String) -> Unit)
