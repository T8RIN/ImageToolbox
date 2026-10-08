import CoreGraphics
import Foundation

/// Crop shapes. Pixels outside the shape become transparent. Shapes are fitted to the crop rect.
public enum CropShape: Sendable, CaseIterable {
  case rectangle
  case roundedRectangle
  case cutCorners
  case oval
  case pill
  case squircle
  case triangle
  case roundedTriangle
  case diamond
  case pentagon
  case roundedPentagon
  case hexagon
  case octagon
  case chevron
  case arrow
  case bookmark
  case shield
  case droplet
  case egg
  case mapPin
  case heart
  case star
  case sharpStar
  case materialStar
  case clover
  case shuriken
  case explosion
  case kotlinLogo
  case burger
  case enhancedHeart

  public func path(in rect: CGRect) -> CGPath {
    let path = CGMutablePath()
    switch self {
    case .rectangle:
      path.addRect(rect)
    case .roundedRectangle:
      path.addRoundedRect(in: rect, cornerWidth: rect.width * 0.2, cornerHeight: rect.height * 0.2)
    case .cutCorners:
      polygon(
        path, rect,
        [(0.2, 0), (0.8, 0), (1, 0.2), (1, 0.8), (0.8, 1), (0.2, 1), (0, 0.8), (0, 0.2)])
    case .oval:
      path.addEllipse(in: rect)
    case .pill:
      path.addRoundedRect(
        in: rect, cornerWidth: min(rect.width, rect.height) / 2,
        cornerHeight: min(rect.width, rect.height) / 2)
    case .squircle:
      path.addLines(between: superellipse(rect, exponent: 4, samples: 160))
    case .triangle:
      polygon(path, rect, [(0.5, 1), (1, 0), (0, 0)])
    case .roundedTriangle:
      roundedPolygon(path, rect, [(0.5, 1), (1, 0), (0, 0)], radius: 0.18)
    case .diamond:
      polygon(path, rect, [(0.5, 1), (1, 0.5), (0.5, 0), (0, 0.5)])
    case .pentagon:
      polygon(path, rect, regular(sides: 5))
    case .roundedPentagon:
      roundedPolygon(path, rect, regular(sides: 5), radius: 0.2)
    case .hexagon:
      polygon(path, rect, regular(sides: 6))
    case .octagon:
      polygon(path, rect, regular(sides: 8))
    case .chevron:
      polygon(path, rect, [(0, 0), (0.7, 0), (1, 0.5), (0.7, 1), (0, 1), (0.3, 0.5)])
    case .arrow:
      polygon(
        path, rect, [(0, 0.35), (0.6, 0.35), (0.6, 0), (1, 0.5), (0.6, 1), (0.6, 0.65), (0, 0.65)])
    case .bookmark:
      polygon(path, rect, [(0, 0), (1, 0), (1, 1), (0.5, 0.78), (0, 1)])
    case .shield:
      polygon(path, rect, [(0.5, 0), (1, 0.14), (1, 0.5), (0.5, 1), (0, 0.5), (0, 0.14)])
    case .droplet:
      path.addLines(between: dropletPoints(rect))
    case .egg:
      path.addLines(between: eggPoints(rect))
    case .mapPin:
      path.addLines(between: mapPinPoints(rect))
    case .heart:
      path.addLines(between: heartPoints(rect))
    case .star:
      path.addLines(between: starPoints(rect, points: 5, inner: 0.44))
    case .sharpStar:
      path.addLines(between: starPoints(rect, points: 5, inner: 0.3))
    case .materialStar:
      path.addLines(between: starPoints(rect, points: 12, inner: 0.84))
    case .clover:
      path.addLines(
        between: polar(rect, samples: 240) { theta in 0.55 + 0.45 * abs(cos(2 * theta)) })
    case .shuriken:
      path.addLines(between: starPoints(rect, points: 4, inner: 0.25))
    case .explosion:
      path.addLines(between: starPoints(rect, points: 16, inner: 0.72))
    case .kotlinLogo:
      polygon(path, rect, [(0, 0), (1, 0), (0.5, 0.5), (1, 1), (0, 1)])
    case .burger:
      burgerLayers(path, rect)
    case .enhancedHeart:
      path.addLines(between: enhancedHeartPoints(rect))
    }
    path.closeSubpath()
    return path
  }
}

public enum ShapeMask {

