package com.example.starter

import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.uxcam.kmp.UXCam

@Composable
actual fun NativeViewOcclusionSection(report: (String) -> Unit) {
    val context = LocalContext.current
    // A single retained TextView so the SAME instance is passed to occlude and un-occlude —
    // per-view occlusion is stateful and keyed on the view.
    val sensitive = remember {
        TextView(context).apply { text = "Secret: 4111 1111 1111 1111" }
    }

    SectionHeader("Native view occlusion (occludeSensitiveView)")
    AndroidView(factory = { sensitive }, modifier = Modifier.fillMaxWidth())
    DemoButton("Occlude this view") {
        UXCam.occludeSensitiveView(sensitive); report("occludeSensitiveView(view)")
    }
    DemoButton("Occlude this view (no gesture)") {
        UXCam.occludeSensitiveViewWithoutGesture(sensitive)
        report("occludeSensitiveViewWithoutGesture(view)")
    }
    DemoButton("Un-occlude this view") {
        UXCam.unOccludeSensitiveView(sensitive); report("unOccludeSensitiveView(view)")
    }
}
