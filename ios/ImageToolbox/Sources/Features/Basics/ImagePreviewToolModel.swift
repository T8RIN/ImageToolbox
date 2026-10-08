import Foundation
import Observation
import SwiftUI

@MainActor
@Observable
final class ImagePreviewToolModel {
  private(set) var fileURL: URL?
  /// Bound to `.quickLookPreview`. SwiftUI clears it when the preview is dismissed.
  var previewURL: URL?
  private(set) var errorMessage: LocalizedStringKey?

  @ObservationIgnored private var isAccessingScope = false

  func receive(_ result: Result<URL, Error>) {
    switch result {
    case .success(let url):
      open(url)
    case .failure:
      errorMessage = "The file could not be opened."
    }
  }

  func showPreview() {
    previewURL = fileURL
  }

  /// Ends the security-scoped access when the screen goes away. While QuickLook is on screen
  /// the access is kept, because the preview reads the file after it has appeared.
  func close() {
    guard previewURL == nil else { return }
    stopAccessing()
    fileURL = nil
  }

  private func open(_ url: URL) {
    stopAccessing()
    errorMessage = nil
    isAccessingScope = url.startAccessingSecurityScopedResource()
    fileURL = url
    previewURL = url
  }

  private func stopAccessing() {
    guard isAccessingScope, let fileURL else { return }
    fileURL.stopAccessingSecurityScopedResource()
    isAccessingScope = false
  }
}
