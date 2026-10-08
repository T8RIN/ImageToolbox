import CoreGraphics
import Foundation
import ImageIO

public enum GIFTools {

  /// Every frame of an animated image, in order. A still image gives one frame.
  public static func frames(_ data: Data) -> [CGImage] {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil) else { return [] }
    return (0..<CGImageSourceGetCount(source)).compactMap { index in
      CGImageSourceCreateImageAtIndex(source, index, nil)
    }
  }

  /// Per-frame delays in seconds. Missing values are reported as 0.1, a common browser default.
  public static func delays(_ data: Data) -> [Double] {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil) else { return [] }
    return (0..<CGImageSourceGetCount(source)).map { index in
      guard
        let properties = CGImageSourceCopyPropertiesAtIndex(source, index, nil) as? [String: Any],
        let gif = properties[kCGImagePropertyGIFDictionary as String] as? [String: Any],
        let delay = gif[kCGImagePropertyGIFDelayTime as String] as? Double
      else { return 0.1 }
      return delay
    }
  }

  public static func makeGIF(frames: [CGImage], frameDelay: Double, loopCount: Int = 0) -> Data? {
    ImageCodec.encodeAnimation(frames, as: .gif, frameDelay: frameDelay, loopCount: loopCount)
  }

  /// Plays forward and then backward, without repeating the end frames.
  public static func pingPong(_ frames: [CGImage]) -> [CGImage] {
    guard frames.count > 2 else { return frames }
    return frames + frames.dropFirst().dropLast().reversed()
  }
}
