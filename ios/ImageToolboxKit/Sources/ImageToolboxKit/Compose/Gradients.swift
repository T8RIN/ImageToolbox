import CoreGraphics
import Foundation

public struct GradientStop: Equatable, Sendable {
  public var color: RGBA
  /// Position along the gradient, 0...1.
  public var location: Double

  public init(color: RGBA, location: Double) {
    self.color = color
    self.location = location
  }
}

public enum GradientRenderer {

  // Raw values of CGGradientDrawingOptions: 1 extends before the start, 2 extends after the end.
  private static let extendBefore = CGGradientDrawingOptions(rawValue: 1)
  private static let extendAfter = CGGradientDrawingOptions(rawValue: 2)
  private static let extendBoth: CGGradientDrawingOptions = [extendBefore, extendAfter]

  /// Linear gradient. `angle` is in degrees, 0 runs left to right, 90 runs top to bottom.
  public static func linear(stops: [GradientStop], angle: Double, width: Int, height: Int)
    -> CGImage?
  {
    guard let context = ImageGeometry.makeContext(width: width, height: height),
      let gradient = makeGradient(stops)
    else { return nil }

    let radians = angle * .pi / 180
    let center = CGPoint(x: CGFloat(width) / 2, y: CGFloat(height) / 2)
    let half = CGFloat(max(width, height)) / 2
    let direction = CGPoint(x: cos(radians), y: -sin(radians))
    let start = CGPoint(x: center.x - direction.x * half, y: center.y - direction.y * half)
    let end = CGPoint(x: center.x + direction.x * half, y: center.y + direction.y * half)
    context.drawLinearGradient(
      gradient, start: start, end: end, options: GradientRenderer.extendBoth)
    return context.makeImage()
  }

  /// Radial gradient centred in the image. Stop 0 is the centre, stop 1 the edge of the half-diagonal.
  public static func radial(stops: [GradientStop], width: Int, height: Int) -> CGImage? {
    guard let context = ImageGeometry.makeContext(width: width, height: height),
      let gradient = makeGradient(stops)
    else { return nil }

    let center = CGPoint(x: CGFloat(width) / 2, y: CGFloat(height) / 2)
    let radius = hypot(CGFloat(width), CGFloat(height)) / 2
    context.drawRadialGradient(
      gradient, startCenter: center, startRadius: 0, endCenter: center, endRadius: radius,
      options: GradientRenderer.extendAfter)
    return context.makeImage()
  }

  private static func makeGradient(_ stops: [GradientStop]) -> CGGradient? {
    guard stops.count >= 2, let colorSpace = CGColorSpace(name: CGColorSpace.sRGB) else {
      return nil
    }
    let sorted = stops.sorted { $0.location < $1.location }
    let colors = sorted.map { ImageStitcher.cgColor($0.color) } as CFArray
    let locations = sorted.map { CGFloat(min(max($0.location, 0), 1)) }
    return CGGradient(colorsSpace: colorSpace, colors: colors, locations: locations)
  }
}
