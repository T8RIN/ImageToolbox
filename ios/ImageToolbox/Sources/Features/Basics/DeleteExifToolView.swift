import ImageToolboxKit
import PhotosUI
import SwiftUI

struct DeleteExifToolView: View {
  private static let previewRowCount = 20

  @State private var model = DeleteExifToolModel()

  var body: some View {
    ToolScaffold(identifier: "deleteExif") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: Bindable(model).selection)
          .accessibilityIdentifier("deleteExif.pick")
      }

      if model.source != nil {
        metadataSection
        removeSection
      }

      if let result = model.stripped {
        resultSection(result)
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }
    }
  }

  private var metadataSection: some View {
    GlassSection("Metadata") {
      Text("Rows before removal: \(model.entries.count)")
        .monospacedDigit()

      if model.entries.isEmpty {
        Text("No metadata found.")
          .font(.footnote)
          .foregroundStyle(.secondary)
      }

      ForEach(
        Array(model.entries.prefix(Self.previewRowCount).enumerated()),
        id: \.offset
      ) { _, entry in
        metadataRow(entry)
      }

      if model.entries.count > Self.previewRowCount {
        Text("And \(model.entries.count - Self.previewRowCount) more rows.")
          .font(.footnote)
          .foregroundStyle(.secondary)
      }
    }
  }

  private var removeSection: some View {
    GlassSection("Remove") {
      Button("Remove metadata") {
        model.removeMetadata()
      }
      .buttonStyle(.glassProminent)
      .disabled(model.isWorking)
      .accessibilityIdentifier("deleteExif.apply")

      if model.isWorking {
        ProgressView()
      }
    }
  }

  private func resultSection(_ result: LoadedImage) -> some View {
    GlassSection("Result") {
      PictureView(image: result.image, maxHeight: 240)
      Text("Rows after removal: \(model.strippedRowCount)")
        .monospacedDigit()
      ShareFileButton(
        title: "Share stripped file",
        data: result.data,
        fileName: "stripped.\(model.outputExtension)"
      )
      .accessibilityIdentifier("deleteExif.share")
    }
  }

  private func metadataRow(_ entry: MetadataEntry) -> some View {
    VStack(alignment: .leading, spacing: 2) {
      Text(verbatim: "\(entry.group) / \(entry.key)")
        .font(.footnote.weight(.semibold))
      Text(verbatim: entry.value)
        .font(.footnote.monospaced())
        .foregroundStyle(.secondary)
        .lineLimit(2)
    }
    .frame(maxWidth: .infinity, alignment: .leading)
  }
}
