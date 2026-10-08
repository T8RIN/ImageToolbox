import ImageToolboxKit
import PhotosUI
import SwiftUI

struct ImageCuttingToolView: View {
  @State private var model = ImageCuttingToolModel()
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "imageCutting") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: $pickedItem)
          .accessibilityIdentifier("imageCutting.pick")
        if let source = model.source {
          PictureView(image: source.image, maxHeight: 240)
        }
      }

      if model.source != nil {
        GlassSection("Cut") {
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("cutting.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("cutting.redo")
          }
          Picker("Lines", selection: $model.axis) {
            Text("Vertical lines").tag(SliceAxis.columns)
            Text("Horizontal lines").tag(SliceAxis.rows)
          }
          .pickerStyle(.segmented)
          ParameterSlider(
            title: "From", value: startBinding, range: 0...1, step: 0.01, format: "%.2f")
          ParameterSlider(
            title: "To", value: endBinding, range: 0...1, step: 0.01, format: "%.2f")
          Toggle("Keep only the cut part", isOn: $model.keepOnlyCut)
          Button("Cut") {
            model.commitParameters()
            Task { await model.apply() }
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("imageCutting.apply")
        }
      }

      if let result = model.result {
        GlassSection("Result") {
          PictureView(image: result.image)
          ShareFileButton(title: "Share", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("imageCutting.share")
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

  private var startBinding: Binding<Double> {
    Binding(get: { model.start }, set: { model.setStart($0) })
  }

  private var endBinding: Binding<Double> {
    Binding(get: { model.end }, set: { model.setEnd($0) })
  }
}
