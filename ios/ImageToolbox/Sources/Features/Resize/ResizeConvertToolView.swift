import ImageToolboxKit
import PhotosUI
import SwiftUI

struct ResizeConvertToolView: View {
  @State private var model = ResizeConvertToolModel()
  @State private var pickedItem: PhotosPickerItem?
  @Environment(\.services) private var services

  var body: some View {
    ToolScaffold(identifier: "resizeConvert") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: $pickedItem)
          .accessibilityIdentifier("resizeConvert.pick")
        if let source = model.source {
          PictureView(image: source.image, maxHeight: 240)
        }
      }

      if model.source != nil {
        GlassSection("Size") {
          HStack {
            TextField("Width", value: widthBinding, format: .number)
            Text("×")
            TextField("Height", value: heightBinding, format: .number)
          }
          .textFieldStyle(.roundedBorder)
          .keyboardType(.numberPad)
          .monospacedDigit()
          Toggle("Keep aspect ratio", isOn: keepAspectBinding)
          Picker("Mode", selection: $model.mode) {
            Text("Stretch").tag(ResizeMode.exact)
            Text("Fit").tag(ResizeMode.fit)
            Text("Fill").tag(ResizeMode.fill)
          }
          .pickerStyle(.segmented)
        }

        GlassSection("Output") {
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("resize.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("resize.redo")
          }
          OutputFormatPicker(selection: $model.format)
          if model.format.isLossy {
            ParameterSlider(
              title: "Quality", value: $model.quality, range: 0.1...1, step: 0.05,
              format: "%.2f")
          }
          Button("Resize") {
            model.commitParameters()
            Task { await model.apply(encoder: services.encoder) }
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("resizeConvert.apply")
        }
      }

      if let result = model.result {
        GlassSection("Result") {
          Text("\(result.image.width) × \(result.image.height) px")
            .monospacedDigit()
            .foregroundStyle(.secondary)
          PictureView(image: result.image)
          ShareFileButton(title: "Share", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("resizeConvert.share")
          SaveFileButton(title: "Save to Files", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("resizeConvert.save")
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

  private var widthBinding: Binding<Int> {
    Binding(get: { model.width }, set: { model.setWidth($0) })
  }

  private var heightBinding: Binding<Int> {
    Binding(get: { model.height }, set: { model.setHeight($0) })
  }

  private var keepAspectBinding: Binding<Bool> {
    Binding(get: { model.keepAspectRatio }, set: { model.setKeepAspectRatio($0) })
  }
}
