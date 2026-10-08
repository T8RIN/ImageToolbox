import SwiftUI

struct ToolGridView: View {
  @Environment(\.dynamicTypeSize) private var dynamicTypeSize

  @State private var path: [ToolID] = []
  @State private var query = ""
  @State private var settings = SettingsStore()
  @State private var isSettingsPresented = false

  var body: some View {
    NavigationStack(path: $path) {
      ScrollView {
        GlassEffectContainer(spacing: DesignTokens.spacing) {
          LazyVGrid(columns: columns, spacing: DesignTokens.spacing) {
            ForEach(filteredTools) { tool in
              ToolCard(tool: tool) {
                open(tool)
              }
            }
          }
        }
        .padding(DesignTokens.spacing)
      }
      .background { AppBackground() }
      .navigationTitle("Image Toolbox")
      .searchable(text: $query, prompt: "Search tools")
      .navigationDestination(for: ToolID.self) { tool in
        destination(for: tool)
      }
      .toolbar {
        ToolbarItem(placement: .topBarTrailing) {
          Button {
            isSettingsPresented = true
          } label: {
            Label("Settings", systemImage: "gearshape")
          }
          .accessibilityIdentifier("grid.settings")
        }
      }
      .sheet(isPresented: $isSettingsPresented) {
        SettingsView(store: settings)
      }
    }
  }

  /// Two columns normally. From the largest standard size up, one column, so long words such as
  /// "Редактирование" are not clipped by the card edge.
  private var columns: [GridItem] {
    dynamicTypeSize >= .xxxLarge
      ? [GridItem(.flexible(), spacing: DesignTokens.spacing)]
      : [GridItem(.adaptive(minimum: 160), spacing: DesignTokens.spacing)]
  }

  private var filteredTools: [ToolID] {
    let needle = query.trimmingCharacters(in: .whitespaces).lowercased()
    guard !needle.isEmpty else { return ToolID.allCases }
    return ToolID.allCases.filter { String(localized: $0.title).lowercased().contains(needle) }
  }

  private func open(_ tool: ToolID) {
    UsageTracker.record(tool)
    path.append(tool)
  }

  @ViewBuilder
  private func destination(for tool: ToolID) -> some View {
    switch tool {
    case .base64: Base64ToolView()
    case .deleteExif: DeleteExifToolView()
    case .editExif: EditExifToolView()
    case .rotateFlip: RotateFlipToolView()
    case .loadFromURL: LoadFromURLToolView()
    case .colorPicker: ColorPickerToolView()
    case .colorLibrary: ColorLibraryToolView()
    case .perlinNoise: PerlinNoiseToolView()
    case .asciiArt: AsciiArtToolView()
    case .imagePreview: ImagePreviewToolView()
    case .snowfall: SnowfallToolView()
    case .libraries: LibrariesToolView()
    case .help: HelpToolView()
    case .appLogs: AppLogsToolView()
    case .usageStatistics: UsageStatisticsToolView()
    case .easterEgg: EasterEggToolView()
    case .resizeConvert: ResizeConvertToolView()
    case .weightResize: WeightResizeToolView()
    case .limitsResize: LimitsResizeToolView()
    case .crop: CropToolView()
    case .imageCutting: ImageCuttingToolView()
    case .imageSplitting: ImageSplittingToolView()
    case .imageStitch: ImageStitchToolView()
    case .imageStacking: ImageStackingToolView()
    case .compare: CompareToolView()
    case .watermarking: WatermarkingToolView()
    case .singleEdit: SingleEditToolView()
    case .batchRename: BatchRenameToolView()
    case .duplicateFinder: DuplicateFinderToolView()
    case .checksumTools: ChecksumToolsView()
    case .colorTools: ColorToolsView()
    case .gifTools: GIFToolsView()
    case .apngTools: APNGToolsView()
    case .webpTools: WebPToolsView()
    case .documentScanner: DocumentScannerToolView()
    case .scanQRCode: ScanQRCodeToolView()
    case .wallpapersExport: WallpapersExportToolView()
    case .audioCoverExtractor: AudioCoverToolView()
    case .screenshotFraming: ScreenshotFramingToolView()
    case .codePreview: CodePreviewToolView()
    case .gradientMaker: GradientMakerToolView()
    case .paletteToPDF: PaletteToPDFToolView()
    case .photomosaic: PhotomosaicToolView()
    }
  }
}

private struct ToolCard: View {
  let tool: ToolID
  let onOpen: () -> Void

  var body: some View {
    Button(action: onOpen) {
      content
    }
    .buttonStyle(.plain)
    .accessibilityIdentifier("tool.\(tool.rawValue)")
  }

  private var content: some View {
    VStack(alignment: .leading, spacing: 12) {
      Image(systemName: tool.systemImage)
        .font(.title2)
      Text(tool.title)
        .font(.headline)
        .multilineTextAlignment(.leading)
    }
    .frame(maxWidth: .infinity, minHeight: 120, alignment: .topLeading)
    .padding()
    .glassCard(interactive: true)
  }
}
