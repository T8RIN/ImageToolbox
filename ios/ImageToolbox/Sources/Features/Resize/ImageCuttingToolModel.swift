import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class ImageCuttingToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var axis: SliceAxis
    var start: Double
    var end: Double
    var keepOnlyCut: Bool
  }

  private(set) var history = UndoHistory(
    Parameters(axis: .columns, start: 0.4, end: 0.6, keepOnlyCut: false))

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
    Parameters(axis: axis, start: start, end: end, keepOnlyCut: keepOnlyCut)
  }

  private func restoreParameters(_ parameters: Parameters) {
    axis = parameters.axis
    start = parameters.start
    end = parameters.end
    keepOnlyCut = parameters.keepOnlyCut
  }
  private(set) var source: LoadedImage?
  var axis = SliceAxis.columns
  private(set) var start = 0.4
  private(set) var end = 0.6
  var keepOnlyCut = false
  private(set) var result: EncodedPicture?
  private(set) var failure: LocalizedStringKey?

  func load(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    source = loaded
    result = nil
    failure = loaded == nil ? "Could not read the image." : nil
  }

  /// The band never starts after it ends.
  func setStart(_ value: Double) {
    start = min(max(value, 0), end)
  }

  /// The band never ends before it starts.
  func setEnd(_ value: Double) {
    end = max(min(value, 1), start)
  }

  func apply() async {
    guard let source else { return }
    let length = Double(axis == .rows ? source.image.height : source.image.width)
    let from = Int((start * length).rounded())
    let to = Int((end * length).rounded())
    let axis = self.axis
    let keepOnlyCut = self.keepOnlyCut
    let sourceData = source.data
    let picture = await Task.detached(priority: .userInitiated) { () -> EncodedPicture? in
      guard
        let cut = ImageSlicer.cut(
          source.image, axis: axis, from: from, to: to, keepOnlyCut: keepOnlyCut),
        let data = ExportEncoding.encode(cut, as: .png, source: sourceData)
      else { return nil }
      let fileName = ExportNaming.fileName(stem: "cut", fileExtension: "png", data: data)
      return EncodedPicture(image: cut, data: data, fileName: fileName)
    }.value
    result = picture
    failure = picture == nil ? "Could not cut the image. Check the range." : nil
  }
}
