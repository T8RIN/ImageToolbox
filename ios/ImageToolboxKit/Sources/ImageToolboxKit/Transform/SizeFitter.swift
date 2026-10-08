import CoreGraphics
import Foundation

public struct FittedImage: Sendable {
  public let data: Data
  public let image: CGImage
  /// Quality used for lossy formats. 1 for lossless formats.
  public let quality: Double
}

/// Finds an encoding that stays under a byte budget.
/// Lossy formats binary-search the quality. Lossless formats shrink the dimensions instead.
public enum SizeFitter {

  /// `metadataSource` is the original file. When given, its EXIF, GPS, TIFF and IPTC blocks are
  /// copied into every candidate, so the size search measures the file that is actually saved.
  public static func fit(
    _ image: CGImage, format: OutputFormat, maxBytes: Int, metadataSource: Data? = nil
  ) -> FittedImage? {
    guard maxBytes > 0 else { return nil }
    let metadata = metadataSource.map { ImageMetadataTools.carriedProperties(from: $0) } ?? [:]
    return format.isLossy
      ? fitLossy(image, format: format, maxBytes: maxBytes, metadata: metadata)
      : fitLossless(image, format: format, maxBytes: maxBytes, metadata: metadata)
  }

  private static func fitLossy(
    _ image: CGImage, format: OutputFormat, maxBytes: Int, metadata: [String: Any]
  ) -> FittedImage? {
    var current = image
    for _ in 0..<12 {
      var low = 0.05
      var high = 1.0
      var best: FittedImage?
      for _ in 0..<10 {
        let quality = (low + high) / 2
        guard
          let data = ImageCodec.encode(
            current, as: format, quality: quality, metadata: metadata)
        else {
          return nil
        }
        if data.count <= maxBytes {
          best = FittedImage(data: data, image: current, quality: quality)
          low = quality
        } else {
          high = quality
        }
      }
      if let best { return best }
      // Even the lowest quality is too large. Shrink and try again.
      guard let smaller = scaled(current, by: 0.8) else { return nil }
      current = smaller
    }
    return nil
  }

  private static func fitLossless(
    _ image: CGImage, format: OutputFormat, maxBytes: Int, metadata: [String: Any]
  ) -> FittedImage? {
    var current = image
    for _ in 0..<24 {
      guard let data = ImageCodec.encode(current, as: format, quality: 1, metadata: metadata)
      else { return nil }
      if data.count <= maxBytes {
        return FittedImage(data: data, image: current, quality: 1)
      }
      guard let smaller = scaled(current, by: 0.85) else { return nil }
      current = smaller
    }
    return nil
  }

  private static func scaled(_ image: CGImage, by factor: CGFloat) -> CGImage? {
    let width = max(1, Int((CGFloat(image.width) * factor).rounded()))
    let height = max(1, Int((CGFloat(image.height) * factor).rounded()))
    guard width < image.width || height < image.height else { return nil }
    return ImageGeometry.resize(image, width: width, height: height)
  }
}
