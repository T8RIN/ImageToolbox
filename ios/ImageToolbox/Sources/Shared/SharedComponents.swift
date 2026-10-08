import CoreGraphics
import ImageToolboxKit
import PhotosUI
import SwiftUI

/// Scrollable screen with the app background and standard spacing. Use it as every tool's root.
struct ToolScaffold<Content: View>: View {
  let identifier: String
  let content: Content

  init(identifier: String, @ViewBuilder content: () -> Content) {
    self.identifier = identifier
    self.content = content()
  }

  var body: some View {
    ScrollView {
      VStack(alignment: .leading, spacing: DesignTokens.spacing) {
        content
      }
      .frame(maxWidth: .infinity, alignment: .leading)
      .padding(DesignTokens.spacing)
    }
    .background { AppBackground() }
    .accessibilityIdentifier("screen.\(identifier)")
  }
}

/// A rounded glass panel that groups related controls.
struct GlassSection<Content: View>: View {
  let title: LocalizedStringKey?
  let content: Content

  init(_ title: LocalizedStringKey? = nil, @ViewBuilder content: () -> Content) {
    self.title = title
    self.content = content()
  }

  var body: some View {
    VStack(alignment: .leading, spacing: 12) {
      if let title {
        Text(title).font(.headline)
      }
      content
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .padding()
    .glassCard()
  }
}

/// Picks one image. Uses the system photo picker.
struct SinglePhotoButton: View {
  let title: LocalizedStringKey
  @Binding var selection: PhotosPickerItem?
  @State private var isPresented = false

  var body: some View {
    Button(title) { isPresented = true }
      .buttonStyle(.glassProminent)
      .photosPicker(isPresented: $isPresented, selection: $selection, matching: .images)
  }
}

/// Picks several images at once.
struct MultiPhotoButton: View {
  let title: LocalizedStringKey
  @Binding var selection: [PhotosPickerItem]
  var maxCount: Int = 10
  @State private var isPresented = false

  var body: some View {
    Button(title) { isPresented = true }
      .buttonStyle(.glassProminent)
      .photosPicker(
        isPresented: $isPresented,
        selection: $selection,
        maxSelectionCount: maxCount,
        matching: .images)
  }
}

/// Shows a decoded picture at its own aspect ratio, capped in height.
struct PictureView: View {
  let image: CGImage
  var maxHeight: CGFloat = 320

  var body: some View {
    Image(decorative: image, scale: 1)
      .resizable()
      .scaledToFit()
      .frame(maxHeight: maxHeight)
      .clipShape(.rect(cornerRadius: 16))
      .frame(maxWidth: .infinity)
  }
}

/// Share button for a finished file. Keeps the same look as the other glass buttons.
struct ShareFileButton: View {
  let title: LocalizedStringKey
  let data: Data
  let fileName: String

  var body: some View {
    ShareLink(item: ExportFile(data: data, fileName: fileName), preview: SharePreview(fileName)) {
      Label(title, systemImage: "square.and.arrow.up")
    }
    .buttonStyle(.glass)
  }
}

/// Labelled slider with the current value shown next to it.
struct ParameterSlider: View {
  let title: LocalizedStringKey
  @Binding var value: Double
  /// Called when a drag ends, so a tool can record one undo step per drag.
  var onEditingEnded: (() -> Void)? = nil
  let range: ClosedRange<Double>
  var step: Double = 1
  var format: String = "%.0f"

  var body: some View {
    VStack(alignment: .leading, spacing: 4) {
      HStack {
        Text(title)
        Spacer()
        Text(String(format: format, value))
          .monospacedDigit()
          .foregroundStyle(.secondary)
      }
      Slider(value: $value, in: range, step: step) { editing in
        if !editing { onEditingEnded?() }
      }
    }
  }
}

/// Segmented picker limited to formats this device can encode.
struct OutputFormatPicker: View {
  @Binding var selection: OutputFormat

  var body: some View {
    Picker("Format", selection: $selection) {
      ForEach(OutputFormat.allCases.filter(\.isEncodable), id: \.self) { format in
        Text(format.fileExtension.uppercased()).tag(format)
      }
    }
    .pickerStyle(.segmented)
  }
}

/// Error line in the tool's accent colour.
struct ErrorText: View {
  let message: LocalizedStringKey

  var body: some View {
    Text(message)
      .font(.footnote)
      .foregroundStyle(.red)
      .accessibilityIdentifier("error")
  }
}
