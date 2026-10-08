import CoreGraphics
import Foundation
import ImageToolboxKit
import PhotosUI
import SwiftUI

/// A picked picture: the original bytes (for metadata and file export) and decoded, upright pixels.
struct LoadedImage: @unchecked Sendable {
  let data: Data
  let image: CGImage
}

/// Turns picker results into `LoadedImage`. Decoding runs off the main actor.
enum PhotoLoader {

  static func load(_ item: PhotosPickerItem) async -> LoadedImage? {
    guard let data = try? await item.loadTransferable(type: Data.self) else { return nil }
    return await decode(data)
  }

  static func load(_ items: [PhotosPickerItem]) async -> [LoadedImage] {
    var loaded: [LoadedImage] = []
    for item in items {
      if let image = await load(item) { loaded.append(image) }
    }
    return loaded
  }

  static func decode(_ data: Data) async -> LoadedImage? {
    await Task.detached(priority: .userInitiated) {
      ImageCodec.decode(data).map { LoadedImage(data: data, image: $0) }
    }.value
  }
}
