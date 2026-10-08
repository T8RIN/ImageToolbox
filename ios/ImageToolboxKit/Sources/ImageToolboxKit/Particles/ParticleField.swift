import Foundation

/// A single particle in points, with its own velocity.
public struct Particle: Equatable, Sendable {
  public var x: Double
  public var y: Double
  public var velocityX: Double
  public var velocityY: Double
  public var size: Double
  public var spin: Double
}

/// Physics for falling snow and confetti. Pure value type, driven by the caller's clock.
public struct ParticleField: Sendable {
  public private(set) var particles: [Particle] = []
  public let width: Double
  public let height: Double
  /// Points per second squared. Snow uses 0 and respawns; confetti uses a positive value and does not respawn.
  public let gravity: Double
  public let respawns: Bool
  private var generator: SeededGenerator

  public init(
    count: Int,
    width: Double,
    height: Double,
    gravity: Double = 0,
    respawns: Bool = true,
    seed: UInt64 = 1
  ) {
    self.width = width
    self.height = height
    self.gravity = gravity
    self.respawns = respawns
    generator = SeededGenerator(seed: seed)
    particles = (0..<count).map { _ in
      Self.spawn(&generator, width: width, height: height, atTop: false)
    }
  }

  /// Advances all particles by `deltaTime` seconds.
  public mutating func step(deltaTime: Double) {
    let dt = min(max(deltaTime, 0), 0.1)
    for index in particles.indices {
      var particle = particles[index]
      particle.velocityY += gravity * dt
      particle.x += particle.velocityX * dt
      particle.y += particle.velocityY * dt
      particle.x = particle.x.truncatingRemainder(dividingBy: width)
      if particle.x < 0 { particle.x += width }

      if particle.y > height + particle.size {
        if respawns {
          particle = Self.spawn(&generator, width: width, height: height, atTop: true)
        }
      }
      particles[index] = particle
    }
  }

  private static func spawn(
    _ generator: inout SeededGenerator,
    width: Double,
    height: Double,
    atTop: Bool
  ) -> Particle {
    let x = Double.random(in: 0..<width, using: &generator)
    let y =
      atTop
      ? -Double.random(in: 0...20, using: &generator)
      : Double.random(in: 0..<height, using: &generator)
    return Particle(
      x: x,
      y: y,
      velocityX: Double.random(in: -12...12, using: &generator),
      velocityY: Double.random(in: 20...60, using: &generator),
      size: Double.random(in: 2...6, using: &generator),
      spin: Double.random(in: -3...3, using: &generator)
    )
  }
}
