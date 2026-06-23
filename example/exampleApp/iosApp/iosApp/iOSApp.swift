import SwiftUI

@main
struct iOSApp: App {

    init() {
        // Initialize UXCam once at startup — mirrors the Android sample's LauncherActivity.
        // (iOS wrapper is currently a no-op stub; see UXCam.ios.kt.)
        UX.start()
        UX.addVerificationListener(
            onSuccess: { print("UXCam: verification success") },
            onFailure: { message in print("UXCam: verification failure — \(message)") }
        )
    }

    var body: some Scene {
        WindowGroup {
            NavigationStack {
                HomeView()
            }
        }
    }
}
