import Foundation

/// RIPEMD-160 (Dobbertin, Bosselaers, Preneel, 1996).
enum RIPEMD160 {

  static let kLeft: [UInt32] = [0x0000_0000, 0x5A82_7999, 0x6ED9_EBA1, 0x8F1B_BCDC, 0xA953_FD4E]
  static let kRight: [UInt32] = [0x50A2_8BE6, 0x5C4D_D124, 0x6D70_3EF3, 0x7A6D_76E9, 0x0000_0000]

  static let rLeft: [Int] = [
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
    7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
    3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
    1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
    4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13,
  ]
  static let rRight: [Int] = [
    5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
    6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
    15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
    8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
    12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11,
  ]
  static let sLeft: [UInt32] = [
    11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
    7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
    11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
    11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
    9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6,
  ]
  static let sRight: [UInt32] = [
    8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
    9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
    9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
    15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
    8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11,
  ]

  static func hash(_ data: Data) -> [UInt8] {
    var h: [UInt32] = [0x6745_2301, 0xEFCD_AB89, 0x98BA_DCFE, 0x1032_5476, 0xC3D2_E1F0]
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

      var al = h[0]
      var bl = h[1]
      var cl = h[2]
      var dl = h[3]
      var el = h[4]
      var ar = h[0]
      var br = h[1]
      var cr = h[2]
      var dr = h[3]
      var er = h[4]
      for j in 0..<80 {
        let round = j / 16
        var t = rotl(al &+ f(round, bl, cl, dl) &+ x[rLeft[j]] &+ kLeft[round], sLeft[j]) &+ el
        al = el
        el = dl
        dl = rotl(cl, 10)
        cl = bl
        bl = t
        t = rotl(ar &+ f(4 - round, br, cr, dr) &+ x[rRight[j]] &+ kRight[round], sRight[j]) &+ er
        ar = er
        er = dr
        dr = rotl(cr, 10)
        cr = br
        br = t
      }
      let t = h[1] &+ cl &+ dr
      h[1] = h[2] &+ dl &+ er
      h[2] = h[3] &+ el &+ ar
      h[3] = h[4] &+ al &+ br
      h[4] = h[0] &+ bl &+ cr
      h[0] = t
    }

    var output: [UInt8] = []
    for word in h {
      for shift in stride(from: 0, to: 32, by: 8) {
        output.append(UInt8((word >> UInt32(shift)) & 0xFF))
      }
    }
    return output
  }

  private static func f(_ round: Int, _ x: UInt32, _ y: UInt32, _ z: UInt32) -> UInt32 {
    switch round {
    case 0: x ^ y ^ z
    case 1: (x & y) | (~x & z)
    case 2: (x | ~y) ^ z
    case 3: (x & z) | (y & ~z)
    default: x ^ (y | ~z)
    }
  }

  private static func rotl(_ value: UInt32, _ count: UInt32) -> UInt32 {
    (value << count) | (value >> (32 - count))
  }
}
