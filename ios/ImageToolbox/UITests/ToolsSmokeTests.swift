import XCTest

/// Opens every tool and checks that its screen appears. Each tool gets a fresh launch, and the
/// grid is filtered with the search field by the tool's English title, so no scrolling is needed.
/// It catches broken routing and crashes on first render; it does not check the image logic.
final class ToolsSmokeTests: XCTestCase {

  override func setUpWithError() throws {
    continueAfterFailure = false
  }

  @MainActor
  func testEveryToolOpensItsScreen() throws {
    for tool in UITestTools.all {
      let app = XCUIApplication()
      app.launchArguments += ["-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
      app.launch()

      let search = app.searchFields.firstMatch
      XCTAssertTrue(search.waitForExistence(timeout: 10), "search field missing for \(tool.id)")
      search.tap()
      search.typeText(tool.title)

      let card = app.buttons.matching(identifier: "tool.\(tool.id)").firstMatch
      XCTAssertTrue(card.waitForExistence(timeout: 5), "card \(tool.id) not found by its title")
      card.tap()

      let screen = app.descendants(matching: .any).matching(identifier: "screen.\(tool.id)")
        .firstMatch
      XCTAssertTrue(screen.waitForExistence(timeout: 10), "screen \(tool.id) did not appear")
      app.terminate()
    }
  }
}
