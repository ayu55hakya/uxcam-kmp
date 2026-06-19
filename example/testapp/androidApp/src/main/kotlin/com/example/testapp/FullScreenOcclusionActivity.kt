package com.example.testapp

import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

class FullScreenOcclusionActivity : SampleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_occlusion)
        UXCam.tagScreenName("Full-Screen Occlusion")

        findViewById<Button>(R.id.applyOverlay).setOnClickListener {
            UXCam.applyOverlayOcclusion(); report("applyOverlayOcclusion()")
        }
        findViewById<Button>(R.id.applyBlur).setOnClickListener {
            UXCam.applyBlurOcclusion(blurRadius = 15); report("applyBlurOcclusion(15)")
        }
    }
}
