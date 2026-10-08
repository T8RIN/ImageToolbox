import Foundation

/// Undo and redo over whole values. Each recorded change is one step. A new change clears the
/// redo steps, the same as in a text editor. Only the last `limit` steps are kept.
public struct UndoHistory<Value: Equatable & Sendable>: Sendable {
  public private(set) var current: Value
  private var undoSteps: [Value] = []
  private var redoSteps: [Value] = []
  private let limit: Int

  public init(_ initial: Value, limit: Int = 50) {
    current = initial
    self.limit = max(1, limit)
  }

  public var canUndo: Bool { !undoSteps.isEmpty }
  public var canRedo: Bool { !redoSteps.isEmpty }

  /// Makes `value` the current state. Returns false, and records nothing, when it equals the
  /// current state.
  @discardableResult
  public mutating func record(_ value: Value) -> Bool {
    guard value != current else { return false }
    undoSteps.append(current)
    if undoSteps.count > limit {
      undoSteps.removeFirst(undoSteps.count - limit)
    }
    redoSteps.removeAll()
    current = value
    return true
  }

  @discardableResult
  public mutating func undo() -> Bool {
    guard let previous = undoSteps.popLast() else { return false }
    redoSteps.append(current)
    current = previous
    return true
  }

  @discardableResult
  public mutating func redo() -> Bool {
    guard let next = redoSteps.popLast() else { return false }
    undoSteps.append(current)
    current = next
    return true
  }
}
