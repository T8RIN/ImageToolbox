import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

private enum DocumentScanResult: Sendable {
  case corrected(PNGPicture)
  case noDocument
  case failed
}

@MainActor
@Observable
final class DocumentScannerToolModel {
  var pickedItem: PhotosPickerItem?
  private(set) var original: CGImage?
  private(set) var corrected: PNGPicture?
  private(set) var correctedFileName = "scan.png"
  private(set) var errorMessage: LocalizedStringKey?

  /// Finds the document in the photo and flattens it. The work runs off the main actor.
  func scan(_ item: PhotosPickerItem) async {
    corrected = nil
    errorMessage = nil
    guard let loaded = await PhotoLoader.load(item) else {
      original = nil
      errorMessage = "Could not read this photo."
      return
    }
    let image = loaded.image
    let sourceData = loaded.data
    original = image

    let result = await Task.detached(priority: .userInitiated) { () -> DocumentScanResult in
      guard let quad = DocumentScanner.detectDocument(in: image) else { return .noDocument }
      guard let flattened = DocumentScanner.correct(image, quad: quad),
        let picture = PNGPicture(flattened, metadataSource: sourceData)
      else { return .failed }
      return .corrected(picture)
    }.value

    switch result {
    case .corrected(let picture):
      corrected = picture
      correctedFileName = ExportNaming.fileName(
        stem: "scan", fileExtension: "png", data: picture.png)
    case .noDocument:
      errorMessage = "No document found"
    case .failed:
      errorMessage = "Could not correct the perspective."
    }
  }
}
