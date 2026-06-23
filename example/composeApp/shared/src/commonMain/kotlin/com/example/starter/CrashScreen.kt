package com.example.starter

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Deliberate uncaught crashes to exercise UXCam's crash handler — mirrors CrashView. */
@Composable
internal fun CrashScreen(onBack: () -> Unit) = DemoScaffold("Crash", "Crash", onBack) { _ ->
    Text("Each button intentionally crashes the app to exercise crash capture. The crash is " +
        "uploaded with the session on the next launch.")

    DemoButton("ArithmeticException (10 / 0)") { Crashes.arithmeticException() }
    DemoButton("RuntimeException") { Crashes.runtimeException() }
    DemoButton("NullPointerException") { Crashes.nullPointerException() }
    DemoButton("StackOverflowError") { Crashes.stackOverflow() }
    DemoButton("OutOfMemoryError") { Crashes.outOfMemory() }
}
