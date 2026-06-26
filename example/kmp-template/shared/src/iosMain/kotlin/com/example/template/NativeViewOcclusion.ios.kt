@file:OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@file:Suppress("DEPRECATION") // UIKitView(factory, modifier, …) — older interop overload, still works.

package com.example.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.uxcam.kmp.UXCamKMP
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UILabel

@Composable
actual fun NativeViewOcclusionSection(report: (String) -> Unit) {
    // A single retained UILabel so the SAME instance is passed to occlude and un-occlude —
    // per-view occlusion is stateful and keyed on the view.
    val sensitive = remember {
        UILabel().apply { text = "Secret: 4111 1111 1111 1111" }
    }

    SectionHeader("Native view occlusion (occludeSensitiveView)")
    UIKitView(factory = { sensitive }, modifier = Modifier.fillMaxWidth().height(24.dp))
    DemoButton("Occlude this view") {
        UXCamKMP.occludeSensitiveView(sensitive); report("occludeSensitiveView(view)")
    }
    DemoButton("Occlude this view (no gesture)") {
        UXCamKMP.occludeSensitiveViewWithoutGesture(sensitive)
        report("occludeSensitiveViewWithoutGesture(view)")
    }
    DemoButton("Un-occlude this view") {
        UXCamKMP.unOccludeSensitiveView(sensitive); report("unOccludeSensitiveView(view)")
    }
}
