import CoreGraphics
import Foundation

@testable import ImageToolboxKit

/// Builds small images from code, so tests need no binary fixtures.
enum TestImages {

  static func image(
    width: Int,
    height: Int,
    pixel: (_ x: Int, _ y: Int) -> RGBA
  ) -> CGImage {
    var pixels = [UInt8](repeating: 0, count: width * height * 4)
    for y in 0..<height {
      for x in 0..<width {
        let color = pixel(x, y)
        let index = (y * width + x) * 4
        pixels[index] = color.red
        pixels[index + 1] = color.green
        pixels[index + 2] = color.blue
        pixels[index + 3] = color.alpha
      }
    }
    return RasterImage(width: width, height: height, pixels: pixels).toCGImage()!
  }

  static func solid(_ color: RGBA, width: Int, height: Int) -> CGImage {
    image(width: width, height: height) { _, _ in color }
  }

  /// Left half `left`, right half `right`.
  static func halves(left: RGBA, right: RGBA, width: Int, height: Int) -> CGImage {
    image(width: width, height: height) { x, _ in x < width / 2 ? left : right }
  }
}