  /// Keeps the pixels of `image` where the luminance of `mask` is high, and makes the rest transparent.
  /// The mask is resampled to the image size. Image masks paint where the sample is 0, so `decode`
  /// inverts the samples.
  public static func apply(_ image: CGImage, mask: CGImage) -> CGImage? {
    let width = image.width
    let height = image.height
    guard let maskScaled = ImageGeometry.resize(mask, width: width, height: height),
      let raster = RasterImage(maskScaled)
    else { return nil }

    var alpha = [UInt8](repeating: 0, count: width * height)
    for y in 0..<height {
      for x in 0..<width {
        alpha[y * width + x] = UInt8((raster.luminance(x: x, y: y) * 255).rounded())
      }
    }
    guard let provider = CGDataProvider(data: Data(alpha) as CFData),
      let maskImage = CGImage(
        maskWidth: width,
        height: height,
        bitsPerComponent: 8,
        bitsPerPixel: 8,
        bytesPerRow: width,
        provider: provider,
        decode: [1, 0],
        shouldInterpolate: false)
    else { return nil }

    guard let context = ImageGeometry.makeContext(width: width, height: height) else { return nil }
    let rect = CGRect(x: 0, y: 0, width: width, height: height)
    context.clip(to: rect, mask: maskImage)
    context.draw(image, in: rect)
    return context.makeImage()
  }

  /// Same size as the input, with everything outside the shape made transparent.
  public static func apply(_ image: CGImage, shape: CropShape) -> CGImage? {
    let width = image.width
    let height = image.height
    guard let context = ImageGeometry.makeContext(width: width, height: height) else { return nil }
    let rect = CGRect(x: 0, y: 0, width: width, height: height)
    context.addPath(shape.path(in: rect))
    context.clip()
    context.draw(image, in: rect)
    return context.makeImage()
  }
}

// MARK: - Geometry helpers. Points are unit-square coordinates mapped into the rect.

private func point(_ rect: CGRect, _ u: CGFloat, _ v: CGFloat) -> CGPoint {
  CGPoint(x: rect.minX + u * rect.width, y: rect.minY + v * rect.height)
}

private func polygon(_ path: CGMutablePath, _ rect: CGRect, _ vertices: [(CGFloat, CGFloat)]) {
  path.addLines(between: vertices.map { point(rect, $0.0, $0.1) })
}

/// Regular polygon with its first vertex at the top, inside the unit square.
private func regular(sides: Int) -> [(CGFloat, CGFloat)] {
  (0..<sides).map { index in
    let angle = CGFloat(index) * 2 * .pi / CGFloat(sides) - .pi / 2
    return (0.5 + 0.5 * cos(angle), 0.5 + 0.5 * sin(angle))
  }
}

/// Polygon whose corners are replaced by quadratic curves. `radius` is the fraction of each edge that is rounded.
private func roundedPolygon(
  _ path: CGMutablePath,
  _ rect: CGRect,
  _ vertices: [(CGFloat, CGFloat)],
  radius: CGFloat
) {
  let count = vertices.count
  for index in 0..<count {
    let previous = vertices[(index + count - 1) % count]
    let current = vertices[index]
    let next = vertices[(index + 1) % count]
    let start = CGPoint(
      x: current.0 + (previous.0 - current.0) * radius,
      y: current.1 + (previous.1 - current.1) * radius)
    let end = CGPoint(
      x: current.0 + (next.0 - current.0) * radius,
      y: current.1 + (next.1 - current.1) * radius)
    let controlPoint = point(rect, current.0, current.1)
    if index == 0 {
      path.move(to: point(rect, start.x, start.y))
    } else {
      path.addLine(to: point(rect, start.x, start.y))
    }
    path.addQuadCurve(to: point(rect, end.x, end.y), control: controlPoint)
  }
}

private func superellipse(_ rect: CGRect, exponent: CGFloat, samples: Int) -> [CGPoint] {
  (0..<samples).map { index in
    let theta = CGFloat(index) * 2 * .pi / CGFloat(samples)
    let x = copysign(pow(abs(cos(theta)), 2 / exponent), cos(theta))
    let y = copysign(pow(abs(sin(theta)), 2 / exponent), sin(theta))
    return point(rect, 0.5 + 0.5 * x, 0.5 + 0.5 * y)
  }
}

