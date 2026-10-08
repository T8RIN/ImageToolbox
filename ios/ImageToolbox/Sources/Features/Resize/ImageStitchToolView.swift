import ImageToolboxKit
import PhotosUI
import SwiftUI

struct ImageStitchToolView: View {
  @State private var model = ImageStitchToolModel()
  @State private var pickedItems: [PhotosPickerItem] = []

  var body: some View {
    ToolScaffold(identifier: "imageStitch") {
      GlassSection("Images") {
        MultiPhotoButton(title: "Choose images", selection: $pickedItems)
          .accessibilityIdentifier("imageStitch.pick")
        Text("Loaded: \(model.images.count)")
          .monospacedDigit()
          .foregroundStyle(.secondary)
      }

      GlassSection("Layout") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("stitch.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("stitch.redo")
        }
        Picker("Direction", selection: $model.direction) {
          Text("Horizontal").tag(StitchDirection.horizontal)
          Text("Vertical").tag(StitchDirection.vertical)
        }
        .pickerStyle(.segmented)
        ParameterSlider(
          title: "Spacing", value: $model.spacing, onEditingEnded: model.commitParameters,
          range: 0...100, format: "%.0f px")
        Button("Stitch") {
          model.commitParameters()
          Task { await model.apply() }
        }
        .buttonStyle(.glassProminent)
        .accessibilityIdentifier("imageStitch.apply")
      }

      if let result = model.result {
        GlassSection("Result") {
          PictureView(image: result.image)
          ShareFileButton(title: "Share", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("imageStitch.share")
        }
      }

      if let failure = model.failure {
        ErrorText(message: failure)
      }
    }
    .task(id: pickedItems) {
      await model.load(pickedItems)
    }
  }
}
