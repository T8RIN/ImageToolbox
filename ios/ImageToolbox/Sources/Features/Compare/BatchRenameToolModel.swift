import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class BatchRenameToolModel {

  /// Settings that the undo history keeps. A step is recorded when the operation starts or a slider
  /// drag ends.
  struct Parameters: Equatable, Sendable {
    var pattern: String
    var exportFormat: OutputFormat
    var exportQuality: Double
  }

  private(set) var history = UndoHistory(
    Parameters(pattern: "photo_{n:3}.{ext}", exportFormat: .png, exportQuality: 0.9))

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
    Parameters(pattern: pattern, exportFormat: exportFormat, exportQuality: exportQuality)
  }

  private func restoreParameters(_ parameters: Parameters) {
    pattern = parameters.pattern
    exportFormat = parameters.exportFormat
    exportQuality = parameters.exportQuality
  }
  struct Entry: Identifiable {
    let id: Int
    let fileName: String
    let data: Data
  }

  /// One picture to re-encode, with the stem its file gets.
  private struct ExportItem: Sendable {
    let stem: String
    let picture: LoadedImage
  }

  enum ExportError: Error {
    case encodingFailed(String)
  }

  var pattern = "photo_{n:3}.{ext}"
  /// Format the batch re-encodes every picture to.
  var exportFormat = OutputFormat.png
  var exportQuality = 0.9

  private(set) var pictures: [LoadedImage] = []
  private(set) var failedCount = 0
  private(set) var isLoading = false
  /// Set while a batch export runs. Nil when idle.
  private(set) var progress: BatchProgress?
  /// Files from the last finished batch, ready to share together.
  private(set) var exports: [ExportFile] = []
  private(set) var exportFailed = false
  private var loadDate = Date.now
  @ObservationIgnored private var exportTask: Task<Void, Never>?

  /// One entry per loaded picture. Photos does not expose file names, so each picture is
  /// named `IMG_<number>.jpg`, numbered from 1 in the order it was picked.
  var entries: [Entry] {
    pictures.enumerated().map { index, picture -> Entry in
      let number = index + 1
      let context = RenameContext(
        originalName: "IMG_\(number)",
        fileExtension: "jpg",
        date: loadDate)
      let name = BatchRename.render(pattern: pattern, context: context, sequenceNumber: number)
      return Entry(
        id: number,
        fileName: name.replacingOccurrences(of: "/", with: "-"),
        data: picture.data)
    }
  }

  func load(_ items: [PhotosPickerItem]) async {
    cancelExport()
    isLoading = true
    let loaded = await PhotoLoader.load(items)
    pictures = loaded
    failedCount = items.count - loaded.count
    loadDate = .now
    exports = []
    exportFailed = false
    isLoading = false
  }

  /// Re-encodes every picture in `exportFormat`, several at a time, with the new names. Progress
  /// is reported as the batch runs, and the batch can be cancelled.
  func startExport(encoder: any ImageEncoding) {
    guard !pictures.isEmpty, exportTask == nil else { return }
    let items = zip(entries, pictures).map { entry, picture in
      ExportItem(stem: (entry.fileName as NSString).deletingPathExtension, picture: picture)
    }
    let format = exportFormat
    let quality = exportQuality
    let keepMetadata = AppSettings.load(from: .standard).keepMetadata

    exports = []
    exportFailed = false
    progress = BatchProgress(completed: 0, total: items.count)
    exportTask = Task {
      do {
        let files = try await BatchRunner.run(
          items,
          label: "batch-rename export",
          operation: { item in
            let data = encoder.encode(
              item.picture.image,
              as: format,
              quality: quality,
              metadataSource: keepMetadata ? item.picture.data : nil)
            guard let data else { throw ExportError.encodingFailed(item.stem) }
            return ExportFile(data: data, fileName: "\(item.stem).\(format.fileExtension)")
          },
          progress: { [weak self] report in
            Task { @MainActor in self?.progress = report }
          })
        exports = files
      } catch is CancellationError {
        exports = []
      } catch {
        exportFailed = true
      }
      progress = nil
      exportTask = nil
    }
  }

  func cancelExport() {
    exportTask?.cancel()
  }
}
