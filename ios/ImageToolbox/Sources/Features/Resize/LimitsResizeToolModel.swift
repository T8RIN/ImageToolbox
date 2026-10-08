import CoreGraphics
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class LimitsResizeToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var maxWidth: Double
    var maxHeight: Double
  }

  private(set) var history = UndoHistory(Parameters(maxWidth: 1024, maxHeight: 1024))

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func commitParameters() {
    history.record(currentParameters)
  }

  func undo() {
    guard history.undo() else { return }
    restoreParameters(history.current)
  }

  func redo() {
    guard history.redo() else { return }
    restoreParameters(history.current)
  }

  private var currentParameters: Parameters {
    Parameters(maxWidth: maxWidth, maxHeight: maxHeight)
  }

  private func restoreParameters(_ parameters: Parameters) {
    maxWidth = parameters.maxWidth
    maxHeight = parameters.maxHeight
  }
  private(set) var source: LoadedImage?
  var maxWidth = 1024.0
  var maxHeight = 1024.0
  private(set) var result: EncodedPicture?
  private(set) var failure: LocalizedStringKey?

  /// Size after applying the limits. Computed without resampling, so it updates live.
  var targetSize: CGSize? {
    guard let image = source?.image else { return nil }
    return ImageGeometry.fitSize(
      width: image.width,
      height: image.height,
      within: CGSize(width: maxWidth, height: maxHeight),
      allowUpscale: false)
  }

  func load(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    source = loaded
    result = nil
    failure = loaded == nil ? "Could not read the image." : nil
  }

  func apply() async {
    guard let source, let target = targetSize else { return }
    let width = Int(target.width)
    let height = Int(target.height)
    let sourceData = source.data
    let picture = await Task.detached(priority: .userInitiated) { () -> EncodedPicture? in
      guard let resized = ImageGeometry.resize(source.image, width: width, height: height),
        let data = ExportEncoding.encode(resized, as: .png, source: sourceData)
      else { return nil }
      let fileName = ExportNaming.fileName(stem: "limited", fileExtension: "png", data: data)
      return EncodedPicture(image: resized, data: data, fileName: fileName)
    }.value
    result = picture
    failure = picture == nil ? "Could not resize the image." : nil
  }
}
