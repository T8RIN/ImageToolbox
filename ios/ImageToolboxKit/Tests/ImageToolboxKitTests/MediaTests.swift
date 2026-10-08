import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Watermarks, animation, naming, code and library")
struct MediaTests {

  private let red = RGBA(red: 255, green: 0, blue: 0)
  private let blue = RGBA(red: 0, green: 0, blue: 255)

  @Test("text watermark changes pixels, image watermark places the overlay")
  func watermark() throws {
    let base = TestImages.solid(blue, width: 240, height: 120)
    let marked = try #require(
      Watermark.applyText(
        TextWatermark(text: "TEST", fontSize: 24, opacity: 1, repeats: true), to: base))
    let original = try #require(RasterImage(base))
    let changed = try #require(RasterImage(marked))
    #expect(original.pixels != changed.pixels)

    let overlay = TestImages.solid(red, width: 20, height: 20)
    let stamped = try #require(
      Watermark.applyImage(overlay, to: base, relativeWidth: 0.25, opacity: 1))
    let stampedRaster = try #require(RasterImage(stamped))
    #expect(stampedRaster.pixel(x: 240 - 16 - 5, y: 120 - 16 - 5) == red)
  }

  @Test("LSB steganography round-trips UTF-8 text and rejects oversized payloads")
  func steganography() throws {
    let carrier = TestImages.solid(RGBA(red: 120, green: 130, blue: 140), width: 64, height: 64)
    let message = "Привет, мир!"
    let hidden = try #require(LSBSteganography.embed(message, in: carrier))
    #expect(LSBSteganography.extract(from: hidden) == message)

    let tiny = TestImages.solid(red, width: 2, height: 2)
    #expect(LSBSteganography.embed(String(repeating: "x", count: 100), in: tiny) == nil)
  }

  @Test("GIF write then read keeps frame count and delays")
  func gifRoundTrip() throws {
    let frames = [red, blue, red].map { TestImages.solid($0, width: 8, height: 8) }
    let gif = try #require(GIFTools.makeGIF(frames: frames, frameDelay: 0.2))
    #expect(GIFTools.frames(gif).count == 3)
    let delays = GIFTools.delays(gif)
    #expect(delays.count == 3 && abs(delays[0] - 0.2) < 0.02)
    #expect(GIFTools.pingPong(frames).count == 4)
  }

  @Test("APNG written by hand is read back as an animation with the same frames")
  func apngRoundTrip() throws {
    let frames = [red, blue, red].map { TestImages.solid($0, width: 8, height: 8) }
    let apng = try #require(APNGWriter.encode(frames: frames, frameDelay: 0.1))
    #expect(ImageCodec.frameCount(apng) == 3)
    let read = GIFTools.frames(apng)
    #expect(read.count == 3)
    let second = try #require(RasterImage(read[1]))
    #expect(second.pixel(x: 4, y: 4) == blue)
  }

  @Test("batch rename tokens expand and unknown tokens stay visible")
  func rename() {
    let context = RenameContext(
      originalName: "IMG_0042", fileExtension: "jpg", date: Date(timeIntervalSince1970: 0))
    #expect(
      BatchRename.render(pattern: "photo_{n:3}.{ext}", context: context, sequenceNumber: 7)
        == "photo_007.jpg")
    #expect(
      BatchRename.render(pattern: "{name}-{n}", context: context, sequenceNumber: 12)
        == "IMG_0042-12")
    #expect(BatchRename.render(pattern: "{oops}", context: context, sequenceNumber: 1) == "{oops}")
    let dated = BatchRename.render(pattern: "{date:yyyy}", context: context, sequenceNumber: 1)
    #expect(dated == "1970")
  }

  @Test("code highlighter marks keywords, strings, comments and numbers")
  func highlighter() {
    let tokens = CodeHighlighter.tokenize("let x = \"hi\" // note", language: .swift)
    #expect(tokens.contains(CodeToken(text: "let", kind: .keyword)))
    #expect(tokens.contains(CodeToken(text: "\"hi\"", kind: .string)))
    #expect(tokens.last == CodeToken(text: "// note", kind: .comment))
    #expect(
      CodeHighlighter.tokenize("n = 42", language: .python).contains(
        CodeToken(text: "42", kind: .number)))
  }

  @Test("bundled colour library loads about 33 thousand named colours")
  func colorLibrary() throws {
    let colors = try ColorLibrary.load()
    #expect(colors.count > 33000)
    #expect(ColorLibrary.search("#FF0000", in: colors).contains { $0.color == red })
    #expect(!ColorLibrary.search("navy", in: colors).isEmpty)
  }
}

@Suite("More languages in the code highlighter")
struct MoreLanguagesTests {

  @Test("Rust keywords and string literals are recognised")
  func rust() {
    let tokens = CodeHighlighter.tokenize("fn main() { let s = \"hi\"; }", language: .rust)
    #expect(tokens.contains(CodeToken(text: "fn", kind: .keyword)))
    #expect(tokens.contains(CodeToken(text: "let", kind: .keyword)))
    #expect(tokens.contains(CodeToken(text: "\"hi\"", kind: .string)))
  }

  @Test("SQL comments start with two dashes")
  func sql() {
    let tokens = CodeHighlighter.tokenize("SELECT id FROM t -- rows", language: .sql)
    #expect(tokens.contains(CodeToken(text: "SELECT", kind: .keyword)))
    #expect(tokens.last == CodeToken(text: "-- rows", kind: .comment))
  }

  @Test("every language renders a code card")
  func everyLanguageRenders() {
    #expect(CodeHighlighter.Language.all.count == 192)
    for language in CodeHighlighter.Language.all {
      #expect(
        CodeImageRenderer.render(code: "x = 1", language: language) != nil, "\(language) failed")
    }
  }
}

@Suite("Block comments in code highlighting")
struct BlockCommentTests {

  @Test("a block comment that spans lines stays a comment on every line")
  func multiLineBlock() {
    let lines = ["let a = 1 /* start", "still comment", "end */ let b = 2"]
    let tokens = CodeHighlighter.tokenize(lines: lines, language: .swift)
    #expect(tokens[1] == [CodeToken(text: "still comment", kind: .comment)])
    #expect(tokens[2].first == CodeToken(text: "end */", kind: .comment))
    #expect(tokens[2].contains(CodeToken(text: "let", kind: .keyword)))
  }

  @Test("the Android language list is 192 languages and block comments are present for C")
  func languageTable() {
    #expect(CodeHighlighter.Language.all.count == 192)
    #expect(CodeHighlighter.Language.c.blockComment?.start == "/*")
    #expect(CodeHighlighter.Language.python.lineComment == "#")
  }
}
