import SwiftUI
import UniformTypeIdentifiers

struct GIFToolsView: View {
  @State private var model = GIFToolsModel()
  @State private var isImporting = false

  var body: some View {
    ToolScaffold(identifier: "gifTools") {
      GlassSection("Source") {
        Button("Choose GIF") { isImporting = true }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("gifTools.choose")

        if let first = model.frames.first {
          PictureView(image: first)
          Text("Frames: \(model.frames.count)")
          Text("Duration: \(model.duration, format: .number.precision(.fractionLength(2))) s")
        }

        if let message = model.errorMessage {
          ErrorText(message: message)
        }
      }

      if !model.frames.isEmpty {
        GlassSection("Make GIF") {
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("gif.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("gif.redo")
          }
          Toggle("Ping-pong", isOn: $model.pingPong)
          ParameterSlider(
            title: "Frame delay (s)",
            value: $model.frameDelay, onEditingEnded: model.commitParameters,
            range: 0.02...1,
            step: 0.01,
            format: "%.2f")

          Button("Make GIF") {
            model.commitParameters()
            Task { await model.makeGIF() }
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("gifTools.make")
          if model.isMaking {
            Button("Cancel") { model.cancelMaking() }
              .buttonStyle(.glass)
              .accessibilityIdentifier("gif.cancel")
          }

          if let output = model.output {
            ShareFileButton(title: "Share GIF", data: output, fileName: model.outputFileName)
              .accessibilityIdentifier("gifTools.share")
          }
        }
      }
    }
    .fileImporter(
      isPresented: $isImporting,
      allowedContentTypes: [.gif, .image],
      allowsMultipleSelection: false
    ) { result in
      guard case .success(let urls) = result, let url = urls.first else { return }
      Task { await model.load(url) }
    }
  }
}
