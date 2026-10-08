import Foundation

/// How an exported file gets its name, chosen once in the settings and used by every tool.
public enum OutputNaming: Codable, Equatable, Sendable {
  /// The tool's own name, such as `resized.png`.
  case fixed
  /// Twelve random lowercase letters and digits.
  case random
  /// The first sixteen hex digits of the SHA-256 of the file's bytes.
  case checksum
  /// A `BatchRename` pattern. The pattern gives the name without extension; the extension is
  /// appended.
  case template(String)

  private static let alphabet = Array("abcdefghijklmnopqrstuvwxyz0123456789")

  /// File name for `data`. `stem` is the tool's own name without extension.
  public func fileName<Generator: RandomNumberGenerator>(
    stem: String,
    fileExtension: String,
    data: Data,
    sequenceNumber: Int = 1,
    date: Date = .now,
    using generator: inout Generator
  ) -> String {
    let base: String
    switch self {
    case .fixed:
      base = stem
    case .random:
      base = String(
        (0..<12).map { _ in
          Self.alphabet[Int.random(in: 0..<Self.alphabet.count, using: &generator)]
        })
    case .checksum:
      base = String(ChecksumAlgorithm.sha256.hexDigest(of: data).prefix(16))
    case .template(let pattern):
      let context = RenameContext(
        originalName: stem, fileExtension: fileExtension, date: date)
      base = BatchRename.render(
        pattern: pattern, context: context, sequenceNumber: sequenceNumber)
    }
    return Self.sanitized(base, fallback: stem) + "." + fileExtension
  }

  /// Same as above with the system random generator.
  public func fileName(
    stem: String,
    fileExtension: String,
    data: Data,
    sequenceNumber: Int = 1,
    date: Date = .now
  ) -> String {
    var generator = SystemRandomNumberGenerator()
    return fileName(
      stem: stem,
      fileExtension: fileExtension,
      data: data,
      sequenceNumber: sequenceNumber,
      date: date,
      using: &generator)
  }

  /// Slashes and colons cannot appear in a file name on every platform. An empty name falls back
  /// to the tool's stem.
  private static func sanitized(_ name: String, fallback: String) -> String {
    let replaced = name.replacingOccurrences(of: "/", with: "-")
      .replacingOccurrences(of: ":", with: "-")
    let cleaned = replaced.trimmingCharacters(in: .whitespacesAndNewlines)
    return cleaned.isEmpty ? fallback : cleaned
  }
}
