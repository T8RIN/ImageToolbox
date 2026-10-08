import ImageToolboxKit
import PhotosUI
import SwiftUI

struct WeightResizeToolView: View {
  @State private var model = WeightResizeToolModel()
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "weightResize") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: $pickedItem)
          .accessibilityIdentifier("weightResize.pick")
        if let source = model.source {
          PictureView(image: source.image, maxHeight: 240)
        }
      }

      if model.source != nil {
        GlassSection("Target") {
          ParameterSlider(
            title: "Target size", value: $model.targetKilobytes,
            onEditingEnded: model.commitParameters, range: 50...5000, step: 50,
            format: "%.0f KB")
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("weightResize.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("weightResize.redo")
          }
          OutputFormatPicker(selection: $model.format)
          Button("Fit to size") {
            model.commitParameters()
            Task { await model.fit() }
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("weightResize.apply")
        }
      }

      if let output = model.result {
        GlassSection("Result") {
          Text("Size: \(output.kilobytes, format: .number.precision(.fractionLength(1))) KB")
            .monospacedDigit()
          Text("Quality: \(String(format: "%.2f", output.fitted.quality))")
            .monospacedDigit()
            .foregroundStyle(.secondary)
          PictureView(image: output.fitted.image)
          ShareFileButton(
            title: "Share", data: output.fitted.data, fileName: output.fileName
          )
          .accessibilityIdentifier("weightResize.share")
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
