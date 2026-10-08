import Foundation

/// One help article. Callers pass localised text, so the search matches what the user reads.
public struct HelpEntry: Identifiable, Equatable, Sendable {
  public let id: String
  public let title: String
  public let body: String
  public let keywords: [String]

  public init(id: String, title: String, body: String, keywords: [String] = []) {
    self.id = id
    self.title = title
    self.body = body
    self.keywords = keywords
  }
}

public enum HelpSearch {
  /// Case-insensitive. Every whitespace-separated word must appear somewhere in the entry.
  public static func results(for query: String, in entries: [HelpEntry]) -> [HelpEntry] {
    let words = query.lowercased().split(whereSeparator: \.isWhitespace).map(String.init)
    guard !words.isEmpty else { return entries }

    return entries.filter { entry in
      let haystack = ([entry.title, entry.body] + entry.keywords).joined(separator: " ")
        .lowercased()
      return words.allSatisfy { haystack.contains($0) }
    }
  }
}
