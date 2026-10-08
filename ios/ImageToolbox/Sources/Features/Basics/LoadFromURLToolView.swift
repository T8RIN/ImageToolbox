import SwiftUI

struct LoadFromURLToolView: View {
  @State private var model = LoadFromURLToolModel()

  var body: some View {
    ToolScaffold(identifier: "loadFromURL") {
      GlassSection("Address") {
        TextField("https://example.com/photo.jpg", text: Bindable(model).address)
          .textFieldStyle(.roundedBorder)
          .keyboardType(.URL)
          .textInputAutocapitalization(.never)
          .autocorrectionDisabled()
          .submitLabel(.go)
          .onSubmit {
            Task { await model.load() }
          }
          .accessibilityIdentifier("loadFromURL.address")

        Button("Load") {
          Task { await model.load() }
        }
        .buttonStyle(.glassProminent)
        .disabled(model.isLoading)
        .accessibilityIdentifier("loadFromURL.load")

        if model.isLoading {
          ProgressView("Downloading")
        }
      }

      if let picture = model.picture, let png = model.pngData {
        GlassSection("Result") {
          PictureView(image: picture)
          ShareFileButton(title: "Share PNG", data: png, fileName: model.pngFileName)
            .accessibilityIdentifier("loadFromURL.share")
        }
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }
    }
  }
}
