import CoreGraphics
import Foundation

/// Writes an animated PNG (APNG) by re-packaging the zlib stream of each frame's PNG.
/// ImageIO cannot write APNG, so the chunk layout is built here.
public enum APNGWriter {

  private static let signature: [UInt8] = [137, 80, 78, 71, 13, 10, 26, 10]

  public static func encode(frames: [CGImage], frameDelay: Double, loopCount: Int = 0) -> Data? {
    guard let first = frames.first, let firstPNG = ImageCodec.encode(first, as: .png),
      let firstChunks = PNGChunks(firstPNG)
    else { return nil }

    var output = Data(signature)
    output.append(chunk("IHDR", firstChunks.header))
    output.append(chunk("acTL", bigEndian(UInt32(frames.count)) + bigEndian(UInt32(loopCount))))

    let delayNumerator = UInt16(min(65535, max(0, (frameDelay * 100).rounded())))
    var sequence: UInt32 = 0
    for (index, frame) in frames.enumerated() {
      // Checked per frame, so a long animation can be cancelled from the calling task.
      guard !Task.isCancelled else { return nil }
      let png: Data
      let chunks: PNGChunks
      if index == 0 {
        png = firstPNG
        chunks = firstChunks
      } else {
        guard let encoded = ImageCodec.encode(frame, as: .png), let parsed = PNGChunks(encoded),
          parsed.width == firstChunks.width, parsed.height == firstChunks.height
        else { return nil }
        png = encoded
        chunks = parsed
      }
      _ = png

      output.append(
        chunk(
          "fcTL",
          frameControl(
            sequence: sequence, width: chunks.width, height: chunks.height,
            delayNumerator: delayNumerator)))
      sequence += 1

      if index == 0 {
        output.append(chunk("IDAT", chunks.imageData))
      } else {
        output.append(chunk("fdAT", bigEndian(sequence) + chunks.imageData))
        sequence += 1
      }
    }

    output.append(chunk("IEND", Data()))
    return output
  }

  private static func frameControl(
    sequence: UInt32,
    width: UInt32,
    height: UInt32,
    delayNumerator: UInt16
  ) -> Data {
    var data = Data()
    data.append(bigEndian(sequence))
    data.append(bigEndian(width))
    data.append(bigEndian(height))
    data.append(bigEndian(UInt32(0)))
    data.append(bigEndian(UInt32(0)))
    data.append(bigEndian(delayNumerator))
    data.append(bigEndian(UInt16(100)))
    data.append(0)  // dispose_op: none
    data.append(0)  // blend_op: source
    return data
  }

  private static func chunk(_ type: String, _ payload: Data) -> Data {
    let typeBytes = Data(type.utf8)
    var output = bigEndian(UInt32(payload.count))
    output.append(typeBytes)
    output.append(payload)
    output.append(bigEndian(CRC32.checksum(typeBytes + payload)))
    return output
  }

  private static func bigEndian<T: FixedWidthInteger>(_ value: T) -> Data {
    withUnsafeBytes(of: value.bigEndian) { Data($0) }
  }

  /// IHDR payload, concatenated IDAT payload and dimensions of a PNG.
  private struct PNGChunks {
    let header: Data
    let imageData: Data
    let width: UInt32
    let height: UInt32

    init?(_ png: Data) {
      guard png.prefix(8).elementsEqual(APNGWriter.signature) else { return nil }
      var offset = 8
      var header: Data?
      var imageData = Data()
      while offset + 8 <= png.count {
        let length = png.subdata(in: offset..<offset + 4).withUnsafeBytes {
          $0.loadUnaligned(as: UInt32.self).bigEndian
        }
        let type = String(decoding: png.subdata(in: offset + 4..<offset + 8), as: UTF8.self)
        let start = offset + 8
        let end = start + Int(length)
        guard end + 4 <= png.count else { return nil }
        let payload = png.subdata(in: start..<end)
        if type == "IHDR" { header = payload }
        if type == "IDAT" { imageData.append(payload) }
        offset = end + 4
        if type == "IEND" { break }
      }
      guard let header, header.count >= 8 else { return nil }
      self.header = header
      self.imageData = imageData
      self.width = header.subdata(in: 0..<4).withUnsafeBytes {
        $0.loadUnaligned(as: UInt32.self).bigEndian
      }
      self.height = header.subdata(in: 4..<8).withUnsafeBytes {
        $0.loadUnaligned(as: UInt32.self).bigEndian
      }
    }
  }
}
