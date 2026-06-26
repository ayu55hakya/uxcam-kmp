package com.example.template

import androidx.compose.runtime.Composable
import com.uxcam.kmp.UXCamKMP

/** SDK status, session/user URLs, pending uploads, and the crash-handler toggle — mirrors StatusView. */
@Composable
internal fun StatusScreen(onBack: () -> Unit) = DemoScaffold("Status & Misc", "Status & Misc", onBack) { report ->
    SectionHeader("URLs & version")
    DemoButton("Get session URL") {
        report("urlForCurrentSession() = ${UXCamKMP.urlForCurrentSession() ?: "nil"}")
    }
    DemoButton("Get user URL") {
        report("urlForCurrentUser() = ${UXCamKMP.urlForCurrentUser() ?: "nil"}")
    }
    DemoButton("Get SDK version") {
        report("getSdkVersionInfo() = ${UXCamKMP.getSdkVersionInfo()}")
    }

    SectionHeader("Pending uploads")
    DemoButton("Get pending session count") {
        report("pendingSessionCount() = ${UXCamKMP.pendingSessionCount()}")
    }
    DemoButton("Get pending uploads (async)") {
        UXCamKMP.pendingUploads { count -> report("pendingUploads callback = $count") }
        report("pendingUploads(callback)…")
    }
    DemoButton("Delete pending uploads") {
        UXCamKMP.deletePendingUploads(); report("deletePendingUploads()")
    }

    SectionHeader("Crash handler")
    DemoButton("Enable crash handler") {
        UXCamKMP.disableCrashHandling(false); report("disableCrashHandling(false)")
    }
    DemoButton("Disable crash handler") {
        UXCamKMP.disableCrashHandling(true); report("disableCrashHandling(true)")
    }
}
