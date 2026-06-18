package com.uxcam.kmp

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import com.uxcam.UXCamKt

actual fun Modifier.uxcamOcclude(identifier: String, isInDialog: Boolean): Modifier = composed {
    val view = LocalView.current
    onGloballyPositioned { coordinates ->
        UXCamKt.occludeSensitiveComposable(identifier, view, coordinates, isInDialog)
    }
}
