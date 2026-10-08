import ImageToolboxKit
import PhotosUI
import SwiftUI

struct ImageSplittingToolView: View {
  @State private var model = ImageSplittingToolModel()
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "imageSplitting") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: $pickedItem)
          .accessibilityIdentifier("imageSplitting.pick")
        if let source = model.source {
          PictureView(image: source.image, maxHeight: 240)
        }
      }

      if model.source != nil {
        GlassSection("Split") {
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("splitting.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("splitting.redo")
          }
          Picker("Direction", selection: $model.axis) {
            Text("Rows").tag(SliceAxis.rows)
            Text("Columns").tag(SliceAxis.columns)
          }
          .pickerStyle(.segmented)
          Stepper("Parts: \(model.parts)", value: $model.parts, in: 2...12)
          Button("Split") {
            model.commitParameters()
            Task { await model.apply() }
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("imageSplitting.apply")
        }
      }

      if !model.pieces.isEmpty {
        GlassSection("Parts") {
          ForEach(Array(model.pieces.enumerated()), id: \.offset) { index, piece in
            VStack(alignment: .leading, spacing: 8) {
              PictureView(image: piece.image, maxHeight: 200)
              ShareFileButton(title: "Share", data: piece.data, fileName: piece.fileName)
                .accessibilityIdentifier("imageSplitting.share.\(index + 1)")
            }
          }
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
