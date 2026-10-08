import CoreGraphics
import Foundation

/// How an image is brought to a target size.
public enum ResizeMode: Sendable, CaseIterable {
  /// Stretch to exactly the target size, ignoring aspect ratio.
  case exact
  /// Keep aspect ratio, fit entirely inside the target. The result may be smaller.
  case fit
  /// Keep aspect ratio, cover the target, and centre-crop the overflow.
  case fill
}

public enum ImageResize {

  public static func resize(_ image: CGImage, to target: CGSize, mode: ResizeMode) -> CGImage? {
    let width = Int(target.width.rounded())
    let height = Int(target.height.rounded())
    guard width > 0, height > 0 else { return nil }

    switch mode {
    case .exact:
      return ImageGeometry.resize(image, width: width, height: height)
    case .fit:
      let size = ImageGeometry.fitSize(
        width: image.width, height: image.height, within: target, allowUpscale: true)
      return ImageGeometry.resize(image, width: Int(size.width), height: Int(size.height))
    case .fill:
      guard let context = ImageGeometry.makeContext(width: width, height: height) else {
        return nil
      }
      context.interpolationQuality = .high
      let rect = ImageGeometry.coverRect(
        sourceWidth: image.width, sourceHeight: image.height, target: target)
      context.draw(image, in: rect)
      return context.makeImage()
    }
  }
}
