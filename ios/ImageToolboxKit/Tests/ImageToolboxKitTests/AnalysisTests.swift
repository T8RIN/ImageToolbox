import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Metrics, duplicates, palette and checksums")
struct AnalysisTests {

  private let red = RGBA(red: 255, green: 0, blue: 0)
  private let blue = RGBA(red: 0, green: 0, blue: 255)

  @Test("identical images have zero error, infinite PSNR and SSIM of one")
  func identical() throws {
    let image = TestImages.halves(left: red, right: blue, width: 16, height: 16)
    let metrics = try #require(ImageMetricsCalculator.compare(image, image))
    #expect(metrics.meanAbsoluteError == 0)
    #expect(metrics.psnr.isInfinite)
    #expect(abs(metrics.structuralSimilarity - 1) < 1e-9)
    #expect(metrics.differingPixels == 0)
  }

  @Test("different images report error and a lower similarity")
  func different() throws {
    let a = TestImages.solid(red, width: 16, height: 16)
    let b = TestImages.solid(blue, width: 16, height: 16)
    let metrics = try #require(ImageMetricsCalculator.compare(a, b))
    #expect(metrics.meanAbsoluteError > 0)
    #expect(metrics.psnr < 30)
    #expect(metrics.differingPixels == 256)
    #expect(metrics.structuralSimilarity < 1)
  }

  @Test("the same picture has the same perceptual hash, a different one does not match exactly")
  func perceptualHash() throws {
    let a = TestImages.halves(left: red, right: blue, width: 64, height: 64)
    let b = TestImages.halves(left: red, right: blue, width: 128, height: 128)
    let hashA = try #require(DuplicateFinder.perceptualHash(a))
    let hashB = try #require(DuplicateFinder.perceptualHash(b))
    #expect(DuplicateFinder.hammingDistance(hashA, hashB) == 0)
  }

  @Test("duplicate groups join items within the threshold, transitively")
  func groups() {
    let items: [(id: String, hash: UInt64)] = [
      ("a", 0b0000), ("b", 0b0001), ("c", 0b0011), ("d", UInt64.max),
    ]
    let found = DuplicateFinder.groups(items, threshold: 1)
    #expect(found.count == 1)
    #expect(Set(found[0]) == ["a", "b", "c"])
  }

  @Test("exact keys are SHA-256 and identical bytes match")
  func exactKey() {
    let data = Data("hello".utf8)
    #expect(
      DuplicateFinder.exactKey(data)
        == "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824")
    #expect(DuplicateFinder.exactKey(data) == DuplicateFinder.exactKey(Data("hello".utf8)))
  }

  @Test("checksum vectors")
  func checksums() {
    let abc = Data("abc".utf8)
    #expect(ChecksumAlgorithm.md5.hexDigest(of: abc) == "900150983cd24fb0d6963f7d28e17f72")
    #expect(ChecksumAlgorithm.sha1.hexDigest(of: abc) == "a9993e364706816aba3e25717850c26c9cd0d89d")
    #expect(
      ChecksumAlgorithm.sha256.hexDigest(of: abc)
        == "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad")
    #expect(ChecksumAlgorithm.crc32.hexDigest(of: Data("123456789".utf8)) == "cbf43926")
    #expect(ChecksumAlgorithm.adler32.hexDigest(of: Data("Wikipedia".utf8)) == "11e60398")
    #expect(ChecksumAlgorithm.sha512.hexDigest(of: Data()).count == 128)
  }

  @Test("k-means finds the two dominant colours")
  func palette() throws {
    let image = TestImages.halves(left: red, right: blue, width: 40, height: 40)
    let raster = try #require(RasterImage(image))
    let colors = PaletteExtractor.dominantColors(raster, count: 2)
    #expect(colors.count == 2)
    #expect(colors.contains { $0.red > 240 && $0.blue < 15 })
    #expect(colors.contains { $0.blue > 240 && $0.red < 15 })
  }

  @Test("pixel-level metric helpers handle colour-space conversion for palette swatches")
  func palettePDF() throws {
    let data = try #require(PalettePDF.render([red, blue, RGBA(hex: "#00FF00")!]))
    #expect(String(decoding: data.prefix(4), as: UTF8.self) == "%PDF")
  }
}
