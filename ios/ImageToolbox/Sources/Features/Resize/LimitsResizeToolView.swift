import PhotosUI
import SwiftUI

struct LimitsResizeToolView: View {
  @State private var model = LimitsResizeToolModel()
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "limitsResize") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: $pickedItem)
          .accessibilityIdentifier("limitsResize.pick")
        if let source = model.source {
          PictureView(image: source.image, maxHeight: 240)
        }
      }

      if model.source != nil {
        GlassSection("Limits") {
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("limitsResize.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("limitsResize.redo")
          }
          ParameterSlider(
            title: "Maximum width", value: $model.maxWidth, onEditingEnded: model.commitParameters,
            range: 16...8000, step: 16,
            format: "%.0f px")
          ParameterSlider(
            title: "Maximum height", value: $model.maxHeight,
            onEditingEnded: model.commitParameters, range: 16...8000, step: 16,
            format: "%.0f px")
          if let size = model.targetSize {
            Text("Output: \(Int(size.width)) × \(Int(size.height)) px")
              .monospacedDigit()
              .foregroundStyle(.secondary)
          }
          Button("Limit size") {
            model.commitParameters()
            Task { await model.apply() }
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("limitsResize.apply")
        }
      }

      if let result = model.result {
        GlassSection("Result") {
          Text("\(result.image.width) × \(result.image.height) px")
            .monospacedDigit()
            .foregroundStyle(.secondary)
          PictureView(image: result.image)
          ShareFileButton(title: "Share", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("limitsResize.share")
          SaveFileButton(title: "Save to Files", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("limitsResize.save")
        }
      }

      if let failure = model.failure {
        ErrorText(message: failure)
      }
    }
    .task(id: pickedItem) {
      guard let pickedItem else { return }
      await model.load(pickedItem)
    }
  }
}
