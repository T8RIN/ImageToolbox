import CoreGraphics
import CryptoKit
import Foundation

public enum DuplicateFinder {

  /// Exact duplicates have byte-identical files. SHA-256 is used as the identity.
  public static func exactKey(_ data: Data) -> String {
    SHA256.hash(data: data).map { String(format: "%02x", $0) }.joined()
  }

  /// 64-bit difference hash. Similar pictures get hashes that differ in few bits.
  public static func perceptualHash(_ image: CGImage) -> UInt64? {
    guard let small = ImageGeometry.resize(image, width: 9, height: 8),
      let raster = RasterImage(small)
    else { return nil }

    var hash: UInt64 = 0
    for row in 0..<8 {
      for column in 0..<8 {
        let left = raster.luminance(x: column, y: row)
        let right = raster.luminance(x: column + 1, y: row)
        hash <<= 1
        if left < right { hash |= 1 }
      }
    }
    return hash
  }

  public static func hammingDistance(_ a: UInt64, _ b: UInt64) -> Int {
    (a ^ b).nonzeroBitCount
  }

  /// Groups identifiers whose hashes are within `threshold` bits of each other, transitively.
  /// Only groups with at least two members are returned.
  public static func groups<ID: Hashable>(_ items: [(id: ID, hash: UInt64)], threshold: Int)
    -> [[ID]]
  {
    var parent = Array(0..<items.count)
    func root(_ index: Int) -> Int {
      var current = index
      while parent[current] != current {
        parent[current] = parent[parent[current]]
        current = parent[current]
      }
      return current
    }

    for i in items.indices {
      for j in (i + 1)..<items.count
      where hammingDistance(items[i].hash, items[j].hash) <= threshold {
        parent[root(i)] = root(j)
      }
    }

    var buckets: [Int: [ID]] = [:]
    for index in items.indices {
      buckets[root(index), default: []].append(items[index].id)
    }
    return buckets.values.filter { $0.count > 1 }.sorted { $0.count > $1.count }
  }
}
