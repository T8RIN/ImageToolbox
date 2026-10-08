import CoreGraphics
import CoreText
import Foundation

/// Colours for a code picture. Each highlight kind has its own colour.
public struct CodeTheme: Sendable {
  public var background: RGBA
  public var text: RGBA
  public var keyword: RGBA
  public var string: RGBA
  public var comment: RGBA
  public var number: RGBA
  public var lineNumber: RGBA

  public init(
    background: RGBA,
    text: RGBA,
    keyword: RGBA,
    string: RGBA,
    comment: RGBA,
    number: RGBA,
    lineNumber: RGBA
  ) {
    self.background = background
    self.text = text
    self.keyword = keyword
    self.string = string
    self.comment = comment
    self.number = number
    self.lineNumber = lineNumber
  }

  public static let dark = CodeTheme(
    background: RGBA(hex: "#1E1E2E")!,
    text: RGBA(hex: "#CDD6F4")!,
    keyword: RGBA(hex: "#CBA6F7")!,
    string: RGBA(hex: "#A6E3A1")!,
    comment: RGBA(hex: "#6C7086")!,
    number: RGBA(hex: "#FAB387")!,
    lineNumber: RGBA(hex: "#585B70")!)

  public static let light = CodeTheme(
    background: RGBA(hex: "#FAFAFA")!,
    text: RGBA(hex: "#383A42")!,
    keyword: RGBA(hex: "#A626A4")!,
    string: RGBA(hex: "#50A14F")!,
    comment: RGBA(hex: "#A0A1A7")!,
    number: RGBA(hex: "#986801")!,
    lineNumber: RGBA(hex: "#9DA5B4")!)
}

/// Draws highlighted source code as a picture, like a shareable code card.
public enum CodeImageRenderer {

  public static func render(
    code: String,
    language: CodeHighlighter.Language,
    fontSize: CGFloat = 14,
    theme: CodeTheme = .dark,
    showLineNumbers: Bool = true,
    padding: CGFloat = 32
  ) -> CGImage? {
    let font = CTFontCreateWithName("Menlo-Regular" as CFString, fontSize, nil)
    let lines = code.components(separatedBy: "\n")
    let ascent = CTFontGetAscent(font)
    let descent = CTFontGetDescent(font)
    let leading = CTFontGetLeading(font)
    let lineHeight = ascent + descent + leading

    let gutter: CGFloat =
      showLineNumbers ? CGFloat(String(lines.count).count + 1) * fontSize * 0.62 + 16 : 0
    let tokenizedLines = CodeHighlighter.tokenize(lines: lines, language: language)
    let attributedLines = tokenizedLines.map {
      attributedLine(for: $0, font: font, theme: theme)
    }
    let widths = attributedLines.map {
      CTLineGetTypographicBounds(CTLineCreateWithAttributedString($0), nil, nil, nil)
    }
    let contentWidth = max(widths.max() ?? 0, 1)

    let width = Int((padding * 2 + gutter + CGFloat(contentWidth)).rounded(.up))
    let height = Int((padding * 2 + CGFloat(lines.count) * lineHeight).rounded(.up))
    guard let context = ImageGeometry.makeContext(width: width, height: height) else { return nil }

    context.setFillColor(ImageStitcher.cgColor(theme.background))
    context.fill(CGRect(x: 0, y: 0, width: width, height: height))

    for (index, attributed) in attributedLines.enumerated() {
      // Core Graphics is bottom-up: the first line sits just under the top padding.
      let top = CGFloat(height) - padding - CGFloat(index) * lineHeight
      let baseline = top - ascent
      if showLineNumbers {
        let number = NSAttributedString(
          string: String(index + 1),
          attributes: [
            NSAttributedString.Key(kCTFontAttributeName as String): font,
            NSAttributedString.Key(kCTForegroundColorAttributeName as String):
              ImageStitcher.cgColor(theme.lineNumber),
          ])
        context.textPosition = CGPoint(x: padding, y: baseline)
        CTLineDraw(CTLineCreateWithAttributedString(number), context)
      }
      context.textPosition = CGPoint(x: padding + gutter, y: baseline)
      CTLineDraw(CTLineCreateWithAttributedString(attributed), context)
    }
    return context.makeImage()
  }

  private static func attributedLine(
    for tokens: [CodeToken],
    font: CTFont,
    theme: CodeTheme
  ) -> NSAttributedString {
    let result = NSMutableAttributedString()
    for token in tokens {
      let color: RGBA
      switch token.kind {
      case .plain: color = theme.text
      case .keyword: color = theme.keyword
      case .string: color = theme.string
      case .comment: color = theme.comment
      case .number: color = theme.number
      }
      result.append(
        NSAttributedString(
          string: token.text,
          attributes: [
            NSAttributedString.Key(kCTFontAttributeName as String): font,
            NSAttributedString.Key(kCTForegroundColorAttributeName as String):
              ImageStitcher.cgColor(color),
          ]))
    }
    if result.length == 0 {
      result.append(
        NSAttributedString(
          string: " ",
          attributes: [
            NSAttributedString.Key(kCTFontAttributeName as String): font
          ]))
    }
    return result
  }
}
