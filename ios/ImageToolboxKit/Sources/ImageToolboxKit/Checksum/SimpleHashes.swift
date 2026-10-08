import Foundation

/// Non-cryptographic hashes: checksums, FNV, MurmurHash3, xxHash and classic string hashes.
enum SimpleHashes {

  static func fletcher16(_ data: Data) -> UInt16 {
    var sum1: UInt16 = 0
    var sum2: UInt16 = 0
    for byte in data {
      sum1 = (sum1 + UInt16(byte)) % 255
      sum2 = (sum2 + sum1) % 255
    }
    return sum2 << 8 | sum1
  }

  static func fletcher32(_ data: Data) -> UInt32 {
    var sum1: UInt32 = 0
    var sum2: UInt32 = 0
    let bytes = [UInt8](data)
    var index = 0
    while index < bytes.count {
      let low = UInt32(bytes[index])
      let high = index + 1 < bytes.count ? UInt32(bytes[index + 1]) : 0
      let word = low | high << 8
      sum1 = (sum1 + word) % 65535
      sum2 = (sum2 + sum1) % 65535
      index += 2
    }
    return sum2 << 16 | sum1
  }

  static func fnv132(_ data: Data) -> UInt32 {
    var hash: UInt32 = 2_166_136_261
    for byte in data {
      hash = hash &* 16_777_619
      hash ^= UInt32(byte)
    }
    return hash
  }

  static func fnv1a32(_ data: Data) -> UInt32 {
    var hash: UInt32 = 2_166_136_261
    for byte in data {
      hash ^= UInt32(byte)
      hash = hash &* 16_777_619
    }
    return hash
  }

  static func fnv164(_ data: Data) -> UInt64 {
    var hash: UInt64 = 14_695_981_039_346_656_037
    for byte in data {
      hash = hash &* 1_099_511_628_211
      hash ^= UInt64(byte)
    }
    return hash
  }

  static func fnv1a64(_ data: Data) -> UInt64 {
    var hash: UInt64 = 14_695_981_039_346_656_037
    for byte in data {
      hash ^= UInt64(byte)
      hash = hash &* 1_099_511_628_211
    }
    return hash
  }

  /// MurmurHash3, x86 32-bit variant, seed 0.
  static func murmur332(_ data: Data) -> UInt32 {
    let c1: UInt32 = 0xCC9E_2D51
    let c2: UInt32 = 0x1B87_3593
    let bytes = [UInt8](data)
    var h: UInt32 = 0
    let blocks = bytes.count / 4
    for block in 0..<blocks {
      let base = block * 4
      var k =
        UInt32(bytes[base]) | UInt32(bytes[base + 1]) << 8
        | UInt32(bytes[base + 2]) << 16 | UInt32(bytes[base + 3]) << 24
      k = k &* c1
      k = rotl32(k, 15)
      k = k &* c2
      h ^= k
      h = rotl32(h, 13)
      h = h &* 5 &+ 0xE654_6B64
    }

    var tail: UInt32 = 0
    let remaining = bytes.count & 3
    if remaining >= 3 { tail ^= UInt32(bytes[blocks * 4 + 2]) << 16 }
    if remaining >= 2 { tail ^= UInt32(bytes[blocks * 4 + 1]) << 8 }
    if remaining >= 1 {
      tail ^= UInt32(bytes[blocks * 4])
      tail = tail &* c1
      tail = rotl32(tail, 15)
      tail = tail &* c2
      h ^= tail
    }

    h ^= UInt32(bytes.count)
    h ^= h >> 16
    h = h &* 0x85EB_CA6B
    h ^= h >> 13
    h = h &* 0xC2B2_AE35
    h ^= h >> 16
    return h
  }

  static func xxHash32(_ data: Data) -> UInt32 {
    let p1: UInt32 = 2_654_435_761
    let p2: UInt32 = 2_246_822_519
    let p3: UInt32 = 3_266_489_917
    let p4: UInt32 = 668_265_263
    let p5: UInt32 = 374_761_393
    let bytes = [UInt8](data)
    var index = 0
    var h: UInt32

    func read32(_ at: Int) -> UInt32 {
      UInt32(bytes[at]) | UInt32(bytes[at + 1]) << 8 | UInt32(bytes[at + 2]) << 16 | UInt32(
        bytes[at + 3]) << 24
    }

    if bytes.count >= 16 {
      var v1 = p1 &+ p2
      var v2 = p2
      var v3: UInt32 = 0
      var v4 = 0 &- p1
      while index + 16 <= bytes.count {
        v1 = rotl32(v1 &+ read32(index) &* p2, 13) &* p1
        v2 = rotl32(v2 &+ read32(index + 4) &* p2, 13) &* p1
        v3 = rotl32(v3 &+ read32(index + 8) &* p2, 13) &* p1
        v4 = rotl32(v4 &+ read32(index + 12) &* p2, 13) &* p1
        index += 16
      }
      h = rotl32(v1, 1) &+ rotl32(v2, 7) &+ rotl32(v3, 12) &+ rotl32(v4, 18)
    } else {
      h = p5
    }
    h = h &+ UInt32(bytes.count)

    while index + 4 <= bytes.count {
      h = h &+ read32(index) &* p3
      h = rotl32(h, 17) &* p4
      index += 4
    }
    while index < bytes.count {
      h = h &+ UInt32(bytes[index]) &* p5
      h = rotl32(h, 11) &* p1
      index += 1
    }

    h ^= h >> 15
    h = h &* p2
    h ^= h >> 13
    h = h &* p3
    h ^= h >> 16
    return h
  }

