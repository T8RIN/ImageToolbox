import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Colour model")
struct ColorAndSamplingTests {

  @Test("hex parses short, long and alpha forms")
  func hexParsing() {
    #expect(RGBA(hex: "#F00") == RGBA(red: 255, green: 0, blue: 0))
    #expect(RGBA(hex: "00ff00") == RGBA(red: 0, green: 255, blue: 0))
    #expect(RGBA(hex: "#0000FF80") == RGBA(red: 0, green: 0, blue: 255, alpha: 128))
    #expect(RGBA(hex: "#12345") == nil)
    #expect(RGBA(hex: "zzzzzz") == nil)
  }

  @Test("hexString round-trips and omits opaque alpha")
  func hexString() {
    #expect(RGBA(red: 255, green: 128, blue: 0).hexString == "#FF8000")
    #expect(RGBA(red: 255, green: 128, blue: 0, alpha: 64).hexString == "#FF800040")
  }

  @Test("HSL and HSV of pure red")
  func redHSL() {
    let red = RGBA(red: 255, green: 0, blue: 0)
    #expect(red.hsl.hue == 0)
    #expect(red.hsl.saturation == 100)
    #expect(red.hsl.lightness == 50)
    #expect(red.hsv.value == 100)
  }

  @Test("hue constructor reproduces the primaries")
  func hueConstructor() {
    #expect(RGBA(hue: 0, saturation: 100, value: 100) == RGBA(red: 255, green: 0, blue: 0))
    #expect(RGBA(hue: 120, saturation: 100, value: 100) == RGBA(red: 0, green: 255, blue: 0))
    #expect(RGBA(hue: 240, saturation: 100, value: 100) == RGBA(red: 0, green: 0, blue: 255))
  }

  @Test("CMYK of black is pure key")
  func cmykBlack() {
    let black = RGBA(red: 0, green: 0, blue: 0).cmyk
    #expect(black.key == 100)
    #expect(black.cyan == 0)
  }

  @Test("WCAG contrast of black on white is 21")
  func contrast() {
    let ratio = RGBA(red: 0, green: 0, blue: 0).contrastRatio(
      with: RGBA(red: 255, green: 255, blue: 255))
    #expect(abs(ratio - 21) < 0.001)
  }

  @Test("triadic harmony keeps saturation and value")
  func triadic() {
    let base = RGBA(hue: 30, saturation: 80, value: 90)
    let colours = base.triadic
    #expect(colours.count == 2)
    #expect(colours.allSatisfy { abs($0.hsv.saturation - base.hsv.saturation) < 1.5 })
  }

  @Test("mixing at 0.5 gives the midpoint")
  func mixing() {
    let mixed = RGBA(red: 0, green: 0, blue: 0).mixed(
      with: RGBA(red: 200, green: 100, blue: 50), fraction: 0.5)
    #expect(mixed == RGBA(red: 100, green: 50, blue: 25))
  }

  @Test("colour sampler clamps and reads the expected pixel")
  func sampler() throws {
    let image = TestImages.halves(
      left: RGBA(red: 255, green: 0, blue: 0), right: RGBA(red: 0, green: 0, blue: 255), width: 10,
      height: 10)
    let raster = try #require(RasterImage(image))
    #expect(
      ColorSampler.sample(raster, at: CGPoint(x: 0.05, y: 0.5)) == RGBA(red: 255, green: 0, blue: 0)
    )
    #expect(
      ColorSampler.sample(raster, at: CGPoint(x: 0.95, y: 0.5)) == RGBA(red: 0, green: 0, blue: 255)
    )
    #expect(
      ColorSampler.sample(raster, at: CGPoint(x: 5, y: -3)) == RGBA(red: 0, green: 0, blue: 255))
  }
}
