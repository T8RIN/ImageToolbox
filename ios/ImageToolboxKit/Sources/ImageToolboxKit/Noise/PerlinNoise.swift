import Foundation

/// Classic 2D gradient noise (Perlin, improved version) with fractal octaves.
/// The permutation table comes from a seed, so the same seed always draws the same field.
public struct PerlinNoise: Sendable {
  private let permutation: [Int]

  private static let gradients: [(Double, Double)] = [
    (1, 0), (-1, 0), (0, 1), (0, -1), (1, 1), (-1, 1), (1, -1), (-1, -1),
  ]

  public init(seed: UInt64) {
    var generator = SeededGenerator(seed: seed)
    var table = Array(0..<256)
    table.shuffle(using: &generator)
    permutation = table + table
  }

  /// Single-octave noise. Output is roughly within -0.75...0.75.
  public func value(x: Double, y: Double) -> Double {
    let floorX = x.rounded(.down)
    let floorY = y.rounded(.down)
    let cellX = Int(floorX) & 255
    let cellY = Int(floorY) & 255
    let fx = x - floorX
    let fy = y - floorY
    let u = fade(fx)
    let v = fade(fy)

    let aa = permutation[permutation[cellX] + cellY]
    let ab = permutation[permutation[cellX] + cellY + 1]
    let ba = permutation[permutation[cellX + 1] + cellY]
    let bb = permutation[permutation[cellX + 1] + cellY + 1]

    let n00 = gradient(aa, fx, fy)
    let n10 = gradient(ba, fx - 1, fy)
    let n01 = gradient(ab, fx, fy - 1)
    let n11 = gradient(bb, fx - 1, fy - 1)
    return lerp(lerp(n00, n10, u), lerp(n01, n11, u), v)
  }

  /// Sums `octaves` layers. Each layer doubles frequency (lacunarity) and scales amplitude by `persistence`.
  /// Result is normalised back to about -0.75...0.75.
  public func fractal(
    x: Double,
    y: Double,
    octaves: Int,
    persistence: Double = 0.5,
    lacunarity: Double = 2
  ) -> Double {
    var total = 0.0
    var amplitude = 1.0
    var frequency = 1.0
    var norm = 0.0
    for _ in 0..<max(1, octaves) {
      total += value(x: x * frequency, y: y * frequency) * amplitude
      norm += amplitude
      amplitude *= persistence
      frequency *= lacunarity
    }
    return total / norm
  }

  private func fade(_ t: Double) -> Double {
    t * t * t * (t * (t * 6 - 15) + 10)
  }

  private func lerp(_ a: Double, _ b: Double, _ t: Double) -> Double {
    a + t * (b - a)
  }

  private func gradient(_ hash: Int, _ x: Double, _ y: Double) -> Double {
    let (gx, gy) = Self.gradients[hash & 7]
    return gx * x + gy * y
  }
}
