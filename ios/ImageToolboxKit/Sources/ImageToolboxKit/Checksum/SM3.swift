import Foundation

/// SM3 (GB/T 32905-2016), the Chinese standard hash. Structure follows BouncyCastle's SM3Digest.
enum SM3 {

  static func hash(_ data: Data) -> [UInt8] {
    var v: [UInt32] = [
      0x7380_166F, 0x4914_B2B9, 0x1724_42D7, 0xDA8A_0600,
      0xA96F_30BC, 0x1631_38AA, 0xE38D_EE4D, 0xB0FB_0E4E,
    ]

    var message = [UInt8](data)
    let bitLength = UInt64(message.count) &* 8
    message.append(0x80)
    while message.count % 64 != 56 { message.append(0) }
    for shift in stride(from: 56, through: 0, by: -8) {
      message.append(UInt8((bitLength >> UInt64(shift)) & 0xFF))
    }

    for block in stride(from: 0, to: message.count, by: 64) {
      v = compress(v, Array(message[block..<block + 64]))
    }

    var output: [UInt8] = []
    for word in v {
      for shift in stride(from: 24, through: 0, by: -8) {
        output.append(UInt8((word >> UInt32(shift)) & 0xFF))
      }
    }
    return output
  }

  private static func compress(_ state: [UInt32], _ block: [UInt8]) -> [UInt32] {
    var w = [UInt32](repeating: 0, count: 68)
    for index in 0..<16 {
      let base = index * 4
      w[index] =
        UInt32(block[base]) << 24 | UInt32(block[base + 1]) << 16
        | UInt32(block[base + 2]) << 8 | UInt32(block[base + 3])
    }
    for index in 16..<68 {
      let x = w[index - 16] ^ w[index - 9] ^ rotl(w[index - 3], 15)
      w[index] = p1(x) ^ rotl(w[index - 13], 7) ^ w[index - 6]
    }

    var a = state[0]
    var b = state[1]
    var c = state[2]
    var d = state[3]
    var e = state[4]
    var f = state[5]
    var g = state[6]
    var h = state[7]
    for j in 0..<64 {
      let t: UInt32 = j < 16 ? rotl(0x79CC_4519, UInt32(j)) : rotl(0x7A87_9D8A, UInt32(j % 32))
      let a12 = rotl(a, 12)
      let ss1 = rotl(a12 &+ e &+ t, 7)
      let ss2 = ss1 ^ a12
      let tt1 = (j < 16 ? a ^ b ^ c : (a & b) | (a & c) | (b & c)) &+ d &+ ss2 &+ (w[j] ^ w[j + 4])
      let tt2 = (j < 16 ? e ^ f ^ g : (e & f) | (~e & g)) &+ h &+ ss1 &+ w[j]
      d = c
      c = rotl(b, 9)
      b = a
      a = tt1
      h = g
      g = rotl(f, 19)
      f = e
      e = p0(tt2)
    }
    return [
      state[0] ^ a, state[1] ^ b, state[2] ^ c, state[3] ^ d,
      state[4] ^ e, state[5] ^ f, state[6] ^ g, state[7] ^ h,
    ]
  }

  private static func p0(_ x: UInt32) -> UInt32 { x ^ rotl(x, 9) ^ rotl(x, 17) }
  private static func p1(_ x: UInt32) -> UInt32 { x ^ rotl(x, 15) ^ rotl(x, 23) }

  private static func rotl(_ value: UInt32, _ count: UInt32) -> UInt32 {
    (value << count) | (value >> (32 - count))
  }
}
