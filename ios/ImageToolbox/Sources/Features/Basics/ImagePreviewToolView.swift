import QuickLook
import SwiftUI
import UniformTypeIdentifiers

struct ImagePreviewToolView: View {
  @State private var model = ImagePreviewToolModel()
  @State private var isImporterPresented = false

  var body: some View {
    ToolScaffold(identifier: "imagePreview") {
      GlassSection("File") {
        Button("Choose file") {
          isImporterPresented = true
        }
        .buttonStyle(.glassProminent)
        .accessibilityIdentifier("imagePreview.pick")

        if let fileURL = model.fileURL {
          Label(fileURL.lastPathComponent, systemImage: "doc")
            .font(.subheadline)
            .accessibilityIdentifier("imagePreview.name")

          Button("Preview") {
            model.showPreview()
          }
          .buttonStyle(.glass)
          .accessibilityIdentifier("imagePreview.show")
        }
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }
    }
    .fileImporter(
      isPresented: $isImporterPresented,
      allowedContentTypes: [.image, .pdf]
    ) { result in
      model.receive(result)
    }
    .quickLookPreview(Bindable(model).previewURL)
    .onDisappear {
      model.close()
    }
  }
}
