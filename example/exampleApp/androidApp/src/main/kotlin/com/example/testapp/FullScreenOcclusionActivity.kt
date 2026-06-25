package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCamKMP

class FullScreenOcclusionActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_occlusion)
        UXCamKMP.tagScreenName("Full-Screen Occlusion")

        findViewById<Button>(R.id.applyOverlay).setOnClickListener {
            UXCamKMP.applyOverlayOcclusion(); report("applyOverlayOcclusion()")
        }
        findViewById<Button>(R.id.applyBlur).setOnClickListener {
            UXCamKMP.applyBlurOcclusion(blurRadius = 15); report("applyBlurOcclusion(15)")
        }
        findViewById<Button>(R.id.removeOcclusion).setOnClickListener {
            UXCamKMP.removeOcclusion(); report("removeOcclusion()")
        }
    }
}
