import Foundation
import ImageToolboxKit
import Observation

/// One bar of the usage chart. `key` is a raw `ToolID` value, or an unknown key kept as is.
struct UsageItem: Identifiable, Sendable {
  let key: String
  let count: Int

  var id: String { key }
}

@MainActor
@Observable
final class UsageStatisticsToolModel {
  private(set) var statistics = UsageStatistics()

  var topItems: [UsageItem] {
    statistics.top(15).map { UsageItem(key: $0.key, count: $0.count) }
  }

  var maxCount: Int {
    topItems.first?.count ?? 0
  }

  func load() {
    statistics = UsageStatistics.load(from: .standard, key: UsageTracker.defaultsKey)
  }

  func reset() {
    let empty = UsageStatistics()
    empty.save(to: .standard, key: UsageTracker.defaultsKey)
    statistics = empty
  }
}
