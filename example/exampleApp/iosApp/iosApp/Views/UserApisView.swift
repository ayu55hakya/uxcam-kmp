import SwiftUI

/// Mirrors `UserApisActivity` — user identity and user properties.
struct UserApisView: View {
    @State private var result = ""
    @State private var key = ""
    @State private var value = ""

    var body: some View {
        Form {
            Section("Predefined") {
                DemoButton("Set user identity", result: $result) {
                    UX.setUserIdentity("user_123456"); return "setUserIdentity(\"user_123456\")"
                }
                DemoButton("Set property (plan = premium)", result: $result) {
                    UX.setUserProperty("plan", "premium"); return "setUserProperty(\"plan\", \"premium\")"
                }
                DemoButton("Set int property (age = 30)", result: $result) {
                    UX.setUserProperty("age", Int32(30)); return "setUserProperty(\"age\", 30)"
                }
                DemoButton("Set float property (rating = 4.5)", result: $result) {
                    UX.setUserProperty("rating", Float(4.5)); return "setUserProperty(\"rating\", 4.5)"
                }
                DemoButton("Set bool property (subscribed = true)", result: $result) {
                    UX.setUserProperty("subscribed", true); return "setUserProperty(\"subscribed\", true)"
                }
                DemoButton("Set push token", result: $result) {
                    UX.setPushNotificationToken("sample_push_token"); return "setPushNotificationToken(…)"
                }
            }
            Section("Custom property") {
                TextField("Key", text: $key)
                TextField("Value", text: $value)
                DemoButton("Set custom property", result: $result) {
                    UX.setUserProperty(key, value); return "setUserProperty(\"\(key)\", \"\(value)\")"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("User APIs")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("User APIs") }
    }
}
