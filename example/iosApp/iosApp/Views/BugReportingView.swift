import SwiftUI

/// Mirrors `BugReportingActivity` — bug events and exception reporting.
struct BugReportingView: View {
    @State private var result = ""

    var body: some View {
        Form {
            Section("Bug events") {
                DemoButton("Bug without properties", result: $result) {
                    UX.reportBugEvent("bug_no_props"); return "reportBugEvent(\"bug_no_props\")"
                }
                DemoButton("Bug with map properties", result: $result) {
                    UX.reportBugEvent("bug_with_map", properties: ["screen": "checkout", "code": 500])
                    return "reportBugEvent(\"bug_with_map\", {screen, code})"
                }
                DemoButton("Bug with JSON properties", result: $result) {
                    UX.reportBugEventWithJson("bug_with_json", json: "{\"screen\":\"checkout\",\"code\":500}")
                    return "reportBugEventWithJson(\"bug_with_json\", json)"
                }
            }
            Section("Exceptions") {
                DemoButton("Report exception", result: $result) {
                    UX.reportException("Sample exception"); return "reportExceptionEvent(IllegalState…)"
                }
                DemoButton("Report exception with properties", result: $result) {
                    UX.reportException("Sample exception", properties: ["where": "BugReportingView"])
                    return "reportExceptionEvent(…, {where})"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Bug & Exception Reporting")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Bug & Exception Reporting") }
    }
}
