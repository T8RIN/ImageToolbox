import Foundation

/// Turns an image into text. Each character cell is replaced by one character picked from a ramp.
public enum AsciiArt {

  /// Dense characters for dark pixels, sparse for light ones.
  public static let defaultRamp = "@%#*+=-:. "

  /// - Parameters:
  ///   - columns: number of characters per line.
  ///   - aspectCorrection: character width divided by character height. Terminal glyphs are about 0.5.
  ///   - invert: when true, bright pixels get dense characters.
  public static func render(
    _ raster: RasterImage,
    columns: Int,
    ramp: String = defaultRamp,
    invert: Bool = false,
    aspectCorrection: Double = 0.5
  ) -> String {
    let symbols = Array(ramp)
    guard columns > 0, raster.width > 0, raster.height > 0, symbols.count > 1 else { return "" }

    let cellWidth = Double(raster.width) / Double(columns)
    let cellHeight = cellWidth / aspectCorrection
    let rows = max(1, Int((Double(raster.height) / cellHeight).rounded(.down)))

    var lines: [String] = []
    lines.reserveCapacity(rows)
    for row in 0..<rows {
      var line = ""
      line.reserveCapacity(columns)
      for column in 0..<columns {
        let luminance = averageLuminance(
          raster,
          x0: Double(column) * cellWidth,
          y0: Double(row) * cellHeight,
          x1: Double(column + 1) * cellWidth,
          y1: Double(row + 1) * cellHeight)
        // Index 0 of the ramp is the densest glyph, so dark pixels map to low indices by default.
        let position = invert ? 1 - luminance : luminance
        let index = min(symbols.count - 1, Int((position * Double(symbols.count - 1)).rounded()))
        line.append(symbols[index])
      }
      lines.append(line)
    }
    return lines.joined(separator: "\n")
  }

  private static func averageLuminance(
    _ raster: RasterImage,
    x0: Double,
    y0: Double,
    x1: Double,
    y1: Double
  ) -> Double {
    let startX = min(raster.width - 1, Int(x0))
    let endX = min(raster.width, max(startX + 1, Int(x1)))
    let startY = min(raster.height - 1, Int(y0))
    let endY = min(raster.height, max(startY + 1, Int(y1)))

    var sum = 0.0
    var count = 0.0
    for y in startY..<endY {
      for x in startX..<endX {
        sum += raster.luminance(x: x, y: y)
        count += 1
      }
    }
    return count == 0 ? 0 : sum / count
  }
}
