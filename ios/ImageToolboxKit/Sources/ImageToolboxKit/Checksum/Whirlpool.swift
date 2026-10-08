import Foundation

/// Whirlpool (ISO/IEC 10118-3, 512-bit output), a Miyaguchi-Preneel construction over a 10-round
/// block cipher. The S-box comes from BouncyCastle's tables.
enum Whirlpool {

  private static let circulant: [UInt8] = [1, 1, 4, 1, 8, 5, 2, 9]
  private static let roundCount = 10

  static func hash(_ data: Data) -> [UInt8] {
    var hash = [UInt8](repeating: 0, count: 64)
    var message = [UInt8](data)
    let bitLength = UInt64(message.count) &* 8
    message.append(0x80)
    while message.count % 64 != 32 { message.append(0) }
    // 256-bit big-endian length: 24 zero bytes, then the 64-bit count.
    message += [UInt8](repeating: 0, count: 24)
    for shift in stride(from: 56, through: 0, by: -8) {
      message.append(UInt8((bitLength >> UInt64(shift)) & 0xFF))
    }

    for block in stride(from: 0, to: message.count, by: 64) {
      let m = Array(message[block..<block + 64])
      let encrypted = cipher(key: hash, block: m)
      for index in 0..<64 {
        hash[index] ^= encrypted[index] ^ m[index]
      }
    }
    return hash
  }

  /// W(K, M): the block cipher used by the Miyaguchi-Preneel mode.
  private static func cipher(key: [UInt8], block: [UInt8]) -> [UInt8] {
    var k = key
    var state = zip(block, key).map { $0 ^ $1 }
    for round in 1...roundCount {
      k = rho(k)
      for column in 0..<8 {
        k[column] ^= BouncyCastleTables.whirlpoolS[8 * (round - 1) + column]
      }
      state = rho(state)
      for index in 0..<64 {
        state[index] ^= k[index]
      }
    }
    return state
  }

  /// One round transform: S-box, column shift, then the row mix.
  private static func rho(_ input: [UInt8]) -> [UInt8] {
    let sboxed = input.map { BouncyCastleTables.whirlpoolS[Int($0)] }
    var shifted = [UInt8](repeating: 0, count: 64)
    for row in 0..<8 {
      for column in 0..<8 {
        shifted[row * 8 + column] = sboxed[((row - column + 8) % 8) * 8 + column]
      }
    }
    var mixed = [UInt8](repeating: 0, count: 64)
    for row in 0..<8 {
      for column in 0..<8 {
        var value: UInt8 = 0
        for k in 0..<8 {
          value ^= gfMultiply(shifted[row * 8 + k], circulant[(column - k + 8) % 8])
        }
        mixed[row * 8 + column] = value
      }
    }
    return mixed
  }

  /// Multiplication in GF(2^8) modulo x^8 + x^4 + x^3 + x^2 + 1 (0x11D).
  private static func gfMultiply(_ a: UInt8, _ b: UInt8) -> UInt8 {
    var product: UInt16 = 0
    var left = UInt16(a)
    var right = b
    while right != 0 {
      if right & 1 != 0 { product ^= left }
      left <<= 1
      if left & 0x100 != 0 { left ^= 0x11D }
      right >>= 1
    }
    return UInt8(product & 0xFF)
  }
}
