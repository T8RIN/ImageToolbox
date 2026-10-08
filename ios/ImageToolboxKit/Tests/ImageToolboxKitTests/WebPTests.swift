import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("WebP encoding through libwebp")
struct WebPTests {

  @Test("lossy WebP has a RIFF/WEBP header and decodes back through ImageIO")
  func lossyRoundTrip() throws {
    let image = TestImages.solid(RGBA(red: 40, green: 160, blue: 90), width: 48, height: 32)
    let data = try #require(ImageCodec.encode(image, as: .webp, quality: 0.9))
    #expect(String(decoding: data.prefix(4), as: UTF8.self) == "RIFF")
    #expect(String(decoding: data.subdata(in: 8..<12), as: UTF8.self) == "WEBP")

    let decoded = try #require(ImageCodec.decode(data))
    #expect(decoded.width == 48 && decoded.height == 32)
    #expect(OutputFormat.webp.isEncodable)
  }

  @Test("lossless WebP keeps exact pixels of an opaque picture")
  func losslessRoundTrip() throws {
    let source = TestImages.image(width: 16, height: 16) { x, y in
      RGBA(red: UInt8(x * 16), green: UInt8(y * 16), blue: 200)
    }
    let data = try #require(WebPCodec.encode(source, lossless: true))
    let decoded = try #require(ImageCodec.decode(data))
    let original = try #require(RasterImage(source))
    let back = try #require(RasterImage(decoded))
    #expect(back.pixels == original.pixels)
  }

  @Test("lossless WebP keeps the alpha channel")
  func alphaRoundTrip() throws {
    // Premultiplied input: colour is already scaled by alpha (128/255 here).
    let source = TestImages.image(width: 8, height: 8) { _, _ in
      RGBA(red: 64, green: 45, blue: 100, alpha: 128)
    }
    let data = try #require(WebPCodec.encode(source, lossless: true))
    let decoded = try #require(ImageCodec.decode(data))
    let back = try #require(RasterImage(decoded))
    #expect(back.pixel(x: 3, y: 3).alpha == 128)
  }

  @Test("WebP is recognised as a format by its type identifier")
  func typeIdentifier() {
    #expect(OutputFormat(typeIdentifier: "org.webmproject.webp") == .webp)
  }
}
