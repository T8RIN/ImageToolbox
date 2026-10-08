import Foundation

/// Finds dominant colours with k-means over a sample of pixels. Deterministic for a given seed.
public enum PaletteExtractor {

  public static func dominantColors(
    _ raster: RasterImage,
    count: Int,
    iterations: Int = 12,
    seed: UInt64 = 1
  ) -> [RGBA] {
    guard count > 0, raster.width > 0, raster.height > 0 else { return [] }

    // Sample at most ~4000 pixels so large images stay fast.
    let total = raster.width * raster.height
    let stride = max(1, total / 4000)
    var samples: [[Double]] = []
    for index in Swift.stride(from: 0, to: total, by: stride) {
      let base = index * 4
      guard raster.pixels[base + 3] > 0 else { continue }
      samples.append([
        Double(raster.pixels[base]), Double(raster.pixels[base + 1]),
        Double(raster.pixels[base + 2]),
      ])
    }
    guard !samples.isEmpty else { return [] }

    var generator = SeededGenerator(seed: seed)
    var centroids = (0..<min(count, samples.count)).map { _ in
      samples.randomElement(using: &generator)!
    }
    var assignment = [Int](repeating: 0, count: samples.count)

    for _ in 0..<iterations {
      for (sampleIndex, sample) in samples.enumerated() {
        assignment[sampleIndex] = nearestCentroid(sample, centroids)
      }
      for centroidIndex in centroids.indices {
        var sum = [0.0, 0.0, 0.0]
        var members = 0
        for (sampleIndex, sample) in samples.enumerated()
        where assignment[sampleIndex] == centroidIndex {
          sum[0] += sample[0]
          sum[1] += sample[1]
          sum[2] += sample[2]
          members += 1
        }
        if members > 0 {
          centroids[centroidIndex] = sum.map { $0 / Double(members) }
        }
      }
    }

    var population = [Int](repeating: 0, count: centroids.count)
    for index in assignment { population[index] += 1 }
    return centroids.indices
      .sorted { population[$0] > population[$1] }
      .map { index in
        let c = centroids[index]
        return RGBA(
          red: UInt8(c[0].rounded()), green: UInt8(c[1].rounded()), blue: UInt8(c[2].rounded()))
      }
  }

  private static func nearestCentroid(_ sample: [Double], _ centroids: [[Double]]) -> Int {
    var best = 0
    var bestDistance = Double.infinity
    for (index, centroid) in centroids.enumerated() {
      let d0 = sample[0] - centroid[0]
      let d1 = sample[1] - centroid[1]
      let d2 = sample[2] - centroid[2]
      let distance = d0 * d0 + d1 * d1 + d2 * d2
      if distance < bestDistance {
        bestDistance = distance
        best = index
      }
    }
    return best
  }
}
