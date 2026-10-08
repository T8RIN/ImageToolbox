import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import SwiftUI

/// Dependencies the tools share. The app builds one set at launch and passes it down through the
/// environment, so a preview or a test can put in another encoder.
struct AppServices {
  let encoder: any ImageEncoding

  static let live = AppServices(encoder: ImageIOEncoder())
}

private struct AppServicesKey: EnvironmentKey {
  static let defaultValue = AppServices.live
}

extension EnvironmentValues {
  var services: AppServices {
    get { self[AppServicesKey.self] }
    set { self[AppServicesKey.self] = newValue }
  }
}

/// Holds the settings screen's state. Every change is written to UserDefaults at once.
@MainActor
@Observable
final class SettingsStore {
  var settings: AppSettings {
    didSet { settings.save(to: .standard) }
  }

  init() {
    settings = AppSettings.load(from: .standard)
  }
}

/// Encodes a picture for export. The metadata of `source` is copied in only when the settings ask
/// for it ("Keep metadata"), so by default exports carry no location.
enum ExportEncoding {
  static func encode(
    _ image: CGImage, as format: OutputFormat, quality: Double = 0.9, source: Data?
  ) -> Data? {
    let keep = AppSettings.load(from: .standard).keepMetadata
    return ImageIOEncoder().encode(
      image, as: format, quality: quality, metadataSource: keep ? source : nil)
  }
}

/// File names for exported files, chosen in the settings screen.
enum ExportNaming {
  /// `stem` is the tool's own name without extension, such as `resized`.
  static func fileName(stem: String, fileExtension: String, data: Data) -> String {
    AppSettings.load(from: .standard).naming.fileName(
      stem: stem, fileExtension: fileExtension, data: data)
  }
}
