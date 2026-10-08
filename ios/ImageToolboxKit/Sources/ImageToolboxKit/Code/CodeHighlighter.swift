import Foundation

public enum CodeTokenKind: Equatable, Sendable {
  case plain
  case keyword
  case string
  case comment
  case number
}

public struct CodeToken: Equatable, Sendable {
  public let text: String
  public let kind: CodeTokenKind

  public init(text: String, kind: CodeTokenKind) {
    self.text = text
    self.kind = kind
  }
}

/// Syntax highlighting for the 192 languages the Android app offers. Grammars come from the
/// highlight.js build bundled in that app. The lexer marks keywords, strings, numbers, line comments
/// and block comments. It is not a parser: it does not understand nested or context-dependent rules.
public enum CodeHighlighter {

  public struct Language: Identifiable, Hashable, Sendable {
    public let key: String
    public let title: String
    public let lineComment: String?
    public let blockComment: (start: String, end: String)?
    let keywords: Set<String>

    public var id: String { key }

    private init(
      key: String,
      title: String,
      lineComment: String?,
      blockComment: (String, String)?,
      keywords: String
    ) {
      self.key = key
      self.title = title
      self.lineComment = lineComment
      self.blockComment = blockComment.map { (start: $0.0, end: $0.1) }
      self.keywords = Set(keywords.split(separator: " ").map(String.init))
    }

    public static func == (lhs: Language, rhs: Language) -> Bool {
      lhs.key == rhs.key
    }

    public func hash(into hasher: inout Hasher) {
      hasher.combine(key)
    }

    /// Every supported language, in alphabetical order of its key.
    public static let all: [Language] = CodeLanguageTable.entries
      .map {
        Language(
          key: $0.key, title: $0.title, lineComment: $0.lineComment,
          blockComment: $0.blockComment, keywords: $0.keywords)
      }
      .sorted { $0.key < $1.key }

    public static func named(_ key: String) -> Language? {
      all.first { $0.key == key }
    }

    public static let swift = named("swift")!
    public static let kotlin = named("kotlin")!
    public static let python = named("python")!
    public static let javascript = named("javascript")!
    public static let typescript = named("typescript")!
    public static let json = named("json")!
    public static let java = named("java")!
    public static let c = named("c")!
    public static let cpp = named("cpp")!
    public static let go = named("go")!
    public static let rust = named("rust")!
    public static let ruby = named("ruby")!
    public static let bash = named("bash")!
    public static let sql = named("sql")!
  }

  /// Tokens for a single line, starting outside any block comment.
  public static func tokenize(_ line: String, language: Language) -> [CodeToken] {
    tokenize(lines: [line], language: language)[0]
  }

  /// Tokens for consecutive lines. A block comment that opens on one line continues on the next.
  public static func tokenize(lines: [String], language: Language) -> [[CodeToken]] {
    var openBlockEnd: [Character]?
    return lines.map { line in
      let (tokens, stillOpen) = tokenizeLine(line, language: language, openBlockEnd: openBlockEnd)
      openBlockEnd = stillOpen
      return tokens
    }
  }

  private static func tokenizeLine(
    _ line: String,
    language: Language,
    openBlockEnd: [Character]?
  ) -> ([CodeToken], [Character]?) {
    var tokens: [CodeToken] = []
    var buffer = ""
    let characters = Array(line)
    var index = 0

    func flush() {
      if !buffer.isEmpty {
        tokens.append(CodeToken(text: buffer, kind: .plain))
        buffer = ""
      }
    }

    // Continue a block comment left open by an earlier line.
    if let end = openBlockEnd {
      if let stop = find(end, in: characters, from: 0) {
        let finish = stop + end.count
        tokens.append(CodeToken(text: String(characters[0..<finish]), kind: .comment))
        index = finish
      } else {
        return ([CodeToken(text: line, kind: .comment)], end)
      }
    }

    while index < characters.count {
      let character = characters[index]
      if let marker = language.lineComment, starts(characters, at: index, with: Array(marker)) {
        flush()
        tokens.append(CodeToken(text: String(characters[index...]), kind: .comment))
        break
      }
      if let block = language.blockComment, starts(characters, at: index, with: Array(block.start))
      {
        flush()
        let end = Array(block.end)
        let afterStart = index + block.start.count
        if let stop = find(end, in: characters, from: afterStart) {
          let finish = stop + end.count
          tokens.append(CodeToken(text: String(characters[index..<finish]), kind: .comment))
          index = finish
          continue
        }
        tokens.append(CodeToken(text: String(characters[index...]), kind: .comment))
        return (tokens, end)
      }
      if character == "\"" || character == "'" || character == "`" {
        flush()
        var end = index + 1
        while end < characters.count && characters[end] != character {
          end += characters[end] == "\\" ? 2 : 1
        }
        let stop = min(end + 1, characters.count)
        tokens.append(CodeToken(text: String(characters[index..<stop]), kind: .string))
        index = stop
        continue
      }
      if character.isLetter || character == "_" {
        var end = index
        while end < characters.count
          && (characters[end].isLetter || characters[end].isNumber || characters[end] == "_")
        {
          end += 1
        }
        let word = String(characters[index..<end])
        flush()
        let isKeyword =
          language.keywords.contains(word) || language.keywords.contains(word.lowercased())
        tokens.append(CodeToken(text: word, kind: isKeyword ? .keyword : .plain))
        index = end
        continue
      }
      if character.isNumber {
        var end = index
        while end < characters.count && (characters[end].isNumber || characters[end] == ".") {
          end += 1
        }
        flush()
        tokens.append(CodeToken(text: String(characters[index..<end]), kind: .number))
        index = end
        continue
      }
      buffer.append(character)
      index += 1
    }
    flush()
    return (tokens, nil)
  }

  private static func starts(_ characters: [Character], at index: Int, with marker: [Character])
    -> Bool
  {
    guard !marker.isEmpty, index + marker.count <= characters.count else { return false }
    for offset in 0..<marker.count where characters[index + offset] != marker[offset] {
      return false
    }
    return true
  }

  /// Index where `marker` starts at or after `from`, or nil.
  private static func find(_ marker: [Character], in characters: [Character], from: Int) -> Int? {
    guard !marker.isEmpty else { return nil }
    var index = from
    while index + marker.count <= characters.count {
      if starts(characters, at: index, with: marker) { return index }
      index += 1
    }
    return nil
  }
}
