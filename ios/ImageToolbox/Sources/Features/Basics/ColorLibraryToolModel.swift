import Foundation
import ImageToolboxKit
import Observation
import SwiftUI

@MainActor
@Observable
final class ColorLibraryToolModel {
  /// Search text. Each change filters the library again, off the main actor.
  var query = "" {
    didSet { refreshResults() }
  }

  private(set) var results: [NamedColor] = []
  private(set) var isLoading = false
  private(set) var errorMessage: LocalizedStringKey?

  @ObservationIgnored private var colors: [NamedColor] = []
  @ObservationIgnored private var searchTask: Task<Void, Never>?

  /// Parses the bundled colour names once. Later calls do nothing once the library is loaded.
  func loadIfNeeded() async {
    guard colors.isEmpty, !isLoading else { return }
    isLoading = true
    defer { isLoading = false }

    do {
      colors = try await Task.detached { try ColorLibrary.load() }.value
      errorMessage = nil
      refreshResults()
    } catch {
      errorMessage = "The colour library could not be loaded."
    }
  }

  private func refreshResults() {
    searchTask?.cancel()
    let needle = query
    let all = colors
    searchTask = Task {
      let matches = await Task.detached { ColorLibrary.search(needle, in: all) }.value
      guard !Task.isCancelled else { return }
      results = matches
    }
  }
}
