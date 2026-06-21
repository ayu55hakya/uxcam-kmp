import SwiftUI

/// Menu hub — mirrors the Android sample's `ViewHomeActivity`, listing every feature
/// screen. Each row pushes a native SwiftUI screen that drives the UXCam wrapper.
struct HomeView: View {
    var body: some View {
        List {
            Section("User & session data") {
                NavigationLink("User APIs") { UserApisView() }
                NavigationLink("Session Properties") { SessionPropertiesView() }
                NavigationLink("Custom Events") { CustomEventsView() }
                NavigationLink("Bug & Exception Reporting") { BugReportingView() }
            }
            Section("Privacy & occlusion") {
                NavigationLink("Occlusion") { OcclusionView() }
                NavigationLink("Full-Screen Occlusion") { FullScreenOcclusionView() }
                NavigationLink("Opt In / Out") { OptInOutView() }
            }
            Section("Recording & diagnostics") {
                NavigationLink("Screen Tagging") { ScreenTaggingView() }
                NavigationLink("Session Controls") { SessionControlsView() }
                NavigationLink("Status & Misc") { StatusView() }
                NavigationLink("Crash") { CrashView() }
            }
        }
        .navigationTitle("UXCam iOS Sample")
        .onAppear { UX.tagScreen("iOS Home") }
    }
}

#Preview {
    NavigationStack { HomeView() }
}
