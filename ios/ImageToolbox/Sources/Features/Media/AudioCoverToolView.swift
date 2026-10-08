import SwiftUI
import UniformTypeIdentifiers

struct AudioCoverToolView: View {
  @State private var model = AudioCoverToolModel()
  @State private var isImporting = false

  var body: some View {
    ToolScaffold(identifier: "audioCoverExtractor") {
      GlassSection {
        Button("Choose audio file") { isImporting = true }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("audioCoverExtractor.choose")

        if let cover = model.cover {
          PictureView(image: cover.image)
          ShareFileButton(title: "Share PNG", data: cover.png, fileName: model.coverFileName)
            .accessibilityIdentifier("audioCoverExtractor.share")
        }
        if let message = model.errorMessage {
          ErrorText(message: message)
        }
      }
    }
    .fileImporter(
      isPresented: $isImporting,
      allowedContentTypes: [.audio],
      allowsMultipleSelection: false
    ) { result in
      guard case .success(let urls) = result, let url = urls.first else { return }
      Task { await model.extractCover(from: url) }
    }
  }
}
