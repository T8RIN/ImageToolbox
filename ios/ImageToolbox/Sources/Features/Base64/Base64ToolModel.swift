import Foundation
import ImageToolboxKit
import Observation
import UIKit
import os

@MainActor
@Observable
final class Base64ToolModel {
  private static let logger = Logger(subsystem: "com.the80hz.imagetoolbox", category: "base64")

  var decodeInput = ""

  private(set) var encodedText = ""
  private(set) var encodedLength = 0
  private(set) var decodedImage: UIImage?
  private(set) var decodedFileURL: URL?
  private(set) var decodeFailed = false

  /// Encodes raw image bytes exactly as picked, like `AndroidBase64Converter.encode`.
  func encode(imageData: Data) async {
    let text = await Task.detached(priority: .userInitiated) {
      Base64Codec.encode(imageData)
    }.value

    encodedText = text
    encodedLength = text.utf8.count
  }

  func decode() async {
    let input = decodeInput
    let data = await Task.detached(priority: .userInitiated) {
      Base64Codec.decode(input)
    }.value

    guard let data, let image = UIImage(data: data), let png = image.pngData() else {
      decodedImage = nil
      decodedFileURL = nil
      decodeFailed = true
      Self.logger.info("Base64 decode failed, input length \(input.utf8.count, privacy: .public)")
      return
    }

    decodeFailed = false
    decodedImage = image
    decodedFileURL = writeTemporaryPNG(png)
  }

  private func writeTemporaryPNG(_ png: Data) -> URL? {
    let timestamp = Int(Date().timeIntervalSince1970 * 1000)
    let url = FileManager.default.temporaryDirectory
      .appendingPathComponent("Base64_decoded_\(timestamp).png")

    do {
      try png.write(to: url)
      return url
    } catch {
      Self.logger.error(
        "Could not write decoded PNG: \(error.localizedDescription, privacy: .public)")
      return nil
    }
  }
}
