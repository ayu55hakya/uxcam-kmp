package com.example.starter

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCam

/** Full-screen overlay/blur occlusion — mirrors FullScreenOcclusionView. */
@Composable
internal fun FullScreenOcclusionScreen(onBack: () -> Unit) =
    DemoScaffold("Full-Screen Occlusion", "Full-Screen Occlusion", onBack) { report ->
        DemoButton("Apply overlay occlusion") {
            UXCam.applyOverlayOcclusion(); report("applyOverlayOcclusion()")
        }
        DemoButton("Apply blur occlusion (radius = 15)") {
            UXCam.applyBlurOcclusion(blurRadius = 15); report("applyBlurOcclusion(blurRadius = 15)")
        }
        DemoButton("Remove occlusion") {
            UXCam.removeOcclusion(); report("removeOcclusion()")
        }
    }
