import Foundation

/// BLAKE2b and BLAKE2s without a key, with a chosen output length (RFC 7693).
enum BLAKE2 {

  static let sigma: [[Int]] = [
    [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
    [14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3],
    [11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4],
    [7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8],
    [9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13],
    [2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9],
    [12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11],
    [13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10],
    [6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5],
    [10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0],
  ]

  static let ivB: [UInt64] = [
    0x6A09_E667_F3BC_C908, 0xBB67_AE85_84CA_A73B, 0x3C6E_F372_FE94_F82B,
    0xA54F_F53A_5F1D_36F1, 0x510E_527F_ADE6_82D1, 0x9B05_688C_2B3E_6C1F, 0x1F83_D9AB_FB41_BD6B,
    0x5BE0_CD19_137E_2179,
  ]

  static let ivS: [UInt32] = [
    0x6A09_E667, 0xBB67_AE85, 0x3C6E_F372, 0xA54F_F53A,
    0x510E_527F, 0x9B05_688C, 0x1F83_D9AB, 0x5BE0_CD19,
  ]

  static func blake2b(_ data: Data, outputBytes: Int) -> [UInt8] {
    var h = ivB
    h[0] ^= 0x0101_0000 ^ UInt64(outputBytes)
    let bytes = [UInt8](data)
    var offset = 0
    var counter: UInt64 = 0
    var block = [UInt8](repeating: 0, count: 128)

    if bytes.count <= 128 {
      // Single block, which is also the last one.
      for index in 0..<bytes.count { block[index] = bytes[index] }
      compressB(&h, block, counter: UInt64(bytes.count), last: true)
    } else {
      while offset + 128 < bytes.count {
        block = Array(bytes[offset..<offset + 128])
        counter += 128
        compressB(&h, block, counter: counter, last: false)
        offset += 128
      }
      let rest = bytes.count - offset
      block = [UInt8](repeating: 0, count: 128)
      for index in 0..<rest { block[index] = bytes[offset + index] }
      counter += UInt64(rest)
      compressB(&h, block, counter: counter, last: true)
    }

    var output: [UInt8] = []
    for word in h {
      for byte in 0..<8 { output.append(UInt8((word >> UInt64(8 * byte)) & 0xFF)) }
    }
    return Array(output.prefix(outputBytes))
  }

  static func blake2s(_ data: Data, outputBytes: Int) -> [UInt8] {
    var h = ivS
    h[0] ^= 0x0101_0000 ^ UInt32(outputBytes)
    let bytes = [UInt8](data)
    var offset = 0
    var counter: UInt64 = 0
    var block = [UInt8](repeating: 0, count: 64)

    if bytes.count <= 64 {
      for index in 0..<bytes.count { block[index] = bytes[index] }
      compressS(&h, block, counter: UInt64(bytes.count), last: true)
    } else {
      while offset + 64 < bytes.count {
        block = Array(bytes[offset..<offset + 64])
        counter += 64
        compressS(&h, block, counter: counter, last: false)
        offset += 64
      }
      let rest = bytes.count - offset
      block = [UInt8](repeating: 0, count: 64)
      for index in 0..<rest { block[index] = bytes[offset + index] }
      counter += UInt64(rest)
      compressS(&h, block, counter: counter, last: true)
    }

    var output: [UInt8] = []
    for word in h {
      for byte in 0..<4 { output.append(UInt8((word >> UInt32(8 * byte)) & 0xFF)) }
    }
    return Array(output.prefix(outputBytes))
  }

  private static func compressB(_ h: inout [UInt64], _ block: [UInt8], counter: UInt64, last: Bool)
  {
    var m = [UInt64](repeating: 0, count: 16)
    for index in 0..<16 {
      var word: UInt64 = 0
      for byte in (0..<8).reversed() { word = word << 8 | UInt64(block[index * 8 + byte]) }
      m[index] = word
    }
    var v = h + ivB
    v[12] ^= counter
    if last { v[14] = ~v[14] }
    for round in 0..<12 {
      let s = sigma[round % 10]
      gB(&v, 0, 4, 8, 12, m[s[0]], m[s[1]])
      gB(&v, 1, 5, 9, 13, m[s[2]], m[s[3]])
      gB(&v, 2, 6, 10, 14, m[s[4]], m[s[5]])
      gB(&v, 3, 7, 11, 15, m[s[6]], m[s[7]])
      gB(&v, 0, 5, 10, 15, m[s[8]], m[s[9]])
      gB(&v, 1, 6, 11, 12, m[s[10]], m[s[11]])
      gB(&v, 2, 7, 8, 13, m[s[12]], m[s[13]])
      gB(&v, 3, 4, 9, 14, m[s[14]], m[s[15]])
    }
    for index in 0..<8 { h[index] ^= v[index] ^ v[index + 8] }
  }

  private static func compressS(_ h: inout [UInt32], _ block: [UInt8], counter: UInt64, last: Bool)
  {
    var m = [UInt32](repeating: 0, count: 16)
    for index in 0..<16 {
      var word: UInt32 = 0
      for byte in (0..<4).reversed() { word = word << 8 | UInt32(block[index * 4 + byte]) }
      m[index] = word
    }
    var v = h + ivS
    v[12] ^= UInt32(truncatingIfNeeded: counter)
    v[13] ^= UInt32(truncatingIfNeeded: counter >> 32)
    if last { v[14] = ~v[14] }
    for round in 0..<10 {
      let s = sigma[round]
      gS(&v, 0, 4, 8, 12, m[s[0]], m[s[1]])
      gS(&v, 1, 5, 9, 13, m[s[2]], m[s[3]])
      gS(&v, 2, 6, 10, 14, m[s[4]], m[s[5]])
      gS(&v, 3, 7, 11, 15, m[s[6]], m[s[7]])
      gS(&v, 0, 5, 10, 15, m[s[8]], m[s[9]])
      gS(&v, 1, 6, 11, 12, m[s[10]], m[s[11]])
      gS(&v, 2, 7, 8, 13, m[s[12]], m[s[13]])
      gS(&v, 3, 4, 9, 14, m[s[14]], m[s[15]])
    }
    for index in 0..<8 { h[index] ^= v[index] ^ v[index + 8] }
  }

  private static func gB(
    _ v: inout [UInt64], _ a: Int, _ b: Int, _ c: Int, _ d: Int, _ x: UInt64, _ y: UInt64
  ) {
    v[a] = v[a] &+ v[b] &+ x
    v[d] = rotr(v[d] ^ v[a], 32)
    v[c] = v[c] &+ v[d]
    v[b] = rotr(v[b] ^ v[c], 24)
    v[a] = v[a] &+ v[b] &+ y
    v[d] = rotr(v[d] ^ v[a], 16)
    v[c] = v[c] &+ v[d]
    v[b] = rotr(v[b] ^ v[c], 63)
  }

  private static func gS(
    _ v: inout [UInt32], _ a: Int, _ b: Int, _ c: Int, _ d: Int, _ x: UInt32, _ y: UInt32
  ) {
    v[a] = v[a] &+ v[b] &+ x
    v[d] = rotr32(v[d] ^ v[a], 16)
    v[c] = v[c] &+ v[d]
    v[b] = rotr32(v[b] ^ v[c], 12)
    v[a] = v[a] &+ v[b] &+ y
    v[d] = rotr32(v[d] ^ v[a], 8)
    v[c] = v[c] &+ v[d]
    v[b] = rotr32(v[b] ^ v[c], 7)
  }

  private static func rotr(_ value: UInt64, _ count: UInt64) -> UInt64 {
    (value >> count) | (value << (64 - count))
  }

  private static func rotr32(_ value: UInt32, _ count: UInt32) -> UInt32 {
    (value >> count) | (value << (32 - count))
  }
}
