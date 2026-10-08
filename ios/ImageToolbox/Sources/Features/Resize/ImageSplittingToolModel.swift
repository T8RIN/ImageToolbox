import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class ImageSplittingToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var axis: SliceAxis
    var parts: Int
  }

  private(set) var history = UndoHistory(Parameters(axis: .rows, parts: 2))

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
    Parameters(axis: axis, parts: parts)
  }

  private func restoreParameters(_ parameters: Parameters) {
    axis = parameters.axis
    parts = parameters.parts
  }
  private(set) var source: LoadedImage?
  var axis = SliceAxis.rows
  var parts = 2
  private(set) var pieces: [EncodedPicture] = []
  private(set) var failure: LocalizedStringKey?

  func load(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    source = loaded
    pieces = []
    failure = loaded == nil ? "Could not read the image." : nil
  }

  func apply() async {
    guard let source else { return }
    let parts = self.parts
    let axis = self.axis
    let sourceData = source.data
    let encoded = await Task.detached(priority: .userInitiated) { () -> [EncodedPicture]? in
      let images = ImageSlicer.split(source.image, parts: parts, axis: axis)
      guard !images.isEmpty else { return nil }
      var pieces: [EncodedPicture] = []
      for (index, image) in images.enumerated() {
        guard let data = ExportEncoding.encode(image, as: .png, source: sourceData) else {
          return nil
        }
        let name = ExportNaming.fileName(
          stem: "part-\(index + 1)", fileExtension: "png", data: data)
        pieces.append(EncodedPicture(image: image, data: data, fileName: name))
      }
      return pieces
    }.value
    pieces = encoded ?? []
    failure = encoded == nil ? "Could not split the image. Try fewer parts." : nil
  }
}
