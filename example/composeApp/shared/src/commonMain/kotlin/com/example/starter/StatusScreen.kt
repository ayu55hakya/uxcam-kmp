package com.example.starter

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCam

/** SDK status, session/user URLs, pending uploads, and the crash-handler toggle — mirrors StatusView. */
@Composable
internal fun StatusScreen(onBack: () -> Unit) = DemoScaffold("Status & Misc", "Status & Misc", onBack) { report ->
    SectionHeader("URLs & version")
    DemoButton("Get session URL") {
        report("urlForCurrentSession() = ${UXCam.urlForCurrentSession() ?: "nil"}")
    }
    DemoButton("Get user URL") {
        report("urlForCurrentUser() = ${UXCam.urlForCurrentUser() ?: "nil"}")
    }
    DemoButton("Get SDK version") {
        report("getSdkVersionInfo() = ${UXCam.getSdkVersionInfo()}")
    }

    SectionHeader("Pending uploads")
    DemoButton("Get pending session count") {
        report("pendingSessionCount() = ${UXCam.pendingSessionCount()}")
    }
    DemoButton("Get pending uploads (async)") {
        UXCam.pendingUploads { count -> report("pendingUploads callback = $count") }
        report("pendingUploads(callback)…")
    }
    DemoButton("Delete pending uploads") {
        UXCam.deletePendingUploads(); report("deletePendingUploads()")
    }

    SectionHeader("Crash handler")
    DemoButton("Enable crash handler") {
        UXCam.disableCrashHandling(false); report("disableCrashHandling(false)")
    }
    DemoButton("Disable crash handler") {
        UXCam.disableCrashHandling(true); report("disableCrashHandling(true)")
    }
}
