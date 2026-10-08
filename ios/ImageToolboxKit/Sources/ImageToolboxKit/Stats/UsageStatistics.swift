import Foundation

/// Counts how often each tool is opened. Stored as JSON so it survives relaunches.
public struct UsageStatistics: Codable, Equatable, Sendable {
  public private(set) var counts: [String: Int]

  public init(counts: [String: Int] = [:]) {
    self.counts = counts
  }

  public var total: Int {
    counts.values.reduce(0, +)
  }

  public mutating func record(_ key: String) {
    counts[key, default: 0] += 1
  }

  /// Most used keys first. Ties are ordered by key, so the result is stable.
  public func top(_ limit: Int) -> [(key: String, count: Int)] {
    counts
      .sorted { lhs, rhs in lhs.value != rhs.value ? lhs.value > rhs.value : lhs.key < rhs.key }
      .prefix(limit)
      .map { (key: $0.key, count: $0.value) }
  }

  public static func load(from defaults: UserDefaults, key: String) -> UsageStatistics {
    guard let data = defaults.data(forKey: key),
      let stored = try? JSONDecoder().decode(UsageStatistics.self, from: data)
    else { return UsageStatistics() }
    return stored
  }

  public func save(to defaults: UserDefaults, key: String) {
    guard let data = try? JSONEncoder().encode(self) else { return }
    defaults.set(data, forKey: key)
  }
}
