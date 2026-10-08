import CoreGraphics
import Foundation
import ImageToolboxKit

/// A decoded picture together with its PNG encoding, ready to show and share.
struct PNGPicture: Sendable {
  let image: CGImage
  let png: Data

  /// Returns nil when ImageIO cannot write the picture as PNG.
  init?(_ image: CGImage) {
    guard let png = ImageCodec.encode(image, as: .png) else { return nil }
    self.image = image
    self.png = png
  }

  /// Like `init?(_:)`, but copies the metadata of `metadataSource` when the settings ask for it.
  init?(_ image: CGImage, metadataSource: Data?) {
    guard let png = ExportEncoding.encode(image, as: .png, source: metadataSource) else {
      return nil
    }
    self.image = image
    self.png = png
  }
}

/// Reads files chosen with `.fileImporter`.
enum MediaFiles {
  /// Reads the whole file. Security-scoped access is held only while reading.
  static func read(_ url: URL) -> Data? {
    let scoped = url.startAccessingSecurityScopedResource()
    defer {
      if scoped { url.stopAccessingSecurityScopedResource() }
    }
    return try? Data(contentsOf: url)
  }
}
