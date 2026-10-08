import SwiftUI
import UniformTypeIdentifiers

struct APNGToolsView: View {
  @State private var model = APNGToolsModel()
  @State private var isImporting = false

  var body: some View {
    ToolScaffold(identifier: "apngTools") {
      GlassSection("Make APNG") {
        MultiPhotoButton(title: "Choose frames", selection: $model.pickedItems, maxCount: 30)
          .accessibilityIdentifier("apngTools.choose")
        Text("Frames: \(model.frames.count)")
          .foregroundStyle(.secondary)

        HStack {

          Button("Undo") { model.undo() }

            .buttonStyle(.glass)

            .disabled(!model.canUndo)

            .accessibilityIdentifier("apng.undo")

          Button("Redo") { model.redo() }

            .buttonStyle(.glass)

            .disabled(!model.canRedo)

            .accessibilityIdentifier("apng.redo")

        }

        ParameterSlider(
          title: "Frame delay (s)",
          value: $model.frameDelay, onEditingEnded: model.commitParameters,
          range: 0.02...1,
          step: 0.01,
          format: "%.2f")

        Button("Make APNG") {
          model.commitParameters()
          Task { await model.makeAPNG() }
        }
        .buttonStyle(.glassProminent)
        .accessibilityIdentifier("apngTools.make")
        if model.isMaking {
          Button("Cancel") { model.cancelMaking() }
            .buttonStyle(.glass)
            .accessibilityIdentifier("apng.cancel")
        }

        if let message = model.makeError {
          ErrorText(message: message)
        }
        if let output = model.output {
          ShareFileButton(title: "Share APNG", data: output, fileName: model.outputFileName)
            .accessibilityIdentifier("apngTools.share")
        }
      }

      GlassSection("Read APNG") {
        Button("Choose PNG") { isImporting = true }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("apngTools.read")

        if let picture = model.readPicture {
          PictureView(image: picture)
          Text("Frames: \(model.readFrameCount)")
        }
        if let message = model.readError {
          ErrorText(message: message)
        }
      }
    }
    .onChange(of: model.pickedItems) { _, _ in
      Task { await model.loadFrames() }
    }
    .fileImporter(
      isPresented: $isImporting,
      allowedContentTypes: [.png],
      allowsMultipleSelection: false
    ) { result in
      guard case .success(let urls) = result, let url = urls.first else { return }
      Task { await model.inspect(url) }
    }
  }
}
