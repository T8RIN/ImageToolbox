import Foundation

/// Tiger-192/3 (Anderson and Biham, 1996) with the original 0x01 padding. Structure follows
/// BouncyCastle's TigerDigest; the S-boxes come from `BouncyCastleTables`.
enum Tiger {

  private static let t1 = BouncyCastleTables.tigerT1
  private static let t2 = BouncyCastleTables.tigerT2
  private static let t3 = BouncyCastleTables.tigerT3
  private static let t4 = BouncyCastleTables.tigerT4

  static func hash(_ data: Data) -> [UInt8] {
    var state: [UInt64] = [0x0123_4567_89AB_CDEF, 0xFEDC_BA98_7654_3210, 0xF096_A5B4_C3B2_E187]

    var message = [UInt8](data)
    let bitLength = UInt64(message.count) &* 8
    message.append(0x01)
    while message.count % 64 != 56 { message.append(0) }
    for shift in stride(from: 0, to: 64, by: 8) {
      message.append(UInt8((bitLength >> UInt64(shift)) & 0xFF))
    }

    for block in stride(from: 0, to: message.count, by: 64) {
      state = compress(state, message[block..<block + 64])
    }

    var output: [UInt8] = []
    for word in state {
      for shift in stride(from: 0, to: 64, by: 8) {
        output.append(UInt8((word >> UInt64(shift)) & 0xFF))
      }
    }
    return output
  }

  private static func compress(_ state: [UInt64], _ block: ArraySlice<UInt8>) -> [UInt64] {
    let start = block.startIndex
    var x = (0..<8).map { word -> UInt64 in
      var value: UInt64 = 0
      for byte in 0..<8 {
        value |= UInt64(block[start + word * 8 + byte]) << UInt64(byte * 8)
      }
      return value
    }

    var a = state[0]
    var b = state[1]
    var c = state[2]
    let (savedA, savedB, savedC) = (a, b, c)

    pass(x, startRole: 0, multiplier: 5, &a, &b, &c)
    keySchedule(&x)
    pass(x, startRole: 2, multiplier: 7, &a, &b, &c)
    keySchedule(&x)
    pass(x, startRole: 1, multiplier: 9, &a, &b, &c)

    return [a ^ savedA, b &- savedB, c &+ savedC]
  }

  /// Eight rounds rotating through the roles (a, b, c) -> (b, c, a) -> (c, a, b). The role of the
  /// first round is `startRole`: 0 is ABC, 1 is BCA, 2 is CAB.
  private static func pass(
    _ x: [UInt64], startRole: Int, multiplier: UInt64,
    _ a: inout UInt64, _ b: inout UInt64, _ c: inout UInt64
  ) {
    var role = startRole
    for word in x {
      switch role % 3 {
      case 0: round(word, multiplier, &a, &b, &c)
      case 1: round(word, multiplier, &b, &c, &a)
      default: round(word, multiplier, &c, &a, &b)
      }
      role += 1
    }
  }

  /// `r` takes the message word, then `p` and `q` are updated from the S-boxes of `r`.
  private static func round(
    _ word: UInt64, _ multiplier: UInt64, _ p: inout UInt64, _ q: inout UInt64, _ r: inout UInt64
  ) {
    r ^= word
    p = p &- sboxF(r)
    q = (q &+ sboxG(r)) &* multiplier
  }

  private static func sboxF(_ value: UInt64) -> UInt64 {
    t1[Int(value & 0xFF)] ^ t2[Int((value >> 16) & 0xFF)] ^ t3[Int((value >> 32) & 0xFF)]
      ^ t4[Int((value >> 48) & 0xFF)]
  }

  private static func sboxG(_ value: UInt64) -> UInt64 {
    t4[Int((value >> 8) & 0xFF)] ^ t3[Int((value >> 24) & 0xFF)] ^ t2[Int((value >> 40) & 0xFF)]
      ^ t1[Int((value >> 56) & 0xFF)]
  }

  private static func keySchedule(_ x: inout [UInt64]) {
    x[0] &-= x[7] ^ 0xA5A5_A5A5_A5A5_A5A5
    x[1] ^= x[0]
    x[2] &+= x[1]
    x[3] &-= x[2] ^ ((~x[1]) << 19)
    x[4] ^= x[3]
    x[5] &+= x[4]
    x[6] &-= x[5] ^ ((~x[4]) >> 23)
    x[7] ^= x[6]
    x[0] &+= x[7]
    x[1] &-= x[0] ^ ((~x[7]) << 19)
    x[2] ^= x[1]
    x[3] &+= x[2]
    x[4] &-= x[3] ^ ((~x[2]) >> 23)
    x[5] ^= x[4]
    x[6] &+= x[5]
    x[7] &-= x[6] ^ 0x0123_4567_89AB_CDEF
  }
}
