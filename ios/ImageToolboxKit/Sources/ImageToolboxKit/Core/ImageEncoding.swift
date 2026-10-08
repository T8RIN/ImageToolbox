import CoreGraphics
import Foundation

/// Encodes pictures for export. The app passes an implementation in, so a model can be tested
/// with a fake encoder.
public protocol ImageEncoding: Sendable {
  /// Encodes `image`. When `metadataSource` holds an image, its metadata is copied into the output.
  func encode(
    _ image: CGImage,
    as format: OutputFormat,
    quality: Double,
    metadataSource: Data?
  ) -> Data?
}

/// The encoder the app uses. Writes through ImageIO, or libwebp for WebP.
public struct ImageIOEncoder: ImageEncoding {

  public init() {}

  public func encode(
    _ image: CGImage,
    as format: OutputFormat,
    quality: Double,
    metadataSource: Data?
  ) -> Data? {
    let metadata = metadataSource.map { ImageMetadataTools.carriedProperties(from: $0) } ?? [:]
    return ImageCodec.encode(image, as: format, quality: quality, metadata: metadata)
  }
}
