import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class DuplicateFinderToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var threshold: Double
  }

  private(set) var history = UndoHistory(Parameters(threshold: 6))

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
    Parameters(threshold: threshold)
  }

  private func restoreParameters(_ parameters: Parameters) {
    threshold = parameters.threshold
  }
  struct ScannedPicture: Identifiable, Sendable {
    /// Position in the picked list, starting at 1.
    let id: Int
    let key: String
    let hash: UInt64?
    let thumbnail: CGImage
  }

  var threshold = 6.0

  private(set) var pictures: [ScannedPicture] = []
  private(set) var failedCount = 0
  private(set) var isScanning = false

  /// Pictures with byte-identical files, in the order of their first picture.
  var exactGroups: [[ScannedPicture]] {
    Dictionary(grouping: pictures, by: \.key)
      .values
      .filter { $0.count > 1 }
      .map { $0.sorted { $0.id < $1.id } }
      .sorted { $0[0].id < $1[0].id }
  }

  /// Pictures whose perceptual hashes are within the threshold. Exact duplicates also appear here.
  var similarGroups: [[ScannedPicture]] {
    let hashed = pictures.compactMap { picture in
      picture.hash.map { (id: picture.id, hash: $0) }
    }
    let byID = Dictionary(uniqueKeysWithValues: pictures.map { ($0.id, $0) })
    return DuplicateFinder.groups(hashed, threshold: Int(threshold))
      .map { ids in ids.compactMap { byID[$0] }.sorted { $0.id < $1.id } }
      .sorted { $0[0].id < $1[0].id }
  }

  func scan(_ items: [PhotosPickerItem]) async {
    isScanning = true
    var scanned: [ScannedPicture] = []
    var failed = 0
    for (offset, item) in items.enumerated() {
      guard let loaded = await PhotoLoader.load(item) else {
        failed += 1
        continue
      }
      let number = offset + 1
      let picture = await Task.detached(priority: .userInitiated) { () -> ScannedPicture? in
        guard
          let thumbnail = ImageResize.resize(
            loaded.image, to: CGSize(width: 180, height: 180), mode: .fill)
        else { return nil }
        return ScannedPicture(
          id: number,
          key: DuplicateFinder.exactKey(loaded.data),
          hash: DuplicateFinder.perceptualHash(loaded.image),
          thumbnail: thumbnail)
      }.value
      if let picture {
        scanned.append(picture)
      } else {
        failed += 1
      }
    }
    pictures = scanned
    failedCount = failed
    isScanning = false
  }
}
