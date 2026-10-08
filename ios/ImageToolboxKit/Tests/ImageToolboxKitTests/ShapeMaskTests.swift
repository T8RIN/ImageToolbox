import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Crop shapes")
struct ShapeMaskTests {

  @Test("there are about thirty shapes, as in the Android app")
  func shapeCount() {
    #expect(CropShape.allCases.count >= 27)
  }

  @Test("every shape has a non-empty area inside the rect")
  func shapesFitRect() {
    let rect = CGRect(x: 0, y: 0, width: 100, height: 80)
    for shape in CropShape.allCases {
      let bounds = shape.path(in: rect).boundingBoxOfPath
      #expect(bounds.width > 0 && bounds.height > 0, "\(shape) is empty")
      #expect(rect.insetBy(dx: -1, dy: -1).contains(bounds), "\(shape) leaves the rect")
    }
  }

  @Test("a rounded or pointed shape leaves the corner transparent")
  func cornerTransparent() throws {
    let image = TestImages.solid(RGBA(red: 10, green: 200, blue: 90), width: 60, height: 60)
    // Kotlin logo has vertices in the corners by design, so its corner stays filled.
    for shape in CropShape.allCases where shape != .rectangle && shape != .kotlinLogo {
      let masked = try #require(ShapeMask.apply(image, shape: shape))
      let raster = try #require(RasterImage(masked))
      #expect(raster.pixel(x: 0, y: 0).alpha < 255, "\(shape) keeps the corner")
    }
  }
}

@Suite("Image mask for crop")
struct ImageMaskTests {

  @Test("a white mask keeps the picture and a black mask makes it transparent")
  func maskPolarity() throws {
    let picture = TestImages.solid(RGBA(red: 10, green: 200, blue: 90), width: 32, height: 32)
    let white = TestImages.solid(RGBA(red: 255, green: 255, blue: 255), width: 16, height: 16)
    let black = TestImages.solid(RGBA(red: 0, green: 0, blue: 0), width: 16, height: 16)

    let kept = try #require(ShapeMask.apply(picture, mask: white))
    #expect(try #require(RasterImage(kept)).pixel(x: 16, y: 16).alpha == 255)

    let cut = try #require(ShapeMask.apply(picture, mask: black))
    #expect(try #require(RasterImage(cut)).pixel(x: 16, y: 16).alpha == 0)
  }

  @Test("the new shapes exist and fit the rect")
  func newShapes() {
    let rect = CGRect(x: 0, y: 0, width: 100, height: 100)
    for shape in [CropShape.kotlinLogo, .burger, .enhancedHeart] {
      let bounds = shape.path(in: rect).boundingBoxOfPath
      #expect(bounds.width > 0 && bounds.height > 0)
      #expect(rect.insetBy(dx: -1, dy: -1).contains(bounds))
    }
    #expect(CropShape.allCases.count == 30)
  }
}
