import Foundation
import ImageToolboxKit
import Observation

@MainActor
@Observable
final class PaletteToPDFToolModel {

  /// Settings that the undo history keeps. A step is recorded when a slider drag ends, a choice
  /// changes, or the operation starts.
  struct Parameters: Equatable, Sendable {
    var input: String
    var columns: Double
  }

  private(set) var history = UndoHistory(
    Parameters(input: "#FF5A5F #FFB400 #00A699 #484848", columns: 4))

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
    Parameters(input: input, columns: columns)
  }

  private func restoreParameters(_ parameters: Parameters) {
    input = parameters.input
    columns = parameters.columns
  }
  var input = "#FF5A5F #FFB400 #00A699 #484848"
  var columns = 4.0

  private(set) var pdf: Data?
  private(set) var renderFailed = false
  private(set) var pdfFileName = "palette.pdf"

  /// Colours parsed from the input. Tokens that are not valid hex codes are skipped.
  var colors: [RGBA] {
    input.split { $0.isWhitespace }.compactMap { RGBA(hex: String($0)) }
  }

  func makePDF() {
    pdf = PalettePDF.render(colors, columns: Int(columns))
    pdfFileName =
      pdf.map { ExportNaming.fileName(stem: "palette", fileExtension: "pdf", data: $0) }
      ?? "palette.pdf"
    renderFailed = pdf == nil
  }

  /// Forgets the rendered file once the palette or layout changes.
  func resetOutput() {
    pdf = nil
    renderFailed = false
  }
}
