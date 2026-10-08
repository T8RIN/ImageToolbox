import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

struct DetectedBarcode: Identifiable, Sendable {
  let id: Int
  let payload: String
  let symbology: String
}

@MainActor
@Observable
final class ScanQRCodeToolModel {
  var pickedItem: PhotosPickerItem?
  /// Nil until a photo has been scanned. An empty array means the photo had no readable codes.
  private(set) var codes: [DetectedBarcode]?
  private(set) var errorMessage: LocalizedStringKey?

  func scan(_ item: PhotosPickerItem) async {
    codes = nil
    errorMessage = nil
    guard let loaded = await PhotoLoader.load(item) else {
      errorMessage = "Could not read this photo."
      return
    }
    let image = loaded.image
    let found = await Task.detached(priority: .userInitiated) { () -> [ScannedCode] in
      CodeScanner.detect(in: image)
    }.value

    // Codes without a readable payload are skipped, as the screen lists payloads only.
    codes = found.compactMap { code in
      code.payload.map { payload in (payload, code.symbology) }
    }
    .enumerated()
    .map { index, entry in
      DetectedBarcode(id: index, payload: entry.0, symbology: entry.1)
    }
  }
}
