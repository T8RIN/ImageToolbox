import ImageToolboxKit
import SwiftUI

/// Preferences that apply to every tool. Opened from the toolbar of the tool grid.
struct SettingsView: View {
  let store: SettingsStore

  @Environment(\.dismiss) private var dismiss
  /// Keeps the template text while the naming mode is switched away and back.
  @State private var template: String

  init(store: SettingsStore) {
    self.store = store
    if case .template(let pattern) = store.settings.naming {
      _template = State(initialValue: pattern)
    } else {
      _template = State(initialValue: "{name}_{n:3}")
    }
  }

  var body: some View {
    NavigationStack {
      ScrollView {
        VStack(alignment: .leading, spacing: DesignTokens.spacing) {
          GlassSection("Metadata") {
            Toggle("Keep metadata", isOn: keepMetadata)
              .accessibilityIdentifier("settings.keepMetadata")
            Text(
              "Copies EXIF, GPS and other tags into exported files. Off by default, so exports carry no location."
            )
            .font(.footnote)
            .foregroundStyle(.secondary)
          }

          GlassSection("File names") {
            Picker("Name", selection: namingKind) {
              ForEach(NamingKind.allCases) { kind in
                Text(kind.title).tag(kind)
              }
            }
            .pickerStyle(.menu)
            .accessibilityIdentifier("settings.naming")

            if namingKind.wrappedValue == .template {
              TextField("Name template", text: $template)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .accessibilityIdentifier("settings.template")
                .onChange(of: template) { _, value in
                  store.settings.naming = .template(value)
                }
              Text("Tokens: {name}, {ext}, {n}, {n:3}, {date:yyyy-MM-dd}")
                .font(.footnote)
                .foregroundStyle(.secondary)
            }

            Text("Example: \(exampleName)")
              .font(.footnote)
              .foregroundStyle(.secondary)
              .accessibilityIdentifier("settings.example")
          }
        }
        .padding(DesignTokens.spacing)
      }
      .background { AppBackground() }
      .navigationTitle("Settings")
      .navigationBarTitleDisplayMode(.inline)
      .toolbar {
        ToolbarItem(placement: .confirmationAction) {
          Button("Done") { dismiss() }
            .accessibilityIdentifier("settings.done")
        }
      }
    }
  }

  /// The name a PNG of `photo` would get now. A seeded generator keeps the random example stable.
  private var exampleName: String {
    var generator = SeededGenerator(seed: 1)
    return store.settings.naming.fileName(
      stem: "photo", fileExtension: "png", data: Data("photo".utf8), sequenceNumber: 1,
      using: &generator)
  }

  private var keepMetadata: Binding<Bool> {
    Binding(
      get: { store.settings.keepMetadata },
      set: { store.settings.keepMetadata = $0 })
  }

  private var namingKind: Binding<NamingKind> {
    Binding(
      get: { NamingKind(store.settings.naming) },
      set: { kind in
        switch kind {
        case .fixed: store.settings.naming = .fixed
        case .random: store.settings.naming = .random
        case .checksum: store.settings.naming = .checksum
        case .template: store.settings.naming = .template(template)
        }
      })
  }
}

/// The four naming modes as a flat list for the picker.
private enum NamingKind: CaseIterable, Identifiable {
  case fixed
  case random
  case checksum
  case template

  var id: Self { self }

  init(_ naming: OutputNaming) {
    switch naming {
    case .fixed: self = .fixed
    case .random: self = .random
    case .checksum: self = .checksum
    case .template: self = .template
    }
  }

  var title: LocalizedStringKey {
    switch self {
    case .fixed: "Tool name"
    case .random: "Random name"
    case .checksum: "Checksum name"
    case .template: "Template"
    }
  }
}
