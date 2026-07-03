package com.example.template

import androidx.compose.runtime.Composable
import com.uxcam.kmp.KMPUXCamBlur
import com.uxcam.kmp.KMPUXCamOverlay
import com.uxcam.kmp.UXCamKMP

/** Full-screen overlay/blur occlusion — mirrors FullScreenOcclusionView. */
@Composable
internal fun FullScreenOcclusionScreen(onBack: () -> Unit) =
    DemoScaffold("Full-Screen Occlusion", "Full-Screen Occlusion", onBack) { report ->
        DemoButton("Apply overlay occlusion") {
           UXCamKMP.applyOverlayOcclusion(KMPUXCamOverlay()); report("applyOverlayOcclusion()")
        }
        DemoButton("Apply blur occlusion (radius = 15)") {
            val blur = KMPUXCamBlur(blurRadius = 14)
            UXCamKMP.applyBlurOcclusion(blur); report("applyBlurOcclusion(blurRadius = 15)")
        }
        DemoButton("Remove occlusion") {
            UXCamKMP.removeOcclusion(); report("removeOcclusion()")
        }
    }
