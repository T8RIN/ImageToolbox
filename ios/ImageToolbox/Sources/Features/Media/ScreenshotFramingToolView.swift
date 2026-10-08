import SwiftUI

struct ScreenshotFramingToolView: View {
  @State private var model = ScreenshotFramingToolModel()

  var body: some View {
    ToolScaffold(identifier: "screenshotFraming") {
      GlassSection {
        SinglePhotoButton(title: "Choose screenshot", selection: $model.pickedItem)
          .accessibilityIdentifier("screenshotFraming.choose")

        if let message = model.errorMessage {
          ErrorText(message: message)
        }
      }

      GlassSection("Frame") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("framing.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("framing.redo")
        }
        ParameterSlider(
          title: "Padding", value: $model.padding, onEditingEnded: model.commitParameters,
          range: 0...200)
        ParameterSlider(
          title: "Corner radius", value: $model.cornerRadius,
          onEditingEnded: model.commitParameters, range: 0...120)
        ParameterSlider(
          title: "Shadow", value: $model.shadowRadius, onEditingEnded: model.commitParameters,
          range: 0...80)
        ColorPicker("Background", selection: $model.background, supportsOpacity: false)
          .onChange(of: model.background) { _, _ in model.commitParameters() }
      }

      if let framed = model.framed {
        GlassSection("Result") {
          PictureView(image: framed.image)
          ShareFileButton(title: "Share PNG", data: framed.png, fileName: model.framedFileName)
            .accessibilityIdentifier("screenshotFraming.share")
        }
      }
    }
    .onChange(of: model.pickedItem) { _, item in
      guard let item else { return }
      Task { await model.pick(item) }
    }
    .task(id: model.settings) {
      await model.render()
    }
  }
}
