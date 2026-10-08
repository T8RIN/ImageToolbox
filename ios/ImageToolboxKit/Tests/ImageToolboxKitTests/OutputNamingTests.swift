import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Output file names")
struct OutputNamingTests {

  private let bytes = Data("picture".utf8)

  @Test("fixed keeps the tool's own stem")
  func fixedName() {
    let name = OutputNaming.fixed.fileName(stem: "resized", fileExtension: "png", data: bytes)
    #expect(name == "resized.png")
  }

  @Test("random gives twelve lowercase letters and digits, and differs between calls")
  func randomName() {
    var generator = SeededGenerator(seed: 1)
    let first = OutputNaming.random.fileName(
      stem: "x", fileExtension: "jpg", data: bytes, using: &generator)
    let second = OutputNaming.random.fileName(
      stem: "x", fileExtension: "jpg", data: bytes, using: &generator)

    let stem = String(first.dropLast(4))
    #expect(first.hasSuffix(".jpg"))
    #expect(stem.count == 12)
    #expect(stem.allSatisfy { $0.isLowercase || $0.isNumber })
    #expect(first != second)
  }

  @Test("random is reproducible for the same seed")
  func randomIsSeeded() {
    var first = SeededGenerator(seed: 9)
    var second = SeededGenerator(seed: 9)
    let a = OutputNaming.random.fileName(
      stem: "x", fileExtension: "png", data: bytes, using: &first)
    let b = OutputNaming.random.fileName(
      stem: "x", fileExtension: "png", data: bytes, using: &second)
    #expect(a == b)
  }

  @Test("checksum uses the first sixteen hex digits of SHA-256 of the bytes")
  func checksumName() {
    let digest = ChecksumAlgorithm.sha256.hexDigest(of: bytes)
    let name = OutputNaming.checksum.fileName(stem: "x", fileExtension: "png", data: bytes)
    #expect(name == String(digest.prefix(16)) + ".png")
  }

  @Test("template renders BatchRename tokens and appends the extension")
  func templateName() {
    let date = Date(timeIntervalSince1970: 0)
    let name = OutputNaming.template("{name}_{n:3}").fileName(
      stem: "IMG", fileExtension: "webp", data: bytes, sequenceNumber: 7, date: date)
    #expect(name == "IMG_007.webp")
  }

  @Test("slashes and colons are replaced, and an empty result falls back to the stem")
  func sanitizes() {
    let slashed = OutputNaming.template("a/b:c").fileName(
      stem: "stem", fileExtension: "png", data: bytes)
    #expect(slashed == "a-b-c.png")

    let empty = OutputNaming.template("   ").fileName(
      stem: "stem", fileExtension: "png", data: bytes)
    #expect(empty == "stem.png")
  }

  @Test("the naming choice survives a JSON round trip")
  func codableRoundTrip() throws {
    for naming in [OutputNaming.fixed, .random, .checksum, .template("{name}-{n}")] {
      let data = try JSONEncoder().encode(naming)
      #expect(try JSONDecoder().decode(OutputNaming.self, from: data) == naming)
    }
  }
}
