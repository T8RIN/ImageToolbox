import CoreGraphics
import Foundation

/// CPU-side RGBA pixels, row 0 at the top. Used for sampling, metrics and per-pixel algorithms.
///
/// Pixels are stored premultiplied, as Core Graphics produces them. For opaque images
/// this is identical to straight alpha.
public struct RasterImage: Sendable {
  public let width: Int
  public let height: Int
  /// Row-major RGBA, 4 bytes per pixel.
  public let pixels: [UInt8]

  public init?(_ image: CGImage) {
    let width = image.width
    let height = image.height
    guard let context = ImageGeometry.makeContext(width: width, height: height),
      let data = context.data
    else { return nil }

    context.draw(image, in: CGRect(x: 0, y: 0, width: width, height: height))
    let count = width * height * 4
    self.width = width
    self.height = height
    self.pixels = Array(
      UnsafeBufferPointer(start: data.assumingMemoryBound(to: UInt8.self), count: count))
  }

  public init(width: Int, height: Int, pixels: [UInt8]) {
    precondition(pixels.count == width * height * 4, "pixel buffer size mismatch")
    self.width = width
    self.height = height
    self.pixels = pixels
  }

  /// Straight-alpha colour at (x, y). Coordinates are not bounds-checked beyond array indexing.
  public func pixel(x: Int, y: Int) -> RGBA {
    let index = (y * width + x) * 4
    let alpha = pixels[index + 3]
    guard alpha > 0 else { return RGBA(red: 0, green: 0, blue: 0, alpha: 0) }
    let scale = 255.0 / Double(alpha)
    return RGBA(
      red: UInt8(min(255, (Double(pixels[index]) * scale).rounded())),
      green: UInt8(min(255, (Double(pixels[index + 1]) * scale).rounded())),
      blue: UInt8(min(255, (Double(pixels[index + 2]) * scale).rounded())),
      alpha: alpha
    )
  }

  /// Rec. 601 luma in 0...1, computed from premultiplied values over black.
  public func luminance(x: Int, y: Int) -> Double {
    let index = (y * width + x) * 4
    let red = Double(pixels[index]) / 255
    let green = Double(pixels[index + 1]) / 255
    let blue = Double(pixels[index + 2]) / 255
    return 0.299 * red + 0.587 * green + 0.114 * blue
  }

  /// Non-premultiplied RGBA bytes, as encoders such as WebP expect.
  public func straightRGBA() -> [UInt8] {
    var output = pixels
    for index in stride(from: 0, to: output.count, by: 4) {
      let alpha = output[index + 3]
      guard alpha < 255 else { continue }
      if alpha == 0 {
        output[index] = 0
        output[index + 1] = 0
        output[index + 2] = 0
        continue
      }
      let scale = 255.0 / Double(alpha)
      for channel in 0..<3 {
        output[index + channel] = UInt8(
          min(255, (Double(output[index + channel]) * scale).rounded()))
      }
    }
    return output
  }

  public func toCGImage() -> CGImage? {
    guard let context = ImageGeometry.makeContext(width: width, height: height),
      let data = context.data
    else { return nil }

    pixels.withUnsafeBufferPointer { source in
      data.copyMemory(from: source.baseAddress!, byteCount: source.count)
    }
    return context.makeImage()
  }
}
