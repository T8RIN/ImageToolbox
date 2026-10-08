import ImageToolboxKit
import SwiftUI
import UIKit

struct ColorLibraryToolView: View {
  @State private var model = ColorLibraryToolModel()

  var body: some View {
    ToolScaffold(identifier: "colorLibrary") {
      GlassSection("Search") {
        TextField("Name or #hex", text: Bindable(model).query)
          .textFieldStyle(.roundedBorder)
          .autocorrectionDisabled()
          .textInputAutocapitalization(.never)
          .accessibilityIdentifier("colorLibrary.search")
      }

      if model.isLoading {
        ProgressView("Loading colours")
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }

      if !model.isLoading {
        Text("\(model.results.count) colours")
          .font(.footnote)
          .foregroundStyle(.secondary)
      }

      LazyVStack(alignment: .leading, spacing: 12) {
        ForEach(model.results) { named in
          colorRow(named)
        }
      }
    }
    .task {
      await model.loadIfNeeded()
    }
  }

  /// Tapping a row copies its hex value.
  private func colorRow(_ named: NamedColor) -> some View {
    Button {
      UIPasteboard.general.string = named.color.hexString
    } label: {
      HStack(spacing: 12) {
        RoundedRectangle(cornerRadius: 8)
          .fill(Color(rgba: named.color))
          .frame(width: 40, height: 40)

        VStack(alignment: .leading, spacing: 2) {
          Text(verbatim: named.name)
          Text(verbatim: named.color.hexString)
            .font(.footnote.monospaced())
            .foregroundStyle(.secondary)
        }

        Spacer(minLength: 0)
      }
      .contentShape(Rectangle())
    }
    .buttonStyle(.plain)
  }
}
