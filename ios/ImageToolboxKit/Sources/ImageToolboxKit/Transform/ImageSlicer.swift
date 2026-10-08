import CoreGraphics
import Foundation

public enum SliceAxis: Sendable, CaseIterable {
  /// Cuts by horizontal lines, producing stacked parts.
  case rows
  /// Cuts by vertical lines, producing side-by-side parts.
  case columns
}

public enum ImageSlicer {

  /// Splits into `parts` equal pieces. The last piece takes any leftover pixels.
  public static func split(_ image: CGImage, parts: Int, axis: SliceAxis) -> [CGImage] {
    guard parts > 0 else { return [] }
    let length = axis == .rows ? image.height : image.width
    guard parts <= length else { return [] }

    let step = length / parts
    return (0..<parts).compactMap { index in
      let start = index * step
      let end = index == parts - 1 ? length : start + step
      return slice(image, axis: axis, from: start, to: end)
    }
  }

  /// Removes the band `[start, end)` along `axis` and joins what is left.
  /// When `keepOnlyCut` is true, returns just the removed band instead.
  public static func cut(
    _ image: CGImage,
    axis: SliceAxis,
    from start: Int,
    to end: Int,
    keepOnlyCut: Bool = false
  ) -> CGImage? {
    let length = axis == .rows ? image.height : image.width
    let lower = max(0, min(start, length))
    let upper = max(lower, min(end, length))
    guard upper > lower else { return nil }
    if keepOnlyCut { return slice(image, axis: axis, from: lower, to: upper) }

    let before = slice(image, axis: axis, from: 0, to: lower)
    let after = slice(image, axis: axis, from: upper, to: length)
    let parts = [before, after].compactMap { $0 }
    guard !parts.isEmpty else { return nil }
    return ImageStitcher.stitch(
      parts,
      direction: axis == .rows ? .vertical : .horizontal,
      spacing: 0,
      background: RGBA(red: 0, green: 0, blue: 0, alpha: 0))
  }

  static func slice(_ image: CGImage, axis: SliceAxis, from start: Int, to end: Int) -> CGImage? {
    let rect =
      axis == .rows
      ? CGRect(x: 0, y: start, width: image.width, height: end - start)
      : CGRect(x: start, y: 0, width: end - start, height: image.height)
    return ImageGeometry.crop(image, to: rect)
  }
}
