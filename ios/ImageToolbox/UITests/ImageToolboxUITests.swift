import XCTest

final class ImageToolboxUITests: XCTestCase {
  override func setUpWithError() throws {
    continueAfterFailure = false
  }

  /// Pins the locale so assertions do not depend on the simulator language.
  @MainActor
  private func launchApp() -> XCUIApplication {
    let app = XCUIApplication()
    app.launchArguments += ["-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
    app.launch()
    return app
  }

  @MainActor
  func testOpensBase64ToolFromGrid() throws {
    let app = launchApp()

    let base64Card = app.buttons.matching(identifier: "tool.base64").firstMatch
    XCTAssertTrue(base64Card.waitForExistence(timeout: 5))

    base64Card.tap()

    XCTAssertTrue(app.navigationBars["Base64"].waitForExistence(timeout: 5))
  }

  @MainActor
  func testDecodesBase64PngIntoImage() throws {
    // 1x1 PNG
    let pngBase64 =
      "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg=="

    let app = launchApp()

    app.buttons.matching(identifier: "tool.base64").firstMatch.tap()

    let input = app.textViews.matching(identifier: "base64.input").firstMatch
    XCTAssertTrue(input.waitForExistence(timeout: 5))
    input.tap()
    input.typeText(pngBase64)

    app.buttons.matching(identifier: "base64.decode").firstMatch.tap()

    XCTAssertTrue(
      app.buttons.matching(identifier: "base64.decodedShare").firstMatch.waitForExistence(
        timeout: 5))
    XCTAssertFalse(app.staticTexts.matching(identifier: "base64.error").firstMatch.exists)
  }
}

extension ImageToolboxUITests {

  /// Picks a picture from the photo library and checks that its Base64 text appears.
  /// The sample picture is added to the simulator library with `simctl addmedia` before the run.
  @MainActor
  func testEncodesPickedPicture() throws {
    let app = XCUIApplication()
    app.launchArguments += ["-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
    app.launch()

    app.buttons.matching(identifier: "tool.base64").firstMatch.tap()
    let pick = app.buttons.matching(identifier: "base64.pick").firstMatch
    XCTAssertTrue(pick.waitForExistence(timeout: 10))
    pick.tap()

    let photo = app.images.firstMatch
    XCTAssertTrue(photo.waitForExistence(timeout: 20))
    photo.tap()

    let encoded = app.staticTexts.matching(NSPredicate(format: "label BEGINSWITH 'Encoded'"))
      .firstMatch
    XCTAssertTrue(encoded.waitForExistence(timeout: 30))
  }
}
