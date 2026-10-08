import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Code image renderer")
struct CodeRendererTests {

  @Test("renders one band per line and the requested theme background")
  func rendersCodeCard() throws {
    let code = "let answer = 42\n// done\nprint(\"hi\")"
    let image = try #require(CodeImageRenderer.render(code: code, language: .swift, theme: .dark))
    #expect(image.width > 100)
    #expect(image.height > 3 * 14)
    let raster = try #require(RasterImage(image))
    // The corner sits in the padding and must show the theme background.
    #expect(raster.pixel(x: 2, y: 2) == CodeTheme.dark.background)
  }

  @Test("empty code still renders a valid picture")
  func emptyCode() {
    #expect(CodeImageRenderer.render(code: "", language: .json, theme: .light) != nil)
  }
}
