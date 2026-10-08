import SwiftUI

struct PerlinNoiseToolView: View {
  @State private var model = PerlinNoiseToolModel()

  var body: some View {
    ToolScaffold(identifier: "perlinNoise") {
      GlassSection {
        if let image = model.image {
          PictureView(image: image)
        } else if model.isGenerating {
          ProgressView()
            .frame(maxWidth: .infinity, minHeight: 200)
        }

        if model.generationFailed {
          ErrorText(message: "The picture could not be generated.")
        }

        if let pngData = model.pngData {
          ShareFileButton(title: "Share PNG", data: pngData, fileName: model.pngFileName)
            .accessibilityIdentifier("perlinNoise.share")
        }
      }

      GlassSection("Parameters") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("perlin.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("perlin.redo")
        }
        ParameterSlider(
          title: "Seed", value: $model.seed, onEditingEnded: model.commitParameters, range: 0...9999
        )
        ParameterSlider(
          title: "Scale", value: $model.scale, onEditingEnded: model.commitParameters,
          range: 1...200)
        ParameterSlider(
          title: "Octaves", value: $model.octaves, onEditingEnded: model.commitParameters,
          range: 1...8)
        ParameterSlider(
          title: "Persistence",
          value: $model.persistence, onEditingEnded: model.commitParameters,
          range: 0.1...0.9,
          step: 0.05,
          format: "%.2f")
        ParameterSlider(
          title: "Width", value: $model.width, onEditingEnded: model.commitParameters,
          range: 128...1024, step: 64)
        ParameterSlider(
          title: "Height", value: $model.height, onEditingEnded: model.commitParameters,
          range: 128...1024, step: 64)

        Button("Generate") {
          Task { await model.generate() }
        }
        .buttonStyle(.glassProminent)
        .disabled(model.isGenerating)
        .accessibilityIdentifier("perlinNoise.generate")
      }
    }
    .task {
      if model.image == nil {
        await model.generate()
      }
    }
  }
}
