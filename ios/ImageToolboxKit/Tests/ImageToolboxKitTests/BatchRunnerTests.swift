import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Batch runner")
struct BatchRunnerTests {

  /// Counts how many operations run at the same time.
  private actor Gauge {
    private(set) var running = 0
    private(set) var peak = 0

    func enter() {
      running += 1
      peak = max(peak, running)
    }

    func leave() {
      running -= 1
    }
  }

  @Test("results come back in input order even when tasks finish out of order")
  func keepsOrder() async throws {
    let inputs = Array(0..<40)
    let outputs = try await BatchRunner.run(inputs, concurrency: 8) { value in
      try await Task.sleep(for: .milliseconds(Int.random(in: 0...3)))
      return value * 2
    }
    #expect(outputs == inputs.map { $0 * 2 })
  }

  @Test("progress reports every item once and ends at the total")
  func reportsProgress() async throws {
    let recorded = ProgressBox()
    _ = try await BatchRunner.run(
      [1, 2, 3, 4, 5], concurrency: 2,
      operation: { $0 },
      progress: { report in recorded.append(report) })
    let all = recorded.values
    #expect(all.map(\.completed) == [1, 2, 3, 4, 5])
    #expect(all.allSatisfy { $0.total == 5 })
    #expect(all.last?.fraction == 1)
  }

  @Test("no more than the requested number of operations run at once")
  func boundsConcurrency() async throws {
    let gauge = Gauge()
    _ = try await BatchRunner.run(Array(0..<20), concurrency: 3) { value in
      await gauge.enter()
      try await Task.sleep(for: .milliseconds(5))
      await gauge.leave()
      return value
    }
    let peak = await gauge.peak
    #expect(peak >= 1)
    #expect(peak <= 3)
  }

  @Test("the first error stops the batch and is rethrown")
  func rethrowsFirstError() async {
    struct Broken: Error, Equatable {}
    await #expect(throws: Broken.self) {
      _ = try await BatchRunner.run(Array(0..<10), concurrency: 2) { value in
        if value == 4 { throw Broken() }
        return value
      }
    }
  }

  @Test("cancelling the surrounding task throws CancellationError")
  func cancellation() async {
    let task = Task {
      try await BatchRunner.run(Array(0..<100), concurrency: 2) { value in
        try await Task.sleep(for: .milliseconds(20))
        return value
      }
    }
    try? await Task.sleep(for: .milliseconds(30))
    task.cancel()
    await #expect(throws: CancellationError.self) {
      _ = try await task.value
    }
  }

  @Test("an empty batch returns no results and reports complete")
  func emptyBatch() async throws {
    let outputs = try await BatchRunner.run([Int]()) { $0 }
    #expect(outputs.isEmpty)
    #expect(BatchProgress(completed: 0, total: 0).fraction == 1)
  }
}

/// Collects progress reports from the batch's callback, which is `@Sendable`.
private final class ProgressBox: @unchecked Sendable {
  private let lock = NSLock()
  private var reports: [BatchProgress] = []

  func append(_ report: BatchProgress) {
    lock.withLock { reports.append(report) }
  }

  var values: [BatchProgress] {
    lock.withLock { reports }
  }
}
