import SwiftUI
import UIKit

struct ScanQRCodeToolView: View {
  @State private var model = ScanQRCodeToolModel()

  var body: some View {
    ToolScaffold(identifier: "scanQRCode") {
      GlassSection {
        SinglePhotoButton(title: "Choose photo", selection: $model.pickedItem)
          .accessibilityIdentifier("scanQRCode.choose")

        if let codes = model.codes {
          if codes.isEmpty {
            Text("No codes found")
              .foregroundStyle(.secondary)
          } else {
            ForEach(codes) { code in
              VStack(alignment: .leading, spacing: 6) {
                Text(code.payload)
                  .textSelection(.enabled)
                HStack {
                  Text(code.symbology)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                  Spacer()
                  Button("Copy") {
                    UIPasteboard.general.string = code.payload
                  }
                  .buttonStyle(.glass)
                  .accessibilityIdentifier("scanQRCode.copy")
                }
              }
            }
          }
        }

        if let message = model.errorMessage {
          ErrorText(message: message)
        }
      }

      Text("Live camera scanning is not part of this screen.")
        .font(.footnote)
        .foregroundStyle(.secondary)
    }
    .onChange(of: model.pickedItem) { _, item in
      guard let item else { return }
      Task { await model.scan(item) }
    }
  }
}
