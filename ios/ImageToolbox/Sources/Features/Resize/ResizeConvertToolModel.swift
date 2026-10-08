import CoreGraphics
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class ResizeConvertToolModel {

  /// Settings the resize uses, one undo step per apply. The first apply sets the starting point,
  /// so undo returns to an earlier apply.
  struct Parameters: Equatable, Sendable {
    var width: Int
    var height: Int
    var keepAspectRatio: Bool
    var mode: ResizeMode
    var format: OutputFormat
    var quality: Double
  }

  private(set) var history: UndoHistory<Parameters>?

  var canUndo: Bool { history?.canUndo ?? false }
  var canRedo: Bool { history?.canRedo ?? false }

  func commitParameters() {
    if history == nil {
      history = UndoHistory(currentParameters)
    } else {
      history?.record(currentParameters)
    }
  }

  func undo() {
    guard history?.undo() == true, let parameters = history?.current else { return }
    restoreParameters(parameters)
  }

  func redo() {
    guard history?.redo() == true, let parameters = history?.current else { return }
    restoreParameters(parameters)
  }

  private var currentParameters: Parameters {
    Parameters(
      width: width, height: height, keepAspectRatio: keepAspectRatio, mode: mode,
      format: format, quality: quality)
  }

  private func restoreParameters(_ parameters: Parameters) {
    width = parameters.width
    height = parameters.height
    keepAspectRatio = parameters.keepAspectRatio
    mode = parameters.mode
    format = parameters.format
    quality = parameters.quality
  }
  static let maximumSide = 8000

  private(set) var source: LoadedImage?
  private(set) var width = 0
  private(set) var height = 0
  private(set) var keepAspectRatio = true
  var mode = ResizeMode.exact
  var format = OutputFormat.png
  var quality = 0.9
  private(set) var result: EncodedPicture?
  private(set) var failure: LocalizedStringKey?

  func load(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    source = loaded
    width = loaded?.image.width ?? 0
    height = loaded?.image.height ?? 0
    result = nil
    failure = loaded == nil ? "Could not read the image." : nil
  }

  func setWidth(_ value: Int) {
    width = Self.clamped(value)
    guard keepAspectRatio, let ratio = aspectRatio else { return }
    height = Self.clamped(Int((Double(width) / ratio).rounded()))
  }

  func setHeight(_ value: Int) {
    height = Self.clamped(value)
    guard keepAspectRatio, let ratio = aspectRatio else { return }
    width = Self.clamped(Int((Double(height) * ratio).rounded()))
  }

  func setKeepAspectRatio(_ enabled: Bool) {
    keepAspectRatio = enabled
    if enabled, source != nil { setWidth(width) }
  }

  func apply(encoder: any ImageEncoding) async {
    guard let source else { return }
    let target = CGSize(width: width, height: height)
    let mode = self.mode
    let format = self.format
    let quality = self.quality
    let keepMetadata = AppSettings.load(from: .standard).keepMetadata
    let picture = await Task.detached(priority: .userInitiated) { () -> EncodedPicture? in
      guard let resized = ImageResize.resize(source.image, to: target, mode: mode),
        let data = encoder.encode(
          resized, as: format, quality: quality,
          metadataSource: keepMetadata ? source.data : nil)
      else { return nil }
      let fileName = ExportNaming.fileName(
        stem: "resized", fileExtension: format.fileExtension, data: data)
      return EncodedPicture(image: resized, data: data, fileName: fileName)
    }.value
    result = picture
    failure = picture == nil ? "Could not resize the image." : nil
  }

  /// Width divided by height of the source image, or nil before an image is picked.
  private var aspectRatio: Double? {
    guard let image = source?.image, image.height > 0 else { return nil }
    return Double(image.width) / Double(image.height)
  }

  private static func clamped(_ value: Int) -> Int {
    min(max(value, 1), maximumSide)
  }
}
