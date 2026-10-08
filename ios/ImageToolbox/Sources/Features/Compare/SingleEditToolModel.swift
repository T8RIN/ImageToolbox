import CoreGraphics
import Foundation
import ImageToolboxKit
import Observation
import PhotosUI
import SwiftUI

@MainActor
@Observable
final class SingleEditToolModel {
  struct ProfileGroup: Identifiable {
    let name: String
    var profiles: [ExportProfile]

    var id: String { name }
  }

  struct EditedPicture: Sendable {
    let image: CGImage
    let data: Data
    let fileName: String
  }

  var profile: ExportProfile? = ExportProfile.builtIn.first
  var format: OutputFormat = .png
  var quality = 0.9

  private(set) var picture: LoadedImage?
  private(set) var pictureFailed = false
  private(set) var result: EditedPicture?
  private(set) var applyFailed = false
  /// The choices that apply() uses, as one value for undo and redo.
  struct Choice: Equatable, Sendable {
    var profile: ExportProfile?
    var format: OutputFormat
    var quality: Double
  }

  /// One step per apply. The first apply sets the starting point, so undo returns to an earlier apply.
  private(set) var history: UndoHistory<Choice>?

  var canUndo: Bool { history?.canUndo ?? false }
  var canRedo: Bool { history?.canRedo ?? false }

  private func commitChoice() {
    let choice = Choice(profile: profile, format: format, quality: quality)
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
    profile = choice.profile
    format = choice.format
    quality = choice.quality
  }

  private(set) var isApplying = false

  /// Built-in profiles in their declared order. Each platform forms one section.
  var profileGroups: [ProfileGroup] {
    var groups: [ProfileGroup] = []
    for profile in ExportProfile.builtIn {
      if groups.last?.name == profile.group {
        groups[groups.count - 1].profiles.append(profile)
      } else {
        groups.append(ProfileGroup(name: profile.group, profiles: [profile]))
      }
    }
    return groups
  }

  func setPicture(_ item: PhotosPickerItem) async {
    let loaded = await PhotoLoader.load(item)
    picture = loaded
    pictureFailed = loaded == nil
    result = nil
    applyFailed = false
  }

  func apply() async {
    guard let picture, let profile else { return }
    commitChoice()
    let image = picture.image
    let sourceData = picture.data
    let outputFormat = format
    let outputQuality = quality
    let name = fileName(for: profile)
    let stem = (name as NSString).deletingPathExtension
    isApplying = true
    applyFailed = false
    let output = await Task.detached(priority: .userInitiated) { () -> EditedPicture? in
      guard let resized = profile.apply(to: image),
        let data = ExportEncoding.encode(
          resized, as: outputFormat, quality: outputQuality, source: sourceData)
      else { return nil }
      let exportName = ExportNaming.fileName(
        stem: stem, fileExtension: outputFormat.fileExtension, data: data)
      return EditedPicture(image: resized, data: data, fileName: exportName)
    }.value
    isApplying = false
    result = output
    applyFailed = output == nil
  }

  /// Lowercase `group-name.ext` with dashes instead of spaces, e.g. `instagram-story.png`.
  private func fileName(for profile: ExportProfile) -> String {
    let base = "\(profile.group)-\(profile.name)"
      .lowercased()
      .replacingOccurrences(of: " ", with: "-")
    return "\(base).\(format.fileExtension)"
  }
}
