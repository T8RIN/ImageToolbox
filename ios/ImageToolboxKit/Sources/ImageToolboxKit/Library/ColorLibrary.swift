import Foundation

public struct NamedColor: Identifiable, Hashable, Sendable {
  public let name: String
  public let color: RGBA

  public var id: String { color.hexString + name }
}

/// About 33 thousand named colours, shipped as `color_names.json` (`#AARRGGBB` → name).
public enum ColorLibrary {

  public enum LoadError: Error {
    case resourceMissing
    case malformed
  }

  public static func load(bundle: Bundle? = nil) throws -> [NamedColor] {
    guard let url = (bundle ?? .module).url(forResource: "color_names", withExtension: "json")
    else {
      throw LoadError.resourceMissing
    }
    let data = try Data(contentsOf: url)
    guard let dictionary = try JSONSerialization.jsonObject(with: data) as? [String: String] else {
      throw LoadError.malformed
    }
    return dictionary.compactMap { key, name -> NamedColor? in
      guard let color = parseARGB(key) else { return nil }
      return NamedColor(name: name, color: color)
    }
    .sorted { $0.name == $1.name ? $0.color.hexString < $1.color.hexString : $0.name < $1.name }
  }

  /// Case-insensitive name match, or exact hex match when the query starts with `#`.
  public static func search(_ query: String, in colors: [NamedColor]) -> [NamedColor] {
    let needle = query.trimmingCharacters(in: .whitespaces).lowercased()
    guard !needle.isEmpty else { return colors }
    if needle.hasPrefix("#") {
      return colors.filter { $0.color.hexString.lowercased().hasPrefix(needle) }
    }
    return colors.filter { $0.name.lowercased().contains(needle) }
  }

  static func parseARGB(_ text: String) -> RGBA? {
    var digits = text
    if digits.hasPrefix("#") { digits.removeFirst() }
    guard digits.count == 8, let value = UInt32(digits, radix: 16) else { return nil }
    return RGBA(
      red: UInt8((value >> 16) & 0xFF),
      green: UInt8((value >> 8) & 0xFF),
      blue: UInt8(value & 0xFF),
      alpha: UInt8(value >> 24))
  }
}
