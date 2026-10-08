import PhotosUI
import SwiftUI

struct PhotomosaicToolView: View {
  @State private var model = PhotomosaicToolModel()
  @State private var targetItem: PhotosPickerItem?
  @State private var tileItems: [PhotosPickerItem] = []

  var body: some View {
    ToolScaffold(identifier: "photomosaic") {
      picturesSection
      gridSection
      mosaicSection
    }
    .onChange(of: targetItem) { _, item in
      guard let item else { return }
      Task {
        await model.loadTarget(item)
        targetItem = nil
      }
    }
    .onChange(of: tileItems) { _, items in
      guard !items.isEmpty else { return }
      Task {
        await model.loadTiles(items)
        tileItems = []
      }
    }
  }

  private var picturesSection: some View {
    GlassSection("Pictures") {
      SinglePhotoButton(title: "Target picture", selection: $targetItem)
        .accessibilityIdentifier("photomosaic.chooseTarget")

      if let target = model.target {
        PictureView(image: target.image, maxHeight: 160)
      }

      if model.targetFailed {
        ErrorText(message: "Could not load the target picture.")
      }

      MultiPhotoButton(title: "Tile pictures", selection: $tileItems, maxCount: 200)
        .accessibilityIdentifier("photomosaic.chooseTiles")

      Text("Tiles loaded: \(model.tiles.count)")
        .font(.footnote)
        .foregroundStyle(.secondary)

      if model.tilesFailed {
        ErrorText(message: "No tile pictures could be loaded.")
      }
    }
  }

  private var gridSection: some View {
    GlassSection("Grid") {
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("mosaic.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("mosaic.redo")
      }
      ParameterSlider(
        title: "Tile size", value: $model.tileSize, onEditingEnded: model.commitParameters,
        range: 8...64, step: 4)
      ParameterSlider(
        title: "Columns", value: $model.columns, onEditingEnded: model.commitParameters,
        range: 8...120)
    }
  }

  private var mosaicSection: some View {
    GlassSection("Mosaic") {
      Button("Build mosaic") {
        model.commitParameters()
        Task { await model.build() }
      }
      .buttonStyle(.glassProminent)
      .disabled(!model.canBuild)
      .accessibilityIdentifier("photomosaic.build")
      if model.isBuilding {
        Button("Cancel") { model.cancelBuild() }
          .buttonStyle(.glass)
          .accessibilityIdentifier("photomosaic.cancel")
      }

      if model.isBuilding {
        ProgressView()
        Text("Building…")
          .font(.footnote)
          .foregroundStyle(.secondary)
      }

      if let mosaic = model.mosaic {
        PictureView(image: mosaic.image, maxHeight: 480)
        ShareFileButton(title: "Share PNG", data: mosaic.data, fileName: model.mosaicFileName)
          .accessibilityIdentifier("photomosaic.share")
      }

      if model.buildFailed {
        ErrorText(message: "Could not build the mosaic.")
      }
    }
  }
}
