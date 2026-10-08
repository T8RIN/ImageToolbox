import CoreGraphics
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

/// Width-to-height ratios offered for the crop selection.
enum CropAspect: CaseIterable {
  case free
  case square
  case fourByThree
  case sixteenByNine

  /// Width divided by height in pixels, or nil when the ratio is free.
  var ratio: Double? {
    switch self {
    case .free: nil
    case .square: 1
    case .fourByThree: 4.0 / 3.0
    case .sixteenByNine: 16.0 / 9.0
    }
  }
}

/// The selection is kept in fractions of the image, so each value stays in 0...1.
@MainActor
@Observable
final class CropToolModel {
  private static let minimumSide = 0.01

  private(set) var source: LoadedImage?
  var aspect = CropAspect.free {
    didSet {
      if aspect != oldValue { applyAspect() }
    }
  }
  private(set) var x = 0.0
  private(set) var y = 0.0
  private(set) var width = 1.0
  private(set) var height = 1.0
  var shape = CropShape.rectangle
  private(set) var result: EncodedPicture?
  private(set) var failure: LocalizedStringKey?
  /// Optional mask picture. When set, its luminance decides what stays, instead of the shape.
  private(set) var mask: CGImage?

  /// The selection and its choices as one value, so undo and redo can restore them together.
  struct Selection: Equatable, Sendable {
    var x: Double
    var y: Double
    var width: Double
    var height: Double
    var aspect: CropAspect
    var shape: CropShape
  }

  private(set) var history = UndoHistory(
    Selection(x: 0, y: 0, width: 1, height: 1, aspect: .free, shape: .rectangle))

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func load(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    source = loaded
    result = nil
    failure = loaded == nil ? "Could not read the image." : nil
    resetSelection()
  }

  func loadMask(_ item: PhotosPickerItem) async {
    mask = await PhotoLoader.load(item)?.image
    if mask == nil { failure = "Could not read the mask picture." }
  }

  func removeMask() {
    mask = nil
  }

  func setX(_ value: Double) {
    x = min(max(value, 0), 1 - width)
  }

  func setY(_ value: Double) {
    y = min(max(value, 0), 1 - height)
  }

  func setWidth(_ value: Double) {
    width = min(max(value, Self.minimumSide), 1 - x)
    if let ratio = aspect.ratio {
      height = width * imageWidth / (ratio * imageHeight)
    }
    keepInsideImage()
  }

  /// Only used with a free ratio. With a fixed ratio the height follows the width.
  func setHeight(_ value: Double) {
    guard aspect.ratio == nil else { return }
    height = min(max(value, Self.minimumSide), 1 - y)
  }

  func apply() async {
    guard let source else { return }
    let rect = CGRect(
      x: x * imageWidth,
      y: y * imageHeight,
      width: width * imageWidth,
      height: height * imageHeight)
    let shape = self.shape
    let mask = self.mask
    let sourceData = source.data
    let picture = await Task.detached(priority: .userInitiated) { () -> EncodedPicture? in
      guard let cropped = ImageGeometry.crop(source.image, to: rect) else { return nil }
      let output: CGImage?
      if let mask {
        output = ShapeMask.apply(cropped, mask: mask)
      } else {
        output = shape == .rectangle ? cropped : ShapeMask.apply(cropped, shape: shape)
      }
      guard let output,
        let data = ExportEncoding.encode(output, as: .png, source: sourceData)
      else { return nil }
      let fileName = ExportNaming.fileName(stem: "cropped", fileExtension: "png", data: data)
      return EncodedPicture(image: output, data: data, fileName: fileName)
    }.value
    result = picture
    failure = picture == nil ? "Could not crop the image." : nil
  }

  private var imageWidth: Double { Double(source?.image.width ?? 1) }

  private var imageHeight: Double { Double(source?.image.height ?? 1) }

  private func resetSelection() {
    x = 0
    y = 0
    width = 1
    height = 1
    applyAspect()
    history = UndoHistory(currentSelection)
  }

  /// Records the current selection as one undo step. Called when a slider drag or a picker change ends.
  func commitSelection() {
    history.record(currentSelection)
  }

  func undo() {
    guard history.undo() else { return }
    restore(history.current)
  }

  func redo() {
    guard history.redo() else { return }
    restore(history.current)
  }

  private var currentSelection: Selection {
    Selection(x: x, y: y, width: width, height: height, aspect: aspect, shape: shape)
  }

  private func restore(_ selection: Selection) {
    aspect = selection.aspect
    x = selection.x
    y = selection.y
    width = selection.width
    height = selection.height
    shape = selection.shape
  }

  /// Largest centred rectangle with the chosen ratio. A free ratio keeps the current selection.
  private func applyAspect() {
    guard let ratio = aspect.ratio, source != nil else { return }
    width = min(1, ratio * imageHeight / imageWidth)
    height = width * imageWidth / (ratio * imageHeight)
    x = (1 - width) / 2
    y = (1 - height) / 2
  }

  /// Shrinks the selection, keeping its ratio, until it lies inside the image.
  private func keepInsideImage() {
    if let ratio = aspect.ratio {
      if y + height > 1 {
        height = 1 - y
        width = height * ratio * imageHeight / imageWidth
      }
      if x + width > 1 {
        width = 1 - x
        height = width * imageWidth / (ratio * imageHeight)
      }
    }
    x = min(x, 1 - width)
    y = min(y, 1 - height)
  }
}
