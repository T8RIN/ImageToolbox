import ImageToolboxKit
import PhotosUI
import SwiftUI

struct RotateFlipToolView: View {
  @State private var model = RotateFlipToolModel()
  @Environment(\.services) private var services

  var body: some View {
    ToolScaffold(identifier: "rotateFlip") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: Bindable(model).selection)
          .accessibilityIdentifier("rotateFlip.pick")
      }

      if let current = model.current {
        GlassSection("Transform") {
          PictureView(image: current)

          HStack(spacing: 12) {
            transformButton(
              "Rotate left",
              systemImage: "rotate.left",
              identifier: "rotateFlip.rotateLeft",
              action: model.rotateLeft
            )
            transformButton(
              "Rotate right",
              systemImage: "rotate.right",
              identifier: "rotateFlip.rotateRight",
              action: model.rotateRight
            )
          }

          HStack(spacing: 12) {
            transformButton(
              "Flip horizontal",
              systemImage: "arrow.left.and.right",
              identifier: "rotateFlip.flipHorizontal",
              action: model.flipHorizontal
            )
            transformButton(
              "Flip vertical",
              systemImage: "arrow.up.and.down",
              identifier: "rotateFlip.flipVertical",
              action: model.flipVertical
            )
          }

          HStack(spacing: 12) {
            historyButton(
              "Undo",
              systemImage: "arrow.uturn.backward",
              identifier: "rotateFlip.undo",
              isEnabled: model.canUndo,
              action: model.undo
            )
            historyButton(
              "Redo",
              systemImage: "arrow.uturn.forward",
              identifier: "rotateFlip.redo",
              isEnabled: model.canRedo,
              action: model.redo
            )
          }

          Button("Reset") {
            model.reset()
          }
          .buttonStyle(.glass)
          .accessibilityIdentifier("rotateFlip.reset")
        }

        GlassSection("Export") {
          OutputFormatPicker(selection: Bindable(model).format)

          if model.format.isLossy {
            ParameterSlider(
              title: "Quality",
              value: Bindable(model).quality,
              range: 0.1...1,
              step: 0.05,
              format: "%.2f"
            )
          }

          if let exportData = model.exportData {
            ShareFileButton(
              title: "Share file",
              data: exportData,
              fileName: model.exportFileName
            )
            .accessibilityIdentifier("rotateFlip.share")

            SaveFileButton(
              title: "Save to Files",
              data: exportData,
              fileName: model.exportFileName
            )
            .accessibilityIdentifier("rotateFlip.save")
          }
        }
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }
    }
    .task(id: model.revision) {
      try? await Task.sleep(for: .milliseconds(300))
      guard !Task.isCancelled else { return }
      model.refreshExport(encoder: services.encoder)
    }
  }

  private func transformButton(
    _ title: LocalizedStringKey,
    systemImage: String,
    identifier: String,
    action: @escaping () -> Void
  ) -> some View {
    Button(action: action) {
      Label(title, systemImage: systemImage)
        .frame(maxWidth: .infinity)
    }
    .buttonStyle(.glass)
    .accessibilityIdentifier(identifier)
  }

  private func historyButton(
    _ title: LocalizedStringKey,
    systemImage: String,
    identifier: String,
    isEnabled: Bool,
    action: @escaping () -> Void
  ) -> some View {
    Button(action: action) {
      Label(title, systemImage: systemImage)
        .frame(maxWidth: .infinity)
    }
    .buttonStyle(.glass)
    .disabled(!isEnabled)
    .accessibilityIdentifier(identifier)
  }
}
