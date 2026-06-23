import SwiftUI

/// Mirrors `CrashActivity` — triggers deliberate, uncaught crashes so UXCam's crash
/// handler can capture them. (The iOS wrapper is currently a no-op stub, so nothing is
/// captured yet; the buttons still produce real crashes.)
struct CrashView: View {
    var body: some View {
        Form {
            Section {
                Button("Division by zero", role: .destructive) {
                    let zero = Int.random(in: 0...0) // opaque 0 so the compiler can't fold it
                    _ = 10 / zero
                }
                Button("Fatal error", role: .destructive) {
                    fatalError("Sample fatal error")
                }
                Button("Force-unwrap nil", role: .destructive) {
                    let value: String? = nil
                    _ = value!
                }
                Button("Array out of bounds", role: .destructive) {
                    let array = [Int]()
                        _ = array[1]
                }
                Button("Stack overflow", role: .destructive) {
                    func recurse(_ n: Int) -> Int { recurse(n + 1) }
                    _ = recurse(0)
                }
            } footer: {
                Text("Each button intentionally crashes the app to exercise crash capture.")
            }
        }
        .navigationTitle("Crash")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Crash") }
    }
}
