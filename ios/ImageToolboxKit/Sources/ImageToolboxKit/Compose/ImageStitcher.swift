import CoreGraphics
import Foundation

public enum StitchDirection: Sendable, CaseIterable {
  case horizontal
  case vertical
}

public enum ImageStitcher {

  /// Joins images edge to edge, aligned at the top (horizontal) or left (vertical).
  /// `spacing` is the gap between neighbours and `background` fills the gaps and the empty corners.
  public static func stitch(
    _ images: [CGImage],
    direction: StitchDirection,
    spacing: Int = 0,
    background: RGBA = RGBA(red: 255, green: 255, blue: 255)
  ) -> CGImage? {
    guard !images.isEmpty else { return nil }
    let gaps = max(0, images.count - 1) * spacing
    let width: Int
    let height: Int
    switch direction {
    case .horizontal:
      width = images.reduce(0) { $0 + $1.width } + gaps
      height = images.map(\.height).max() ?? 0
    case .vertical:
      width = images.map(\.width).max() ?? 0
      height = images.reduce(0) { $0 + $1.height } + gaps
    }

    guard let context = ImageGeometry.makeContext(width: width, height: height) else { return nil }
    context.setFillColor(cgColor(background))
    context.fill(CGRect(x: 0, y: 0, width: width, height: height))

    var cursor = 0
    for image in images {
      switch direction {
      case .horizontal:
        // Core Graphics is bottom-up, so the top edge sits at height - image.height.
        let y = height - image.height
        context.draw(image, in: CGRect(x: cursor, y: y, width: image.width, height: image.height))
        cursor += image.width + spacing
      case .vertical:
        let y = height - cursor - image.height
        context.draw(image, in: CGRect(x: 0, y: y, width: image.width, height: image.height))
        cursor += image.height + spacing
      }
    }
    return context.makeImage()
  }

  /// sRGB colour. Without an explicit space, Core Graphics converts the values when drawing
  /// into the sRGB bitmaps used here and the colour shifts.
  static func cgColor(_ color: RGBA) -> CGColor {
    let space = CGColorSpace(name: CGColorSpace.sRGB) ?? CGColorSpaceCreateDeviceRGB()
    return CGColor(
      colorSpace: space,
      components: [
        CGFloat(color.red) / 255,
        CGFloat(color.green) / 255,
        CGFloat(color.blue) / 255,
        CGFloat(color.alpha) / 255,
      ]) ?? CGColor(gray: 0, alpha: 1)
  }
}
