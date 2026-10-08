import CoreGraphics
import Foundation

/// Objective difference measures between two images of the same size.
public struct ImageMetrics: Equatable, Sendable {
  /// Mean absolute error per channel, 0...255.
  public let meanAbsoluteError: Double
  /// Mean squared error per channel.
  public let meanSquaredError: Double
  /// Root mean squared error per channel, 0...255.
  public let rootMeanSquaredError: Double
  /// Peak signal-to-noise ratio in dB. Infinite for identical images.
  public let psnr: Double
  /// Normalised cross-correlation of luminance, 0...1 for non-negative images.
  public let normalizedCrossCorrelation: Double
  /// Number of pixels where any channel differs.
  public let differingPixels: Int
  /// Mean structural similarity over 8x8 blocks of luminance, -1...1. 1 means identical.
  public let structuralSimilarity: Double
}

public enum ImageMetricsCalculator {

  /// `right` is resized to `left`'s size when they differ.
  public static func compare(_ left: CGImage, _ right: CGImage) -> ImageMetrics? {
    guard let a = RasterImage(left) else { return nil }
    let bImage =
      right.width == left.width && right.height == left.height
      ? right : ImageGeometry.resize(right, width: left.width, height: left.height)
    guard let bImage, let b = RasterImage(bImage) else { return nil }
    return compare(a, b)
  }

  public static func compare(_ a: RasterImage, _ b: RasterImage) -> ImageMetrics {
    let count = a.pixels.count
    var absolute = 0.0
    var squared = 0.0
    var differing = 0
    var crossAB = 0.0
    var sumA = 0.0
    var sumB = 0.0
    var sumAA = 0.0
    var sumBB = 0.0

    for pixel in stride(from: 0, to: count, by: 4) {
      var differs = false
      for channel in 0..<3 {
        let x = Double(a.pixels[pixel + channel])
        let y = Double(b.pixels[pixel + channel])
        absolute += abs(x - y)
        squared += (x - y) * (x - y)
        if x != y { differs = true }
      }
      if differs { differing += 1 }

      let lumA = a.luminance(x: (pixel / 4) % a.width, y: (pixel / 4) / a.width) * 255
      let lumB = b.luminance(x: (pixel / 4) % b.width, y: (pixel / 4) / b.width) * 255
      crossAB += lumA * lumB
      sumA += lumA
      sumB += lumB
      sumAA += lumA * lumA
      sumBB += lumB * lumB
    }

    let channels = Double(count / 4 * 3)
    let mse = channels > 0 ? squared / channels : 0
    let psnr = mse == 0 ? Double.infinity : 10 * log10(255 * 255 / mse)
    let ncc = sumAA > 0 && sumBB > 0 ? crossAB / (sumAA * sumBB).squareRoot() : 0

    return ImageMetrics(
      meanAbsoluteError: channels > 0 ? absolute / channels : 0,
      meanSquaredError: mse,
      rootMeanSquaredError: mse.squareRoot(),
      psnr: psnr,
      normalizedCrossCorrelation: ncc,
      differingPixels: differing,
      structuralSimilarity: ssim(a, b))
  }

  /// Windowed SSIM on luminance, using 8x8 blocks without overlap. Constants follow Wang et al. (2004).
  static func ssim(_ a: RasterImage, _ b: RasterImage) -> Double {
    let c1 = (0.01 * 255.0) * (0.01 * 255.0)
    let c2 = (0.03 * 255.0) * (0.03 * 255.0)
    let block = 8
    var total = 0.0
    var blocks = 0

    for top in stride(from: 0, to: a.height - block + 1, by: block) {
      for left in stride(from: 0, to: a.width - block + 1, by: block) {
        var sumX = 0.0
        var sumY = 0.0
        var sumXX = 0.0
        var sumYY = 0.0
        var sumXY = 0.0
        for y in top..<(top + block) {
          for x in left..<(left + block) {
            let vx = a.luminance(x: x, y: y) * 255
            let vy = b.luminance(x: x, y: y) * 255
            sumX += vx
            sumY += vy
            sumXX += vx * vx
            sumYY += vy * vy
            sumXY += vx * vy
          }
        }
        let n = Double(block * block)
        let meanX = sumX / n
        let meanY = sumY / n
        let varX = sumXX / n - meanX * meanX
        let varY = sumYY / n - meanY * meanY
        let covariance = sumXY / n - meanX * meanY
        let numerator = (2 * meanX * meanY + c1) * (2 * covariance + c2)
        let denominator = (meanX * meanX + meanY * meanY + c1) * (varX + varY + c2)
        total += numerator / denominator
        blocks += 1
      }
    }
    return blocks == 0 ? 1 : total / Double(blocks)
  }
}
