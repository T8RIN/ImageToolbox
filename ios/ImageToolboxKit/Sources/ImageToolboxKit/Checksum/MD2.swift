import Foundation

/// MD2 (RFC 1319). The substitution table is the one used by BouncyCastle.
enum MD2 {

  static func hash(_ data: Data) -> [UInt8] {
    let s = BouncyCastleTables.md2S
    var state = [UInt8](repeating: 0, count: 48)
    var checksum = [UInt8](repeating: 0, count: 16)

    // Padding: append n bytes of value n, where 1 <= n <= 16, so the length becomes a multiple of 16.
    var message = [UInt8](data)
    let padding = 16 - message.count % 16
    message += [UInt8](repeating: UInt8(padding), count: padding)

    func absorb(_ block: [UInt8]) {
      var last = checksum[15]
      for index in 0..<16 {
        checksum[index] ^= s[Int(block[index] ^ last)]
        last = checksum[index]
      }
      compress(block)
    }

    func compress(_ block: [UInt8]) {
      for index in 0..<16 {
        state[16 + index] = block[index]
        state[32 + index] = block[index] ^ state[index]
      }
      var t: UInt8 = 0
      for round in 0..<18 {
        for index in 0..<48 {
          state[index] ^= s[Int(t)]
          t = state[index]
        }
        t = t &+ UInt8(round)
      }
    }

    for block in stride(from: 0, to: message.count, by: 16) {
      absorb(Array(message[block..<block + 16]))
    }
    absorb(checksum)
    return Array(state[0..<16])
  }
}
