import SwiftUI

/// Mirrors `OptInOutActivity` — recording consent (overall + video) and status checks.
struct OptInOutView: View {
    @State private var result = ""

    var body: some View {
        Form {
            Section("Overall") {
                DemoButton("Opt in overall", result: $result) {
                    UX.optInOverall(); return "optInOverall()"
                }
                DemoButton("Opt out overall", result: $result) {
                    UX.optOutOverall(); return "optOutOverall()"
                }
                DemoButton("Check opt-in status", result: $result) {
                    "optInOverallStatus() = \(UX.optInOverallStatus())"
                }
            }
            Section("Video recording") {
                DemoButton("Opt in to video recording", result: $result) {
                    UX.optIntoVideoRecording(); return "optIntoVideoRecording()"
                }
                DemoButton("Opt out of video recording", result: $result) {
                    UX.optOutOfVideoRecording(); return "optOutOfVideoRecording()"
                }
                DemoButton("Check video opt-in status", result: $result) {
                    "optInVideoRecordingStatus() = \(UX.optInVideoRecordingStatus())"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Opt In / Out")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Opt In / Out") }
    }
}
