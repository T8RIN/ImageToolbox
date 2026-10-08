import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class ColorPickerToolModel {
  var selection: PhotosPickerItem? {
    didSet {
      guard let selection else { return }
      loadTask?.cancel()
      loadTask = Task { await load(selection) }
    }
  }

  private(set) var picture: LoadedImage?
  /// Last touch in normalised image coordinates, 0...1 on both axes, origin top-left.
  private(set) var samplePoint: CGPoint?
  private(set) var sample: RGBA?
  private(set) var errorMessage: LocalizedStringKey?

  @ObservationIgnored private var raster: RasterImage?
  @ObservationIgnored private var loadTask: Task<Void, Never>?

  /// Reads the colour under a normalised point of the picture.
  func pick(at point: CGPoint) {
    guard let raster else { return }
    samplePoint = point
    sample = ColorSampler.sample(raster, at: point)
  }

  private func load(_ item: PhotosPickerItem) async {
    let result = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    guard let picked = result else {
      errorMessage = "The picked file could not be read as an image."
      return
    }

    // The raster is built once, off the main actor. CGImage stays inside the closure.
    let prepared = await Task.detached { RasterImage(picked.image) }.value
    guard !Task.isCancelled else { return }
    guard let rasterImage = prepared else {
      errorMessage = "The pixels of this image could not be read."
      return
    }

    picture = picked
    raster = rasterImage
    samplePoint = nil
    sample = nil
    errorMessage = nil
  }
}
