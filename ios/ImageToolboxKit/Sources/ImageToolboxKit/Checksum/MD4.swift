import Foundation

/// MD4 (RFC 1320). Obsolete, but still listed by many hashing tools.
enum MD4 {

  static func hash(_ data: Data) -> [UInt8] {
    var state: [UInt32] = [0x6745_2301, 0xEFCD_AB89, 0x98BA_DCFE, 0x1032_5476]
    var message = [UInt8](data)
    let bitLength = UInt64(message.count) &* 8
    message.append(0x80)
    while message.count % 64 != 56 { message.append(0) }
    for shift in stride(from: 0, to: 64, by: 8) {
      message.append(UInt8((bitLength >> UInt64(shift)) & 0xFF))
    }

    for block in stride(from: 0, to: message.count, by: 64) {
      var x = [UInt32](repeating: 0, count: 16)
      for index in 0..<16 {
        let base = block + index * 4
        x[index] =
          UInt32(message[base]) | UInt32(message[base + 1]) << 8
          | UInt32(message[base + 2]) << 16 | UInt32(message[base + 3]) << 24
      }

      var a = state[0]
      var b = state[1]
      var c = state[2]
      var d = state[3]
      let r1: [UInt32] = [3, 7, 11, 19]
      for i in 0..<16 {
        let t = rotl(a &+ ((b & c) | (~b & d)) &+ x[i], r1[i % 4])
        a = d
        d = c
        c = b
        b = t
      }
      let r2: [UInt32] = [3, 5, 9, 13]
      let order2 = [0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15]
      for i in 0..<16 {
        let t = rotl(a &+ ((b & c) | (b & d) | (c & d)) &+ x[order2[i]] &+ 0x5A82_7999, r2[i % 4])
        a = d
        d = c
        c = b
        b = t
      }
      let r3: [UInt32] = [3, 9, 11, 15]
      let order3 = [0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15]
      for i in 0..<16 {
        let t = rotl(a &+ (b ^ c ^ d) &+ x[order3[i]] &+ 0x6ED9_EBA1, r3[i % 4])
        a = d
        d = c
        c = b
        b = t
      }

      state[0] = state[0] &+ a
      state[1] = state[1] &+ b
      state[2] = state[2] &+ c
      state[3] = state[3] &+ d
    }

    var output: [UInt8] = []
    for word in state {
      for shift in stride(from: 0, to: 32, by: 8) {
        output.append(UInt8((word >> UInt32(shift)) & 0xFF))
      }
    }
    return output
  }

  private static func rotl(_ value: UInt32, _ count: UInt32) -> UInt32 {
    (value << count) | (value >> (32 - count))
  }
}
