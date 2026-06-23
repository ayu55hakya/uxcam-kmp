import SwiftUI

/// Mirrors `FullScreenOcclusionActivity` — full-screen overlay/blur occlusion.
struct FullScreenOcclusionView: View {
    @State private var result = ""

    var body: some View {
        Form {
            Section {
                DemoButton("Apply overlay occlusion", result: $result) {
                    UX.applyOverlayOcclusion(); return "applyOverlayOcclusion()"
                }
                DemoButton("Apply blur occlusion (radius = 15)", result: $result) {
                    UX.applyBlurOcclusion(blurRadius: 15); return "applyBlurOcclusion(blurRadius: 15)"
                }
                DemoButton("Remove occlusion", result: $result) {
                    UX.removeOcclusion(); return "removeOcclusion()"
                }
            }
            Section { ResultPanel(text: result) }
        }
        .navigationTitle("Full-Screen Occlusion")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { UX.tagScreen("Full-Screen Occlusion") }
    }
}
