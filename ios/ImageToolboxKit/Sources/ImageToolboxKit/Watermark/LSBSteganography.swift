import CoreGraphics
import Foundation

/// Hides UTF-8 text in the lowest bit of the red, green and blue channels.
/// The payload is a 32-bit big-endian byte count followed by the bytes. Alpha is untouched.
public enum LSBSteganography {

  public static func capacity(of image: CGImage) -> Int {
    (image.width * image.height * 3) / 8 - 4
  }

  public static func embed(_ text: String, in image: CGImage) -> CGImage? {
    let payload = Array(text.utf8)
    guard payload.count <= capacity(of: image), var raster = RasterImage(image) else { return nil }

    var bits: [UInt8] = []
    bits.reserveCapacity((payload.count + 4) * 8)
    let length = UInt32(payload.count)
    for shift in stride(from: 24, through: 0, by: -8) {
      bits.append(contentsOf: bitsOf(UInt8((length >> UInt32(shift)) & 0xFF)))
    }
    for byte in payload {
      bits.append(contentsOf: bitsOf(byte))
    }

    var pixels = raster.pixels
    var bitIndex = 0
    var index = 0
    while bitIndex < bits.count && index < pixels.count {
      if index % 4 != 3 {
        pixels[index] = (pixels[index] & 0xFE) | bits[bitIndex]
        bitIndex += 1
      }
      index += 1
    }
    raster = RasterImage(width: raster.width, height: raster.height, pixels: pixels)
    return raster.toCGImage()
  }

  public static func extract(from image: CGImage) -> String? {
    guard let raster = RasterImage(image) else { return nil }
    var reader = BitReader(pixels: raster.pixels)

    let length =
      Int(reader.readByte()) << 24 | Int(reader.readByte()) << 16
      | Int(reader.readByte()) << 8 | Int(reader.readByte())
    guard length <= capacity(of: image) else { return nil }

    var bytes: [UInt8] = []
    bytes.reserveCapacity(length)
    for _ in 0..<length {
      bytes.append(reader.readByte())
    }
    return String(bytes: bytes, encoding: .utf8)
  }

  private static func bitsOf(_ byte: UInt8) -> [UInt8] {
    (0..<8).map { (byte >> UInt8(7 - $0)) & 1 }
  }

  private struct BitReader {
    let pixels: [UInt8]
    var index = 0

    mutating func readBit() -> UInt8 {
      while index % 4 == 3 { index += 1 }
      let bit = pixels[index] & 1
      index += 1
      return bit
    }

    mutating func readByte() -> UInt8 {
      var value: UInt8 = 0
      for _ in 0..<8 {
        value = (value << 1) | readBit()
      }
      return value
    }
  }
}
