package com.uxcam.kmp

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import com.uxcam.UXCamKt

private class ReportedBounds(var value: androidx.compose.ui.geometry.Rect? = null)

actual fun Modifier.uxcamOcclude(identifier: String, isInDialog: Boolean): Modifier = composed {
    val view = LocalView.current
    // Plain holder, not MutableState: written on every layout pass, never read in composition.
    val reported = remember(identifier, view, isInDialog) { ReportedBounds() }
    onGloballyPositioned { coordinates ->
        val bounds = coordinates.boundsInWindow()
        if (bounds != reported.value) {
            reported.value = bounds
            UXCamKt.occludeSensitiveComposable(identifier, view, coordinates, isInDialog)
        }
    }
}