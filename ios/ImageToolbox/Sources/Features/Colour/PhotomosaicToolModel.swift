import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class PhotomosaicToolModel {

  /// Settings that the undo history keeps. A step is recorded when a slider drag ends, a choice
  /// changes, or the operation starts.
  struct Parameters: Equatable, Sendable {
    var tileSize: Double
    var columns: Double
  }

  private(set) var history = UndoHistory(Parameters(tileSize: 16, columns: 40))

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
    Parameters(tileSize: tileSize, columns: columns)
  }

  private func restoreParameters(_ parameters: Parameters) {
    tileSize = parameters.tileSize
    columns = parameters.columns
  }
  var tileSize = 16.0
  var columns = 40.0

  private(set) var target: LoadedImage?
  private(set) var targetFailed = false
  private(set) var tiles: [LoadedImage] = []
  private(set) var tilesFailed = false
  private(set) var mosaic: LoadedImage?
  private(set) var buildFailed = false
  private(set) var mosaicFileName = "mosaic.png"
  private(set) var isBuilding = false
  @ObservationIgnored private var buildTask: Task<LoadedImage?, Never>?
  @ObservationIgnored private var cancelledByUser = false

  /// Stops the build between rows. A cancelled build keeps the previous mosaic.
  func cancelBuild() {
    cancelledByUser = true
    buildTask?.cancel()
  }

  var canBuild: Bool { target != nil && !tiles.isEmpty && !isBuilding }

  func loadTarget(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    target = loaded
    targetFailed = loaded == nil
    mosaic = nil
  }

  /// Replaces the tile set with the newly picked pictures.
  func loadTiles(_ items: [PhotosPickerItem]) async {
    tiles = await PhotoLoader.load(items)
    tilesFailed = tiles.isEmpty
    mosaic = nil
  }

  func build() async {
    guard let target, !tiles.isEmpty else { return }
    isBuilding = true
    cancelledByUser = false
    defer {
      isBuilding = false
      buildTask = nil
    }

    let sourceTarget = target
    let sourceTiles = tiles
    let pixelTileSize = Int(tileSize)
    let gridColumns = Int(columns)
    let targetData = sourceTarget.data
    let task = Task.detached(priority: .userInitiated) { () -> LoadedImage? in
      guard
        let image = Photomosaic.build(
          target: sourceTarget.image,
          tiles: sourceTiles.map(\.image),
          tileSize: pixelTileSize,
          columns: gridColumns),
        let png = ExportEncoding.encode(image, as: .png, source: targetData)
      else { return nil }
      return LoadedImage(data: png, image: image)
    }
    buildTask = task
    let result = await task.value
    if cancelledByUser { return }

    mosaic = result
    mosaicFileName =
      result.map { ExportNaming.fileName(stem: "mosaic", fileExtension: "png", data: $0.data) }
      ?? "mosaic.png"
    buildFailed = result == nil
  }
}
