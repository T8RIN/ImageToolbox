import Testing

@testable import ImageToolboxKit

@Suite("Undo history")
struct UndoHistoryTests {

  @Test("undo returns to earlier states and redo goes forward again")
  func undoAndRedo() {
    var history = UndoHistory(0)
    history.record(1)
    history.record(2)
    #expect(history.current == 2)

    let undoneOnce = history.undo()
    #expect(undoneOnce)
    #expect(history.current == 1)
    let undoneTwice = history.undo()
    #expect(undoneTwice)
    #expect(history.current == 0)
    let undoneAgain = history.undo()
    #expect(!undoneAgain)

    let redoneOnce = history.redo()
    #expect(redoneOnce)
    #expect(history.current == 1)
    let redoneTwice = history.redo()
    #expect(redoneTwice)
    #expect(history.current == 2)
    let redoneAgain = history.redo()
    #expect(!redoneAgain)
  }

  @Test("a new change clears the redo steps")
  func newChangeClearsRedo() {
    var history = UndoHistory("a")
    history.record("b")
    history.undo()
    #expect(history.canRedo)

    history.record("c")
    #expect(!history.canRedo)
    #expect(history.current == "c")
    history.undo()
    #expect(history.current == "a")
  }

  @Test("recording the current value creates no step")
  func sameValueIsIgnored() {
    var history = UndoHistory(5)
    let recorded = history.record(5)
    #expect(!recorded)
    #expect(!history.canUndo)
  }

  @Test("only the last `limit` steps are kept")
  func limitDropsOldest() {
    var history = UndoHistory(0, limit: 3)
    for value in 1...6 { history.record(value) }
    #expect(history.current == 6)

    var undone: [Int] = []
    while history.undo() { undone.append(history.current) }
    #expect(undone == [5, 4, 3])
  }
}
