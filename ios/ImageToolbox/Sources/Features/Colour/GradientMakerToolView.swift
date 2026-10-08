import SwiftUI

struct GradientMakerToolView: View {
  @State private var model = GradientMakerToolModel()

  var body: some View {
    ToolScaffold(identifier: "gradientMaker") {
      stopsSection
      typeSection
      sizeSection
      outputSection
      meshSection
    }
  }

  private var stopsSection: some View {
    GlassSection("Stops") {
      ForEach($model.stops) { $stop in
        VStack(alignment: .leading, spacing: 8) {
          ColorPicker("Colour", selection: $stop.color)
          ParameterSlider(
            title: "Position",
            value: $stop.position,
            range: 0...1,
            step: 0.01,
            format: "%.2f")
        }
      }

      HStack {
        Button("Add stop") { model.addStop() }
          .buttonStyle(.glass)
          .disabled(!model.canAddStop)
          .accessibilityIdentifier("gradientMaker.addStop")
        Button("Remove stop") { model.removeStop() }
          .buttonStyle(.glass)
          .disabled(!model.canRemoveStop)
          .accessibilityIdentifier("gradientMaker.removeStop")
      }
    }
  }

  private var typeSection: some View {
    GlassSection("Type") {
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("gradient.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("gradient.redo")
      }
      Picker("Type", selection: $model.kind) {
        Text("Linear").tag(GradientKind.linear)
        Text("Radial").tag(GradientKind.radial)
      }
      .pickerStyle(.segmented)

      if model.kind == .linear {
        ParameterSlider(
          title: "Angle", value: $model.angle, onEditingEnded: model.commitParameters,
          range: 0...360)
      }
    }
  }

  private var sizeSection: some View {
    GlassSection("Size") {
      ParameterSlider(
        title: "Width", value: $model.width, onEditingEnded: model.commitParameters,
        range: 64...2048, step: 64)
      ParameterSlider(
        title: "Height", value: $model.height, onEditingEnded: model.commitParameters,
        range: 64...2048, step: 64)
    }
  }

  private var outputSection: some View {
    GlassSection("Output") {
      Button("Render") {
        model.commitParameters()
        Task { await model.render() }
      }
      .buttonStyle(.glassProminent)
      .accessibilityIdentifier("gradientMaker.render")

      if let rendered = model.rendered {
        PictureView(image: rendered.image, maxHeight: 320)
        ShareFileButton(title: "Share PNG", data: rendered.data, fileName: model.renderedFileName)
          .accessibilityIdentifier("gradientMaker.share")
      }

      if model.renderFailed {
        ErrorText(message: "Could not render the gradient.")
      }
    }
  }

  private var meshSection: some View {
    GlassSection("Mesh gradient") {
      MeshSampleView()
        .frame(height: 240)
        .clipShape(.rect(cornerRadius: 16))

      Button("Export mesh PNG") { model.exportMesh() }
        .buttonStyle(.glassProminent)
        .accessibilityIdentifier("gradientMaker.exportMesh")

      if let meshPNG = model.meshPNG {
        ShareFileButton(title: "Share mesh PNG", data: meshPNG, fileName: model.meshFileName)
          .accessibilityIdentifier("gradientMaker.shareMesh")
      }

      if model.meshFailed {
        ErrorText(message: "Could not render the mesh gradient.")
      }
    }
  }
}

/// Sample 3x3 mesh. The centre point is moved off the grid so the mesh looks organic.
struct MeshSampleView: View {
  private static let points: [SIMD2<Float>] = [
    SIMD2(0, 0), SIMD2(0.5, 0), SIMD2(1, 0),
    SIMD2(0, 0.5), SIMD2(0.55, 0.45), SIMD2(1, 0.5),
    SIMD2(0, 1), SIMD2(0.5, 1), SIMD2(1, 1),
  ]

  private static let colors: [Color] = [
    .pink, .orange, .yellow,
    .purple, .white, .mint,
    .blue, .teal, .indigo,
  ]

  var body: some View {
    MeshGradient(width: 3, height: 3, points: Self.points, colors: Self.colors)
  }
}
