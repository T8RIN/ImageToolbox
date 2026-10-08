import CoreGraphics
import Foundation
import ImageIO
import Testing

@testable import ImageToolboxKit

@Suite("Metadata")
struct MetadataTests {

  private func jpegWithGPSAndMake() throws -> Data {
    let image = TestImages.solid(RGBA(red: 90, green: 120, blue: 200), width: 16, height: 16)
    let metadata: [String: Any] = [
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

  @Test("entries lists GPS and TIFF rows from the source file")
  func listsEntries() throws {
    let rows = ImageMetadataTools.entries(try jpegWithGPSAndMake())
    #expect(rows.contains { $0.group == "{GPS}" })
    #expect(rows.contains { $0.group == "{TIFF}" && $0.key == "Make" && $0.value == "Acme" })
  }

  @Test("strip removes GPS and TIFF metadata and keeps dimensions")
  func stripRemovesMetadata() throws {
    let stripped = try #require(ImageMetadataTools.strip(try jpegWithGPSAndMake()))
    let rows = ImageMetadataTools.entries(stripped)
    #expect(!rows.contains { $0.group == "{GPS}" })
    #expect(!rows.contains { $0.group == "{TIFF}" && $0.key == "Make" })

    let image = try #require(ImageCodec.decode(stripped))
    #expect(image.width == 16 && image.height == 16)
  }

  @Test("edit writes a TIFF text field that reads back")
  func editWritesField() throws {
    let source = try jpegWithGPSAndMake()
    let edited = try #require(
      ImageMetadataTools.edit(source, fields: [.make: "Zed", .artist: "Ann"]))
    let values = ImageMetadataTools.editableValues(edited)
    #expect(values[.make] == "Zed")
    #expect(values[.artist] == "Ann")
  }

  @Test("edit keeps untouched metadata")
  func editKeepsOthers() throws {
    let edited = try #require(
      ImageMetadataTools.edit(try jpegWithGPSAndMake(), fields: [.artist: "Ann"]))
    #expect(ImageMetadataTools.entries(edited).contains { $0.group == "{GPS}" })
  }

  @Test("an empty value clears the field")
  func editClearsField() throws {
    let source = try jpegWithGPSAndMake()
    let cleared = try #require(ImageMetadataTools.edit(source, fields: [.make: ""]))
    let values = ImageMetadataTools.editableValues(cleared)
    #expect(values[.make] == "" || values[.make] == nil)
  }
}
