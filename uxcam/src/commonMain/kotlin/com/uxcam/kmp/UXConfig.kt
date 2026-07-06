package com.uxcam.kmp

import kotlin.concurrent.Volatile

data class UXConfig(
    val appKey: String,
    val enableAutomaticScreenNameTagging: Boolean = true,
    val enableMultiSessionRecord: Boolean = true,
    val enableCrashHandling: Boolean = true,
    val enableIntegrationLogging: Boolean = false,
    val occludeAllTextFields: Boolean = false,
    val occlusions: List<Occlusion> = emptyList(),
)

internal object UXCamStartGuard {
    // True while a session started via startWithConfiguration is live. Guards against
    // duplicate starts; reset by stopSessionAndUploadData()/cancelCurrentSession() so
    // the SDK can be restarted (native SDKs allow stop → start).
    @Volatile
    var started: Boolean = false
}
