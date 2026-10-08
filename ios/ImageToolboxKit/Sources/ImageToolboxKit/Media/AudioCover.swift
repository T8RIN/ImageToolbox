import AVFoundation
import Foundation

/// Reads the cover picture embedded in an audio file's metadata (ID3 APIC, iTunes covr, etc.).
public enum AudioCover {

  /// Returns the raw image bytes of the first artwork item, or nil when the file has none.
  public static func artwork(in url: URL) async throws -> Data? {
    let asset = AVURLAsset(url: url)
    let items = try await asset.load(.commonMetadata)
    for item in items where isArtwork(item) {
      if let data = try await item.load(.dataValue), !data.isEmpty {
        return data
      }
    }
    return nil
  }

  private static func isArtwork(_ item: AVMetadataItem) -> Bool {
    item.commonKey == .commonKeyArtwork || item.identifier == .commonIdentifierArtwork
  }
}
