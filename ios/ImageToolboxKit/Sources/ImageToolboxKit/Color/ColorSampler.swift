import CoreGraphics
import Foundation

/// Reads a colour from a picked point. Used by the colour picker tool.
public enum ColorSampler {

  /// `point` is in normalised image coordinates, 0...1 on both axes, origin top-left.
  /// Points outside the image are clamped to the nearest edge pixel.
  public static func sample(_ raster: RasterImage, at point: CGPoint) -> RGBA {
    let x = Int((min(max(point.x, 0), 1) * CGFloat(raster.width - 1)).rounded())
    let y = Int((min(max(point.y, 0), 1) * CGFloat(raster.height - 1)).rounded())
    return raster.pixel(x: x, y: y)
  }
}
