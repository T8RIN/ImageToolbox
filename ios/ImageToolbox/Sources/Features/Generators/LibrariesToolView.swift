import SwiftUI

struct LibrariesToolView: View {
  var body: some View {
    ToolScaffold(identifier: "libraries") {
      ForEach(Self.entries) { entry in
        GlassSection {
          VStack(alignment: .leading, spacing: 6) {
            Text(entry.name)
              .font(.headline)
            Text(entry.license)
              .font(.subheadline)
              .foregroundStyle(.secondary)
            Text(entry.note)
              .font(.footnote)
          }
          .frame(maxWidth: .infinity, alignment: .leading)
        }
      }
    }
  }

  private static let entries: [LibraryEntry] = [
    LibraryEntry(
      id: "image-toolbox",
      name: "Image Toolbox",
      license: "Apache License 2.0",
      note: "Original Android project by T8RIN."),
    LibraryEntry(
      id: "apple-frameworks",
      name: "Apple frameworks",
      license: "Apple SDK License",
      note: """
        SwiftUI, PhotosUI, ImageIO, CoreGraphics, CoreText, Vision, AVFoundation, \
        CryptoKit, UIKit.
        """),
    LibraryEntry(
      id: "image-toolbox-kit",
      name: "ImageToolboxKit",
      license: "In-house",
      note: "Image logic package of this project."),
  ]
}

private struct LibraryEntry: Identifiable {
  let id: String
  let name: LocalizedStringKey
  let license: LocalizedStringKey
  let note: LocalizedStringKey
}
