import CoreGraphics
import CoreText
import Foundation

public struct TextWatermark: Sendable {
  public var text: String
  public var fontSize: CGFloat
  public var color: RGBA
  /// 0...1
  public var opacity: Double
  /// Degrees, counter-clockwise.
  public var rotation: Double
  /// When true the text is tiled across the picture. Otherwise it is drawn once at the bottom-right.
  public var repeats: Bool
  public var spacing: CGFloat

  public init(
    text: String,
    fontSize: CGFloat = 36,
    color: RGBA = RGBA(red: 255, green: 255, blue: 255),
    opacity: Double = 0.5,
    rotation: Double = 0,
    repeats: Bool = false,
    spacing: CGFloat = 160
  ) {
    self.text = text
    self.fontSize = fontSize
    self.color = color
    self.opacity = opacity
    self.rotation = rotation
    self.repeats = repeats
    self.spacing = spacing
  }
}

public enum Watermark {

  public static func applyText(_ watermark: TextWatermark, to image: CGImage) -> CGImage? {
    guard let context = ImageGeometry.makeContext(width: image.width, height: image.height) else {
      return nil
    }
    let bounds = CGRect(x: 0, y: 0, width: image.width, height: image.height)
    context.draw(image, in: bounds)

    let font = CTFontCreateWithName("HelveticaNeue-Bold" as CFString, watermark.fontSize, nil)
    let fill = ImageStitcher.cgColor(
      RGBA(
        red: watermark.color.red, green: watermark.color.green, blue: watermark.color.blue,
        alpha: UInt8((watermark.opacity * 255).rounded())))
    let attributes: [NSAttributedString.Key: Any] = [
      NSAttributedString.Key(kCTFontAttributeName as String): font,
      NSAttributedString.Key(kCTForegroundColorAttributeName as String): fill,
    ]
    let line = CTLineCreateWithAttributedString(
      NSAttributedString(string: watermark.text, attributes: attributes))
    let lineBounds = CTLineGetBoundsWithOptions(line, [])
    let radians = CGFloat(watermark.rotation) * .pi / 180

    if watermark.repeats {
      let stepX = lineBounds.width + watermark.spacing
      let stepY = lineBounds.height + watermark.spacing
      var y: CGFloat = -stepY
      var row = 0
      while y < CGFloat(image.height) + stepY {
        var x = CGFloat(row % 2) * stepX / 2 - stepX
        while x < CGFloat(image.width) + stepX {
          drawLine(line, at: CGPoint(x: x, y: y), rotation: radians, in: context)
          x += stepX
        }
        y += stepY
        row += 1
      }
    } else {
      let margin: CGFloat = 16
      let origin = CGPoint(
        x: CGFloat(image.width) - lineBounds.width - margin,
        y: margin)
      drawLine(line, at: origin, rotation: radians, in: context)
    }
    return context.makeImage()
  }

  /// Draws `image` over `base`, scaled so its width is `relativeWidth` of the base width.
  public static func applyImage(
    _ overlay: CGImage,
    to base: CGImage,
    relativeWidth: CGFloat = 0.25,
    opacity: CGFloat = 0.8,
    margin: CGFloat = 16
  ) -> CGImage? {
    guard let context = ImageGeometry.makeContext(width: base.width, height: base.height) else {
      return nil
    }
    context.draw(base, in: CGRect(x: 0, y: 0, width: base.width, height: base.height))

    let width = CGFloat(base.width) * relativeWidth
    let height = width * CGFloat(overlay.height) / CGFloat(max(1, overlay.width))
    let rect = CGRect(
      x: CGFloat(base.width) - width - margin,
      y: margin,
      width: width,
      height: height)
    context.setAlpha(opacity)
    context.draw(overlay, in: rect)
    return context.makeImage()
  }

  private static func drawLine(
    _ line: CTLine, at point: CGPoint, rotation: CGFloat, in context: CGContext
  ) {
    context.saveGState()
    context.translateBy(x: point.x, y: point.y)
    context.rotate(by: rotation)
    context.textPosition = .zero
    CTLineDraw(line, context)
    context.restoreGState()
  }
}
