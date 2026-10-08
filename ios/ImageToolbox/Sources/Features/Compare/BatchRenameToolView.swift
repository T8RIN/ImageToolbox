import ImageToolboxKit
import PhotosUI
import SwiftUI

struct BatchRenameToolView: View {
  @State private var model = BatchRenameToolModel()
  @State private var pickedItems: [PhotosPickerItem] = []
  @Environment(\.services) private var services

  var body: some View {
    ToolScaffold(identifier: "batchRename") {
      GlassSection("Pictures") {
        MultiPhotoButton(title: "Choose pictures", selection: $pickedItems, maxCount: 50)
          .accessibilityIdentifier("batchRename.pick")
        if model.isLoading {
          ProgressView()
        }
        if model.failedCount > 0 {
          ErrorText(message: "\(model.failedCount) pictures could not be read.")
        }
      }

      GlassSection("Pattern") {
        TextField("Pattern", text: $model.pattern)
          .textInputAutocapitalization(.never)
          .autocorrectionDisabled()
          .accessibilityIdentifier("batchRename.pattern")
        Text("Tokens: {name}, {ext}, {n}, {n:3}, {date:yyyy-MM-dd}")
          .font(.footnote)
          .foregroundStyle(.secondary)
      }

      GlassSection("Names") {
        if model.entries.isEmpty {
          Text("Choose pictures to see the new names.")
            .foregroundStyle(.secondary)
        }
        ForEach(model.entries) { entry in
          HStack {
            Text(entry.fileName)
              .lineLimit(1)
              .truncationMode(.middle)
            Spacer()
            ShareFileButton(title: "Share", data: entry.data, fileName: entry.fileName)
              .disabled(entry.fileName.isEmpty)
              .accessibilityIdentifier("batchRename.share")
          }
        }
      }

      if !model.pictures.isEmpty {
        GlassSection("Export all") {
          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("batchRename.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("batchRename.redo")
          }
          OutputFormatPicker(selection: $model.exportFormat)
          if model.exportFormat.isLossy {
            ParameterSlider(
              title: "Quality", value: $model.exportQuality, onEditingEnded: model.commitParameters,
              range: 0.1...1, step: 0.05,
              format: "%.2f")
          }

          if let progress = model.progress {
            ProgressView(value: progress.fraction)
              .accessibilityIdentifier("batchRename.progress")
            Text("Exporting \(progress.completed) of \(progress.total)")
              .font(.footnote)
              .monospacedDigit()
              .foregroundStyle(.secondary)
            Button("Cancel") { model.cancelExport() }
              .buttonStyle(.glass)
              .accessibilityIdentifier("batchRename.cancel")
          } else {
            Button("Prepare files") {
              model.commitParameters()
              model.startExport(encoder: services.encoder)
            }
            .buttonStyle(.glassProminent)
            .accessibilityIdentifier("batchRename.prepare")
          }

          if model.exportFailed {
            ErrorText(message: "The batch stopped because a picture could not be encoded.")
          }

          if !model.exports.isEmpty {
            ShareLink(items: model.exports, preview: { SharePreview($0.fileName) }) {
              Label("Share all \(model.exports.count) files", systemImage: "square.and.arrow.up")
            }
            .buttonStyle(.glass)
            .accessibilityIdentifier("batchRename.shareAll")
          }
        }
      }
    }
    .onChange(of: pickedItems) { _, items in
      Task { await model.load(items) }
    }
  }
}
