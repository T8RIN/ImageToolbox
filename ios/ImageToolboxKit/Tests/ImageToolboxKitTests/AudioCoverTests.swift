import AVFoundation
import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Audio cover art")
struct AudioCoverTests {

  /// Writes a short silent AAC file and optionally embeds `artwork` as the cover.
  private func makeAudio(artwork: Data?) async throws -> URL {
    let directory = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString)
    try FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)
    let plain = directory.appendingPathComponent("plain.m4a")
    let final = directory.appendingPathComponent("cover.m4a")

    let format = try #require(AVAudioFormat(standardFormatWithSampleRate: 44_100, channels: 1))
    let settings: [String: Any] = [
      AVFormatIDKey: kAudioFormatMPEG4AAC,
      AVSampleRateKey: 44_100,
      AVNumberOfChannelsKey: 1,
    ]
    // AVAudioFile finalises the container when it is released, so write inside its own scope.
    do {
      let file = try AVAudioFile(forWriting: plain, settings: settings)
      let buffer = try #require(AVAudioPCMBuffer(pcmFormat: format, frameCapacity: 22_050))
      buffer.frameLength = 22_050
      try file.write(from: buffer)
    }

    guard let artwork else { return plain }
    let session = try #require(
      AVAssetExportSession(asset: AVURLAsset(url: plain), presetName: AVAssetExportPresetAppleM4A))
    let item = AVMutableMetadataItem()
    item.identifier = .commonIdentifierArtwork
    item.dataType = kCMMetadataBaseDataType_JPEG as String
    item.value = artwork as NSData
    session.metadata = [item]
    try await session.export(to: final, as: .m4a)
    return final
  }

  @Test("embedded cover art is read back as the same image")
  func readsArtwork() async throws {
    let picture = TestImages.solid(RGBA(red: 200, green: 80, blue: 30), width: 64, height: 64)
    let jpeg = try #require(ImageCodec.encode(picture, as: .jpeg, quality: 0.9))
    let url = try await makeAudio(artwork: jpeg)

    let artwork = try #require(try await AudioCover.artwork(in: url))
    let decoded = try #require(ImageCodec.decode(artwork))
    #expect(decoded.width == 64 && decoded.height == 64)
  }

  @Test("a file without cover art gives nil")
  func noArtwork() async throws {
    let url = try await makeAudio(artwork: nil)
    #expect(try await AudioCover.artwork(in: url) == nil)
  }
}
