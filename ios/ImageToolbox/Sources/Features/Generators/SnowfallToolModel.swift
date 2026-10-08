import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation

/// Owns the snow simulation. The field is rebuilt when the canvas size or the flake count changes.
@MainActor
@Observable
final class SnowfallToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var flakes: Double
  }

  private(set) var history = UndoHistory(Parameters(flakes: 150))

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
    Parameters(flakes: flakes)
  }

  private func restoreParameters(_ parameters: Parameters) {
    flakes = parameters.flakes
  }
  private static let seed: UInt64 = 3

  var flakes: Double = 150

  @ObservationIgnored private var canvasSize = CGSize.zero
  @ObservationIgnored private var field: ParticleField?
  @ObservationIgnored private var lastFrame: Date?

  func resize(to size: CGSize) {
    guard size != canvasSize else { return }
    canvasSize = size
    rebuild()
  }

  func rebuild() {
    guard canvasSize.width > 0, canvasSize.height > 0 else { return }
    field = ParticleField(
      count: Int(flakes.rounded()),
      width: Double(canvasSize.width),
      height: Double(canvasSize.height),
      gravity: 0,
      respawns: true,
      seed: Self.seed)
    lastFrame = nil
  }

  /// Steps the field by the time since the previous call and returns the flakes to draw.
  func advance(to date: Date) -> [Particle] {
    let deltaTime = lastFrame.map { date.timeIntervalSince($0) } ?? 0
    lastFrame = date
    field?.step(deltaTime: deltaTime)
    return field?.particles ?? []
  }
}
