import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

/// One step of the transform chain. The chain is replayed on the original picture.
enum RotateFlipStep: Equatable, Sendable {
  case rotateLeft
  case rotateRight
  case flipHorizontal
  case flipVertical

  func apply(to image: CGImage) -> CGImage? {
    switch self {
    case .rotateLeft: ImageGeometry.rotate(image, quarterTurnsClockwise: -1)
    case .rotateRight: ImageGeometry.rotate(image, quarterTurnsClockwise: 1)
    case .flipHorizontal: ImageGeometry.flip(image, horizontal: true)
    case .flipVertical: ImageGeometry.flip(image, horizontal: false)
    }
  }
}

@MainActor
@Observable
final class RotateFlipToolModel {
  var selection: PhotosPickerItem? {
    didSet {
      guard let selection else { return }
      loadTask?.cancel()
      loadTask = Task { await load(selection) }
    }
  }

  /// Output container. Changing it makes the export stale until `refreshExport()` runs.
  var format: OutputFormat = .png {
    didSet { changed() }
  }

  /// Lossy quality in 0.1...1. Only JPEG and HEIC use it.
  var quality = 0.9 {
    didSet { changed() }
  }

  private(set) var current: CGImage?
  private(set) var exportData: Data?
  private(set) var exportFileName = "transformed.png"
  private(set) var errorMessage: LocalizedStringKey?
  /// Bumped on every change. The view watches it and calls `refreshExport()` after a pause.
  private(set) var revision = 0
  /// Every transform so far, with undo and redo over it. Each button press is one step.
  private(set) var history = UndoHistory<[RotateFlipStep]>([])

  @ObservationIgnored private var original: LoadedImage?
  @ObservationIgnored private var loadTask: Task<Void, Never>?

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func rotateLeft() {
    perform(.rotateLeft)
  }

  func rotateRight() {
    perform(.rotateRight)
  }

  func flipHorizontal() {
    perform(.flipHorizontal)
  }

  func flipVertical() {
    perform(.flipVertical)
  }

  /// Back to the picture as it was picked. This is one step, so it can be undone too.
  func reset() {
    guard history.record([]) else { return }
    replay()
  }

  func undo() {
    guard history.undo() else { return }
    replay()
  }

  func redo() {
    guard history.redo() else { return }
    replay()
  }

  /// Encodes the working image with the current format, quality and settings.
  func refreshExport(encoder: any ImageEncoding) {
    guard let current else { return }
    let settings = AppSettings.load(from: .standard)
    let metadataSource = settings.keepMetadata ? original?.data : nil
    guard
      let data = encoder.encode(
        current, as: format, quality: quality, metadataSource: metadataSource)
    else {
      exportData = nil
      errorMessage = "The image could not be encoded as \(format.fileExtension.uppercased())."
      return
    }
    errorMessage = nil
    exportFileName = ExportNaming.fileName(
      stem: "transformed", fileExtension: format.fileExtension, data: data)
    exportData = data
  }

  /// Applies one step to the picture on screen. The step is recorded only if it succeeds.
  private func perform(_ step: RotateFlipStep) {
    guard let latest = current else { return }
    guard let next = step.apply(to: latest) else {
      errorMessage = "The image could not be transformed."
      return
    }
    errorMessage = nil
    history.record(history.current + [step])
    current = next
    changed()
  }

  /// Rebuilds the picture from the original and the steps in `history`, after undo or redo.
  private func replay() {
    guard let original else { return }
    var image: CGImage? = original.image
    for step in history.current {
      image = image.flatMap { step.apply(to: $0) }
    }
    guard let image else {
      errorMessage = "The image could not be transformed."
      return
    }
    errorMessage = nil
    current = image
    changed()
  }

  private func load(_ item: PhotosPickerItem) async {
    let result = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    guard let picked = result else {
      errorMessage = "The picked file could not be read as an image."
      return
    }
    original = picked
    current = picked.image
    history = UndoHistory([])
    errorMessage = nil
    changed()
  }

  private func changed() {
    exportData = nil
    revision += 1
  }
}
