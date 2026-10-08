import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class WeightResizeToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var targetKilobytes: Double
    var format: OutputFormat
  }

  private(set) var history = UndoHistory(Parameters(targetKilobytes: 500, format: .jpeg))

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
    Parameters(targetKilobytes: targetKilobytes, format: format)
  }

  private func restoreParameters(_ parameters: Parameters) {
    targetKilobytes = parameters.targetKilobytes
    format = parameters.format
  }
  /// A fitted encoding with the file name its share button uses.
  struct Output {
    let fitted: FittedImage
    let fileName: String

    var kilobytes: Double { Double(fitted.data.count) / 1024 }
  }

  private(set) var source: LoadedImage?
  var targetKilobytes = 500.0
  var format = OutputFormat.jpeg
  private(set) var result: Output?
  private(set) var failure: LocalizedStringKey?

  func load(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    source = loaded
    result = nil
    failure = loaded == nil ? "Could not read the image." : nil
  }

  func fit() async {
    guard let source else { return }
    let format = self.format
    let maxBytes = Int((targetKilobytes * 1024).rounded())
    let sourceData = source.data
    let keepMetadata = AppSettings.load(from: .standard).keepMetadata
    let fitted = await Task.detached(priority: .userInitiated) {
      SizeFitter.fit(
        source.image, format: format, maxBytes: maxBytes,
        metadataSource: keepMetadata ? sourceData : nil)
    }.value
    guard let fitted else {
      result = nil
      failure = "Could not fit the image into this size."
      return
    }
    let fileName = ExportNaming.fileName(
      stem: "fitted", fileExtension: format.fileExtension, data: fitted.data)
    result = Output(fitted: fitted, fileName: fileName)
    failure = nil
  }
}
