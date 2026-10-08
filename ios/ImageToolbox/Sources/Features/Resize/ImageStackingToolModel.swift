import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class ImageStackingToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var mode: StackMode
  }

  private(set) var history = UndoHistory(Parameters(mode: .average))

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
    Parameters(mode: mode)
  }

  private func restoreParameters(_ parameters: Parameters) {
    mode = parameters.mode
  }
  private(set) var images: [LoadedImage] = []
  var mode = StackMode.average
  private(set) var result: EncodedPicture?
  private(set) var failure: LocalizedStringKey?

  func load(_ items: [PhotosPickerItem]) async {
    let loaded = await PhotoLoader.load(items)
    guard !Task.isCancelled else { return }
    images = loaded
    result = nil
    failure = loaded.count < items.count ? "Some images could not be read." : nil
  }

  func apply() async {
    guard images.count >= 2 else {
      result = nil
      failure = "Choose at least two images."
      return
    }
    let inputs = images.map(\.image)
    let mode = self.mode
    let picture = await Task.detached(priority: .userInitiated) { () -> EncodedPicture? in
      guard let stacked = ImageStacker.stack(inputs, mode: mode),
        let data = ImageCodec.encode(stacked, as: .png)
      else { return nil }
      let fileName = ExportNaming.fileName(stem: "stacked", fileExtension: "png", data: data)
      return EncodedPicture(image: stacked, data: data, fileName: fileName)
    }.value
    result = picture
    failure = picture == nil ? "Could not stack the images." : nil
  }
}
