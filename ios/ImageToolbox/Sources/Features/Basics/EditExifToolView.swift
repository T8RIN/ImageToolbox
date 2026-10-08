import ImageToolboxKit
import PhotosUI
import SwiftUI

struct EditExifToolView: View {
  @State private var model = EditExifToolModel()

  var body: some View {
    ToolScaffold(identifier: "editExif") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: Bindable(model).selection)
          .accessibilityIdentifier("editExif.pick")
      }

      if model.source != nil {
        GlassSection("Text fields") {
          ForEach(EditableMetadataField.allCases, id: \.self) { field in
            VStack(alignment: .leading, spacing: 4) {
              Text(verbatim: field.rawValue)
                .font(.footnote)
                .foregroundStyle(.secondary)
              TextField(field.rawValue, text: binding(for: field))
                .textFieldStyle(.roundedBorder)
            }
          }
        }

        GlassSection("Save") {
          Button("Save") {
            model.save()
          }
          .buttonStyle(.glassProminent)
          .accessibilityIdentifier("editExif.apply")

          HStack {
            Button("Undo") { model.undo() }
              .buttonStyle(.glass)
              .disabled(!model.canUndo)
              .accessibilityIdentifier("editExif.undo")
            Button("Redo") { model.redo() }
              .buttonStyle(.glass)
              .disabled(!model.canRedo)
              .accessibilityIdentifier("editExif.redo")
          }

          if let edited = model.edited {
            ShareFileButton(
              title: "Share edited file",
              data: edited,
              fileName: "edited.\(model.outputExtension)"
            )
            .accessibilityIdentifier("editExif.share")
          }
        }
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }
    }
  }

  private func binding(for field: EditableMetadataField) -> Binding<String> {
    Binding(
      get: { model.values[field] ?? "" },
      set: { model.update(field, to: $0) }
    )
  }
}
