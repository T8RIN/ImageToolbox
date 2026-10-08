import PhotosUI
import SwiftUI
import os

struct Base64ToolView: View {
  private static let logger = Logger(subsystem: "com.the80hz.imagetoolbox", category: "base64")

  @State private var model = Base64ToolModel()
  @Environment(\.dynamicTypeSize) private var dynamicTypeSize
  @State private var isPickerPresented = false
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ScrollView {
      VStack(spacing: DesignTokens.spacing) {
        encodeCard
        decodeCard
      }
      .padding(DesignTokens.spacing)
    }
    .background { AppBackground() }
    .accessibilityIdentifier("screen.base64")
    .navigationTitle("Base64")
    .navigationBarTitleDisplayMode(.inline)
    .photosPicker(isPresented: $isPickerPresented, selection: $pickedItem, matching: .images)
    .onChange(of: pickedItem) { _, item in
      guard let item else { return }
      Task { await encode(item) }
    }
  }

  private var encodeCard: some View {
    VStack(alignment: .leading, spacing: 12) {
      Text("Image to Base64")
        .font(.headline)

      Button("Choose image") {
        isPickerPresented = true
      }
      .buttonStyle(.glassProminent)
      .accessibilityIdentifier("base64.pick")

      if !model.encodedText.isEmpty {
        Text("Encoded \(model.encodedLength) characters")
          .font(.caption)
          .foregroundStyle(.secondary)

        Text(String(model.encodedText.prefix(300)) + "…")
          .font(.footnote.monospaced())
          .lineLimit(6)

        HStack {
          Button("Copy") {
            UIPasteboard.general.string = model.encodedText
          }
          ShareLink("Share", item: model.encodedText)
        }
        .buttonStyle(.glass)
      }
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .padding()
    .glassCard()
  }

  private var decodeCard: some View {
    VStack(alignment: .leading, spacing: 12) {
      Text("Base64 to Image")
        .font(.headline)

      TextEditor(text: $model.decodeInput)
        .font(.footnote.monospaced())
        .frame(minHeight: 120)
        .accessibilityIdentifier("base64.input")
        .overlay(alignment: .topLeading) {
          if model.decodeInput.isEmpty {
            Text("Paste Base64 text here")
              .foregroundStyle(.tertiary)
              .padding(.top, 8)
              .padding(.leading, 5)
              .allowsHitTesting(false)
          }
        }

      let actions =
        dynamicTypeSize >= .xxxLarge
        ? AnyLayout(VStackLayout(alignment: .leading, spacing: 8))
        : AnyLayout(HStackLayout(spacing: 8))
      actions {
        Button("Paste") {
          model.decodeInput = UIPasteboard.general.string ?? ""
        }
        .buttonStyle(.glass)

        OpenTextFileButton(title: "Open text file") { text in
          model.decodeInput = text
        }
        .accessibilityIdentifier("base64.openFile")

        Button("Decode") {
          Task { await model.decode() }
        }
        .buttonStyle(.glassProminent)
        .disabled(model.decodeInput.isEmpty)
        .accessibilityIdentifier("base64.decode")
      }

      if model.decodeFailed {
        Text("Could not decode the Base64 string.")
          .foregroundStyle(.red)
          .accessibilityIdentifier("base64.error")
      }

      if let image = model.decodedImage {
        Image(uiImage: image)
          .resizable()
          .scaledToFit()
          .frame(maxHeight: 320)
          .clipShape(.rect(cornerRadius: 16))

        if let url = model.decodedFileURL {
          ShareLink("Share", item: url)
            .buttonStyle(.glass)
            .accessibilityIdentifier("base64.decodedShare")
        }
      }
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .padding()
    .glassCard()
  }

  private func encode(_ item: PhotosPickerItem) async {
    defer { pickedItem = nil }

    do {
      guard let data = try await item.loadTransferable(type: Data.self) else {
        Self.logger.error("Picked item returned no data")
        return
      }
      await model.encode(imageData: data)
    } catch {
      Self.logger.error(
        "Loading picked image failed: \(error.localizedDescription, privacy: .public)")
    }
  }
}
