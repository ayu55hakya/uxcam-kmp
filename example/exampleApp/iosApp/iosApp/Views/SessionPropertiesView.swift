import SwiftUI

/// Mirrors `SessionPropertiesActivity` — metadata on the current recording session.
struct SessionPropertiesView: View {
    @State private var result = ""
    @State private var key = ""
    @State private var value = ""

    var body: some View {
        Form {
            Section("Predefined") {
                DemoButton("Set string property (tier = gold)", result: $result) {
                    UX.setSessionProperty("tier", "gold"); return "setSessionProperty(\"tier\", \"gold\")"
                }
                DemoButton("Set int property (cart_items = 3)", result: $result) {
                    UX.setSessionProperty("cart_items", Int32(3)); return "setSessionProperty(\"cart_items\", 3)"
                }
                DemoButton("Set float property (cart_total = 49.99)", result: $result) {
                    UX.setSessionProperty("cart_total", Float(49.99)); return "setSessionProperty(\"cart_total\", 49.99)"
                }
                DemoButton("Set bool property (checkout_started = true)", result: $result) {
                    UX.setSessionProperty("checkout_started", true); return "setSessionProperty(\"checkout_started\", true)"
                }
                DemoButton("Mark session as favorite", result: $result) {
                    UX.markSessionAsFavorite(); return "markSessionAsFavorite()"
                }
            }
            Section("Custom property") {
                TextField("Key", text: $key)
                TextField("Value", text: $value)
                DemoButton("Set custom property", result: $result) {
                    UX.setSessionProperty(key, value); return "setSessionProperty(\"\(key)\", \"\(value)\")"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Session Properties")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Session Properties") }
    }
}
