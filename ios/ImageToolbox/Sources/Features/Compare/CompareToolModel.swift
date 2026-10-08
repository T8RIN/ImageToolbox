import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class CompareToolModel {
  enum Mode: Hashable, Sendable {
    case sideBySide
    case slider
  }

  /// Frames are capped in size, because full-size frames for many pictures exhaust memory.
  private static let gifMaxSide: CGFloat = 1024
  private static let gifSteps = 24
  private static let gifFrameDelay = 0.08

  var mode: Mode = .sideBySide {
    didSet { refreshPreview() }
  }

  var split = 0.5 {
    didSet { refreshPreview() }
  }

  private(set) var before: LoadedImage?
  private(set) var after: LoadedImage?
  private(set) var beforeFailed = false
  private(set) var afterFailed = false
  private(set) var preview: CGImage?
  private(set) var previewFailed = false
  private(set) var metrics: ImageMetrics?
  private(set) var metricsFailed = false
  private(set) var isMakingGIF = false
  private(set) var gifData: Data?
  private(set) var gifFileName = "comparison.gif"
  private(set) var gifFailed = false
  @ObservationIgnored private var gifTask: Task<Data?, Never>?
  @ObservationIgnored private var cancelledGIF = false

  /// Stops the GIF between frames. A cancelled export is not reported as an error.
  func cancelGIF() {
    cancelledGIF = true
    gifTask?.cancel()
  }

  @ObservationIgnored private var previewTask: Task<Void, Never>?
  @ObservationIgnored private var metricsTask: Task<Void, Never>?
  @ObservationIgnored private var generation = 0

  var hasBothImages: Bool {
    before != nil && after != nil
  }

  func setBefore(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    before = loaded
    beforeFailed = loaded == nil
    imagesDidChange()
  }

  func setAfter(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    after = loaded
    afterFailed = loaded == nil
    imagesDidChange()
  }

  /// The split line sweeps across the picture and back.
  func makeGIF() async {
    guard let before, let after else { return }
    let left = before.image
    let right = after.image
    let maxSide = Self.gifMaxSide
    let steps = Self.gifSteps
    let frameDelay = Self.gifFrameDelay
    let current = generation

    isMakingGIF = true
    gifFailed = false
    cancelledGIF = false
    let task = Task.detached(priority: .userInitiated) { () -> Data? in
      guard let small = limitedSize(left, maxSide: maxSide),
        let smallRight = limitedSize(right, maxSide: maxSide)
      else { return nil }
      let frames = ImageComparison.sliderFrames(small, smallRight, steps: steps)
      return GIFTools.makeGIF(frames: frames, frameDelay: frameDelay)
    }
    gifTask = task
    let data = await task.value
    isMakingGIF = false
    gifTask = nil
    if cancelledGIF { return }

    guard current == generation else { return }
    gifData = data
    gifFileName =
      data.map { ExportNaming.fileName(stem: "comparison", fileExtension: "gif", data: $0) }
      ?? "comparison.gif"
    gifFailed = data == nil
  }

  private func imagesDidChange() {
    generation += 1
    gifData = nil
    gifFailed = false
    refreshPreview()
    refreshMetrics()
  }

  /// Refreshes run one after another. A superseded refresh is skipped before it starts its work,
  /// so a fast slider drag never runs several full-size compositions at once.
  private func refreshPreview() {
    let previous = previewTask
    previous?.cancel()
    guard let before, let after else {
      previewTask = nil
      preview = nil
      previewFailed = false
      return
    }
    let left = before.image
    let right = after.image
    let currentMode = mode
    let fraction = split
    previewTask = Task {
      await previous?.value
      guard !Task.isCancelled else { return }
      let image = await Task.detached(priority: .userInitiated) { () -> CGImage? in
        switch currentMode {
        case .sideBySide:
          return ImageComparison.sideBySide(left, right)
        case .slider:
          return ImageComparison.slider(left, right, fraction: fraction)
        }
      }.value
      guard !Task.isCancelled else { return }
      preview = image
      previewFailed = image == nil
    }
  }

  private func refreshMetrics() {
    let previous = metricsTask
    previous?.cancel()
    guard let before, let after else {
      metricsTask = nil
      metrics = nil
      metricsFailed = false
      return
    }
    let left = before.image
    let right = after.image
    metricsTask = Task {
      await previous?.value
      guard !Task.isCancelled else { return }
      let result = await Task.detached(priority: .userInitiated) {
        ImageMetricsCalculator.compare(left, right)
      }.value
      guard !Task.isCancelled else { return }
      metrics = result
      metricsFailed = result == nil
    }
  }
}

/// Shrinks `image` so its longest side is at most `maxSide`. Smaller images stay as they are.
private func limitedSize(_ image: CGImage, maxSide: CGFloat) -> CGImage? {
  let longest = CGFloat(max(image.width, image.height))
  guard longest > maxSide else { return image }
  return ImageResize.resize(image, to: CGSize(width: maxSide, height: maxSide), mode: .fit)
}
