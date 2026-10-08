import Foundation

/// GOST R 34.11-94 with the CryptoPro-A S-box (BouncyCastle's "D-A", its default). The compression
/// function is built on the GOST 28147-89 block cipher, which is implemented below in encryption
/// mode only. Structure follows BouncyCastle's GOST3411Digest.
enum GOST341194 {

  private static let sbox = BouncyCastleTables.gostSboxA

  /// C2 from the standard. C1 and C3 are zero.
  private static let c2: [UInt8] = [
    0x00, 0xFF, 0x00, 0xFF, 0x00, 0xFF, 0x00, 0xFF,
    0xFF, 0x00, 0xFF, 0x00, 0xFF, 0x00, 0xFF, 0x00,
    0x00, 0xFF, 0xFF, 0x00, 0xFF, 0x00, 0x00, 0xFF,
    0xFF, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0x00, 0xFF,
  ]

  static func hash(_ data: Data) -> [UInt8] {
    var h = [UInt8](repeating: 0, count: 32)
    var sum = [UInt8](repeating: 0, count: 32)

    let bytes = [UInt8](data)
    var offset = 0
    while bytes.count - offset >= 32 {
      let block = Array(bytes[offset..<offset + 32])
      addSum(&sum, block)
      processBlock(&h, block)
      offset += 32
    }

    // Zero-pad the tail to a full block. An empty tail adds no block.
    let tail = bytes[offset...]
    if !tail.isEmpty {
      var block = [UInt8](repeating: 0, count: 32)
      for (index, byte) in tail.enumerated() { block[index] = byte }
      addSum(&sum, block)
      processBlock(&h, block)
    }

    var lengthBlock = [UInt8](repeating: 0, count: 32)
    let bitLength = UInt64(bytes.count) &* 8
    for shift in 0..<8 {
      lengthBlock[shift] = UInt8((bitLength >> UInt64(shift * 8)) & 0xFF)
    }
    processBlock(&h, lengthBlock)
    processBlock(&h, sum)
    return h
  }

  /// `h = g(h, m)`: four GOST 28147-89 encryptions keyed from the mixed `h ^ m`, then 61 rounds of
  /// the LFSR `fw` around the message and chaining value.
  private static func processBlock(_ h: inout [UInt8], _ m: [UInt8]) {
    var u = h
    var v = m
    var w = zip(u, v).map { $0 ^ $1 }
    var s = [UInt8](repeating: 0, count: 32)

    let first = encrypt(permute(w), block: Array(h[0..<8]))
    s.replaceSubrange(0..<8, with: first)

    for index in 1...3 {
      u = transform(u)
      xor(&u, constant: index)
      v = transform(transform(v))
      w = zip(u, v).map { $0 ^ $1 }
      let block = encrypt(permute(w), block: Array(h[index * 8..<index * 8 + 8]))
      s.replaceSubrange(index * 8..<index * 8 + 8, with: block)
    }

    for _ in 0..<12 { feedback(&s) }
    xor(&s, m)
    feedback(&s)
    xor(&s, h)
    for _ in 0..<61 { feedback(&s) }
    h = s
  }

  private static func addSum(_ sum: inout [UInt8], _ block: [UInt8]) {
    var carry = 0
    for index in 0..<32 {
      carry += Int(sum[index]) + Int(block[index])
      sum[index] = UInt8(carry & 0xFF)
      carry >>= 8
    }
  }

  /// Key permutation P: bytes of the 32-byte value are interleaved into the 8-byte key groups.
  private static func permute(_ w: [UInt8]) -> [UInt8] {
    var key = [UInt8](repeating: 0, count: 32)
    for k in 0..<8 {
      key[4 * k] = w[k]
      key[4 * k + 1] = w[8 + k]
      key[4 * k + 2] = w[16 + k]
      key[4 * k + 3] = w[24 + k]
    }
    return key
  }

  /// Transformation A: the two 8-byte halves of each 16-byte pair are XORed into the last quarter.
  private static func transform(_ x: [UInt8]) -> [UInt8] {
    var out = [UInt8](repeating: 0, count: 32)
    for j in 0..<8 { out[24 + j] = x[j] ^ x[j + 8] }
    for j in 0..<24 { out[j] = x[j + 8] }
    return out
  }

  /// The 16-bit LFSR of the standard, applied to 16 little-endian words.
  private static func feedback(_ s: inout [UInt8]) {
    var words = (0..<16).map { UInt16(s[2 * $0]) | UInt16(s[2 * $0 + 1]) << 8 }
    let next = words[0] ^ words[1] ^ words[2] ^ words[3] ^ words[12] ^ words[15]
    words.removeFirst()
    words.append(next)
    for index in 0..<16 {
      s[2 * index] = UInt8(words[index] & 0xFF)
      s[2 * index + 1] = UInt8(words[index] >> 8)
    }
  }

  private static func xor(_ a: inout [UInt8], _ b: [UInt8]) {
    for index in a.indices { a[index] ^= b[index] }
  }

  private static func xor(_ a: inout [UInt8], constant index: Int) {
    guard index == 2 else { return }
    xor(&a, c2)
  }

  // MARK: - GOST 28147-89 (encryption only)

  /// One 8-byte ECB block with a 32-byte key, as BouncyCastle's GOST28147Engine does it.
  private static func encrypt(_ key: [UInt8], block: [UInt8]) -> [UInt8] {
    let words = (0..<8).map { littleEndian32(key, at: 4 * $0) }
    var n1 = littleEndian32(block, at: 0)
    var n2 = littleEndian32(block, at: 4)

    for _ in 0..<3 {
      for index in 0..<8 {
        (n1, n2) = (n2 ^ step(n1, words[index]), n1)
      }
    }
    for index in stride(from: 7, to: 0, by: -1) {
      (n1, n2) = (n2 ^ step(n1, words[index]), n1)
    }
    n2 ^= step(n1, words[0])

    return bytes(n1) + bytes(n2)
  }

  /// The round function: add the subkey, substitute eight nibbles, rotate left by 11.
  private static func step(_ n: UInt32, _ key: UInt32) -> UInt32 {
    let sum = key &+ n
    var substituted: UInt32 = 0
    for index in 0..<8 {
      let nibble = Int((sum >> UInt32(4 * index)) & 0xF)
      substituted |= UInt32(sbox[16 * index + nibble]) << UInt32(4 * index)
    }
    return (substituted << 11) | (substituted >> 21)
  }

  private static func littleEndian32(_ bytes: [UInt8], at offset: Int) -> UInt32 {
    UInt32(bytes[offset]) | UInt32(bytes[offset + 1]) << 8 | UInt32(bytes[offset + 2]) << 16
      | UInt32(bytes[offset + 3]) << 24
  }

  private static func bytes(_ value: UInt32) -> [UInt8] {
    (0..<4).map { UInt8((value >> UInt32(8 * $0)) & 0xFF) }
  }
}
