import Foundation

/// Keccak-f[1600] sponge. SHA-3 and SHAKE differ only in the rate, the output length and the
/// domain-separation byte; original Keccak uses 0x01 instead of 0x06.
enum Keccak {

  static let roundConstants: [UInt64] = [
    0x0000_0000_0000_0001, 0x0000_0000_0000_8082, 0x8000_0000_0000_808A, 0x8000_0000_8000_8000,
    0x0000_0000_0000_808B, 0x0000_0000_8000_0001, 0x8000_0000_8000_8081, 0x8000_0000_0000_8009,
    0x0000_0000_0000_008A, 0x0000_0000_0000_0088, 0x0000_0000_8000_8009, 0x0000_0000_8000_000A,
    0x0000_0000_8000_808B, 0x8000_0000_0000_008B, 0x8000_0000_0000_8089, 0x8000_0000_0000_8003,
    0x8000_0000_0000_8002, 0x8000_0000_0000_0080, 0x0000_0000_0000_800A, 0x8000_0000_8000_000A,
    0x8000_0000_8000_8081, 0x8000_0000_0000_8080, 0x0000_0000_8000_0001, 0x8000_0000_8000_8008,
  ]

  /// Rotation offsets in lane order, index = x + 5 * y.
  static let rotations: [UInt64] = [
    0, 1, 62, 28, 27, 36, 44, 6, 55, 20, 3, 10, 43, 25, 39, 41, 45, 15, 21, 8, 18, 2, 61, 56, 14,
  ]

  /// `rate` is in bytes: 144 (SHA3-224), 136 (SHA3-256 and SHAKE256), 104 (SHA3-384), 72 (SHA3-512),
  /// 168 (SHAKE128).
  static func hash(_ data: Data, rate: Int, outputBytes: Int, domain: UInt8) -> [UInt8] {
    var state = [UInt64](repeating: 0, count: 25)
    var message = [UInt8](data)
    message.append(domain)
    while message.count % rate != 0 { message.append(0) }
    message[message.count - 1] |= 0x80

    for block in stride(from: 0, to: message.count, by: rate) {
      for lane in 0..<(rate / 8) {
        var word: UInt64 = 0
        for byte in (0..<8).reversed() {
          word = word << 8 | UInt64(message[block + lane * 8 + byte])
        }
        state[lane] ^= word
      }
      permute(&state)
    }

    var output: [UInt8] = []
    while output.count < outputBytes {
      for lane in 0..<(rate / 8) {
        for byte in 0..<8 where output.count < outputBytes {
          output.append(UInt8((state[lane] >> UInt64(8 * byte)) & 0xFF))
        }
      }
      if output.count < outputBytes { permute(&state) }
    }
    return output
  }

  static func permute(_ a: inout [UInt64]) {
    for round in 0..<24 {
      var c = [UInt64](repeating: 0, count: 5)
      for x in 0..<5 {
        c[x] = a[x] ^ a[x + 5] ^ a[x + 10] ^ a[x + 15] ^ a[x + 20]
      }
      for x in 0..<5 {
        let d = c[(x + 4) % 5] ^ rotl(c[(x + 1) % 5], 1)
        for y in 0..<5 {
          a[x + 5 * y] ^= d
        }
      }

      var b = [UInt64](repeating: 0, count: 25)
      for x in 0..<5 {
        for y in 0..<5 {
          let lane = x + 5 * y
          let target = y + 5 * ((2 * x + 3 * y) % 5)
          b[target] = rotl(a[lane], rotations[lane])
        }
      }

      for x in 0..<5 {
        for y in 0..<5 {
          a[x + 5 * y] = b[x + 5 * y] ^ (~b[(x + 1) % 5 + 5 * y] & b[(x + 2) % 5 + 5 * y])
        }
      }

      a[0] ^= roundConstants[round]
    }
  }

  private static func rotl(_ value: UInt64, _ count: UInt64) -> UInt64 {
    count == 0 ? value : (value << count) | (value >> (64 - count))
  }
}
