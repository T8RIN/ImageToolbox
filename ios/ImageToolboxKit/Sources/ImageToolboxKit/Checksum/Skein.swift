import Foundation

/// Skein-512-512 (Ferguson et al., a SHA-3 finalist) built on the Threefish-512 block cipher.
/// Structure follows BouncyCastle's SkeinEngine and ThreefishEngine: a configuration UBI, the
/// message UBI, then an output UBI over a zero counter.
enum Skein512 {

  /// Mix rotations per round position (row = round mod 8) and pair.
  private static let rotations: [[UInt64]] = [
    [46, 36, 19, 37], [33, 27, 14, 42], [17, 49, 36, 39], [44, 9, 54, 56],
    [39, 30, 34, 24], [13, 50, 10, 17], [25, 29, 39, 43], [8, 35, 56, 22],
  ]
  private static let permutation = [2, 1, 4, 7, 6, 5, 0, 3]
  private static let keyParity: UInt64 = 0x1BD1_1BDA_A9FC_1A22

  private static let configType: UInt64 = 4
  private static let messageType: UInt64 = 48
  private static let outputType: UInt64 = 63

  static func hash(_ data: Data) -> [UInt8] {
    // Configuration string: "SHA3", version 1, reserved, 512-bit output length, tree parameters.
    var config = [UInt8](repeating: 0, count: 32)
    config.replaceSubrange(0..<4, with: Array("SHA3".utf8))
    config[4] = 1
    config[9] = 0x02

    let configured = ubi(
      chain: [UInt64](repeating: 0, count: 8), message: config, type: configType)
    let state = ubi(chain: configured, message: [UInt8](data), type: messageType)
    let output = ubi(chain: state, message: [UInt8](repeating: 0, count: 8), type: outputType)
    var bytes: [UInt8] = []
    for word in output {
      for shift in stride(from: 0, to: 64, by: 8) {
        bytes.append(UInt8((word >> UInt64(shift)) & 0xFF))
      }
    }
    return bytes
  }

  /// Unique block iteration: each 64-byte block is encrypted under the chaining value with a
  /// tweak of (position, type, first, final), and fed forward with XOR.
  private static func ubi(chain: [UInt64], message: [UInt8], type: UInt64) -> [UInt64] {
    var h = chain
    let blockCount = max(1, (message.count + 63) / 64)
    for index in 0..<blockCount {
      let start = index * 64
      let end = min(start + 64, message.count)
      let block = words(message[start..<end])
      let first: UInt64 = index == 0 ? 1 << 62 : 0
      let final: UInt64 = index == blockCount - 1 ? 1 << 63 : 0
      let tweak = [UInt64(end), (type << 56) | first | final]
      let encrypted = threefish(block, key: h, tweak: tweak)
      h = zip(encrypted, block).map { $0 ^ $1 }
    }
    return h
  }

  /// Eight little-endian words from up to 64 bytes, zero-padded.
  private static func words(_ bytes: ArraySlice<UInt8>) -> [UInt64] {
    var padded = [UInt8](repeating: 0, count: 64)
    for (index, byte) in bytes.enumerated() { padded[index] = byte }
    var result = [UInt64](repeating: 0, count: 8)
    for index in 0..<64 {
      result[index / 8] |= UInt64(padded[index]) << UInt64(8 * (index % 8))
    }
    return result
  }

  /// Threefish-512 with 72 rounds and a subkey every four rounds.
  private static func threefish(_ block: [UInt64], key: [UInt64], tweak: [UInt64]) -> [UInt64] {
    let keyWords = key + [key.reduce(keyParity) { $0 ^ $1 }]
    let tweakWords = [tweak[0], tweak[1], tweak[0] ^ tweak[1]]

    var v = block
    addSubkey(&v, round: 0, key: keyWords, tweak: tweakWords)
    for round in 0..<72 {
      let rotation = rotations[round % 8]
      for pair in 0..<4 {
        v[2 * pair] &+= v[2 * pair + 1]
        v[2 * pair + 1] = rotl(v[2 * pair + 1], rotation[pair]) ^ v[2 * pair]
      }
      let previous = v
      for index in 0..<8 { v[index] = previous[permutation[index]] }
      if (round + 1) % 4 == 0 {
        addSubkey(&v, round: (round + 1) / 4, key: keyWords, tweak: tweakWords)
      }
    }
    return v
  }

  /// Subkey `s`: key words rotate through the nine-word key, with tweak words in positions 5 and
  /// 6 and the subkey index in position 7.
  private static func addSubkey(_ v: inout [UInt64], round s: Int, key: [UInt64], tweak: [UInt64]) {
    for index in 0..<8 {
      var word = key[(s + index) % 9]
      switch index {
      case 5: word &+= tweak[s % 3]
      case 6: word &+= tweak[(s + 1) % 3]
      case 7: word &+= UInt64(s)
      default: break
      }
      v[index] &+= word
    }
  }

  private static func rotl(_ value: UInt64, _ count: UInt64) -> UInt64 {
    (value << count) | (value >> (64 - count))
  }
}
