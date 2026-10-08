import XCTest

/// Opens every tool and attaches a screenshot of each screen, once per appearance. The attachments
/// are for review. Each test checks only that the screens appear.
///
/// Light and dark come from the simulator's appearance, set outside the test with
/// `xcrun simctl ui <device> appearance light|dark`, because launch arguments do not change it.
/// Only the largest standard text size is set by a launch argument.
final class AppearanceAuditTests: XCTestCase {

  override func setUpWithError() throws {
    continueAfterFailure = true
  }

  @MainActor
  func testLightAppearance() throws {
    try auditEveryTool(variant: "light", arguments: [])
  }

  @MainActor
  func testDarkAppearance() throws {
    try auditEveryTool(variant: "dark", arguments: [])
  }

  @MainActor
  func testLargestTextAppearance() throws {
    try auditEveryTool(
      variant: "xxxl",
      arguments: ["-UIPreferredContentSizeCategoryName", "UICTContentSizeCategoryXXXL"])
  }

  @MainActor
  private func auditEveryTool(variant: String, arguments: [String]) throws {
    for tool in UITestTools.all {
      let app = XCUIApplication()
      app.launchArguments += ["-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
      app.launchArguments += arguments
      app.launch()

      let search = app.searchFields.firstMatch
      XCTAssertTrue(search.waitForExistence(timeout: 10), "search field missing")
      search.tap()
      search.typeText(tool.title)

      let card = app.buttons.matching(identifier: "tool.\(tool.id)").firstMatch
      if card.waitForExistence(timeout: 5) {
        card.tap()
      }

      let screen = app.descendants(matching: .any).matching(identifier: "screen.\(tool.id)")
        .firstMatch
      let appeared = screen.waitForExistence(timeout: 10)
      let attachment = XCTAttachment(screenshot: app.screenshot())
      attachment.name = "\(tool.id)-\(variant)"
      attachment.lifetime = .keepAlways
      add(attachment)
      XCTAssertTrue(appeared, "screen \(tool.id) did not appear in \(variant)")
      app.terminate()
    }
  }
}
