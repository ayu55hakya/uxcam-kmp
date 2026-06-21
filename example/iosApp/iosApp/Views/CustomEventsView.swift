import SwiftUI

/// Mirrors `CustomEventsActivity` — event logging with various payload shapes.
struct CustomEventsView: View {
    @State private var result = ""
    @State private var eventName = ""
    @State private var eventKey = ""
    @State private var eventValue = ""

    var body: some View {
        Form {
            Section("Map payloads") {
                DemoButton("Event without properties", result: $result) {
                    UX.logEvent("event_no_props"); return "logEvent(\"event_no_props\")"
                }
                DemoButton("Event with map properties", result: $result) {
                    UX.logEvent("event_with_map", properties: ["source": "sample", "count": 3])
                    return "logEvent(\"event_with_map\", {source, count})"
                }
                DemoButton("Event with empty map", result: $result) {
                    UX.logEvent("event_empty_map", properties: [:]); return "logEvent(\"event_empty_map\", {})"
                }
                DemoButton("Event with null map", result: $result) {
                    UX.logEvent("event_null_map", properties: nil); return "logEvent(\"event_null_map\", null)"
                }
            }
            Section("JSON payloads") {
                DemoButton("Event with JSON properties", result: $result) {
                    UX.logEventWithJson("event_with_json", json: "{\"source\":\"sample\",\"count\":3}")
                    return "logEventWithJson(\"event_with_json\", json)"
                }
                DemoButton("Event with empty JSON", result: $result) {
                    UX.logEventWithJson("event_empty_json", json: "{}"); return "logEventWithJson(\"event_empty_json\", \"{}\")"
                }
                DemoButton("Event with null JSON", result: $result) {
                    UX.logEventWithJson("event_null_json", json: nil); return "logEventWithJson(\"event_null_json\", null)"
                }
            }
            Section("Custom event") {
                TextField("Event name", text: $eventName)
                TextField("Property key", text: $eventKey)
                TextField("Property value", text: $eventValue)
                DemoButton("Log custom event", result: $result) {
                    UX.logEvent(eventName, properties: [eventKey: eventValue])
                    return "logEvent(\"\(eventName)\", {\(eventKey): \(eventValue)})"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Custom Events")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Custom Events") }
    }
}
