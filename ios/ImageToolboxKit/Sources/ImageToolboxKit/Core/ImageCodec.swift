import CoreGraphics
import Foundation
import ImageIO

/// Decoding and encoding through ImageIO. Platform-neutral, so it runs under `swift test` on macOS too.
public enum ImageCodec {

  public static var encodableTypeIdentifiers: Set<String> {
    Set((CGImageDestinationCopyTypeIdentifiers() as? [String]) ?? [])
  }

  public static var decodableTypeIdentifiers: Set<String> {
    Set((CGImageSourceCopyTypeIdentifiers() as? [String]) ?? [])
  }

  /// Decodes the first frame and applies the EXIF orientation, so the returned pixels are upright.
  public static func decode(_ data: Data) -> CGImage? {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil),
      let image = CGImageSourceCreateImageAtIndex(source, 0, nil)
    else { return nil }

    let orientation = exifOrientation(of: source)
    return ImageGeometry.applyOrientation(image, exifOrientation: orientation) ?? image
  }

  /// Number of frames. Animated GIF and APNG return more than one.
  public static func frameCount(_ data: Data) -> Int {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil) else { return 0 }
    return CGImageSourceGetCount(source)
  }

  public static func encode(
    _ image: CGImage,
    as format: OutputFormat,
    quality: Double = 0.9,
    metadata: [String: Any] = [:]
  ) -> Data? {
    if format == .webp {
      // ImageIO cannot write WebP; libwebp takes quality in 0...100. Metadata is not written.
      return WebPCodec.encode(image, quality: Float(min(max(quality, 0), 1) * 100))
    }

    let output = NSMutableData()
    guard
      let destination = CGImageDestinationCreateWithData(
        output, format.typeIdentifier as CFString, 1, nil)
    else { return nil }

    var properties = metadata
    if format.isLossy {
      properties[kCGImageDestinationLossyCompressionQuality as String] = min(max(quality, 0), 1)
    }

    CGImageDestinationAddImage(destination, image, properties as CFDictionary)
    guard CGImageDestinationFinalize(destination) else { return nil }
    return output as Data
  }

  /// Writes several frames into one animated container (GIF).
  public static func encodeAnimation(
    _ frames: [CGImage],
    as format: OutputFormat,
    frameDelay: Double,
    loopCount: Int = 0
  ) -> Data? {
    guard !frames.isEmpty else { return nil }
    let output = NSMutableData()
    guard
      let destination = CGImageDestinationCreateWithData(
        output, format.typeIdentifier as CFString, frames.count, nil)
    else { return nil }

    let containerProperties: [String: Any] = [
      kCGImagePropertyGIFDictionary as String: [
        kCGImagePropertyGIFLoopCount as String: loopCount
      ]
    ]
    CGImageDestinationSetProperties(destination, containerProperties as CFDictionary)

    let frameProperties: [String: Any] = [
      kCGImagePropertyGIFDictionary as String: [
        kCGImagePropertyGIFDelayTime as String: frameDelay
      ]
    ]
    for frame in frames {
      // Checked per frame, so a long animation can be cancelled from the calling task.
      guard !Task.isCancelled else { return nil }
      CGImageDestinationAddImage(destination, frame, frameProperties as CFDictionary)
    }
    guard CGImageDestinationFinalize(destination) else { return nil }
    return output as Data
  }

  static func exifOrientation(of source: CGImageSource) -> Int {
    guard
      let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [String: Any],
      let orientation = properties[kCGImagePropertyOrientation as String] as? Int
    else { return 1 }
    return orientation
  }
}
