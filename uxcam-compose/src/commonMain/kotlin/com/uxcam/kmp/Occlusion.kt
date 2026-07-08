package com.uxcam.kmp

import androidx.compose.ui.Modifier

/**
 * Occludes (blurs out) this composable in the UXCam recording. Apply it to any node
 * holding sensitive content — text, password fields, etc.
 *
 * @param identifier a stable name for this occluded region (shown in diagnostics).
 * @param isInDialog set true when the composable lives inside a Dialog/popup window,
 *   so its on-screen position is computed relative to the dialog's own window.
 *
 * On Android this registers the node's bounds with UXCam on every layout pass. On iOS the
 * node's window rect is forwarded to the native SDK's rect-based occlusion whenever the
 * node moves, appears, or disappears.
 */
expect fun Modifier.uxcamOcclude(identifier: String, isInDialog: Boolean = false): Modifier
