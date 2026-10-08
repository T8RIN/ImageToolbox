import Foundation

/// How far a batch has got: `completed` items out of `total`.
public struct BatchProgress: Equatable, Sendable {
  public let completed: Int
  public let total: Int

  public init(completed: Int, total: Int) {
    self.completed = completed
    self.total = total
  }

  /// 0...1. An empty batch is already complete.
  public var fraction: Double {
    total == 0 ? 1 : Double(completed) / Double(total)
  }
}

/// Runs one operation per item with a bounded number of tasks in flight.
///
/// Results come back in the order of the input, whatever order the tasks finish in. The first
/// error stops the batch and is rethrown. Cancelling the surrounding task stops the batch and
/// throws `CancellationError`. Each run gets a `ProcessingRun` so its log lines share one id.
public enum BatchRunner {

  public static func run<Input: Sendable, Output: Sendable>(
    _ inputs: [Input],
    label: String = "batch",
    concurrency: Int = max(1, ProcessInfo.processInfo.activeProcessorCount),
    operation: @escaping @Sendable (Input) async throws -> Output,
    progress: @escaping @Sendable (BatchProgress) -> Void = { _ in }
  ) async throws -> [Output] {
    let run = ProcessingRun.begin(label, itemCount: inputs.count)
    do {
      let outputs = try await execute(
        inputs, limit: max(1, concurrency), operation: operation, progress: progress)
      run.finish()
      return outputs
    } catch is CancellationError {
      run.cancel()
      throw CancellationError()
    } catch {
      run.fail(error)
      throw error
    }
  }

  private static func execute<Input: Sendable, Output: Sendable>(
    _ inputs: [Input],
    limit: Int,
    operation: @escaping @Sendable (Input) async throws -> Output,
    progress: @escaping @Sendable (BatchProgress) -> Void
  ) async throws -> [Output] {
    var results = [Output?](repeating: nil, count: inputs.count)
    var completed = 0

    try await withThrowingTaskGroup(of: (index: Int, output: Output).self) { group in
      var nextIndex = 0

      func enqueueNext() {
        guard nextIndex < inputs.count else { return }
        let index = nextIndex
        let input = inputs[index]
        nextIndex += 1
        group.addTask {
          try Task.checkCancellation()
          return (index, try await operation(input))
        }
      }

      for _ in 0..<min(limit, inputs.count) { enqueueNext() }

      for try await (index, output) in group {
        results[index] = output
        completed += 1
        progress(BatchProgress(completed: completed, total: inputs.count))
        enqueueNext()
      }
    }

    try Task.checkCancellation()
    var outputs: [Output] = []
    outputs.reserveCapacity(inputs.count)
    for result in results {
      guard let result else { throw CancellationError() }
      outputs.append(result)
    }
    return outputs
  }
}
