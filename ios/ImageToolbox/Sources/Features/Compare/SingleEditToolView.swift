import ImageToolboxKit
import PhotosUI
import SwiftUI

struct SingleEditToolView: View {
  @State private var model = SingleEditToolModel()
  @State private var pickedItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "singleEdit") {
      GlassSection("Picture") {
        SinglePhotoButton(title: "Choose picture", selection: $pickedItem)
          .accessibilityIdentifier("singleEdit.pick")
        if model.pictureFailed {
          ErrorText(message: "Could not read the picture.")
        }
      }

      GlassSection("Profile") {
        Picker("Profile", selection: $model.profile) {
          ForEach(model.profileGroups) { group in
            Section(group.name) {
              ForEach(group.profiles) { profile in
                Text("\(profile.group) – \(profile.name)")
                  .tag(profile as ExportProfile?)
              }
            }
          }
        }
        .pickerStyle(.menu)
        .accessibilityIdentifier("singleEdit.profile")

        if let profile = model.profile {
          Text(sizeDescription(profile))
            .font(.footnote)
            .foregroundStyle(.secondary)
        }
      }

      GlassSection("Format") {
        OutputFormatPicker(selection: $model.format)
        if model.format.isLossy {
          ParameterSlider(
            title: "Quality", value: $model.quality, range: 0.1...1, step: 0.05, format: "%.2f")
        }
      }

      GlassSection("Result") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("singleEdit.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("singleEdit.redo")
        }
        Button("Apply profile") {
          Task { await model.apply() }
        }
        .buttonStyle(.glassProminent)
        .disabled(model.picture == nil || model.profile == nil || model.isApplying)
        .accessibilityIdentifier("singleEdit.apply")

        if model.isApplying {
          ProgressView()
        }
        if let edited = model.result {
          PictureView(image: edited.image)
          ShareFileButton(title: "Share", data: edited.data, fileName: edited.fileName)
            .accessibilityIdentifier("singleEdit.share")
        }
        if model.applyFailed {
          ErrorText(message: "Could not export the picture with this profile.")
        }
      }
    }
    .onChange(of: pickedItem) { _, item in
      guard let item else { return }
      Task { await model.setPicture(item) }
    }
  }

  private func sizeDescription(_ profile: ExportProfile) -> String {
    if let size = profile.size {
      return "\(Int(size.width)) × \(Int(size.height)) px"
    }
    if let maxSide = profile.maxSide {
      return "Longest side up to \(maxSide) px"
    }
    return ""
  }
}
