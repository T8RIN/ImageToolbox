import Foundation

public struct RenameContext: Sendable {
  /// File name without extension.
  public var originalName: String
  /// Extension without the dot, e.g. `jpg`.
  public var fileExtension: String
  public var date: Date

  public init(originalName: String, fileExtension: String, date: Date) {
    self.originalName = originalName
    self.fileExtension = fileExtension
    self.date = date
  }
}

/// Renders file names from a pattern.
/// Tokens: `{name}`, `{ext}`, `{n}`, `{n:3}` (zero-padded width), `{date:yyyy-MM-dd}` (any DateFormatter pattern).
/// Unknown tokens are kept as written, so a typo is visible in the preview.
public enum BatchRename {

  public static func render(pattern: String, context: RenameContext, sequenceNumber: Int) -> String
  {
    var output = ""
    var rest = Substring(pattern)
    while let open = rest.firstIndex(of: "{") {
      output += rest[..<open]
      guard let close = rest[open...].firstIndex(of: "}") else {
        output += rest[open...]
        return output
      }
      let token = String(rest[rest.index(after: open)..<close])
      output += expand(token, context: context, sequenceNumber: sequenceNumber) ?? "{\(token)}"
      rest = rest[rest.index(after: close)...]
    }
    output += rest
    return output
  }

  private static func expand(_ token: String, context: RenameContext, sequenceNumber: Int)
    -> String?
  {
    let parts = token.split(separator: ":", maxSplits: 1).map(String.init)
    switch parts.first {
    case "name":
      return context.originalName
    case "ext":
      return context.fileExtension
    case "n":
      let width = parts.count > 1 ? Int(parts[1]) ?? 0 : 0
      let digits = String(sequenceNumber)
      return String(repeating: "0", count: max(0, width - digits.count)) + digits
    case "date":
      let formatter = DateFormatter()
      formatter.locale = Locale(identifier: "en_US_POSIX")
      formatter.dateFormat = parts.count > 1 ? parts[1] : "yyyy-MM-dd"
      return formatter.string(from: context.date)
    default:
      return nil
    }
  }
}
