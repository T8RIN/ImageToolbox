import ImageToolboxKit
import PhotosUI
import SwiftUI

struct ImageStackingToolView: View {
  @State private var model = ImageStackingToolModel()
  @State private var pickedItems: [PhotosPickerItem] = []

  var body: some View {
    ToolScaffold(identifier: "imageStacking") {
      GlassSection("Images") {
        MultiPhotoButton(title: "Choose images", selection: $pickedItems)
          .accessibilityIdentifier("imageStacking.pick")
        Text("Loaded: \(model.images.count)")
          .monospacedDigit()
          .foregroundStyle(.secondary)
      }

      GlassSection("Mode") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("stacking.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("stacking.redo")
        }
        Picker("Mode", selection: $model.mode) {
          Text("Average").tag(StackMode.average)
          Text("Median").tag(StackMode.median)
          Text("Maximum").tag(StackMode.maximum)
          Text("Minimum").tag(StackMode.minimum)
        }
        .pickerStyle(.menu)
        Button("Stack") {
          model.commitParameters()
          Task { await model.apply() }
        }
        .buttonStyle(.glassProminent)
        .accessibilityIdentifier("imageStacking.apply")
      }

      if let result = model.result {
        GlassSection("Result") {
          PictureView(image: result.image)
          ShareFileButton(title: "Share", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("imageStacking.share")
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
