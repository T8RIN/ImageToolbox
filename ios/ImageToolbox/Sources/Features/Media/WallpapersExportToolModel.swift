import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

/// The centre-cropped picture at the chosen screen size, with its encoded file.
struct RenderedWallpaper: Sendable {
  let preview: CGImage
  let data: Data
  let fileName: String
}

@MainActor
@Observable
final class WallpapersExportToolModel {

  /// Settings that the undo history keeps. A step is recorded when a slider drag ends, a choice
  /// changes, or the operation starts.
  struct Parameters: Equatable, Sendable {
    var preset: WallpaperPreset
    var format: OutputFormat
    var quality: Double
  }

  private(set) var history = UndoHistory(
    Parameters(
      preset: WallpaperPreset.all[0], format: OutputFormat.jpeg.isEncodable ? .jpeg : .png,
      quality: 0.9))

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
    Parameters(preset: preset, format: format, quality: quality)
  }

  private func restoreParameters(_ parameters: Parameters) {
    preset = parameters.preset
    format = parameters.format
    quality = parameters.quality
  }
  var pickedItem: PhotosPickerItem?
  var preset = WallpaperPreset.all[0]
  var format: OutputFormat = OutputFormat.jpeg.isEncodable ? .jpeg : .png
  var quality = 0.9

  private(set) var source: CGImage?
  private var sourceData: Data?
  private(set) var rendered: RenderedWallpaper?
  private(set) var errorMessage: LocalizedStringKey?

  func choosePhoto(_ item: PhotosPickerItem) async {
    rendered = nil
    errorMessage = nil
    guard let loaded = await PhotoLoader.load(item) else {
      source = nil
      errorMessage = "Could not read this photo."
      return
    }
    source = loaded.image
    sourceData = loaded.data
  }

  /// Fills the chosen screen size (centre crop) and encodes it in the selected format.
  func export() async {
    guard let source else { return }
    errorMessage = nil
    let target = CGSize(width: preset.width, height: preset.height)
    let selectedFormat = format
    let selectedQuality = quality
    let sourceData = self.sourceData
    let stem = "wallpaper-\(preset.width)x\(preset.height)"
    let result = await Task.detached(priority: .userInitiated) { () -> RenderedWallpaper? in
      guard let resized = ImageResize.resize(source, to: target, mode: .fill),
        let data = ExportEncoding.encode(
          resized, as: selectedFormat, quality: selectedQuality, source: sourceData)
      else { return nil }
      let fileName = ExportNaming.fileName(
        stem: stem, fileExtension: selectedFormat.fileExtension, data: data)
      return RenderedWallpaper(preview: resized, data: data, fileName: fileName)
    }.value

    guard let result else {
      errorMessage = "Could not export this photo."
      return
    }
    rendered = result
  }
}
