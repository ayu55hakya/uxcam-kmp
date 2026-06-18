package com.uxcam.kmp

data class UXConfig(
    val appKey: String,
    val enableAutomaticScreenNameTagging: Boolean = true,
    val enableMultiSessionRecord: Boolean = true,
    val enableCrashHandling: Boolean = true,
    val enableIntegrationLogging: Boolean = false,
    val occludeAllTextFields: Boolean = false,
)
