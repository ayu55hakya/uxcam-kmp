package com.example.testapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.uxcam.kmp.UXCam

/** Uncaught crashes to exercise UXCam's crash handler (shared triggers in Crashes). */
class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        UXCam.tagScreenName("Crash")

        findViewById<Button>(R.id.arithmetic).setOnClickListener { Crashes.arithmeticException() }
        findViewById<Button>(R.id.runtime).setOnClickListener { Crashes.runtimeException() }
        findViewById<Button>(R.id.npe).setOnClickListener { Crashes.nullPointerException() }
        findViewById<Button>(R.id.stackOverflow).setOnClickListener { Crashes.stackOverflow() }
        findViewById<Button>(R.id.oom).setOnClickListener { Crashes.outOfMemory() }
    }
}
