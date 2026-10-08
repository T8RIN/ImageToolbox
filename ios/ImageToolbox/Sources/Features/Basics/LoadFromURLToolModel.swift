import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import SwiftUI

@MainActor
@Observable
final class LoadFromURLToolModel {
  var address = ""

  private(set) var picture: CGImage?
  private(set) var pngData: Data?
  private(set) var pngFileName = "downloaded.png"
  private(set) var isLoading = false
  private(set) var errorMessage: LocalizedStringKey?

  /// Downloads the address, decodes it and prepares a PNG copy for sharing.
  func load() async {
    guard !isLoading else { return }
    guard let url = RemoteImageLoader.url(from: address) else {
      errorMessage = "Enter a valid http or https address."
      return
    }

    isLoading = true
    picture = nil
    pngData = nil
    errorMessage = nil
    defer { isLoading = false }

    do {
      let data = try await RemoteImageLoader().load(url)
      let result = await Task.detached { () -> (loaded: LoadedImage, png: Data)? in
        guard let image = ImageCodec.decode(data),
          let png = ExportEncoding.encode(image, as: .png, source: data)
        else { return nil }
        return (LoadedImage(data: data, image: image), png)
      }.value

      guard let prepared = result else {
        errorMessage = "The downloaded file could not be decoded."
        return
      }
      picture = prepared.loaded.image
      pngData = prepared.png
      pngFileName = ExportNaming.fileName(
        stem: "downloaded", fileExtension: "png", data: prepared.png)
    } catch let error as RemoteImageError {
      errorMessage = Self.message(for: error)
    } catch {
      errorMessage = "The image could not be downloaded."
    }
  }

  private static func message(for error: RemoteImageError) -> LocalizedStringKey {
    switch error {
    case .invalidURL: "Enter a valid http or https address."
    case .unsupportedScheme: "Only http and https addresses are supported."
    case .badStatus(let code): "The server answered with status \(code)."
    case .tooLarge: "The image file is too large."
    case .notAnImage: "The address does not point to an image."
    }
  }
}
