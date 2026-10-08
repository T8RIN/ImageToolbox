import CoreGraphics
import Foundation

/// Rebuilds a target picture from many small tiles. Each cell gets the tile whose average colour is closest.
public enum Photomosaic {

  /// - Parameters:
  ///   - target: picture to imitate.
  ///   - tiles: candidate pictures. They are cropped to squares and scaled to `tileSize`.
  ///   - tileSize: edge length of one tile in the output, in pixels.
  ///   - columns: number of tiles across. Rows follow the target's aspect ratio.
  public static func build(target: CGImage, tiles: [CGImage], tileSize: Int, columns: Int)
    -> CGImage?
  {
    guard !tiles.isEmpty, tileSize > 0, columns > 0 else { return nil }
    let rows = max(
      1, Int((Double(target.height) / Double(target.width) * Double(columns)).rounded()))

    let squares: [CGImage] = tiles.compactMap { tile in
      let side = min(tile.width, tile.height)
      let rect = CGRect(
        x: (tile.width - side) / 2, y: (tile.height - side) / 2, width: side, height: side)
      return ImageGeometry.crop(tile, to: rect).flatMap {
        ImageGeometry.resize($0, width: tileSize, height: tileSize)
      }
    }
    let averages = squares.compactMap { tile in RasterImage(tile).map(averageColor) }
    guard averages.count == squares.count, !averages.isEmpty else { return nil }

    guard let cells = ImageGeometry.resize(target, width: columns, height: rows),
      let cellRaster = RasterImage(cells),
      let context = ImageGeometry.makeContext(width: columns * tileSize, height: rows * tileSize)
    else { return nil }

    for row in 0..<rows {
      // Checked per row, so a long mosaic can be cancelled from the calling task.
      guard !Task.isCancelled else { return nil }
      for column in 0..<columns {
        let wanted = cellRaster.pixel(x: column, y: row)
        let index = nearest(to: wanted, in: averages)
        // Core Graphics origin is bottom-left, so flip the row index.
        let y = (rows - 1 - row) * tileSize
        context.draw(
          squares[index], in: CGRect(x: column * tileSize, y: y, width: tileSize, height: tileSize))
      }
    }
    return context.makeImage()
  }

  static func averageColor(_ raster: RasterImage) -> RGBA {
    var red = 0
    var green = 0
    var blue = 0
    let count = raster.width * raster.height
    for index in stride(from: 0, to: raster.pixels.count, by: 4) {
      red += Int(raster.pixels[index])
      green += Int(raster.pixels[index + 1])
      blue += Int(raster.pixels[index + 2])
    }
    guard count > 0 else { return RGBA(red: 0, green: 0, blue: 0) }
    return RGBA(red: UInt8(red / count), green: UInt8(green / count), blue: UInt8(blue / count))
  }

  static func nearest(to color: RGBA, in palette: [RGBA]) -> Int {
    var best = 0
    var bestDistance = Int.max
    for (index, candidate) in palette.enumerated() {
      let dr = Int(color.red) - Int(candidate.red)
      let dg = Int(color.green) - Int(candidate.green)
      let db = Int(color.blue) - Int(candidate.blue)
      let distance = dr * dr + dg * dg + db * db
      if distance < bestDistance {
        bestDistance = distance
        best = index
      }
    }
    return best
  }
}
