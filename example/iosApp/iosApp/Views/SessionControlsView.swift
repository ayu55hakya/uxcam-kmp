import SwiftUI
import PhotosUI

/// Mirrors `SessionControlsActivity` — recording lifecycle, pause/resume, short break for
/// another app (via a photo picker), and multi-session control.
struct SessionControlsView: View {
    @State private var result = ""
    @State private var pickerItem: PhotosPickerItem?
    @State private var showPicker = false

    var body: some View {
        Form {
            Section("Lifecycle") {
                DemoButton("Start new session", result: $result) {
                    UX.startNewSession(); return "startNewSession()"
                }
                DemoButton("Stop and upload", result: $result) {
                    UX.stopSessionAndUploadData(); return "stopSessionAndUploadData()"
                }
                DemoButton("Stop with callback", result: $result) {
                    UX.stopSessionAndUploadData { result = "onSessionStopped() callback" }
                    return "stopSessionAndUploadData(callback)"
                }
                DemoButton("Cancel current session", result: $result) {
                    UX.cancelCurrentSession(); return "cancelCurrentSession()"
                }
            }
            Section("Recording control") {
                DemoButton("Pause recording", result: $result) {
                    UX.pauseScreenRecording(); return "pauseScreenRecording()"
                }
                DemoButton("Resume recording", result: $result) {
                    UX.resumeScreenRecording(); return "resumeScreenRecording()"
                }
                DemoButton("Check if recording", result: $result) {
                    "isRecording() = \(UX.isRecording())"
                }
            }
            Section("Short break for another app") {
                // Grant a 20s break, then leave to pick a photo; end the break on return.
                Button("Allow short break (20s) + pick photo") {
                    UX.allowShortBreakForAnotherApp(millis: 20_000)
                    result = "allowShortBreakForAnotherApp(millis: 20000)"
                    showPicker = true
                }
                DemoButton("Allow short break (default)", result: $result) {
                    UX.allowShortBreakForAnotherApp(); return "allowShortBreakForAnotherApp()"
                }
                DemoButton("Resume short break", result: $result) {
                    UX.resumeShortBreakForAnotherApp(); return "resumeShortBreakForAnotherApp()"
                }
            }
            Section("Multi-session") {
                DemoButton("Get multi-session status", result: $result) {
                    "getMultiSessionRecord() = \(UX.getMultiSessionRecord())"
                }
                DemoButton("Enable multi-session", result: $result) {
                    UX.setMultiSessionRecord(true); return "setMultiSessionRecord(true)"
                }
                DemoButton("Disable multi-session", result: $result) {
                    UX.setMultiSessionRecord(false); return "setMultiSessionRecord(false)"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Session Controls")
        .navigationBarTitleDisplayMode(.inline)
        .photosPicker(isPresented: $showPicker, selection: $pickerItem, matching: .images)
        .onAppear { UX.tagScreen("Session Controls") }
        .onChange(of: pickerItem) { _, newValue in
            // Returned from the picker — end the short break without continuing it.
            if newValue != nil {
                UX.allowShortBreakForAnotherApp(continueSession: false)
                result = "allowShortBreakForAnotherApp(continueSession: false)"
            }
        }
    }
}
