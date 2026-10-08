import CoreGraphics
import Foundation

/// Rotation, flipping, cropping and resizing on CGImage. Pure geometry, no UI.
public enum ImageGeometry {

  /// sRGB, 8 bits per channel, premultiplied RGBA. Matches `RasterImage`.
  public static func makeContext(width: Int, height: Int) -> CGContext? {
    guard width > 0, height > 0, let colorSpace = CGColorSpace(name: CGColorSpace.sRGB) else {
      return nil
    }
    // Rows are tightly packed so RasterImage can read the buffer as width * height * 4 bytes.
    return CGContext(
      data: nil,
      width: width,
      height: height,
      bitsPerComponent: 8,
      bytesPerRow: width * 4,
      space: colorSpace,
      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue
    )
  }

  /// Rotates clockwise in quarter turns. Negative and large values are normalised.
  public static func rotate(_ image: CGImage, quarterTurnsClockwise turns: Int) -> CGImage? {
    let turns = ((turns % 4) + 4) % 4
    guard turns != 0 else { return image }

    let width = image.width
    let height = image.height
    let swapsAxes = turns % 2 == 1
    let outWidth = swapsAxes ? height : width
    let outHeight = swapsAxes ? width : height

    guard let context = makeContext(width: outWidth, height: outHeight) else { return nil }
    context.translateBy(x: CGFloat(outWidth) / 2, y: CGFloat(outHeight) / 2)
    context.rotate(by: -CGFloat.pi / 2 * CGFloat(turns))
    context.translateBy(x: -CGFloat(width) / 2, y: -CGFloat(height) / 2)
    context.draw(image, in: CGRect(x: 0, y: 0, width: width, height: height))
    return context.makeImage()
  }

  public static func flip(_ image: CGImage, horizontal: Bool) -> CGImage? {
    let width = image.width
    let height = image.height
    guard let context = makeContext(width: width, height: height) else { return nil }
    if horizontal {
      context.translateBy(x: CGFloat(width), y: 0)
      context.scaleBy(x: -1, y: 1)
    } else {
      context.translateBy(x: 0, y: CGFloat(height))
      context.scaleBy(x: 1, y: -1)
    }
    context.draw(image, in: CGRect(x: 0, y: 0, width: width, height: height))
    return context.makeImage()
  }

  /// Crops to `rect` in top-left pixel coordinates. The rect is clamped to the image bounds.
  public static func crop(_ image: CGImage, to rect: CGRect) -> CGImage? {
    let bounds = CGRect(x: 0, y: 0, width: image.width, height: image.height)
    let clamped = rect.integral.intersection(bounds)
    guard !clamped.isNull, clamped.width >= 1, clamped.height >= 1 else { return nil }
    return image.cropping(to: clamped)
  }

  public static func resize(_ image: CGImage, width: Int, height: Int) -> CGImage? {
    guard let context = makeContext(width: width, height: height) else { return nil }
    context.interpolationQuality = .high
    context.draw(image, in: CGRect(x: 0, y: 0, width: width, height: height))
    return context.makeImage()
  }

  /// Largest size with the same aspect ratio that fits inside `bounds`.
  /// Images smaller than the bounds are only enlarged when `allowUpscale` is true.
  public static func fitSize(
    width: Int,
    height: Int,
    within bounds: CGSize,
    allowUpscale: Bool = false
  ) -> CGSize {
    guard width > 0, height > 0, bounds.width > 0, bounds.height > 0 else { return .zero }
    var scale = min(bounds.width / CGFloat(width), bounds.height / CGFloat(height))
    if !allowUpscale { scale = min(scale, 1) }
    return CGSize(
      width: max(1, (CGFloat(width) * scale).rounded()),
      height: max(1, (CGFloat(height) * scale).rounded())
    )
  }

  /// Rect that covers `target` with the source aspect ratio, centred. It may extend past `target`.
  public static func coverRect(sourceWidth: Int, sourceHeight: Int, target: CGSize) -> CGRect {
    guard sourceWidth > 0, sourceHeight > 0 else { return .zero }
    let scale = max(target.width / CGFloat(sourceWidth), target.height / CGFloat(sourceHeight))
    let width = CGFloat(sourceWidth) * scale
    let height = CGFloat(sourceHeight) * scale
    return CGRect(
      x: (target.width - width) / 2,
      y: (target.height - height) / 2,
      width: width,
      height: height
    )
  }

  /// Applies an EXIF orientation value (1...8) so the returned pixels are upright.
  public static func applyOrientation(_ image: CGImage, exifOrientation: Int) -> CGImage? {
    switch exifOrientation {
    case 2: flip(image, horizontal: true)
    case 3: rotate(image, quarterTurnsClockwise: 2)
    case 4: flip(image, horizontal: false)
    case 5: flip(rotate(image, quarterTurnsClockwise: 1) ?? image, horizontal: true)
    case 6: rotate(image, quarterTurnsClockwise: 1)
    case 7: flip(rotate(image, quarterTurnsClockwise: 3) ?? image, horizontal: true)
    case 8: rotate(image, quarterTurnsClockwise: 3)
    default: image
    }
  }
}
