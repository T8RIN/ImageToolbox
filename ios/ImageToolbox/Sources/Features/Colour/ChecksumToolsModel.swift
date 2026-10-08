import Foundation
import ImageToolboxKit
import Observation

@MainActor
@Observable
final class ChecksumToolsModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var algorithm: ChecksumAlgorithm
    var text: String
    var expected: String
  }

  private(set) var history = UndoHistory(Parameters(algorithm: .sha256, text: "", expected: ""))

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func commitParameters() {
    history.record(currentParameters)
  }

  func undo() {
    guard history.undo() else { return }
    restoreParameters(history.current)
  }

  func redo() {
    guard history.redo() else { return }
    restoreParameters(history.current)
  }

  private var currentParameters: Parameters {
    Parameters(algorithm: algorithm, text: text, expected: expected)
  }

  private func restoreParameters(_ parameters: Parameters) {
    algorithm = parameters.algorithm
    text = parameters.text
    expected = parameters.expected
  }
  var algorithm = ChecksumAlgorithm.sha256
  var text = ""
  var expected = ""

  private(set) var fileName: String?
  private(set) var digest: String?
  private(set) var isComputing = false
  private(set) var readFailed = false

  private var fileURL: URL?

  /// `nil` until there is both a digest and a non-empty expected value.
  var expectedMatches: Bool? {
    guard let digest else { return nil }
    let wanted = expected.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
    guard !wanted.isEmpty else { return nil }
    return wanted == digest
  }

  func selectFile(_ url: URL) {
    fileURL = url
    fileName = url.lastPathComponent
    clearDigest()
  }

  func removeFile() {
    fileURL = nil
    fileName = nil
    clearDigest()
  }

  func clearDigest() {
    digest = nil
    readFailed = false
  }

  /// Hashes the picked file off the main actor, or the typed text when no file is picked.
  func compute() async {
    readFailed = false
    guard let fileURL else {
      digest = algorithm.hexDigest(of: Data(text.utf8))
      return
    }

    isComputing = true
    defer { isComputing = false }
    let chosenAlgorithm = algorithm
    let result = await Task.detached(priority: .userInitiated) {
      ChecksumToolsModel.fileDigest(at: fileURL, algorithm: chosenAlgorithm)
    }.value
    digest = result
    readFailed = result == nil
  }

  /// Reads and hashes a file. The security-scoped access is balanced within this call.
  nonisolated static func fileDigest(at url: URL, algorithm: ChecksumAlgorithm) -> String? {
    let isScoped = url.startAccessingSecurityScopedResource()
    defer {
      if isScoped { url.stopAccessingSecurityScopedResource() }
    }
    guard let data = try? Data(contentsOf: url) else { return nil }
    return algorithm.hexDigest(of: data)
  }
}
