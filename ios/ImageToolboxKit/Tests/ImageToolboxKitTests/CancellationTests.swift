import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

/// The encoders check for cancellation between frames and rows. Each task waits until it has been
/// cancelled, so the first check already sees the cancellation and the result is deterministic.
@Suite("Cooperative cancellation")
struct CancellationTests {

  private let frames = (0..<60).map { index in
    TestImages.solid(RGBA(red: UInt8(index), green: 80, blue: 160), width: 32, height: 32)
  }

  @Test("a cancelled GIF encode returns nothing")
  func gifStopsWhenCancelled() async {
    let frames = self.frames
    let task = Task.detached { () -> Data? in
      while !Task.isCancelled { await Task.yield() }
      return ImageCodec.encodeAnimation(frames, as: .gif, frameDelay: 0.1)
    }
    task.cancel()
    #expect(await task.value == nil)
  }

  @Test("a cancelled APNG encode returns nothing")
  func apngStopsWhenCancelled() async {
    let frames = self.frames
    let task = Task.detached { () -> Data? in
      while !Task.isCancelled { await Task.yield() }
      return APNGWriter.encode(frames: frames, frameDelay: 0.1)
    }
    task.cancel()
    #expect(await task.value == nil)
  }

  @Test("a cancelled mosaic build returns nothing")
  func mosaicStopsWhenCancelled() async {
    let target = TestImages.solid(RGBA(red: 10, green: 10, blue: 10), width: 64, height: 64)
    let tiles = frames
    let task = Task.detached { () -> CGImage? in
      while !Task.isCancelled { await Task.yield() }
      return Photomosaic.build(target: target, tiles: tiles, tileSize: 8, columns: 8)
    }
    task.cancel()
    #expect(await task.value == nil)
  }

  @Test("an uncancelled GIF encode still produces data")
  func gifWorksWithoutCancellation() async {
    let frames = self.frames
    let data = await Task.detached { ImageCodec.encodeAnimation(frames, as: .gif, frameDelay: 0.1) }
      .value
    #expect(data != nil)
  }
}
