import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Resize, weight, slice, shape, stitch and stack")
struct TransformTests {

  private let red = RGBA(red: 255, green: 0, blue: 0)
  private let blue = RGBA(red: 0, green: 0, blue: 255)

  @Test("resize modes produce the expected sizes")
  func resizeModes() throws {
    let image = TestImages.solid(red, width: 200, height: 100)
    let exact = try #require(
      ImageResize.resize(image, to: CGSize(width: 50, height: 50), mode: .exact))
    #expect(exact.width == 50 && exact.height == 50)

    let fit = try #require(
      ImageResize.resize(image, to: CGSize(width: 100, height: 100), mode: .fit))
    #expect(fit.width == 100 && fit.height == 50)

    let fill = try #require(
      ImageResize.resize(image, to: CGSize(width: 100, height: 100), mode: .fill))
    #expect(fill.width == 100 && fill.height == 100)
  }

  @Test("weight fit keeps the JPEG under the byte budget")
  func weightFit() throws {
    var generator = SeededGenerator(seed: 11)
    let noise = TestImages.image(width: 128, height: 128) { _, _ in
      RGBA(
        red: UInt8.random(in: 0...255, using: &generator),
        green: UInt8.random(in: 0...255, using: &generator),
        blue: UInt8.random(in: 0...255, using: &generator))
    }
    let fitted = try #require(SizeFitter.fit(noise, format: .jpeg, maxBytes: 20_000))
    #expect(fitted.data.count <= 20_000)
    #expect(fitted.quality < 1)
  }

  @Test("split into rows and columns gives equal parts, last one takes the remainder")
  func split() throws {
    let image = TestImages.solid(red, width: 10, height: 10)
    let rows = ImageSlicer.split(image, parts: 3, axis: .rows)
    #expect(rows.map(\.height) == [3, 3, 4])
    let columns = ImageSlicer.split(image, parts: 2, axis: .columns)
    #expect(columns.map(\.width) == [5, 5])
  }

  @Test("cut removes the band and joins the rest, or keeps only the band")
  func cut() throws {
    let image = TestImages.solid(red, width: 10, height: 4)
    let removed = try #require(ImageSlicer.cut(image, axis: .columns, from: 2, to: 5))
    #expect(removed.width == 7 && removed.height == 4)

    let kept = try #require(
      ImageSlicer.cut(image, axis: .columns, from: 2, to: 5, keepOnlyCut: true))
    #expect(kept.width == 3)
  }

  @Test("oval mask makes the corners transparent and keeps the centre")
  func ovalMask() throws {
    let image = TestImages.solid(blue, width: 40, height: 40)
    let masked = try #require(ShapeMask.apply(image, shape: .oval))
    let raster = try #require(RasterImage(masked))
    #expect(raster.pixel(x: 0, y: 0).alpha == 0)
    #expect(raster.pixel(x: 20, y: 20) == blue)
  }

  @Test("stitching sums widths horizontally and keeps the tallest height")
  func stitch() throws {
    let wide = TestImages.solid(red, width: 30, height: 10)
    let tall = TestImages.solid(blue, width: 20, height: 25)
    let joined = try #require(
      ImageStitcher.stitch([wide, tall], direction: .horizontal, spacing: 5))
    #expect(joined.width == 55 && joined.height == 25)

    let stacked = try #require(ImageStitcher.stitch([wide, tall], direction: .vertical))
    #expect(stacked.width == 30 && stacked.height == 35)
  }

  @Test("average stacking of two frames gives the midpoint")
  func stackAverage() throws {
    let dark = TestImages.solid(RGBA(red: 0, green: 0, blue: 0), width: 4, height: 4)
    let light = TestImages.solid(RGBA(red: 200, green: 100, blue: 50), width: 4, height: 4)
    let stacked = try #require(ImageStacker.stack([dark, light], mode: .average))
    let raster = try #require(RasterImage(stacked))
    #expect(raster.pixel(x: 1, y: 1) == RGBA(red: 100, green: 50, blue: 25))
  }

  @Test("screenshot frame adds padding and keeps the picture in the middle")
  func screenshotFrame() throws {
    let picture = TestImages.solid(red, width: 100, height: 60)
    let framed = try #require(ScreenshotFrame(padding: 20).render(picture))
    #expect(framed.width == 140 && framed.height == 100)
    let raster = try #require(RasterImage(framed))
    #expect(raster.pixel(x: 70, y: 50) == red)
  }

  @Test("linear gradient runs between its stops")
  func linearGradient() throws {
    let stops = [
      GradientStop(color: RGBA(red: 0, green: 0, blue: 0), location: 0),
      GradientStop(color: RGBA(red: 255, green: 255, blue: 255), location: 1),
    ]
    let image = try #require(
      GradientRenderer.linear(stops: stops, angle: 0, width: 100, height: 10))
    let raster = try #require(RasterImage(image))
    #expect(raster.pixel(x: 0, y: 5).red < 20)
    #expect(raster.pixel(x: 99, y: 5).red > 235)
  }

  @Test("photomosaic uses the tile whose average matches each cell")
  func photomosaic() throws {
    let target = TestImages.halves(left: red, right: blue, width: 8, height: 4)
    let mosaic = try #require(
      Photomosaic.build(
        target: target, tiles: [red, blue].map { TestImages.solid($0, width: 16, height: 16) },
        tileSize: 4, columns: 2))
    #expect(mosaic.width == 8 && mosaic.height == 4)
    let raster = try #require(RasterImage(mosaic))
    #expect(raster.pixel(x: 0, y: 0) == red)
    #expect(raster.pixel(x: 7, y: 3) == blue)
  }

  @Test("export profiles: exact sizes fill, limits keep aspect within the box")
  func exportProfiles() throws {
    let image = TestImages.solid(red, width: 400, height: 200)
    let square = try #require(
      ExportProfile.builtIn.first { $0.name == "Square Post" && $0.group == "Instagram" })
    let out = try #require(square.apply(to: image))
    #expect(out.width == 1080 && out.height == 1080)

    let thumbnail = try #require(ExportProfile.builtIn.first { $0.name == "Thumbnail" })
    let small = try #require(thumbnail.apply(to: image))
    #expect(small.width == 640 && small.height == 320)
    #expect(ExportProfile.builtIn.count == 61)
  }
}
