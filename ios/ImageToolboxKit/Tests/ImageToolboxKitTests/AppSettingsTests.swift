import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("App settings")
struct AppSettingsTests {

  /// A fresh defaults suite for each test, removed afterwards.
  private func withDefaults(_ body: (UserDefaults) throws -> Void) rethrows {
    let name = "settings-test-\(UUID().uuidString)"
    let defaults = UserDefaults(suiteName: name)!
    defer { defaults.removePersistentDomain(forName: name) }
    try body(defaults)
  }

  @Test("nothing stored gives the defaults")
  func defaultsWhenEmpty() {
    withDefaults { defaults in
      #expect(AppSettings.load(from: defaults) == AppSettings())
      #expect(AppSettings().keepMetadata == false)
      #expect(AppSettings().naming == .fixed)
    }
  }

  @Test("saved settings load back unchanged")
  func saveAndLoad() {
    withDefaults { defaults in
      let settings = AppSettings(keepMetadata: true, naming: .template("{name}_{n:2}"))
      settings.save(to: defaults)
      #expect(AppSettings.load(from: defaults) == settings)
    }
  }

  @Test("an unreadable stored value falls back to the defaults")
  func corruptValueFallsBack() {
    withDefaults { defaults in
      defaults.set(Data("not json".utf8), forKey: AppSettings.defaultsKey)
      #expect(AppSettings.load(from: defaults) == AppSettings())
    }
  }
}
