package com.example.testapp

import android.app.Activity
import android.widget.TextView

/**
 * Base for the XML feature screens: provides [report], which writes the last UXCam call
 * to a `@id/status` TextView (the View-world equivalent of the Compose "Called: …" line).
 */
abstract class SampleActivity : Activity() {
    protected fun report(call: String) {
        findViewById<TextView?>(R.id.status)?.text = "Called: $call"
    }
}
