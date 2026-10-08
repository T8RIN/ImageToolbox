import Foundation

/// Preferences that apply to every tool, stored as JSON. A stored value that no longer decodes
/// (for example after a field is added) is replaced by the defaults.
public struct AppSettings: Codable, Equatable, Sendable {
  public static let defaultsKey = "settings.app"

  /// Copy EXIF, GPS, TIFF and IPTC blocks from the source into re-encoded files.
  public var keepMetadata: Bool
  public var naming: OutputNaming

  public init(keepMetadata: Bool = false, naming: OutputNaming = .fixed) {
    self.keepMetadata = keepMetadata
    self.naming = naming
  }

  /// Stored settings, or the defaults when nothing is stored or the stored value is unreadable.
  public static func load(from defaults: UserDefaults, key: String = defaultsKey) -> AppSettings {
    guard let data = defaults.data(forKey: key),
      let stored = try? JSONDecoder().decode(AppSettings.self, from: data)
    else { return AppSettings() }
    return stored
  }

  public func save(to defaults: UserDefaults, key: String = defaultsKey) {
    guard let data = try? JSONEncoder().encode(self) else { return }
    defaults.set(data, forKey: key)
  }
}
