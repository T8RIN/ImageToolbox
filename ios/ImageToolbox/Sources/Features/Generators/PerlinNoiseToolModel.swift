import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation

/// Pure rendering of grayscale Perlin noise. Safe to run off the main actor.
enum PerlinNoiseRenderer {
  struct Settings: Sendable {
    let seed: UInt64
    let width: Int
    let height: Int
    let scale: Double
    let octaves: Int
    let persistence: Double
  }

  struct Output: Sendable {
    let raster: RasterImage
    let png: Data
  }

  /// Each pixel samples the fractal field at (x / scale, y / scale).
  static func render(_ settings: Settings) -> Output? {
    let noise = PerlinNoise(seed: settings.seed)
    var pixels = [UInt8](repeating: 255, count: settings.width * settings.height * 4)

    for y in 0..<settings.height {
      for x in 0..<settings.width {
        let value = noise.fractal(
          x: Double(x) / settings.scale,
          y: Double(y) / settings.scale,
          octaves: settings.octaves,
          persistence: settings.persistence)
        let gray = UInt8((normalize(value) * 255).rounded())
        let index = (y * settings.width + x) * 4
        pixels[index] = gray
        pixels[index + 1] = gray
        pixels[index + 2] = gray
      }
    }

    let raster = RasterImage(width: settings.width, height: settings.height, pixels: pixels)
    guard let image = raster.toCGImage(), let png = ImageCodec.encode(image, as: .png) else {
      return nil
    }
    return Output(raster: raster, png: png)
  }

  /// Maps the roughly -0.75...0.75 range of fractal noise to 0...1.
  private static func normalize(_ value: Double) -> Double {
    min(1, max(0, (value + 0.75) / 1.5))
  }
}

@MainActor
@Observable
final class PerlinNoiseToolModel {

  /// Settings that the undo history keeps. A step is recorded when a slider drag ends, a choice
  /// changes, or the operation starts.
  struct Parameters: Equatable, Sendable {
    var seed: Double
    var scale: Double
    var octaves: Double
    var persistence: Double
    var width: Double
    var height: Double
  }

  private(set) var history = UndoHistory(
    Parameters(seed: 42, scale: 64, octaves: 4, persistence: 0.5, width: 512, height: 512))

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
    Parameters(
      seed: seed, scale: scale, octaves: octaves, persistence: persistence, width: width,
      height: height)
  }

  private func restoreParameters(_ parameters: Parameters) {
    seed = parameters.seed
    scale = parameters.scale
    octaves = parameters.octaves
    persistence = parameters.persistence
    width = parameters.width
    height = parameters.height
  }
  var seed: Double = 42
  var scale: Double = 64
  var octaves: Double = 4
  var persistence: Double = 0.5
  var width: Double = 512
  var height: Double = 512

  private(set) var image: CGImage?
  private(set) var pngData: Data?
  private(set) var pngFileName = "perlin.png"
  private(set) var isGenerating = false
  private(set) var generationFailed = false

  func generate() async {
    guard !isGenerating else { return }
    isGenerating = true
    defer { isGenerating = false }

    let settings = PerlinNoiseRenderer.Settings(
      seed: UInt64(seed.rounded()),
      width: Int(width.rounded()),
      height: Int(height.rounded()),
      scale: scale,
      octaves: Int(octaves.rounded()),
      persistence: persistence)
    let output = await Task.detached(priority: .userInitiated) {
      PerlinNoiseRenderer.render(settings)
    }.value

    guard let output, let cgImage = output.raster.toCGImage() else {
      image = nil
      pngData = nil
      generationFailed = true
      return
    }
    generationFailed = false
    image = cgImage
    pngData = output.png
    pngFileName = ExportNaming.fileName(
      stem: "perlin", fileExtension: "png", data: output.png)
  }
}
