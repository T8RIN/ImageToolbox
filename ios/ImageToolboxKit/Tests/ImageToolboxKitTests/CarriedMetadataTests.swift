import CoreGraphics
import Foundation
import ImageIO
import Testing

@testable import ImageToolboxKit

@Suite("Carried metadata")
struct CarriedMetadataTests {

  /// A JPEG with GPS, a camera make and an orientation tag (rotated 90 degrees).
  private func sourceWithMetadata() throws -> Data {
    let image = TestImages.solid(RGBA(red: 90, green: 120, blue: 200), width: 16, height: 8)
    let metadata: [String: Any] = [
      kCGImagePropertyOrientation as String: 6,
      kCGImagePropertyGPSDictionary as String: [
        kCGImagePropertyGPSLatitude as String: 55.75,
        kCGImagePropertyGPSLatitudeRef as String: "N",
      ],
      kCGImagePropertyTIFFDictionary as String: [
        kCGImagePropertyTIFFMake as String: "Acme"
      ],
    ]
    return try #require(ImageCodec.encode(image, as: .jpeg, metadata: metadata))
  }

  @Test("carried properties keep GPS and make, and drop orientation")
  func carriesWithoutOrientation() throws {
    let carried = ImageMetadataTools.carriedProperties(from: try sourceWithMetadata())

    let tiff = carried[kCGImagePropertyTIFFDictionary as String] as? [String: Any]
    #expect(tiff?[kCGImagePropertyTIFFMake as String] as? String == "Acme")
    #expect(tiff?[kCGImagePropertyTIFFOrientation as String] == nil)
    #expect(carried[kCGImagePropertyGPSDictionary as String] != nil)
    #expect(carried[kCGImagePropertyOrientation as String] == nil)
  }

  @Test("the encoder copies metadata from the source when one is given")
  func encoderKeepsMetadata() throws {
    let image = TestImages.solid(RGBA(red: 10, green: 20, blue: 30), width: 8, height: 8)
    let encoded = try #require(
      ImageIOEncoder().encode(
        image, as: .jpeg, quality: 0.9, metadataSource: try sourceWithMetadata()))

    let rows = ImageMetadataTools.entries(encoded)
    #expect(rows.contains { $0.group == "{TIFF}" && $0.key == "Make" && $0.value == "Acme" })
    #expect(rows.contains { $0.group == "{GPS}" })
  }

  @Test("PNG output keeps the camera make when metadata is carried over")
  func pngKeepsMetadata() throws {
    let image = TestImages.solid(RGBA(red: 10, green: 20, blue: 30), width: 8, height: 8)
    let encoded = try #require(
      ImageIOEncoder().encode(
        image, as: .png, quality: 0.9, metadataSource: try sourceWithMetadata()))

    let rows = ImageMetadataTools.entries(encoded)
    #expect(rows.contains { $0.group == "{TIFF}" && $0.key == "Make" && $0.value == "Acme" })
  }

  @Test("without a metadata source the encoder writes no GPS block")
  func encoderStripsByDefault() throws {
    let image = TestImages.solid(RGBA(red: 10, green: 20, blue: 30), width: 8, height: 8)
    let encoded = try #require(
      ImageIOEncoder().encode(image, as: .jpeg, quality: 0.9, metadataSource: nil))

    let rows = ImageMetadataTools.entries(encoded)
    #expect(!rows.contains { $0.group == "{GPS}" })
    #expect(!rows.contains { $0.group == "{TIFF}" && $0.key == "Make" })
  }
}
