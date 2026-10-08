import Foundation
import OSLog
import Observation
import os

/// Reads the log entries this process wrote to its own subsystem during the last hour.
enum AppLogReader {
  static let subsystem = "com.the80hz.imagetoolbox"
  private static let window: TimeInterval = 60 * 60
  private static let maxLines = 200

  /// Returns the newest lines, oldest first, as `time [category] message`.
  static func readRecentLines() throws -> String {
    let store = try OSLogStore(scope: .currentProcessIdentifier)
    let position = store.position(date: Date().addingTimeInterval(-window))
    let lines = try store.getEntries(at: position).compactMap { entry -> String? in
      guard let log = entry as? OSLogEntryLog, log.subsystem == subsystem else { return nil }
      let time = log.date.formatted(date: .omitted, time: .standard)
      return "\(time) [\(log.category)] \(log.composedMessage)"
    }
    return lines.suffix(maxLines).joined(separator: "\n")
  }
}

@MainActor
@Observable
final class AppLogsToolModel {
  private static let logger = Logger(subsystem: AppLogReader.subsystem, category: "app-logs")

  private(set) var text = ""
  private(set) var isLoading = false
  private(set) var failed = false

  func refresh() async {
    guard !isLoading else { return }
    isLoading = true
    defer { isLoading = false }

    do {
      let lines = try await Task.detached(priority: .utility) {
        try AppLogReader.readRecentLines()
      }.value
      failed = false
      text = lines.isEmpty ? "No log entries in the last hour." : lines
    } catch {
      failed = true
      Self.logger.error(
        "Reading the app log failed: \(error.localizedDescription, privacy: .public)")
    }
  }
}
