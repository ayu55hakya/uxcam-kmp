import SwiftUI

/// Mirrors `ScreenTaggingActivity` — manual screen naming, auto-tagging config, and the
/// screen-name ignore list.
struct ScreenTaggingView: View {
    @State private var result = ""
    @State private var screenName = ""
    @State private var ignoreName = ""

    var body: some View {
        Form {
            Section("Tagging") {
                TextField("Screen name", text: $screenName)
                DemoButton("Tag screen", result: $result) {
                    let name = screenName.isEmpty ? "CustomScreen" : screenName
                    UX.tagScreen(name); return "tagScreenName(\"\(name)\")"
                }
                DemoButton("Enable auto screen naming", result: $result) {
                    UX.setAutomaticScreenNameTagging(true); return "setAutomaticScreenNameTagging(true)"
                }
                DemoButton("Disable auto screen naming", result: $result) {
                    UX.setAutomaticScreenNameTagging(false); return "setAutomaticScreenNameTagging(false)"
                }
                DemoButton("Enable improved screen capture", result: $result) {
                    UX.setImprovedScreenCaptureEnabled(true); return "setImprovedScreenCaptureEnabled(true)"
                }
                DemoButton("Disable improved screen capture", result: $result) {
                    UX.setImprovedScreenCaptureEnabled(false); return "setImprovedScreenCaptureEnabled(false)"
                }
            }
            Section("Ignore list") {
                TextField("Screen name to ignore", text: $ignoreName)
                DemoButton("Add to ignore list", result: $result) {
                    let name = ignoreName.isEmpty ? "Settings" : ignoreName
                    UX.addScreenNameToIgnore(name); return "addScreenNameToIgnore(\"\(name)\")"
                }
                DemoButton("Add multiple to ignore", result: $result) {
                    UX.addScreenNamesToIgnore(["Login", "Payment"]); return "addScreenNamesToIgnore([Login, Payment])"
                }
                DemoButton("Remove from ignore list", result: $result) {
                    let name = ignoreName.isEmpty ? "Settings" : ignoreName
                    UX.removeScreenNameToIgnore(name); return "removeScreenNameToIgnore(\"\(name)\")"
                }
                DemoButton("Remove multiple from ignore", result: $result) {
                    UX.removeScreenNamesToIgnore(["Login", "Payment"]); return "removeScreenNamesToIgnore([Login, Payment])"
                }
                DemoButton("Remove all ignored", result: $result) {
                    UX.removeAllScreenNamesToIgnore(); return "removeAllScreenNamesToIgnore()"
                }
                DemoButton("List ignored screens", result: $result) {
                    "screenNamesBeingIgnored() = \(UX.screenNamesBeingIgnored())"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Screen Tagging")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Screen Tagging") }
    }
}
