import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

private struct APNGInspection: Sendable {
  let frameCount: Int
  let firstFrame: CGImage
}

@MainActor
@Observable
final class APNGToolsModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var frameDelay: Double
  }

  private(set) var history = UndoHistory(Parameters(frameDelay: 0.1))

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func commitParameters() {
    history.record(currentParameters)
  }

  func undo() {
    guard history.undo() else { return }
    restoreParameters(history.current)
  }

  func redo() {
    guard history.redo() else { return }
    restoreParameters(history.current)
  }

  private var currentParameters: Parameters {
    Parameters(frameDelay: frameDelay)
  }

  private func restoreParameters(_ parameters: Parameters) {
    frameDelay = parameters.frameDelay
  }
  var pickedItems: [PhotosPickerItem] = []
  private(set) var frames: [CGImage] = []
  private(set) var output: Data?
  private(set) var outputFileName = "animation.png"
  private(set) var makeError: LocalizedStringKey?
  private(set) var isMaking = false
  @ObservationIgnored private var makeTask: Task<Data?, Never>?
  @ObservationIgnored private var cancelledByUser = false

  /// Stops the encoder between frames. A cancelled make is not reported as an error.
  func cancelMaking() {
    cancelledByUser = true
    makeTask?.cancel()
  }

  private(set) var readPicture: CGImage?
  private(set) var readFrameCount = 0
  private(set) var readError: LocalizedStringKey?

  var frameDelay = 0.1 {
    didSet { output = nil }
  }

  /// Loads the picked frames in selection order. Items that fail to load are skipped.
  func loadFrames() async {
    let loaded = await PhotoLoader.load(pickedItems)
    frames = loaded.map(\.image)
    output = nil
    makeError = nil
  }

  func makeAPNG() async {
    output = nil
    guard !frames.isEmpty else {
      makeError = "Choose at least one frame."
      return
    }
    makeError = nil
    cancelledByUser = false
    let source = frames
    let delay = frameDelay
    let task = Task.detached(priority: .userInitiated) { () -> Data? in
      APNGWriter.encode(frames: source, frameDelay: delay, loopCount: 0)
    }
    makeTask = task
    isMaking = true
    let data = await task.value
    isMaking = false
    makeTask = nil
    if cancelledByUser { return }

    guard let data else {
      makeError = "Could not make the APNG. All frames must have the same size."
      return
    }
    output = data
    outputFileName = ExportNaming.fileName(stem: "animation", fileExtension: "png", data: data)
  }

  /// Reads the frame count and the first frame of a PNG chosen from disk.
  func inspect(_ url: URL) async {
    readError = nil
    let inspection = await Task.detached(priority: .userInitiated) { () -> APNGInspection? in
      guard let data = MediaFiles.read(url), let first = GIFTools.frames(data).first else {
        return nil
      }
      return APNGInspection(frameCount: ImageCodec.frameCount(data), firstFrame: first)
    }.value

    guard let inspection else {
      readPicture = nil
      readFrameCount = 0
      readError = "This file could not be read as a PNG."
      return
    }
    readPicture = inspection.firstFrame
    readFrameCount = inspection.frameCount
  }
}
