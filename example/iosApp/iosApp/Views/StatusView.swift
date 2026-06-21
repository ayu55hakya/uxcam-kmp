import SwiftUI

/// Mirrors `StatusActivity` — SDK status, session/user URLs, pending uploads, crash toggle.
struct StatusView: View {
    @State private var result = ""

    var body: some View {
        Form {
            Section("URLs & version") {
                DemoButton("Get session URL", result: $result) {
                    "urlForCurrentSession() = \(UX.urlForCurrentSession() ?? "nil")"
                }
                DemoButton("Get user URL", result: $result) {
                    "urlForCurrentUser() = \(UX.urlForCurrentUser() ?? "nil")"
                }
                DemoButton("Get SDK version", result: $result) {
                    "getSdkVersionInfo() = \(UX.sdkVersionInfo())"
                }
            }
            Section("Pending uploads") {
                DemoButton("Get pending session count", result: $result) {
                    "pendingSessionCount() = \(UX.pendingSessionCount())"
                }
                DemoButton("Get pending uploads (async)", result: $result) {
                    UX.pendingUploads { count in result = "pendingUploads callback = \(count)" }
                    return "pendingUploads(callback)…"
                }
                DemoButton("Delete pending uploads", result: $result) {
                    UX.deletePendingUploads(); return "deletePendingUploads()"
                }
            }
            Section("Crash handler") {
                DemoButton("Enable crash handler", result: $result) {
                    UX.disableCrashHandling(false); return "disableCrashHandling(false)"
                }
                DemoButton("Disable crash handler", result: $result) {
                    UX.disableCrashHandling(true); return "disableCrashHandling(true)"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Status & Misc")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Status & Misc") }
    }
}