private func polar(_ rect: CGRect, samples: Int, radius: (CGFloat) -> CGFloat) -> [CGPoint] {
  (0..<samples).map { index in
    let theta = CGFloat(index) * 2 * .pi / CGFloat(samples) - .pi / 2
    let r = radius(theta)
    return point(rect, 0.5 + 0.5 * r * cos(theta), 0.5 + 0.5 * r * sin(theta))
  }
}

private func starPoints(_ rect: CGRect, points: Int, inner: CGFloat) -> [CGPoint] {
  (0..<(points * 2)).map { index in
    let theta = CGFloat(index) * .pi / CGFloat(points) - .pi / 2
    let r: CGFloat = index % 2 == 0 ? 1 : inner
    return point(rect, 0.5 + 0.5 * r * cos(theta), 0.5 + 0.5 * r * sin(theta))
  }
}

private func dropletPoints(_ rect: CGRect) -> [CGPoint] {
  var points = [point(rect, 0.5, 0)]
  let center = (u: CGFloat(0.5), v: CGFloat(0.62))
  for step in 0...60 {
    let theta = (150 + CGFloat(step) * 240 / 60) * .pi / 180
    points.append(point(rect, center.u + 0.38 * cos(theta), center.v + 0.38 * sin(theta)))
  }
  return points
}

private func mapPinPoints(_ rect: CGRect) -> [CGPoint] {
  // Tip at the bottom, round head on top. The head arc starts and ends at the tangent points.
  let center = (u: CGFloat(0.5), v: CGFloat(0.6))
  let radius: CGFloat = 0.4
  let tangent = acos(radius / 0.6)
  var points = [point(rect, 0.5, 0)]
  for step in 0...80 {
    let theta = (270 + tangent * 180 / .pi + CGFloat(step) * (360 - 2 * tangent * 180 / .pi) / 80)
    let radians = theta * .pi / 180
    points.append(point(rect, center.u + radius * cos(radians), center.v + radius * sin(radians)))
  }
  return points
}

private func eggPoints(_ rect: CGRect) -> [CGPoint] {
  (0..<120).map { index in
    let theta = CGFloat(index) * 2 * .pi / 120
    let x = 0.5 * cos(theta)
    let y = 0.5 * sin(theta) * (1 - 0.1 * sin(theta)) / 1.1
    return point(rect, 0.5 + x, 0.5 + y)
  }
}

private func heartPoints(_ rect: CGRect) -> [CGPoint] {
  (0..<160).map { index in
    let t = CGFloat(index) * 2 * .pi / 160
    let x = 16 * pow(sin(t), 3)
    let y = 13 * cos(t) - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t)
    return point(rect, 0.5 + x / 34, 0.5 - (y + 2.5) / 30)
  }
}

private func burgerLayers(_ path: CGMutablePath, _ rect: CGRect) {
  // Top bun, patty and bottom bun as three rounded bars. Corner radii are taken from each bar's own
  // height, because CGPath rejects a radius larger than half the rect.
  let layers: [(bottom: CGFloat, top: CGFloat, inset: CGFloat, radius: CGFloat)] = [
    (0.62, 0.98, 0.04, 0.45),
    (0.42, 0.56, 0.0, 0.3),
    (0.02, 0.34, 0.04, 0.45),
  ]
  for layer in layers {
    let bar = point(rect, layer.inset, layer.bottom).rectTo(point(rect, 1 - layer.inset, layer.top))
    let radius = bar.height * layer.radius
    path.addRoundedRect(in: bar, cornerWidth: radius, cornerHeight: radius)
  }
}

private func enhancedHeartPoints(_ rect: CGRect) -> [CGPoint] {
  // The heart curve with wider lobes: the x term is stretched and the lower cusp is softened.
  (0..<160).map { index in
    let t = CGFloat(index) * 2 * .pi / 160
    let x = 17 * pow(sin(t), 3)
    let y = 12 * cos(t) - 4.5 * cos(2 * t) - 2 * cos(3 * t) - 0.8 * cos(4 * t)
    return point(rect, 0.5 + x / 36, 0.5 - (y + 2.2) / 30)
  }
}

extension CGPoint {
  /// The rect spanned by this point and `other`, which must be the opposite corner.
  fileprivate func rectTo(_ other: CGPoint) -> CGRect {
    CGRect(
      x: min(x, other.x), y: min(y, other.y),
      width: abs(other.x - x), height: abs(other.y - y))
  }
}