  static func xxHash64(_ data: Data) -> UInt64 {
    let p1: UInt64 = 11_400_714_785_074_694_791
    let p2: UInt64 = 14_029_467_366_897_019_727
    let p3: UInt64 = 1_609_587_929_392_839_161
    let p4: UInt64 = 9_650_029_242_287_828_579
    let p5: UInt64 = 2_870_177_450_012_600_261
    let bytes = [UInt8](data)
    var index = 0
    var h: UInt64

    func read64(_ at: Int) -> UInt64 {
      var value: UInt64 = 0
      for byte in (0..<8).reversed() { value = value << 8 | UInt64(bytes[at + byte]) }
      return value
    }
    func read32(_ at: Int) -> UInt64 {
      UInt64(bytes[at]) | UInt64(bytes[at + 1]) << 8 | UInt64(bytes[at + 2]) << 16 | UInt64(
        bytes[at + 3]) << 24
    }
    func round(_ accumulator: UInt64, _ input: UInt64) -> UInt64 {
      rotl64(accumulator &+ input &* p2, 31) &* p1
    }
    func merge(_ accumulator: UInt64, _ value: UInt64) -> UInt64 {
      (accumulator ^ round(0, value)) &* p1 &+ p4
    }

    if bytes.count >= 32 {
      var v1 = p1 &+ p2
      var v2 = p2
      var v3: UInt64 = 0
      var v4 = 0 &- p1
      while index + 32 <= bytes.count {
        v1 = round(v1, read64(index))
        v2 = round(v2, read64(index + 8))
        v3 = round(v3, read64(index + 16))
        v4 = round(v4, read64(index + 24))
        index += 32
      }
      h = rotl64(v1, 1) &+ rotl64(v2, 7) &+ rotl64(v3, 12) &+ rotl64(v4, 18)
      h = merge(h, v1)
      h = merge(h, v2)
      h = merge(h, v3)
      h = merge(h, v4)
    } else {
      h = p5
    }
    h = h &+ UInt64(bytes.count)

    while index + 8 <= bytes.count {
      h ^= round(0, read64(index))
      h = rotl64(h, 27) &* p1 &+ p4
      index += 8
    }
    if index + 4 <= bytes.count {
      h ^= read32(index) &* p1
      h = rotl64(h, 23) &* p2 &+ p3
      index += 4
    }
    while index < bytes.count {
      h ^= UInt64(bytes[index]) &* p5
      h = rotl64(h, 11) &* p1
      index += 1
    }

    h ^= h >> 33
    h = h &* p2
    h ^= h >> 29
    h = h &* p3
    h ^= h >> 32
    return h
  }

  static func djb2(_ data: Data) -> UInt32 {
    var hash: UInt32 = 5381
    for byte in data {
      hash = hash &* 33 &+ UInt32(byte)
    }
    return hash
  }

  static func sdbm(_ data: Data) -> UInt32 {
    var hash: UInt32 = 0
    for byte in data {
      hash = UInt32(byte) &+ (hash << 6) &+ (hash << 16) &- hash
    }
    return hash
  }

  /// Bob Jenkins' one-at-a-time hash.
  static func jenkinsOneAtATime(_ data: Data) -> UInt32 {
    var hash: UInt32 = 0
    for byte in data {
      hash = hash &+ UInt32(byte)
      hash = hash &+ (hash << 10)
      hash ^= hash >> 6
    }
    hash = hash &+ (hash << 3)
    hash ^= hash >> 11
    hash = hash &+ (hash << 15)
    return hash
  }

  private static func rotl32(_ value: UInt32, _ count: UInt32) -> UInt32 {
    (value << count) | (value >> (32 - count))
  }

  private static func rotl64(_ value: UInt64, _ count: UInt64) -> UInt64 {
    (value << count) | (value >> (64 - count))
  }
}
