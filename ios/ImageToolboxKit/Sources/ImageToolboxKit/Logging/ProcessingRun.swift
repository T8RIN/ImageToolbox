import Foundation
import os

/// One processing job: a batch, an export or an edit. Every log line it writes carries the same
/// short `id`, so the lines of one run can be picked out of the log.
public struct ProcessingRun: Sendable {
  public let id: String
  public let label: String
  private let started: ContinuousClock.Instant

  private static let logger = Logger(subsystem: "com.the80hz.imagetoolbox", category: "processing")

  public static func begin(_ label: String, itemCount: Int) -> ProcessingRun {
    let run = ProcessingRun(
      id: String(UUID().uuidString.prefix(8)).lowercased(),
      label: label,
      started: .now)
    logger.info(
      "run \(run.id, privacy: .public) start \(label, privacy: .public) items=\(itemCount)")
    return run
  }

  public func finish() {
    Self.logger.info(
      "run \(id, privacy: .public) finish \(label, privacy: .public) ms=\(elapsedMilliseconds)")
  }

  public func cancel() {
    Self.logger.notice(
      "run \(id, privacy: .public) cancelled \(label, privacy: .public) ms=\(elapsedMilliseconds)")
  }

  public func fail(_ error: any Error) {
    Self.logger.error(
      "run \(id, privacy: .public) failed \(label, privacy: .public): \(String(describing: error), privacy: .public)"
    )
  }

  private var elapsedMilliseconds: Int64 {
    let duration = started.duration(to: .now)
    let attosecondsPerMillisecond: Int64 = 1_000_000_000_000_000
    return duration.components.seconds * 1000
      + duration.components.attoseconds / attosecondsPerMillisecond
  }
}
