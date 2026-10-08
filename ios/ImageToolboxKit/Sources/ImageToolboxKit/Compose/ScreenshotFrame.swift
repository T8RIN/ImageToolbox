import CoreGraphics
import Foundation

/// Places a picture on a backdrop with padding, rounded corners and a drop shadow.
public struct ScreenshotFrame: Sendable {
  public var padding: Int
  public var cornerRadius: CGFloat
  public var shadowRadius: CGFloat
  public var background: RGBA

  public init(
    padding: Int = 48,
    cornerRadius: CGFloat = 24,
    shadowRadius: CGFloat = 24,
    background: RGBA = RGBA(red: 240, green: 242, blue: 247)
  ) {
    self.padding = padding
    self.cornerRadius = cornerRadius
    self.shadowRadius = shadowRadius
    self.background = background
  }

  public func render(_ image: CGImage) -> CGImage? {
    let width = image.width + padding * 2
    let height = image.height + padding * 2
    guard let context = ImageGeometry.makeContext(width: width, height: height) else { return nil }

    context.setFillColor(ImageStitcher.cgColor(background))
    context.fill(CGRect(x: 0, y: 0, width: width, height: height))

    let picture = CGRect(x: padding, y: padding, width: image.width, height: image.height)
    context.saveGState()
    context.setShadow(
      offset: CGSize(width: 0, height: -8),
      blur: shadowRadius,
      color: CGColor(red: 0, green: 0, blue: 0, alpha: 0.35))
    context.addPath(
      CGPath(
        roundedRect: picture, cornerWidth: cornerRadius, cornerHeight: cornerRadius, transform: nil)
    )
    context.setFillColor(CGColor(red: 1, green: 1, blue: 1, alpha: 1))
    context.fillPath()
    context.restoreGState()

    context.saveGState()
    context.addPath(
      CGPath(
        roundedRect: picture, cornerWidth: cornerRadius, cornerHeight: cornerRadius, transform: nil)
    )
    context.clip()
    context.draw(image, in: picture)
    context.restoreGState()
    return context.makeImage()
  }
}
