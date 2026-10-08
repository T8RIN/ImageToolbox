import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI
import UIKit

@MainActor
@Observable
final class WatermarkingToolModel {
  var text = "© Image Toolbox"
  var fontSize = 48.0
  var opacity = 0.5
  var rotation = 0.0
  var repeatsText = false
  var useTimestamp = false
  var color: Color = .white
  var secret = ""

  private(set) var base: LoadedImage?
  private(set) var baseFailed = false
  private(set) var overlay: LoadedImage?
  private(set) var overlayFailed = false
  private(set) var result: LoadedImage?
  private(set) var applyFailed = false
  private(set) var isApplying = false
  private(set) var hiddenData: Data?
  private(set) var hideFailed = false
  private(set) var hiddenText: String?
  /// The styling and text that apply() uses, as one value for undo and redo.
  struct Choice: Equatable, Sendable {
    var text: String
    var fontSize: Double
    var opacity: Double
    var rotation: Double
    var repeatsText: Bool
    var useTimestamp: Bool
    var color: Color
    var secret: String
  }

  /// One step per apply. The first apply sets the starting point, so undo returns to an earlier apply.
  private(set) var history: UndoHistory<Choice>?

  var canUndo: Bool { history?.canUndo ?? false }
  var canRedo: Bool { history?.canRedo ?? false }

  private func commitChoice() {
    let choice = Choice(
      text: text, fontSize: fontSize, opacity: opacity, rotation: rotation,
      repeatsText: repeatsText, useTimestamp: useTimestamp, color: color, secret: secret)
    if history == nil {
      history = UndoHistory(choice)
    } else {
      history?.record(choice)
    }
  }

  func undo() {
    guard history?.undo() == true, let choice = history?.current else { return }
    restore(choice)
  }

  func redo() {
    guard history?.redo() == true, let choice = history?.current else { return }
    restore(choice)
  }

  private func restore(_ choice: Choice) {
    text = choice.text
    fontSize = choice.fontSize
    opacity = choice.opacity
    rotation = choice.rotation
    repeatsText = choice.repeatsText
    useTimestamp = choice.useTimestamp
    color = choice.color
    secret = choice.secret
  }

  private(set) var readFailed = false
  private(set) var watermarkedFileName = "watermarked.png"
  private(set) var hiddenFileName = "hidden.png"

  func setBase(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    base = loaded
    baseFailed = loaded == nil
    clearResults()
  }

  func setOverlay(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    overlay = loaded
    overlayFailed = loaded == nil
  }

  func removeOverlay() {
    overlay = nil
    overlayFailed = false
  }

  /// Draws the text first, then the overlay picture on top of it.
  func apply() async {
    guard let base else { return }
    commitChoice()
    let image = base.image
    let sourceData = base.data
    let watermark = makeTextWatermark()
    let overlayImage = overlay?.image
    isApplying = true
    applyFailed = false
    let output = await Task.detached(priority: .userInitiated) { () -> LoadedImage? in
      var current = image
      if let watermark {
        guard let next = Watermark.applyText(watermark, to: current) else { return nil }
        current = next
      }
      if let overlayImage {
        guard let next = Watermark.applyImage(overlayImage, to: current) else { return nil }
        current = next
      }
      guard let data = ExportEncoding.encode(current, as: .png, source: sourceData) else {
        return nil
      }
      return LoadedImage(data: data, image: current)
    }.value
    isApplying = false
    result = output
    watermarkedFileName =
      output.map { ExportNaming.fileName(stem: "watermarked", fileExtension: "png", data: $0.data) }
      ?? "watermarked.png"
    applyFailed = output == nil
  }

  func hideSecret() async {
    guard let base else { return }
    let image = base.image
    let message = secret
    let data = await Task.detached(priority: .userInitiated) { () -> Data? in
      guard let embedded = LSBSteganography.embed(message, in: image) else { return nil }
      return ImageCodec.encode(embedded, as: .png)
    }.value
    hiddenData = data
    hiddenFileName =
      data.map { ExportNaming.fileName(stem: "hidden", fileExtension: "png", data: $0) }
      ?? "hidden.png"
    hideFailed = data == nil
  }

  func readHiddenText() async {
    guard let base else { return }
    let image = base.image
    let found = await Task.detached(priority: .userInitiated) {
      LSBSteganography.extract(from: image)
    }.value
    hiddenText = found
    readFailed = found == nil
  }

  private func clearResults() {
    result = nil
    applyFailed = false
    hiddenData = nil
    hideFailed = false
    hiddenText = nil
    readFailed = false
  }

  private func makeTextWatermark() -> TextWatermark? {
    let content = useTimestamp ? timestamp(Date.now) : text
    guard !content.isEmpty else { return nil }
    return TextWatermark(
      text: content,
      fontSize: CGFloat(fontSize),
      color: Self.rgba(from: color),
      opacity: opacity,
      rotation: rotation,
      repeats: repeatsText)
  }

  private func timestamp(_ date: Date) -> String {
    let formatter = DateFormatter()
    formatter.locale = Locale(identifier: "en_US_POSIX")
    formatter.dateFormat = "yyyy-MM-dd HH:mm"
    return formatter.string(from: date)
  }

  /// Converts a SwiftUI colour to the package's 8-bit RGBA. Falls back to white.
  private static func rgba(from color: Color) -> RGBA {
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var alpha: CGFloat = 0
    guard UIColor(color).getRed(&red, green: &green, blue: &blue, alpha: &alpha) else {
      return RGBA(red: 255, green: 255, blue: 255)
    }
    return RGBA(
      red: channel(red),
      green: channel(green),
      blue: channel(blue),
      alpha: channel(alpha))
  }

  private static func channel(_ value: CGFloat) -> UInt8 {
    UInt8((min(max(value, 0), 1) * 255).rounded())
  }
}
