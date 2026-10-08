import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import SwiftUI

@MainActor
@Observable
final class WebPToolsModel {

  /// Settings that the encoding uses. A step is recorded when a slider drag ends or the format changes.
  struct Parameters: Equatable, Sendable {
    var format: OutputFormat
    var quality: Double
  }

  private(set) var history = UndoHistory(Parameters(format: .webp, quality: 0.85))

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
    Parameters(format: format, quality: quality)
  }

  private func restoreParameters(_ parameters: Parameters) {
    format = parameters.format
    quality = parameters.quality
  }
  private(set) var source: CGImage?
  private(set) var encoded: Data?
  private(set) var errorMessage: LocalizedStringKey?

  var format: OutputFormat = .webp {
    didSet { refreshEncoding() }
  }

  var quality: Double = 0.85 {
    didSet { refreshEncoding() }
  }

  private(set) var fileName = "converted.png"

  /// Decodes the first frame of a WebP file (or any other picked image).
  func open(_ url: URL) async {
    source = nil
    encoded = nil
    errorMessage = nil
    let decoded = await Task.detached(priority: .userInitiated) { () -> CGImage? in
      guard let data = MediaFiles.read(url) else { return nil }
      return ImageCodec.decode(data)
    }.value

    guard let decoded else {
      errorMessage = "This file could not be decoded as WebP."
      return
    }
    source = decoded
    refreshEncoding()
  }

  private func refreshEncoding() {
    guard let source else { return }
    encoded = ImageCodec.encode(source, as: format, quality: quality)
    fileName =
      encoded.map {
        ExportNaming.fileName(stem: "converted", fileExtension: format.fileExtension, data: $0)
      }
      ?? "converted.\(format.fileExtension)"
    if encoded == nil {
      errorMessage = "The image could not be encoded as \(format.fileExtension.uppercased())."
    } else {
      errorMessage = nil
    }
  }
}
