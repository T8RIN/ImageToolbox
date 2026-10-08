import PhotosUI
import SwiftUI

struct DuplicateFinderToolView: View {
  @State private var model = DuplicateFinderToolModel()
  @State private var pickedItems: [PhotosPickerItem] = []

  var body: some View {
    ToolScaffold(identifier: "duplicateFinder") {
      GlassSection("Pictures") {
        MultiPhotoButton(title: "Choose pictures", selection: $pickedItems, maxCount: 100)
          .accessibilityIdentifier("duplicateFinder.pick")
        Text("\(pickedItems.count) selected")
          .font(.footnote)
          .foregroundStyle(.secondary)
        Button("Find duplicates") {
          model.commitParameters()
          Task { await model.scan(pickedItems) }
        }
        .buttonStyle(.glassProminent)
        .disabled(pickedItems.isEmpty || model.isScanning)
        .accessibilityIdentifier("duplicateFinder.scan")

        if model.isScanning {
          ProgressView()
        }
        if model.failedCount > 0 {
          ErrorText(message: "\(model.failedCount) pictures could not be read.")
        }
      }

      if !model.pictures.isEmpty {
        resultsSection
      }
    }
  }

  @ViewBuilder
  private var resultsSection: some View {
    let exact = model.exactGroups
    let similar = model.similarGroups
    GlassSection("Result") {
      Text("\(exact.count) exact groups, \(similar.count) similar groups")
        .font(.headline)
        .accessibilityIdentifier("duplicateFinder.summary")
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("duplicates.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("duplicates.redo")
      }
      ParameterSlider(
        title: "Similarity threshold", value: $model.threshold,
        onEditingEnded: model.commitParameters, range: 0...20)
      groupRows("Exact duplicates", groups: exact)
      groupRows("Similar pictures", groups: similar)
    }
  }

  private func groupRows(
    _ title: LocalizedStringKey,
    groups: [[DuplicateFinderToolModel.ScannedPicture]]
  ) -> some View {
    VStack(alignment: .leading, spacing: 8) {
      Text(title)
        .font(.subheadline.bold())
      if groups.isEmpty {
        Text("None")
          .foregroundStyle(.secondary)
      }
      ForEach(groups.indices, id: \.self) { index in
        HStack(spacing: 8) {
          Text("\(index + 1)")
            .font(.headline)
            .monospacedDigit()
          ForEach(groups[index]) { picture in
            Image(decorative: picture.thumbnail, scale: 1)
              .resizable()
              .scaledToFill()
              .frame(width: 60, height: 60)
              .clipShape(.rect(cornerRadius: 8))
          }
        }
      }
    }
  }
}
