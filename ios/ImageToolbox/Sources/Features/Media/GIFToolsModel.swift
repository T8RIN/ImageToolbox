import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import SwiftUI

private struct DecodedGIF: Sendable {
  let frames: [CGImage]
  let duration: Double
}

@MainActor
@Observable
final class GIFToolsModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var pingPong: Bool
    var frameDelay: Double
  }

  private(set) var history = UndoHistory(Parameters(pingPong: false, frameDelay: 0.1))

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
    Parameters(pingPong: pingPong, frameDelay: frameDelay)
  }

  private func restoreParameters(_ parameters: Parameters) {
    pingPong = parameters.pingPong
    frameDelay = parameters.frameDelay
  }
  private(set) var frames: [CGImage] = []
  /// Sum of the source frame delays, in seconds.
  private(set) var duration = 0.0
  private(set) var output: Data?
  private(set) var outputFileName = "animation.gif"
  private(set) var errorMessage: LocalizedStringKey?
  private(set) var isMaking = false
  @ObservationIgnored private var makeTask: Task<Data?, Never>?
  @ObservationIgnored private var cancelledByUser = false

  /// Stops the encoder between frames. A cancelled make is not reported as an error.
  func cancelMaking() {
    cancelledByUser = true
    makeTask?.cancel()
  }

  var pingPong = false {
    didSet { output = nil }
  }

  var frameDelay = 0.1 {
    didSet { output = nil }
  }

  /// Decodes the chosen file off the main actor and keeps its frames.
  func load(_ url: URL) async {
    output = nil
    errorMessage = nil
    let decoded = await Task.detached(priority: .userInitiated) { () -> DecodedGIF? in
      guard let data = MediaFiles.read(url) else { return nil }
      let frames = GIFTools.frames(data)
      guard !frames.isEmpty else { return nil }
      return DecodedGIF(frames: frames, duration: GIFTools.delays(data).reduce(0, +))
    }.value

    guard let decoded else {
      frames = []
      duration = 0
      errorMessage = "This file could not be read as an image."
      return
    }
    frames = decoded.frames
    duration = decoded.duration
  }

  /// Encodes the frames (ping-pong applied when enabled) into one GIF.
  func makeGIF() async {
    output = nil
    errorMessage = nil
    cancelledByUser = false
    let source = frames
    let usePingPong = pingPong
    let delay = frameDelay
    let task = Task.detached(priority: .userInitiated) { () -> Data? in
      let sequence = usePingPong ? GIFTools.pingPong(source) : source
      return GIFTools.makeGIF(frames: sequence, frameDelay: delay, loopCount: 0)
    }
    makeTask = task
    isMaking = true
    let data = await task.value
    isMaking = false
    makeTask = nil
    if cancelledByUser { return }

    guard let data else {
      errorMessage = "Could not make the GIF."
      return
    }
    output = data
    outputFileName = ExportNaming.fileName(stem: "animation", fileExtension: "gif", data: data)
  }
}
