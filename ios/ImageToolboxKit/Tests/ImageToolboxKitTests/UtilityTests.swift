import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Remote loader, statistics and help")
struct UtilityTests {

  @Test("bare hosts get https, other schemes are rejected")
  func urlParsing() {
    #expect(
      RemoteImageLoader.url(from: "example.com/a.png")?.absoluteString
        == "https://example.com/a.png")
    #expect(RemoteImageLoader.url(from: "http://example.com")?.scheme == "http")
    #expect(RemoteImageLoader.url(from: "ftp://example.com/a.png") == nil)
    #expect(RemoteImageLoader.url(from: "   ") == nil)
  }

  @Test("usage statistics count, rank and persist")
  func usageStatistics() throws {
    var stats = UsageStatistics()
    stats.record("base64")
    stats.record("base64")
    stats.record("exif")
    #expect(stats.total == 3)
    #expect(stats.top(1).first?.key == "base64")

    let suite = try #require(UserDefaults(suiteName: "UsageStatisticsTests-\(UUID().uuidString)"))
    stats.save(to: suite, key: "stats")
    #expect(UsageStatistics.load(from: suite, key: "stats") == stats)
  }

  @Test("help search matches every word, case-insensitively")
  func helpSearch() {
    let entries = [
      HelpEntry(id: "a", title: "Rotate", body: "Turn the picture by quarter turns"),
      HelpEntry(id: "b", title: "Noise", body: "Generate fractal textures", keywords: ["perlin"]),
    ]
    #expect(HelpSearch.results(for: "QUARTER turns", in: entries).map(\.id) == ["a"])
    #expect(HelpSearch.results(for: "perlin", in: entries).map(\.id) == ["b"])
    #expect(HelpSearch.results(for: "rotate noise", in: entries).isEmpty)
    #expect(HelpSearch.results(for: "", in: entries).count == 2)
  }
}
