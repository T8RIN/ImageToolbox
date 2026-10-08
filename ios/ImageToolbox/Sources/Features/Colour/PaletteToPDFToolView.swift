import SwiftUI

struct PaletteToPDFToolView: View {
  @State private var model = PaletteToPDFToolModel()

  var body: some View {
    ToolScaffold(identifier: "paletteToPDF") {
      inputSection
      previewSection
      exportSection
    }
    .onChange(of: model.input) { _, _ in model.resetOutput() }
    .onChange(of: model.columns) { _, _ in model.resetOutput() }
  }

  private var inputSection: some View {
    GlassSection("Colours") {
      TextEditor(text: $model.input)
        .font(.body.monospaced())
        .frame(minHeight: 120)
        .accessibilityIdentifier("paletteToPDF.input")

      Text("Separate hex colours with spaces or new lines.")
        .font(.footnote)
        .foregroundStyle(.secondary)
    }
  }

  private var previewSection: some View {
    GlassSection("Preview") {
      if model.colors.isEmpty {
        ErrorText(message: "Enter at least one valid hex colour.")
      } else {
        LazyVGrid(columns: [GridItem(.adaptive(minimum: 84), spacing: 12)], spacing: 12) {
          ForEach(model.colors.indices, id: \.self) { index in
            ColorSwatch(color: model.colors[index])
          }
        }
      }
    }
  }

  private var exportSection: some View {
    GlassSection("Export") {
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("palette.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("palette.redo")
      }
      ParameterSlider(
        title: "Columns", value: $model.columns, onEditingEnded: model.commitParameters,
        range: 1...8)

      Button("Make PDF") {
        model.commitParameters()
        model.makePDF()
      }
      .buttonStyle(.glassProminent)
      .disabled(model.colors.isEmpty)
      .accessibilityIdentifier("paletteToPDF.make")

      if let pdf = model.pdf {
        ShareFileButton(title: "Share PDF", data: pdf, fileName: model.pdfFileName)
          .accessibilityIdentifier("paletteToPDF.share")
      }

      if model.renderFailed {
        ErrorText(message: "Could not render the PDF.")
      }
    }
  }
}
