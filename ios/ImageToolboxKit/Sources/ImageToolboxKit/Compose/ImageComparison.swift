import CoreGraphics
import Foundation

/// Builds the visual forms of a before/after comparison.
public enum ImageComparison {

  /// Left and right images next to each other, scaled to the same height.
  public static func sideBySide(_ left: CGImage, _ right: CGImage, spacing: Int = 8) -> CGImage? {
    let height = max(left.height, right.height)
    let leftScaled = scaledToHeight(left, height)
    let rightScaled = scaledToHeight(right, height)
    guard let leftScaled, let rightScaled else { return nil }
    return ImageStitcher.stitch(
      [leftScaled, rightScaled], direction: .horizontal, spacing: spacing)
  }

  /// `left` up to `fraction` of the width, `right` after it. Both are resized to the left image.
  public static func slider(_ left: CGImage, _ right: CGImage, fraction: Double) -> CGImage? {
    let width = left.width
    let height = left.height
    guard let rightScaled = ImageGeometry.resize(right, width: width, height: height),
      let context = ImageGeometry.makeContext(width: width, height: height)
    else { return nil }

    let split = Int((Double(width) * min(max(fraction, 0), 1)).rounded())
    context.draw(rightScaled, in: CGRect(x: 0, y: 0, width: width, height: height))
    context.saveGState()
    context.clip(to: CGRect(x: 0, y: 0, width: split, height: height))
    context.draw(left, in: CGRect(x: 0, y: 0, width: width, height: height))
    context.restoreGState()
    return context.makeImage()
  }

  /// Frames of the slider sweeping left to right and back. Used for the animated GIF export.
  public static func sliderFrames(_ left: CGImage, _ right: CGImage, steps: Int) -> [CGImage] {
    guard steps > 1 else { return [] }
    let forward = (0..<steps).map { Double($0) / Double(steps - 1) }
    let backward = forward.dropFirst().dropLast().reversed()
    return (forward + backward).compactMap { slider(left, right, fraction: $0) }
  }

  private static func scaledToHeight(_ image: CGImage, _ height: Int) -> CGImage? {
    guard image.height != height else { return image }
    let width = max(1, Int((Double(image.width) * Double(height) / Double(image.height)).rounded()))
    return ImageGeometry.resize(image, width: width, height: height)
  }
}
