import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI
import UIKit

@MainActor
@Observable
final class ScreenshotFramingToolModel {

  /// Settings that the undo history keeps. A step is recorded when a slider drag ends, a choice
  /// changes, or the operation starts.
  struct Parameters: Equatable, Sendable {
    var padding: Double
    var cornerRadius: Double
    var shadowRadius: Double
    var background: Color
  }

  private(set) var history = UndoHistory(
    Parameters(
      padding: 48, cornerRadius: 24, shadowRadius: 24,
      background: Color(red: 240.0 / 255, green: 242.0 / 255, blue: 247.0 / 255)))

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func commitParameters() {
    history.record(currentParameters)
  }

  func undo() {
    guard history.undo() else { return }
    restoreParameters(history.current)
  }

  func redo() {
    guard history.redo() else { return }
    restoreParameters(history.current)
  }

  private var currentParameters: Parameters {
    Parameters(
      padding: padding, cornerRadius: cornerRadius, shadowRadius: shadowRadius,
      background: background)
  }

  private func restoreParameters(_ parameters: Parameters) {
    padding = parameters.padding
    cornerRadius = parameters.cornerRadius
    shadowRadius = parameters.shadowRadius
    background = parameters.background
  }
  /// Everything that changes the render. The view re-renders when it changes.
  struct Settings: Equatable, Sendable {
    let padding: Int
    let cornerRadius: CGFloat
    let shadowRadius: CGFloat
    let background: RGBA

    var frame: ScreenshotFrame {
      ScreenshotFrame(
        padding: padding,
        cornerRadius: cornerRadius,
        shadowRadius: shadowRadius,
        background: background)
    }
  }

  var pickedItem: PhotosPickerItem?
  var padding: Double = 48
  var cornerRadius: Double = 24
  var shadowRadius: Double = 24
  var background = Color(red: 240.0 / 255, green: 242.0 / 255, blue: 247.0 / 255)

  private(set) var source: CGImage?
  private(set) var framed: PNGPicture?
  private(set) var framedFileName = "framed.png"
  private(set) var errorMessage: LocalizedStringKey?

  var settings: Settings {
    Settings(
      padding: Int(padding.rounded()),
      cornerRadius: CGFloat(cornerRadius),
      shadowRadius: CGFloat(shadowRadius),
      background: Self.rgba(background))
  }

  func pick(_ item: PhotosPickerItem) async {
    framed = nil
    errorMessage = nil
    guard let loaded = await PhotoLoader.load(item) else {
      source = nil
      errorMessage = "Could not read this photo."
      return
    }
    source = loaded.image
    await render()
  }

  /// Renders the framed picture off the main actor. A render superseded by new settings is dropped.
  func render() async {
    guard let source else { return }
    let frame = settings.frame
    let picture = await Task.detached(priority: .userInitiated) { () -> PNGPicture? in
      guard let rendered = frame.render(source) else { return nil }
      return PNGPicture(rendered)
    }.value
    guard !Task.isCancelled else { return }

    framed = picture
    framedFileName =
      picture.map { ExportNaming.fileName(stem: "framed", fileExtension: "png", data: $0.png) }
      ?? "framed.png"
    if picture == nil {
      errorMessage = "Could not render the frame."
    } else {
      errorMessage = nil
    }
  }

  private static func rgba(_ color: Color) -> RGBA {
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var alpha: CGFloat = 0
    UIColor(color).getRed(&red, green: &green, blue: &blue, alpha: &alpha)
    return RGBA(red: channel(red), green: channel(green), blue: channel(blue))
  }

  private static func channel(_ value: CGFloat) -> UInt8 {
    UInt8((min(max(value, 0), 1) * 255).rounded())
  }
}
