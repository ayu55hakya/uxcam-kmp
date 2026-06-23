import SwiftUI
import UIKit

/// Mirrors `OcclusionActivity`. Covers screen-level occlusion plus iOS per-view occlusion
/// (`occludeSensitiveView` / `unOccludeSensitiveView`), which operate on a specific
/// `UIView`. SwiftUI has no `UIView` to hand the SDK, so a small `UIViewRepresentable`
/// (`SensitiveLabel`) hosts a real `UILabel` and the buttons occlude/un-occlude that exact
/// instance.
struct OcclusionView: View {
    @State private var result = ""
    @State private var password = ""
    // A single retained UILabel so the SAME instance is passed to both occlude and
    // un-occlude — per-view occlusion is stateful and keyed on the view.
    @State private var sensitive = SensitiveViewHolder()

    var body: some View {
        Form {
            Section("Sample sensitive content") {
                Text("Secret: 4111 1111 1111 1111")
                SecureField("Password", text: $password)
            }
            Section("Per-view occlusion (iOS)") {
                SensitiveLabel(label: sensitive.label)
                    .frame(height: 24)
                DemoButton("Occlude this view", result: $result) {
                    UX.occludeSensitiveView(sensitive.label); return "occludeSensitiveView(label)"
                }
                DemoButton("Occlude this view (no gesture)", result: $result) {
                    UX.occludeSensitiveViewWithoutGesture(sensitive.label)
                    return "occludeSensitiveViewWithoutGesture(label)"
                }
                DemoButton("Un-occlude this view", result: $result) {
                    UX.unOccludeSensitiveView(sensitive.label); return "unOccludeSensitiveView(label)"
                }
            }
            Section("Screen occlusion") {
                DemoButton("Enable screen occlusion", result: $result) {
                    UX.occludeSensitiveScreen(true); return "occludeSensitiveScreen(true)"
                }
                DemoButton("Disable screen occlusion", result: $result) {
                    UX.occludeSensitiveScreen(false); return "occludeSensitiveScreen(false)"
                }
                DemoButton("Occlude without gesture", result: $result) {
                    UX.occludeSensitiveScreen(true, withoutGesture: true)
                    return "occludeSensitiveScreen(true, withoutGesture: true)"
                }
            }
            Section("Text fields") {
                DemoButton("Occlude all text fields", result: $result) {
                    UX.occludeAllTextFields(true); return "occludeAllTextFields(true)"
                }
                DemoButton("Unocclude all text fields", result: $result) {
                    UX.occludeAllTextFields(false); return "occludeAllTextFields(false)"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Occlusion")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Occlusion") }
    }
}

/// Retains one `UILabel` instance for the lifetime of the screen so the same view can be
/// occluded and later un-occluded.
final class SensitiveViewHolder {
    let label: UILabel = {
        let label = UILabel()
        label.text = "Secret: 4111 1111 1111 1111"
        label.font = .monospacedSystemFont(ofSize: 16, weight: .regular)
        label.textColor = .label
        return label
    }()
}

/// Hosts the UIKit `UILabel` inside SwiftUI so per-view occlusion has a real `UIView`.
struct SensitiveLabel: UIViewRepresentable {
    let label: UILabel
    func makeUIView(context: Context) -> UILabel { label }
    func updateUIView(_ uiView: UILabel, context: Context) {}
}
