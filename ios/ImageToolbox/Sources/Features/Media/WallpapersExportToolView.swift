import ImageToolboxKit
import SwiftUI

struct WallpapersExportToolView: View {
  @State private var model = WallpapersExportToolModel()

  var body: some View {
    ToolScaffold(identifier: "wallpapersExport") {
      GlassSection("Photo") {
        SinglePhotoButton(title: "Choose photo", selection: $model.pickedItem)
          .accessibilityIdentifier("wallpapersExport.choose")

        if let message = model.errorMessage {
          ErrorText(message: message)
        }
      }

      GlassSection("Export") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("wallpaper.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("wallpaper.redo")
        }
        Picker("Screen size", selection: $model.preset) {
          ForEach(WallpaperPreset.all) { preset in
            Text("\(preset.name), \(preset.width) x \(preset.height)").tag(preset)
          }
        }
        .pickerStyle(.menu)

        OutputFormatPicker(selection: $model.format)

        if model.format.isLossy {
          ParameterSlider(
            title: "Quality",
            value: $model.quality, onEditingEnded: model.commitParameters,
            range: 0.1...1,
            step: 0.05,
            format: "%.2f")
        }

        Button("Export") {
          model.commitParameters()
          Task { await model.export() }
        }
        .buttonStyle(.glassProminent)
        .disabled(model.source == nil)
        .accessibilityIdentifier("wallpapersExport.export")
      }

      if let rendered = model.rendered {
        GlassSection("Result") {
          PictureView(image: rendered.preview)
          ShareFileButton(
            title: "Share wallpaper", data: rendered.data, fileName: rendered.fileName
          )
          .accessibilityIdentifier("wallpapersExport.share")
        }
      }
    }
    .onChange(of: model.pickedItem) { _, item in
      guard let item else { return }
      Task { await model.choosePhoto(item) }
    }
  }
}
