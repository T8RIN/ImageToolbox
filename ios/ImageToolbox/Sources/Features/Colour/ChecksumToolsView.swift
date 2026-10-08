import ImageToolboxKit
import SwiftUI
import UIKit
import UniformTypeIdentifiers

struct ChecksumToolsView: View {
  @State private var model = ChecksumToolsModel()
  @State private var isImporterPresented = false

  var body: some View {
    ToolScaffold(identifier: "checksumTools") {
      algorithmSection
      inputSection
      digestSection
      compareSection
    }
    .fileImporter(
      isPresented: $isImporterPresented,
      allowedContentTypes: [.data],
      allowsMultipleSelection: false
    ) { result in
      if case .success(let urls) = result, let url = urls.first {
        model.selectFile(url)
      }
    }
    .onChange(of: model.algorithm) { _, _ in model.clearDigest() }
    .onChange(of: model.text) { _, _ in model.clearDigest() }
  }

  private var algorithmSection: some View {
    GlassSection("Algorithm") {
      HStack {
        Button("Undo") { model.undo() }
          .buttonStyle(.glass)
          .disabled(!model.canUndo)
          .accessibilityIdentifier("checksum.undo")
        Button("Redo") { model.redo() }
          .buttonStyle(.glass)
          .disabled(!model.canRedo)
          .accessibilityIdentifier("checksum.redo")
      }
      Picker("Algorithm", selection: $model.algorithm) {
        ForEach(ChecksumAlgorithm.allCases, id: \.self) { algorithm in
          Text(algorithm.rawValue.uppercased()).tag(algorithm)
        }
      }
      .pickerStyle(.menu)
      .accessibilityIdentifier("checksumTools.algorithm")
    }
  }

  private var inputSection: some View {
    GlassSection("Input") {
      Button("Choose file") { isImporterPresented = true }
        .buttonStyle(.glassProminent)
        .accessibilityIdentifier("checksumTools.chooseFile")

      if let fileName = model.fileName {
        HStack {
          Text(fileName)
            .lineLimit(1)
            .truncationMode(.middle)
          Spacer()
          Button("Remove") { model.removeFile() }
            .buttonStyle(.glass)
            .accessibilityIdentifier("checksumTools.removeFile")
        }
        Text("The file is hashed. Remove it to type text instead.")
          .font(.footnote)
          .foregroundStyle(.secondary)
      }

      TextField("Or type text", text: $model.text, axis: .vertical)
        .lineLimit(1...6)
        .textFieldStyle(.roundedBorder)
        .disabled(model.fileName != nil)
    }
  }

  private var digestSection: some View {
    GlassSection("Digest") {
      Button("Compute") {
        model.commitParameters()
        Task { await model.compute() }
      }
      .buttonStyle(.glassProminent)
      .disabled(model.isComputing)
      .accessibilityIdentifier("checksumTools.compute")

      if model.isComputing {
        ProgressView()
      }

      if let digest = model.digest {
        Text(digest)
          .font(.footnote.monospaced())
          .textSelection(.enabled)
          .accessibilityIdentifier("checksumTools.digest")

        Button("Copy") { UIPasteboard.general.string = digest }
          .buttonStyle(.glass)
          .accessibilityIdentifier("checksumTools.copy")
      }

      if model.readFailed {
        ErrorText(message: "Could not read the selected file.")
      }
    }
  }

  private var compareSection: some View {
    GlassSection("Compare") {
      TextField("Compare with", text: $model.expected)
        .font(.footnote.monospaced())
        .textInputAutocapitalization(.never)
        .autocorrectionDisabled()
        .textFieldStyle(.roundedBorder)
        .accessibilityIdentifier("checksumTools.expected")

      if let matches = model.expectedMatches {
        Text(matches ? "Match" : "Different")
          .font(.headline)
          .foregroundStyle(matches ? .green : .red)
          .accessibilityIdentifier("checksumTools.result")
      }
    }
  }
}
