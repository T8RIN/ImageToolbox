import Foundation
import PhotosUI
import SwiftUI

struct CompareToolView: View {
  @State private var model = CompareToolModel()
  @State private var beforeItem: PhotosPickerItem?
  @State private var afterItem: PhotosPickerItem?

  var body: some View {
    ToolScaffold(identifier: "compare") {
      GlassSection("Pictures") {
        HStack {
          SinglePhotoButton(title: "Before", selection: $beforeItem)
            .accessibilityIdentifier("compare.before")
          SinglePhotoButton(title: "After", selection: $afterItem)
            .accessibilityIdentifier("compare.after")
        }
        if model.beforeFailed || model.afterFailed {
          ErrorText(message: "Could not read one of the pictures.")
        }
        if !model.hasBothImages {
          Text("Pick both pictures to compare them.")
            .font(.footnote)
            .foregroundStyle(.secondary)
        }
      }

      if model.hasBothImages {
        resultSection
        metricsSection
        exportSection
      }
    }
    .onChange(of: beforeItem) { _, item in
      guard let item else { return }
      Task { await model.setBefore(item) }
    }
    .onChange(of: afterItem) { _, item in
      guard let item else { return }
      Task { await model.setAfter(item) }
    }
  }

  private var resultSection: some View {
    GlassSection("Result") {
      Picker("Mode", selection: $model.mode) {
        Text("Side by side").tag(CompareToolModel.Mode.sideBySide)
        Text("Slider").tag(CompareToolModel.Mode.slider)
      }
      .pickerStyle(.segmented)
      .accessibilityIdentifier("compare.mode")

      if model.mode == .slider {
        ParameterSlider(
          title: "Split", value: $model.split, range: 0...1, step: 0.01, format: "%.2f")
      }

      if let preview = model.preview {
        PictureView(image: preview)
      } else if model.previewFailed {
        ErrorText(message: "Could not build the comparison.")
      } else {
        ProgressView()
      }
    }
  }

  private var metricsSection: some View {
    GlassSection("Metrics") {
      if let metrics = model.metrics {
        metricRow("MAE", value: String(format: "%.2f", metrics.meanAbsoluteError))
        metricRow("RMSE", value: String(format: "%.2f", metrics.rootMeanSquaredError))
        metricRow("PSNR", value: psnrText(metrics.psnr))
        metricRow("SSIM", value: String(format: "%.3f", metrics.structuralSimilarity))
        metricRow("NCC", value: String(format: "%.3f", metrics.normalizedCrossCorrelation))
        metricRow("Differing pixels", value: metrics.differingPixels.formatted())
      } else if model.metricsFailed {
        ErrorText(message: "Could not compare the pictures.")
      } else {
        ProgressView()
      }
    }
  }

  private var exportSection: some View {
    GlassSection("Export") {
      Button("Export animated GIF") {
        Task { await model.makeGIF() }
      }
      .buttonStyle(.glassProminent)
      .disabled(model.isMakingGIF)
      .accessibilityIdentifier("compare.gif")

      if model.isMakingGIF {
        Button("Cancel") { model.cancelGIF() }
          .buttonStyle(.glass)
          .accessibilityIdentifier("compare.cancelGIF")
        ProgressView()
      } else if let gifData = model.gifData {
        ShareFileButton(title: "Share GIF", data: gifData, fileName: model.gifFileName)
          .accessibilityIdentifier("compare.share")
      } else if model.gifFailed {
        ErrorText(message: "Could not build the GIF.")
      }
    }
  }

  private func metricRow(_ title: LocalizedStringKey, value: String) -> some View {
    HStack {
      Text(title)
      Spacer()
      Text(value)
        .monospacedDigit()
        .foregroundStyle(.secondary)
    }
  }

  private func psnrText(_ psnr: Double) -> String {
    psnr.isInfinite ? "∞" : String(format: "%.2f dB", psnr)
  }
}
