import ImageToolboxKit
import PhotosUI
import SwiftUI

struct CropToolView: View {
  @State private var model = CropToolModel()
  @State private var pickedItem: PhotosPickerItem?
  @State private var maskItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "crop") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: $pickedItem)
          .accessibilityIdentifier("crop.pick")
        if let source = model.source {
          PictureView(image: source.image, maxHeight: 240)
        }
      }

      if model.source != nil {
        GlassSection("Area") {
          Picker("Aspect", selection: $model.aspect) {
            Text("Free").tag(CropAspect.free)
            Text("Square (1:1)").tag(CropAspect.square)
            Text("4:3").tag(CropAspect.fourByThree)
            Text("16:9").tag(CropAspect.sixteenByNine)
          }
          .pickerStyle(.menu)
          .onChange(of: model.aspect) { _, _ in model.commitSelection() }
          ParameterSlider(
            title: "X", value: xBinding, onEditingEnded: model.commitSelection, range: 0...1,
            step: 0.01, format: "%.2f")
          ParameterSlider(
            title: "Y", value: yBinding, onEditingEnded: model.commitSelection, range: 0...1,
            step: 0.01, format: "%.2f")
          ParameterSlider(
            title: "Width", value: widthBinding, onEditingEnded: model.commitSelection,
            range: 0...1, step: 0.01, format: "%.2f")
          ParameterSlider(
            title: "Height", value: heightBinding, onEditingEnded: model.commitSelection,
            range: 0...1, step: 0.01, format: "%.2f"
          )
          .disabled(model.aspect.ratio != nil)
        }

        GlassSection("Shape") {
          Picker("Shape", selection: $model.shape) {
            ForEach(CropShape.allCases, id: \.self) { shape in
              Text(shapeName(shape)).tag(shape)
            }
          }
          .pickerStyle(.menu)
          .disabled(model.mask != nil)
          .onChange(of: model.shape) { _, _ in model.commitSelection() }

          SinglePhotoButton(title: "Choose mask picture", selection: $maskItem)
            .accessibilityIdentifier("crop.mask")
          if model.mask != nil {
            Button("Remove mask") { model.removeMask() }
              .buttonStyle(.glass)
              .accessibilityIdentifier("crop.removeMask")
          }
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("crop.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("crop.redo")
          }
          Button("Crop") { Task { await model.apply() } }
            .buttonStyle(.glassProminent)
            .accessibilityIdentifier("crop.apply")
        }
      }

      if let result = model.result {
        GlassSection("Result") {
          PictureView(image: result.image)
          ShareFileButton(title: "Share", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("crop.share")
          SaveFileButton(title: "Save to Files", data: result.data, fileName: result.fileName)
            .accessibilityIdentifier("crop.save")
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
    .task(id: maskItem) {
      guard let maskItem else { return }
      await model.loadMask(maskItem)
    }
  }

  private var xBinding: Binding<Double> {
    Binding(get: { model.x }, set: { model.setX($0) })
  }

  private var yBinding: Binding<Double> {
    Binding(get: { model.y }, set: { model.setY($0) })
  }

  private var widthBinding: Binding<Double> {
    Binding(get: { model.width }, set: { model.setWidth($0) })
  }

  private var heightBinding: Binding<Double> {
    Binding(get: { model.height }, set: { model.setHeight($0) })
  }

  private func shapeName(_ shape: CropShape) -> LocalizedStringKey {
    switch shape {
    case .rectangle: "Rectangle"
    case .roundedRectangle: "Rounded rectangle"
    case .cutCorners: "Cut corners"
    case .oval: "Oval"
    case .pill: "Pill"
    case .squircle: "Squircle"
    case .triangle: "Triangle"
    case .roundedTriangle: "Rounded triangle"
    case .diamond: "Diamond"
    case .pentagon: "Pentagon"
    case .roundedPentagon: "Rounded pentagon"
    case .hexagon: "Hexagon"
    case .octagon: "Octagon"
    case .chevron: "Chevron"
    case .arrow: "Arrow"
    case .bookmark: "Bookmark"
    case .shield: "Shield"
    case .droplet: "Droplet"
    case .egg: "Egg"
    case .mapPin: "Map pin"
    case .heart: "Heart"
    case .star: "Star"
    case .kotlinLogo: "Kotlin logo"
    case .burger: "Burger"
    case .enhancedHeart: "Enhanced heart"
    case .sharpStar: "Sharp star"
    case .materialStar: "Material star"
    case .clover: "Clover"
    case .shuriken: "Shuriken"
    case .explosion: "Explosion"
    }
  }
}
