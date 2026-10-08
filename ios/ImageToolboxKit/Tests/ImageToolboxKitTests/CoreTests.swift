import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Core geometry and raster")
struct CoreTests {

  private let red = RGBA(red: 255, green: 0, blue: 0)
  private let blue = RGBA(red: 0, green: 0, blue: 255)

  @Test("rotate clockwise by 90 moves the left half to the top")
  func rotateClockwise() throws {
    let image = TestImages.halves(left: red, right: blue, width: 4, height: 2)
    let rotated = try #require(ImageGeometry.rotate(image, quarterTurnsClockwise: 1))
    #expect(rotated.width == 2)
    #expect(rotated.height == 4)

    let raster = try #require(RasterImage(rotated))
    #expect(raster.pixel(x: 0, y: 0) == red)
    #expect(raster.pixel(x: 0, y: 3) == blue)
  }

  @Test("rotate normalises negative and large turn counts")
  func rotateNormalises() throws {
    let image = TestImages.solid(red, width: 3, height: 5)
    let minusOne = try #require(ImageGeometry.rotate(image, quarterTurnsClockwise: -1))
    #expect(minusOne.width == 5 && minusOne.height == 3)
    let four = try #require(ImageGeometry.rotate(image, quarterTurnsClockwise: 4))
    #expect(four.width == 3 && four.height == 5)
  }

  @Test("horizontal flip swaps left and right")
  func flipHorizontal() throws {
    let image = TestImages.halves(left: red, right: blue, width: 4, height: 2)
    let flipped = try #require(ImageGeometry.flip(image, horizontal: true))
    let raster = try #require(RasterImage(flipped))
    #expect(raster.pixel(x: 0, y: 0) == blue)
    #expect(raster.pixel(x: 3, y: 0) == red)
  }

  @Test("vertical flip keeps columns and swaps rows")
  func flipVertical() throws {
    let image = TestImages.image(width: 2, height: 2) { _, y in y == 0 ? red : blue }
    let flipped = try #require(ImageGeometry.flip(image, horizontal: false))
    let raster = try #require(RasterImage(flipped))
    #expect(raster.pixel(x: 0, y: 0) == blue)
    #expect(raster.pixel(x: 1, y: 1) == red)
  }

  @Test("crop clamps the rect to the image")
  func cropClamps() throws {
    let image = TestImages.solid(red, width: 10, height: 10)
    let cropped = try #require(
      ImageGeometry.crop(image, to: CGRect(x: 8, y: 8, width: 10, height: 10)))
    #expect(cropped.width == 2 && cropped.height == 2)
    #expect(ImageGeometry.crop(image, to: CGRect(x: 20, y: 20, width: 5, height: 5)) == nil)
  }

  @Test("fitSize keeps aspect ratio and does not upscale by default")
  func fitSize() {
    let fitted = ImageGeometry.fitSize(
      width: 400, height: 200, within: CGSize(width: 100, height: 100))
    #expect(fitted == CGSize(width: 100, height: 50))
    let small = ImageGeometry.fitSize(
      width: 40, height: 20, within: CGSize(width: 100, height: 100))
    #expect(small == CGSize(width: 40, height: 20))
    let upscaled = ImageGeometry.fitSize(
      width: 40, height: 20, within: CGSize(width: 100, height: 100), allowUpscale: true)
    #expect(upscaled == CGSize(width: 100, height: 50))
  }

  @Test("coverRect centres a wider source and overflows the target")
  func coverRect() {
    let rect = ImageGeometry.coverRect(
      sourceWidth: 200, sourceHeight: 100, target: CGSize(width: 100, height: 100))
    #expect(rect.width == 200 && rect.height == 100)
    #expect(rect.minX == -50 && rect.minY == 0)
  }

  @Test("EXIF orientation 6 rotates the stored image clockwise")
  func orientationSix() throws {
    let image = TestImages.halves(left: red, right: blue, width: 4, height: 2)
    let upright = try #require(ImageGeometry.applyOrientation(image, exifOrientation: 6))
    #expect(upright.width == 2 && upright.height == 4)
    let raster = try #require(RasterImage(upright))
    #expect(raster.pixel(x: 0, y: 0) == red)
  }

  @Test("RasterImage round-trips through CGImage")
  func rasterRoundTrip() throws {
    let source = TestImages.halves(left: red, right: blue, width: 6, height: 3)
    let raster = try #require(RasterImage(source))
    let rebuilt = try #require(raster.toCGImage())
    let back = try #require(RasterImage(rebuilt))
    #expect(back.pixels == raster.pixels)
  }

  @Test("PNG encode and decode keep pixels")
  func pngRoundTrip() throws {
    let source = TestImages.halves(left: red, right: blue, width: 8, height: 4)
    let data = try #require(ImageCodec.encode(source, as: .png))
    let decoded = try #require(ImageCodec.decode(data))
    let raster = try #require(RasterImage(decoded))
    #expect(raster.pixel(x: 0, y: 0) == red)
    #expect(raster.pixel(x: 7, y: 3) == blue)
  }

  @Test("ImageIO can write the core formats on every platform")
  func coreFormatsEncodable() {
    #expect(OutputFormat.png.isEncodable)
    #expect(OutputFormat.jpeg.isEncodable)
    #expect(OutputFormat.gif.isEncodable)
  }
}
