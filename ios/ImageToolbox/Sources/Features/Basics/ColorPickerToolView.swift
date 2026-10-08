import CoreGraphics
import ImageToolboxKit
import SwiftUI
import UIKit

struct ColorPickerToolView: View {
  private static let canvasHeight: CGFloat = 360

  @State private var model = ColorPickerToolModel()

  var body: some View {
    ToolScaffold(identifier: "colorPicker") {
      GlassSection("Image") {
        SinglePhotoButton(title: "Choose image", selection: Bindable(model).selection)
          .accessibilityIdentifier("colorPicker.pick")
      }

      if let picture = model.picture {
        GlassSection("Drag over the image") {
          pickingArea(for: picture.image)
            .frame(height: Self.canvasHeight)
        }
      }

      if let sample = model.sample {
        readout(sample)
      }

      if let errorMessage = model.errorMessage {
        ErrorText(message: errorMessage)
      }
    }
  }

  /// Shows the picture scaled to fit, and maps touches inside the picture to normalised points.
  private func pickingArea(for image: CGImage) -> some View {
    let imageSize = CGSize(width: CGFloat(image.width), height: CGFloat(image.height))
    return GeometryReader { proxy in
      let frame = Self.fittedRect(for: imageSize, in: proxy.size)
      ZStack(alignment: .topLeading) {
        Image(decorative: image, scale: 1)
          .resizable()
          .scaledToFit()
          .frame(width: frame.width, height: frame.height)
          .offset(x: frame.minX, y: frame.minY)

        if let point = model.samplePoint {
          Circle()
            .strokeBorder(Color.white, lineWidth: 2)
            .shadow(radius: 2)
            .frame(width: 24, height: 24)
            .offset(
              x: frame.minX + point.x * frame.width - 12,
              y: frame.minY + point.y * frame.height - 12
            )
            .allowsHitTesting(false)
        }
      }
      .frame(width: proxy.size.width, height: proxy.size.height, alignment: .topLeading)
      .contentShape(Rectangle())
      .gesture(
        DragGesture(minimumDistance: 0)
          .onChanged { value in
            guard frame.contains(value.location) else { return }
            let point = CGPoint(
              x: (value.location.x - frame.minX) / frame.width,
              y: (value.location.y - frame.minY) / frame.height
            )
            model.pick(at: point)
          }
      )
    }
  }

  private func readout(_ color: RGBA) -> some View {
    GlassSection("Colour") {
      RoundedRectangle(cornerRadius: 16)
        .fill(Color(rgba: color))
        .frame(height: 80)

      Text(verbatim: color.hexString)
        .font(.title3.monospaced().weight(.semibold))
      Text(verbatim: Self.hslText(color))
        .font(.footnote.monospaced())
        .foregroundStyle(.secondary)
      Text(verbatim: Self.cmykText(color))
        .font(.footnote.monospaced())
        .foregroundStyle(.secondary)

      Button("Copy hex") {
        UIPasteboard.general.string = color.hexString
      }
      .buttonStyle(.glass)
      .accessibilityIdentifier("colorPicker.copy")
    }
  }

  /// The rect that `.scaledToFit()` gives an image of `imageSize` inside `container`, centred.
  private static func fittedRect(for imageSize: CGSize, in container: CGSize) -> CGRect {
    guard imageSize.width > 0, imageSize.height > 0, container.width > 0, container.height > 0
    else { return .zero }
    let scale = min(container.width / imageSize.width, container.height / imageSize.height)
    let size = CGSize(width: imageSize.width * scale, height: imageSize.height * scale)
    let origin = CGPoint(
      x: (container.width - size.width) / 2,
      y: (container.height - size.height) / 2
    )
    return CGRect(origin: origin, size: size)
  }

  private static func hslText(_ color: RGBA) -> String {
    let hsl = color.hsl
    return String(
      format: "HSL %.0f, %.0f%%, %.0f%%",
      hsl.hue,
      hsl.saturation,
      hsl.lightness
    )
  }

  private static func cmykText(_ color: RGBA) -> String {
    let cmyk = color.cmyk
    return String(
      format: "CMYK %.0f%%, %.0f%%, %.0f%%, %.0f%%",
      cmyk.cyan,
      cmyk.magenta,
      cmyk.yellow,
      cmyk.key
    )
  }
}
