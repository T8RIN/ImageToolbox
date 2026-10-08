import Foundation
import ImageIO
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI
import UniformTypeIdentifiers

@MainActor
@Observable
final class EditExifToolModel {
  var selection: PhotosPickerItem? {
    didSet {
      guard let selection else { return }
      loadTask?.cancel()
      loadTask = Task { await load(selection) }
    }
  }

  private(set) var source: LoadedImage?
  private(set) var outputExtension = "jpg"
  private(set) var values: [EditableMetadataField: String] = [:]
  private(set) var edited: Data?
  private(set) var errorMessage: LocalizedStringKey?
  /// One step per save: the field values that were written.
  private(set) var history = UndoHistory<[EditableMetadataField: String]>([:])

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  @ObservationIgnored private var loadTask: Task<Void, Never>?

  /// Changes one field. Any previous save result is stale after this, so it is dropped.
  func update(_ field: EditableMetadataField, to value: String) {
    values[field] = value
    edited = nil
  }

  /// Writes the field values into a copy of the picture. Pixels are not re-encoded.
  func save() {
    guard let source else { return }
    history.record(values)
    guard let output = ImageMetadataTools.edit(source.data, fields: values) else {
      edited = nil
      errorMessage = "The metadata could not be saved to this image."
      return
    }
    edited = output
    errorMessage = nil
  }

  func undo() {
    guard history.undo() else { return }
    values = history.current
    edited = nil
  }

  func redo() {
    guard history.redo() else { return }
    values = history.current
    edited = nil
  }

  private func load(_ item: PhotosPickerItem) async {
    let result = await PhotoLoader.load(item)
    guard !Task.isCancelled else { return }
    guard let picked = result else {
      errorMessage = "The picked file could not be read as an image."
      return
    }
    source = picked
    values = ImageMetadataTools.editableValues(picked.data)
    history = UndoHistory(values)
    outputExtension = Self.fileExtension(of: picked.data)
    edited = nil
    errorMessage = nil
  }

  /// Edits keep the source container, so the file name must use the source's own extension.
  private static func fileExtension(of data: Data) -> String {
    guard let source = CGImageSourceCreateWithData(data as CFData, nil),
      let type = CGImageSourceGetType(source)
    else { return "jpg" }
    let identifier = type as String
    return OutputFormat(typeIdentifier: identifier)?.fileExtension
      ?? UTType(identifier)?.preferredFilenameExtension
      ?? "jpg"
  }
}
