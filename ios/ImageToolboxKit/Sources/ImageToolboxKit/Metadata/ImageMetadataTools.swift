import CoreGraphics
import Foundation
import ImageIO

/// One row of metadata shown to the user, e.g. `GPS / Latitude / 55.75`.
public struct MetadataEntry: Equatable, Sendable {
  public let group: String
  public let key: String
  public let value: String
}

/// Text fields that can be changed without re-encoding pixels.
public enum EditableMetadataField: String, CaseIterable, Sendable {
  case make
  case model
  case software
  case artist
  case copyright
  case imageDescription
  case dateTime

  var tiffKey: CFString {
    switch self {
    case .make: kCGImagePropertyTIFFMake
    case .model: kCGImagePropertyTIFFModel
    case .software: kCGImagePropertyTIFFSoftware
    case .artist: kCGImagePropertyTIFFArtist
    case .copyright: kCGImagePropertyTIFFCopyright
    case .imageDescription: kCGImagePropertyTIFFImageDescription
    case .dateTime: kCGImagePropertyTIFFDateTime
    }
  }
}

/// Reads, edits and strips image metadata (EXIF, GPS, TIFF, IPTC, XMP) with ImageIO.
public enum ImageMetadataTools {

  /// Flattens every metadata dictionary ImageIO reports into displayable rows.
  public static func entries(_ data: Data) -> [MetadataEntry] {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil),
      let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [String: Any]
    else { return [] }

    var rows: [MetadataEntry] = []
    for (group, value) in properties.sorted(by: { $0.key < $1.key }) {
      if let dictionary = value as? [String: Any] {
        for (key, nested) in dictionary.sorted(by: { $0.key < $1.key }) {
          rows.append(MetadataEntry(group: group, key: key, value: String(describing: nested)))
        }
      } else {
        rows.append(MetadataEntry(group: "Image", key: group, value: String(describing: value)))
      }
    }
    return rows
  }

  /// Metadata to copy into a re-encoded version of `source`: the EXIF, GPS, TIFF and IPTC blocks.
  /// Orientation and pixel sizes are dropped, because the pixels were already rotated and resized.
  public static func carriedProperties(from source: Data) -> [String: Any] {
    guard let imageSource = CGImageSourceCreateWithData(source as CFData, nil),
      let properties = CGImageSourceCopyPropertiesAtIndex(imageSource, 0, nil) as? [String: Any]
    else { return [:] }

    let blocks: Set<String> = [
      kCGImagePropertyExifDictionary as String,
      kCGImagePropertyGPSDictionary as String,
      kCGImagePropertyTIFFDictionary as String,
      kCGImagePropertyIPTCDictionary as String,
    ]
    var carried: [String: Any] = [:]
    for (block, value) in properties where blocks.contains(block) {
      guard var dictionary = value as? [String: Any] else { continue }
      dictionary.removeValue(forKey: kCGImagePropertyTIFFOrientation as String)
      dictionary.removeValue(forKey: kCGImagePropertyExifPixelXDimension as String)
      dictionary.removeValue(forKey: kCGImagePropertyExifPixelYDimension as String)
      carried[block] = dictionary
    }
    return carried
  }

  /// Current values of the editable text fields, if the image has them.
  public static func editableValues(_ data: Data) -> [EditableMetadataField: String] {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil),
      let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [String: Any],
      let tiff = properties[kCGImagePropertyTIFFDictionary as String] as? [String: Any]
    else { return [:] }

    var values: [EditableMetadataField: String] = [:]
    for field in EditableMetadataField.allCases {
      if let value = tiff[field.tiffKey as String] {
        values[field] = String(describing: value)
      }
    }
    return values
  }

  /// Removes EXIF, GPS, TIFF, IPTC and XMP. The image is decoded and encoded again with
  /// quality 1.0, because ImageIO ignores requests to drop metadata while copying a source.
  public static func strip(_ data: Data) -> Data? {
    guard let image = ImageCodec.decode(data) else { return nil }
    let source = CGImageSourceCreateWithData(data as CFData, nil)
    let type = source.flatMap { CGImageSourceGetType($0) } as String?
    let format = type.flatMap { OutputFormat(typeIdentifier: $0) } ?? .png
    return ImageCodec.encode(image, as: format, quality: 1.0)
  }

  /// Sets the given text fields and keeps every other tag. Pixels are copied, not re-encoded.
  /// An empty value writes an empty tag, which clears the field.
  public static func edit(_ data: Data, fields: [EditableMetadataField: String]) -> Data? {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil),
      let type = CGImageSourceGetType(source)
    else { return nil }

    var tiff: [String: Any] = [:]
    for (field, value) in fields {
      tiff[field.tiffKey as String] = value
    }

    let output = NSMutableData()
    guard let destination = CGImageDestinationCreateWithData(output, type, 1, nil) else {
      return nil
    }
    let properties: [String: Any] = [kCGImagePropertyTIFFDictionary as String: tiff]
    CGImageDestinationAddImageFromSource(destination, source, 0, properties as CFDictionary)
    guard CGImageDestinationFinalize(destination) else { return nil }
    return output as Data
  }
}
