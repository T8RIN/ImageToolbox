import PhotosUI
import SwiftUI

struct WatermarkingToolView: View {
  @State private var model = WatermarkingToolModel()
  @State private var baseItem: PhotosPickerItem?
  @State private var overlayItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "watermarking") {
      GlassSection("Picture") {
        SinglePhotoButton(title: "Choose picture", selection: $baseItem)
          .accessibilityIdentifier("watermarking.pick")
        if model.baseFailed {
          ErrorText(message: "Could not read the picture.")
        }
      }

      textSection
      overlaySection

      GlassSection("Timestamp") {
        Toggle("Replace text with the current date and time", isOn: $model.useTimestamp)
      }

      resultSection
      hideSection
    }
    .onChange(of: baseItem) { _, item in
      guard let item else { return }
      Task { await model.setBase(item) }
    }
    .onChange(of: overlayItem) { _, item in
      guard let item else { return }
      Task { await model.setOverlay(item) }
    }
  }

  private var textSection: some View {
    GlassSection("Text") {
      TextField("Text", text: $model.text)
        .accessibilityIdentifier("watermarking.text")
      ParameterSlider(title: "Font size", value: $model.fontSize, range: 12...200)
      ParameterSlider(
        title: "Opacity", value: $model.opacity, range: 0...1, step: 0.01, format: "%.2f")
      ParameterSlider(
        title: "Rotation", value: $model.rotation, range: -90...90, format: "%.0f°")
      Toggle("Repeat across the picture", isOn: $model.repeatsText)
      ColorPicker("Color", selection: $model.color)
    }
  }

  private var overlaySection: some View {
    GlassSection("Image") {
      SinglePhotoButton(title: "Choose overlay", selection: $overlayItem)
        .accessibilityIdentifier("watermarking.overlay")
      if model.overlay != nil {
        Button("Remove overlay") {
          overlayItem = nil
          model.removeOverlay()
        }
        .buttonStyle(.glass)
        .accessibilityIdentifier("watermarking.removeOverlay")
      }
      if model.overlayFailed {
        ErrorText(message: "Could not read the overlay picture.")
      }
    }
  }

  private var resultSection: some View {
    GlassSection("Result") {
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("watermark.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("watermark.redo")
      }
      Button("Apply watermark") {
        Task { await model.apply() }
      }
      .buttonStyle(.glassProminent)
      .disabled(model.base == nil || model.isApplying)
      .accessibilityIdentifier("watermarking.apply")

      if model.isApplying {
        ProgressView()
      }
      if let result = model.result {
        PictureView(image: result.image)
        ShareFileButton(title: "Share", data: result.data, fileName: model.watermarkedFileName)
          .accessibilityIdentifier("watermarking.share")
      }
      if model.applyFailed {
        ErrorText(message: "Could not apply the watermark.")
      }
    }
  }

  private var hideSection: some View {
    GlassSection("Hide text") {
      TextEditor(text: $model.secret)
        .frame(minHeight: 100)
        .accessibilityIdentifier("watermarking.secret")

      Button("Hide in picture") {
        Task { await model.hideSecret() }
      }
      .buttonStyle(.glassProminent)
      .disabled(model.base == nil || model.secret.isEmpty)
      .accessibilityIdentifier("watermarking.hide")

      if let hiddenData = model.hiddenData {
        ShareFileButton(title: "Share", data: hiddenData, fileName: model.hiddenFileName)
          .accessibilityIdentifier("watermarking.shareHidden")
      }
      if model.hideFailed {
        ErrorText(message: "Could not hide the text. The picture may be too small.")
      }

      Button("Read hidden text") {
        Task { await model.readHiddenText() }
      }
      .buttonStyle(.glass)
      .disabled(model.base == nil)
      .accessibilityIdentifier("watermarking.read")

      if let hiddenText = model.hiddenText {
        Text(hiddenText)
          .textSelection(.enabled)
      } else if model.readFailed {
        ErrorText(message: "No hidden text found")
      }
    }
  }
}
