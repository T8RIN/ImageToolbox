import ImageToolboxKit
import PhotosUI
import SwiftUI

struct ColorToolsView: View {
  @State private var model = ColorToolsModel()
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "colorTools") {
      baseSection
      harmonySection
      mixSection
      paletteSection
    }
    .onChange(of: pickedItem) { _, item in
      guard let item else { return }
      Task {
        await model.loadPicture(item)
        pickedItem = nil
      }
    }
  }

  private var baseSection: some View {
    GlassSection("Base colour") {
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("colorTools.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("colorTools.redo")
      }
      ColorPicker("Colour", selection: $model.baseColor, supportsOpacity: false)
        .onChange(of: model.baseColor) { _, _ in model.commitParameters() }

      RoundedRectangle(cornerRadius: 16)
        .fill(model.baseColor)
        .frame(height: 120)

      LabeledContent("Hex", value: model.base.hexString)
      LabeledContent("HSL", value: model.hslText)
      LabeledContent("HSV", value: model.hsvText)
      LabeledContent("CMYK", value: model.cmykText)
    }
  }

  private var harmonySection: some View {
    GlassSection("Harmonies") {
      harmonyRow("Complementary", [model.base.complementary])
      harmonyRow("Analogous", model.base.analogous)
      harmonyRow("Triadic", model.base.triadic)
      harmonyRow("Split complementary", model.base.splitComplementary)
      harmonyRow("Tetradic", model.base.tetradic)
    }
  }

  private var mixSection: some View {
    GlassSection("Mix") {
      ParameterSlider(
        title: "Mix with white",
        value: $model.mixFraction, onEditingEnded: model.commitParameters,
        range: 0...1,
        step: 0.01,
        format: "%.2f")
      ColorSwatch(color: model.mixed)
    }
  }

  private var paletteSection: some View {
    GlassSection("Palette from picture") {
      SinglePhotoButton(title: "Choose picture", selection: $pickedItem)
        .accessibilityIdentifier("colorTools.choosePicture")

      if let picture = model.picture {
        PictureView(image: picture.image, maxHeight: 160)
      }

      if model.pictureFailed {
        ErrorText(message: "Could not load the picture.")
      }

      ParameterSlider(
        title: "Colours", value: $model.paletteCount, onEditingEnded: model.commitParameters,
        range: 2...12)

      Button("Extract") {
        model.commitParameters()
        Task { await model.extractPalette() }
      }
      .buttonStyle(.glassProminent)
      .disabled(model.picture == nil || model.isExtracting)
      .accessibilityIdentifier("colorTools.extract")

      if model.isExtracting {
        ProgressView()
      }

      if model.paletteFailed {
        ErrorText(message: "No colours could be found in this picture.")
      }

      if !model.palette.isEmpty {
        LazyVGrid(columns: [GridItem(.adaptive(minimum: 84), spacing: 12)], spacing: 12) {
          ForEach(model.palette.indices, id: \.self) { index in
            ColorSwatch(color: model.palette[index])
          }
        }
      }
    }
  }

  private func harmonyRow(_ title: LocalizedStringKey, _ colors: [RGBA]) -> some View {
    VStack(alignment: .leading, spacing: 8) {
      Text(title)
        .font(.subheadline)
        .foregroundStyle(.secondary)
      HStack(alignment: .top, spacing: 8) {
        ForEach(colors.indices, id: \.self) { index in
          ColorSwatch(color: colors[index])
        }
      }
    }
  }
}
