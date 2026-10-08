import CoreGraphics
import Foundation
import libwebp

/// WebP encoding through libwebp. ImageIO on iOS and macOS can decode WebP but cannot write it.
public enum WebPCodec {

  /// - Parameters:
  ///   - quality: 0...100 for lossy output. Ignored when `lossless` is true.
  ///   - lossless: when true, encodes losslessly and keeps every pixel.
  public static func encode(_ image: CGImage, quality: Float = 80, lossless: Bool = false) -> Data?
  {
    guard let raster = RasterImage(image) else { return nil }
    let straight = raster.straightRGBA()
    let stride = Int32(raster.width * 4)
    var output: UnsafeMutablePointer<UInt8>?

    let size: Int = straight.withUnsafeBufferPointer { buffer in
      guard let base = buffer.baseAddress else { return 0 }
      if lossless {
        return WebPEncodeLosslessRGBA(
          base, Int32(raster.width), Int32(raster.height), stride, &output)
      }
      let clamped = min(max(quality, 0), 100)
      return WebPEncodeRGBA(
        base, Int32(raster.width), Int32(raster.height), stride, clamped, &output)
    }

    guard size > 0, let output else { return nil }
    let data = Data(bytes: output, count: size)
    WebPFree(output)
    return data
  }
}
