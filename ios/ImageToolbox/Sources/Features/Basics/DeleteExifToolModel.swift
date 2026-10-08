import Foundation
import ImageIO
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class DeleteExifToolModel {
  var selection: PhotosPickerItem? {
    didSet {
      guard let selection else { return }
      loadTask?.cancel()
      loadTask = Task { await load(selection) }
    }
  }

  private(set) var source: LoadedImage?
  private(set) var entries: [MetadataEntry] = []
  private(set) var outputExtension = "png"
  private(set) var stripped: LoadedImage?
  private(set) var strippedRowCount = 0
  private(set) var isWorking = false
  private(set) var errorMessage: LocalizedStringKey?

  @ObservationIgnored private var loadTask: Task<Void, Never>?

  /// Removes EXIF, GPS, TIFF, IPTC and XMP. The pixels are re-encoded in the source format.
  func removeMetadata() {
    guard let data = source?.data, !isWorking else { return }
    isWorking = true
    errorMessage = nil
    Task {
      defer { isWorking = false }
      let output = await Task.detached { ImageMetadataTools.strip(data) }.value
      guard let bytes = output, let decoded = await PhotoLoader.decode(bytes) else {
        errorMessage = "The metadata could not be removed from this image."
        return
      }
      stripped = decoded
      strippedRowCount = ImageMetadataTools.entries(bytes).count
    }
  }

  private func load(_ item: PhotosPickerItem) async {
    let result = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    guard let picked = result else {
      errorMessage = "The picked file could not be read as an image."
      return
    }
    source = picked
    entries = ImageMetadataTools.entries(picked.data)
    outputExtension = Self.sourceFormat(of: picked.data)?.fileExtension ?? "png"
    stripped = nil
    strippedRowCount = 0
    errorMessage = nil
  }

  /// The format `ImageMetadataTools.strip` writes: the source format, or PNG when the source
  /// type has no `OutputFormat` equivalent.
  private static func sourceFormat(of data: Data) -> OutputFormat? {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil),
      let type = CGImageSourceGetType(source)
    else { return nil }
    return OutputFormat(typeIdentifier: type as String)
  }
}
