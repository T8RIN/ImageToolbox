import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Base64Codec")
struct Base64CodecTests {

  // MARK: - encode

  @Test("encode matches RFC 4648 vectors")
  func encodeMatchesRfc4648Vectors() {
    #expect(Base64Codec.encode(Data()) == "")
    #expect(Base64Codec.encode(Data("f".utf8)) == "Zg==")
    #expect(Base64Codec.encode(Data("fo".utf8)) == "Zm8=")
    #expect(Base64Codec.encode(Data("foo".utf8)) == "Zm9v")
    #expect(Base64Codec.encode(Data("foobar".utf8)) == "Zm9vYmFy")
  }

  @Test("encode uses the standard alphabet, not URL-safe")
  func encodeUsesStandardAlphabet() {
    // 0xFB 0xFF encodes to "+/8=" in the standard alphabet
    #expect(Base64Codec.encode(Data([0xFB, 0xFF])) == "+/8=")
  }

  @Test("encode does not wrap lines")
  func encodeDoesNotWrapLines() {
    let long = Data(repeating: 0x41, count: 300)
    #expect(!Base64Codec.encode(long).contains("\n"))
  }

  // MARK: - decode

  @Test("decode round-trips every byte value")
  func decodeRoundTrip() {
    let original = Data((0...255).map { UInt8($0) })
    #expect(Base64Codec.decode(Base64Codec.encode(original)) == original)
  }

  @Test("decode accepts a data URI prefix and whitespace")
  func decodeAcceptsDataUriPrefixAndWhitespace() {
    let text = "data:image/png;base64,\nZm9v\n YmFy\n"
    #expect(Base64Codec.decode(text) == Data("foobar".utf8))
  }

  @Test("decode accepts missing padding (ASSUMPTION, see Base64Codec.decode)")
  func decodeAcceptsMissingPadding() {
    #expect(Base64Codec.decode("Zg") == Data("f".utf8))
    #expect(Base64Codec.decode("Zm8") == Data("fo".utf8))
  }

  @Test("decode rejects invalid input")
  func decodeRejectsInvalidInput() {
    #expect(Base64Codec.decode("") == nil)
    #expect(Base64Codec.decode("A") == nil)  // length remainder 1: impossible
    #expect(Base64Codec.decode("Zm9v!") == nil)  // illegal character
    #expect(Base64Codec.decode("-_8=") == nil)  // URL-safe alphabet is rejected, as on Android
  }

  // MARK: - trimToBase64

  @Test("trimToBase64 strips whitespace and only the first prefix")
  func trimToBase64StripsWhitespaceAndFirstPrefixOnly() {
    #expect(Base64Codec.trimToBase64(" a b\n") == "ab")
    #expect(Base64Codec.trimToBase64("a,b,c") == "b,c")
    #expect(Base64Codec.trimToBase64("noprefix") == "noprefix")
  }

  // MARK: - isBase64

  @Test("isBase64 accepts valid padded and unpadded-length input")
  func isBase64Accepts() {
    #expect(Base64Codec.isBase64("Zm9v"))
    #expect(Base64Codec.isBase64("Zg=="))
    #expect(Base64Codec.isBase64("Zm8="))
  }

  @Test("isBase64 rejects malformed input")
  func isBase64Rejects() {
    #expect(!Base64Codec.isBase64(""))
    #expect(!Base64Codec.isBase64("Zg="))  // length not a multiple of 4
    #expect(!Base64Codec.isBase64("Zm9=x"))  // padding in the middle
    #expect(!Base64Codec.isBase64("Zm9v\n"))  // whitespace is outside the alphabet
    #expect(!Base64Codec.isBase64("Zg==="))  // more than two padding characters
  }
}
