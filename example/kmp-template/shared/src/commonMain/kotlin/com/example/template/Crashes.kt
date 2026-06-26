package com.example.template

/**
 * Deliberate UNCAUGHT crashes, to exercise UXCam's crash handler. Each one kills the app;
 * UXCam captures the crash and uploads it with the session on the next launch.
 *
 * Triggered by behaviour (recursion / allocation) rather than by referencing JVM-only
 * error types, so this stays in commonMain and works on both Android and iOS.
 */
object Crashes {
    /** ArithmeticException — "0".toInt() avoids compile-time division-by-zero folding. */
    fun arithmeticException(): Int = 10 / "0".toInt()

    fun runtimeException(): Nothing = throw RuntimeException("This is a crash")

    fun nullPointerException() {
        val nothing: String? = null
        nothing!!.length
    }

    /** StackOverflowError via unbounded recursion. */
    fun stackOverflow() {
        stackOverflow()
    }

    /** OutOfMemoryError via unbounded allocation. */
    fun outOfMemory() {
        val blocks = ArrayList<ByteArray>()
        while (true) {
            blocks.add(ByteArray(10 * 1024 * 1024))
        }
    }
}
