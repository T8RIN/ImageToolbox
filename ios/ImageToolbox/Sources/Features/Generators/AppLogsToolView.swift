import SwiftUI

struct AppLogsToolView: View {
  @State private var model = AppLogsToolModel()

  var body: some View {
    ToolScaffold(identifier: "appLogs") {
      GlassSection("Last hour") {
        HStack {
          Button("Refresh") {
            Task { await model.refresh() }
          }
          .buttonStyle(.glassProminent)
          .disabled(model.isLoading)
          .accessibilityIdentifier("appLogs.refresh")

          if !model.text.isEmpty {
            ShareFileButton(title: "Share", data: Data(model.text.utf8), fileName: "app-logs.txt")
              .accessibilityIdentifier("appLogs.share")
          }
        }

        if model.failed {
          ErrorText(message: "The app log could not be read.")
        }

        if model.isLoading {
          ProgressView()
        }

        if !model.text.isEmpty {
          ScrollView(.horizontal) {
            Text(model.text)
              .font(.system(.caption2, design: .monospaced))
              .textSelection(.enabled)
              .fixedSize()
          }
        }
      }
    }
    .task {
      await model.refresh()
    }
  }
}
