import AVFoundation
import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import SwiftUI

@MainActor
@Observable
final class AudioCoverToolModel {
  private(set) var cover: PNGPicture?
  private(set) var coverFileName = "cover.png"
  private(set) var errorMessage: LocalizedStringKey?

  /// Reads the embedded artwork of an audio file. Security-scoped access covers the whole load.
  func extractCover(from url: URL) async {
    cover = nil
    errorMessage = nil
    let scoped = url.startAccessingSecurityScopedResource()
    defer {
      if scoped { url.stopAccessingSecurityScopedResource() }
    }

    let artwork: Data?
    do {
      artwork = try await AudioCover.artwork(in: url)
    } catch {
      errorMessage = "Could not read this audio file."
      return
    }
    guard let bytes = artwork else {
      errorMessage = "This file has no cover art"
      return
    }
    guard let decoded = await PhotoLoader.decode(bytes) else {
      errorMessage = "Could not decode the cover art."
      return
    }

    let image = decoded.image
    let picture = await Task.detached(priority: .userInitiated) { PNGPicture(image) }.value
    guard let picture else {
      errorMessage = "Could not encode the cover art."
      return
    }
    cover = picture
    coverFileName = ExportNaming.fileName(
      stem: "cover", fileExtension: "png", data: picture.png)
  }
}
