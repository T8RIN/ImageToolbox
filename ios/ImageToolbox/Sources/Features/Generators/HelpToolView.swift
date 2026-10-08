import ImageToolboxKit
import SwiftUI

struct HelpToolView: View {
  @State private var query = ""

  var body: some View {
    ToolScaffold(identifier: "help") {
      GlassSection {
        HStack(spacing: 8) {
          Image(systemName: "magnifyingglass")
            .foregroundStyle(.secondary)
          TextField("Search help", text: $query)
            .autocorrectionDisabled()
            .accessibilityIdentifier("help.search")
        }
      }

      GlassSection {
        if results.isEmpty {
          Text("No help topics match this search.")
            .foregroundStyle(.secondary)
        } else {
          ForEach(results) { entry in
            DisclosureGroup(entry.title) {
              Text(entry.body)
                .font(.callout)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.top, 4)
            }
          }
        }
      }
    }
  }

  private var results: [HelpEntry] {
    HelpSearch.results(for: query, in: Self.entries)
  }

  private static let entries: [HelpEntry] = [
    HelpEntry(
      id: "pick-images",
      title: "Picking images",
      body: """
        Tap Choose image and select a photo from your library. \
        The app keeps the original file bytes, so metadata can be read or removed later.
        """,
      keywords: ["photo", "picture", "library", "open", "select", "choose"]),
    HelpEntry(
      id: "sharing",
      title: "Sharing results",
      body: """
        Every result is exported through the system share sheet. From there you can send \
        the file to another app or AirDrop it. The app itself cannot choose a folder.
        """,
      keywords: ["export", "share", "send", "airdrop", "files"]),
    HelpEntry(
      id: "base64",
      title: "Base64 text",
      body: """
        Base64 turns the bytes of an image into plain text that can be pasted into code, \
        JSON or HTML. Encode a picked image, or paste a Base64 string to decode it.
        """,
      keywords: ["encode", "decode", "string", "text", "data uri", "code"]),
    HelpEntry(
      id: "exif",
      title: "Removing EXIF metadata",
      body: """
        Photos often store the camera model, capture time and GPS location in EXIF data. \
        Deleting EXIF writes a copy without that metadata. The original file stays unchanged.
        """,
      keywords: ["metadata", "gps", "location", "privacy", "strip", "camera", "delete exif"]),
    HelpEntry(
      id: "color-picker",
      title: "Color picker",
      body: """
        The color picker reads the color of one pixel and shows it as HEX and RGB values. \
        Copy the value you need and paste it into your design or code.
        """,
      keywords: ["color", "pixel", "hex", "rgb", "eyedropper"]),
    HelpEntry(
      id: "weight-resize",
      title: "Resize to file size",
      body: """
        Set a target file size and the tool adjusts the output until the file fits. \
        Quality changes work best with JPEG and HEIC, which are lossy formats.
        """,
      keywords: ["weight", "kilobytes", "megabytes", "compress", "size", "limit"]),
    HelpEntry(
      id: "checksums",
      title: "Checksums",
      body: """
        A checksum is a short fingerprint of a file, such as SHA-256. Compare it with the \
        value published by the source to confirm that the file was not changed.
        """,
      keywords: ["hash", "sha", "md5", "verify", "integrity", "fingerprint"]),
    HelpEntry(
      id: "limits",
      title: "What the app cannot do",
      body: """
        On iOS an app cannot choose a folder to save into or delete files it did not create. \
        Results are shared instead, and original photos must be removed in the Photos app.
        """,
      keywords: ["folder", "delete", "original", "save", "limits", "not possible", "ios"]),
  ]
}
