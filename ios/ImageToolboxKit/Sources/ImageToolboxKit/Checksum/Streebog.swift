import Foundation

/// GOST R 34.11-2012 ("Streebog") with 256-bit and 512-bit outputs. Byte layout follows
/// BouncyCastle's GOST3411_2012Digest: the state is little-endian bytes, while the length counter
/// and the checksum are big-endian 512-bit integers.
enum Streebog {

  private static let table = BouncyCastleTables.streebogT
  private static let constants = BouncyCastleTables.streebogC

  /// The 256-bit variant starts from the 0x01 IV and returns the last 32 bytes of the output.
  static func hash256(_ data: Data) -> [UInt8] {
    Array(digest(data, iv: [UInt8](repeating: 0x01, count: 64)).suffix(32))
  }

  static func hash512(_ data: Data) -> [UInt8] {
    digest(data, iv: [UInt8](repeating: 0, count: 64))
  }

  private static func digest(_ data: Data, iv: [UInt8]) -> [UInt8] {
    let zero = [UInt8](repeating: 0, count: 64)
    var h = iv
    var n = zero
    var sigma = zero

    let bytes = [UInt8](data)
    var offset = 0
    while bytes.count - offset >= 64 {
      let block = Array(bytes[offset..<offset + 64].reversed())
      g(&h, n, block)
      addCounter(&n, bits: 512)
      addChecksum(&sigma, block)
      offset += 64
    }

    // The tail is padded with a single 1 bit (0x01 byte) and zeros, then stored reversed.
    let tail = bytes[offset...]
    var padded = zero
    for (index, byte) in tail.enumerated() { padded[index] = byte }
    padded[tail.count] = 0x01
    let block = Array(padded.reversed())
    g(&h, n, block)
    addCounter(&n, bits: tail.count * 8)
    addChecksum(&sigma, block)

    g(&h, zero, n)
    g(&h, zero, sigma)
    return Array(h.reversed())
  }

  /// `h = g(h, n, m) = E(LPS(h ^ n), m) ^ h ^ m`.
  private static func g(_ h: inout [UInt8], _ n: [UInt8], _ m: [UInt8]) {
    let previous = h
    xor(&h, n)
    lps(&h)
    cipher(&h, m)
    xor(&h, previous)
    xor(&h, m)
  }

  /// `E(k, m)`: the key schedule runs on a copy of the key while `k` absorbs the message.
  private static func cipher(_ k: inout [UInt8], _ m: [UInt8]) {
    var ki = k
    xor(&k, m)
    lps(&k)
    for round in 0..<11 {
      xor(&ki, constant: round)
      lps(&ki)
      xor(&k, ki)
      lps(&k)
    }
    xor(&ki, constant: 11)
    lps(&ki)
    xor(&k, ki)
  }

  /// The LPS transform: S-box, linear diffusion and permutation folded into eight table lookups per
  /// output word.
  private static func lps(_ v: inout [UInt8]) {
    var words = [UInt64](repeating: 0, count: 8)
    for j in 0..<8 {
      var r: UInt64 = 0
      for i in 0..<8 {
        r ^= table[i * 256 + Int(v[8 * (7 - i) + j])]
      }
      words[j] = r
    }
    for j in 0..<8 {
      for k in 0..<8 {
        v[8 * j + k] = UInt8((words[j] >> UInt64(8 * k)) & 0xFF)
      }
    }
  }

  /// Adds `bits` to the 512-bit counter, where index 63 is the least significant byte.
  private static func addCounter(_ n: inout [UInt8], bits: Int) {
    var carry = Int(n[63]) + (bits & 0xFF)
    n[63] = UInt8(carry & 0xFF)
    carry = Int(n[62]) + ((bits >> 8) & 0xFF) + (carry >> 8)
    n[62] = UInt8(carry & 0xFF)
    var index = 61
    while index >= 0 && carry > 0 {
      carry = Int(n[index]) + (carry >> 8)
      n[index] = UInt8(carry & 0xFF)
      index -= 1
    }
  }

  /// Adds the block to the 512-bit checksum, modulo 2^512.
  private static func addChecksum(_ sigma: inout [UInt8], _ block: [UInt8]) {
    var carry = 0
    for index in stride(from: 63, through: 0, by: -1) {
      carry = Int(sigma[index]) + Int(block[index]) + (carry >> 8)
      sigma[index] = UInt8(carry & 0xFF)
    }
  }

  private static func xor(_ a: inout [UInt8], _ b: [UInt8]) {
    for index in a.indices { a[index] ^= b[index] }
  }

  private static func xor(_ a: inout [UInt8], constant round: Int) {
    for index in 0..<64 { a[index] ^= constants[round * 64 + index] }
  }
}
