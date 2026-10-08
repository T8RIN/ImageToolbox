import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class ColorToolsModel {

  /// Settings the colour tools use. A step is recorded when a slider drag ends, the base colour
  /// changes, or a palette is extracted.
  struct Parameters: Equatable, Sendable {
    var baseColor: Color
    var mixFraction: Double
    var paletteCount: Double
  }

  private(set) var history = UndoHistory(
    Parameters(
      baseColor: Color(red: 0.35, green: 0.6, blue: 0.9), mixFraction: 0, paletteCount: 6))

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
    Parameters(baseColor: baseColor, mixFraction: mixFraction, paletteCount: paletteCount)
  }

  private func restoreParameters(_ parameters: Parameters) {
    baseColor = parameters.baseColor
    mixFraction = parameters.mixFraction
    paletteCount = parameters.paletteCount
  }
  private static let white = RGBA(red: 255, green: 255, blue: 255)

  var baseColor = Color(red: 0.35, green: 0.6, blue: 0.9)
  var mixFraction = 0.0
  var paletteCount = 6.0

  private(set) var picture: LoadedImage?
  private(set) var pictureFailed = false
  private(set) var palette: [RGBA] = []
  private(set) var paletteFailed = false
  private(set) var isExtracting = false

  var base: RGBA { ColorConversion.rgba(baseColor) }

  var mixed: RGBA { base.mixed(with: Self.white, fraction: mixFraction) }

  var hslText: String {
    let value = base.hsl
    return String(format: "%.1f°, %.1f%%, %.1f%%", value.hue, value.saturation, value.lightness)
  }

  var hsvText: String {
    let value = base.hsv
    return String(format: "%.1f°, %.1f%%, %.1f%%", value.hue, value.saturation, value.value)
  }

  var cmykText: String {
    let value = base.cmyk
    return String(
      format: "%.1f%%, %.1f%%, %.1f%%, %.1f%%",
      value.cyan, value.magenta, value.yellow, value.key)
  }

  func loadPicture(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    picture = loaded
    pictureFailed = loaded == nil
    palette = []
    paletteFailed = false
  }

  func extractPalette() async {
    guard let picture else { return }
    isExtracting = true
    defer { isExtracting = false }
    let count = Int(paletteCount)
    let colors = await Task.detached(priority: .userInitiated) { () -> [RGBA]? in
      guard let raster = RasterImage(picture.image) else { return nil }
      return PaletteExtractor.dominantColors(raster, count: count)
    }.value
    palette = colors ?? []
    paletteFailed = colors?.isEmpty ?? true
  }
}
