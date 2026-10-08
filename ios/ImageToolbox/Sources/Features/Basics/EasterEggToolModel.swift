import Foundation
import ImageToolboxKit
import Observation

@MainActor
@Observable
final class EasterEggToolModel {
  static let canvasHeight: CGFloat = 360
  private static let particleCount = 120
  private static let gravity = 600.0

  /// Drives the timeline. It stops once every piece of confetti has fallen out of view.
  private(set) var isRunning = false

  // The physics state changes on every frame, so it is not observed.
  @ObservationIgnored private var seed: UInt64 = 1
  @ObservationIgnored private var field: ParticleField?
  @ObservationIgnored private var lastDate: Date?
  @ObservationIgnored private var elapsed = 0.0

  /// Starts a new burst. Every tap gets a new seed, so the pattern differs each time.
  func celebrate() {
    seed = UInt64.random(in: 1...UInt64.max)
    field = nil
    lastDate = nil
    elapsed = 0
    isRunning = true
  }

  /// Steps the confetti to `date` and returns what to draw. It is called from the timeline's
  /// content closure, so it writes only unobserved state, except when the burst has finished.
  func advance(to date: Date, width: Double) -> (particles: [Particle], elapsed: Double) {
    guard isRunning, width > 0 else { return ([], 0) }

    let height = Double(Self.canvasHeight)
    if field?.width != width {
      field = ParticleField(
        count: Self.particleCount,
        width: width,
        height: height,
        gravity: Self.gravity,
        respawns: false,
        seed: seed
      )
    }

    let delta = lastDate.map { date.timeIntervalSince($0) } ?? 0
    lastDate = date
    elapsed += delta
    field?.step(deltaTime: delta)

    let particles = field?.particles ?? []
    if particles.allSatisfy({ $0.y - $0.size > height }) {
      // Deferred, so the observed flag is not changed while the view body is being evaluated.
      Task { self.stop() }
    }
    return (particles, elapsed)
  }

  private func stop() {
    isRunning = false
  }
}
