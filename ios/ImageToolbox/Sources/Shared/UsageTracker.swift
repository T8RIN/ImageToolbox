import Foundation
import ImageToolboxKit

/// Records which tools are opened. Stored in UserDefaults, read by the usage statistics screen.
enum UsageTracker {
  static let defaultsKey = "usage.statistics"

  static func record(_ tool: ToolID) {
    var stats = UsageStatistics.load(from: .standard, key: defaultsKey)
    stats.record(tool.rawValue)
    stats.save(to: .standard, key: defaultsKey)
  }
}
