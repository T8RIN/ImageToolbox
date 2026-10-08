import CoreGraphics
import Foundation

public enum StackMode: Sendable, CaseIterable {
  case average
  case median
  case maximum
  case minimum
}

/// Combines several frames of the same scene per pixel. Averaging reduces noise.
public enum ImageStacker {

  /// Inputs are resized to the first image's size. Alpha is taken from the first image.
  public static func stack(_ images: [CGImage], mode: StackMode) -> CGImage? {
    guard let first = images.first else { return nil }
    let width = first.width
    let height = first.height
    let rasters: [RasterImage] = images.compactMap { image in
      let sized =
        image.width == width && image.height == height
        ? image : ImageGeometry.resize(image, width: width, height: height)
      return sized.flatMap(RasterImage.init)
    }
    guard rasters.count == images.count else { return nil }

    var output = [UInt8](repeating: 0, count: width * height * 4)
    var samples = [UInt8](repeating: 0, count: rasters.count)
    for index in 0..<(width * height * 4) {
      if index % 4 == 3 {
        output[index] = rasters[0].pixels[index]
        continue
      }
      for (frame, raster) in rasters.enumerated() {
        samples[frame] = raster.pixels[index]
      }
      output[index] = reduce(samples, mode: mode)
    }
    return RasterImage(width: width, height: height, pixels: output).toCGImage()
  }

  private static func reduce(_ samples: [UInt8], mode: StackMode) -> UInt8 {
    switch mode {
    case .average:
      let sum = samples.reduce(0) { $0 + Int($1) }
      return UInt8(sum / samples.count)
    case .median:
      let sorted = samples.sorted()
      return sorted[sorted.count / 2]
    case .maximum:
      return samples.max() ?? 0
    case .minimum:
      return samples.min() ?? 0
    }
  }
}
