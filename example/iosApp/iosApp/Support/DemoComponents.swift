import SwiftUI

/// A button that runs a UXCam demo action and writes a one-line description of what it
/// did into the screen's shared `result` string — mirrors the Android sample's
/// `SampleActivity.report()` pattern, which echoes the last call onto the screen.
struct DemoButton: View {
    let title: String
    @Binding var result: String
    let run: () -> String

    init(_ title: String, result: Binding<String>, run: @escaping () -> String) {
        self.title = title
        self._result = result
        self.run = run
    }

    var body: some View {
        Button(title) { result = run() }
    }
}

/// Footer panel that shows the last action / returned value. Hidden until something runs.
struct ResultPanel: View {
    let text: String

    var body: some View {
        if !text.isEmpty {
            VStack(alignment: .leading, spacing: 4) {
                Text("Last call").font(.caption2).foregroundStyle(.secondary)
                Text(text).font(.footnote.monospaced())
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
    }
}
