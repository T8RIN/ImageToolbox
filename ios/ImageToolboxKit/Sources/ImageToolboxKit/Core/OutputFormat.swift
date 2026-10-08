import Foundation

/// Formats the app can write. Availability is checked at runtime against ImageIO.
public enum OutputFormat: String, CaseIterable, Sendable {
  case png
  case jpeg
  case heic
  case tiff
  case bmp
  case gif
  case webp

  public var typeIdentifier: String {
    switch self {
    case .png: "public.png"
    case .jpeg: "public.jpeg"
    case .heic: "public.heic"
    case .tiff: "public.tiff"
    case .bmp: "com.microsoft.bmp"
    case .gif: "com.compuserve.gif"
    case .webp: "org.webmproject.webp"
    }
  }

  public var fileExtension: String {
    switch self {
    case .png: "png"
    case .jpeg: "jpg"
    case .heic: "heic"
    case .tiff: "tiff"
    case .bmp: "bmp"
    case .gif: "gif"
    case .webp: "webp"
    }
  }

  /// Formats where `quality` changes the output.
  public var isLossy: Bool {
    self == .jpeg || self == .heic || self == .webp
  }

  /// Whether this build can write the format. WebP is written by libwebp, the rest by ImageIO.
  public var isEncodable: Bool {
    self == .webp || ImageCodec.encodableTypeIdentifiers.contains(typeIdentifier)
  }
}
