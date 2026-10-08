import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Noise, ASCII and particles")
struct GeneratorTests {

  @Test("seeded generator repeats its sequence")
  func seededRepeats() {
    var first = SeededGenerator(seed: 42)
    var second = SeededGenerator(seed: 42)
    #expect((0..<5).map { _ in first.next() } == (0..<5).map { _ in second.next() })
  }

  @Test("same seed gives the same noise, another seed differs")
  func perlinDeterminism() {
    let a = PerlinNoise(seed: 7)
    let b = PerlinNoise(seed: 7)
    let c = PerlinNoise(seed: 8)
    #expect(a.value(x: 1.3, y: 2.7) == b.value(x: 1.3, y: 2.7))
    #expect(a.value(x: 1.3, y: 2.7) != c.value(x: 1.3, y: 2.7))
  }

  @Test("fractal noise stays inside the documented range")
  func perlinRange() {
    let noise = PerlinNoise(seed: 3)
    for step in 0..<200 {
      let value = noise.fractal(x: Double(step) * 0.37, y: Double(step) * 0.11, octaves: 4)
      #expect(abs(value) <= 0.8)
    }
  }

  @Test("ASCII art of a white image is all blanks and of black is all dense glyphs")
  func asciiExtremes() throws {
    let white = try #require(
      RasterImage(TestImages.solid(RGBA(red: 255, green: 255, blue: 255), width: 40, height: 40)))
    let black = try #require(
      RasterImage(TestImages.solid(RGBA(red: 0, green: 0, blue: 0), width: 40, height: 40)))

    let blank = AsciiArt.render(white, columns: 10)
    #expect(blank.split(separator: "\n").allSatisfy { $0.allSatisfy { $0 == " " } })

    let dense = AsciiArt.render(black, columns: 10)
    #expect(dense.split(separator: "\n").allSatisfy { $0.allSatisfy { $0 == "@" } })
  }

  @Test("ASCII art has the requested columns and an aspect-corrected row count")
  func asciiShape() throws {
    let raster = try #require(
      RasterImage(TestImages.solid(RGBA(red: 128, green: 128, blue: 128), width: 100, height: 100)))
    let art = AsciiArt.render(raster, columns: 20)
    let lines = art.split(separator: "\n")
    #expect(lines.count == 10)
    #expect(lines.allSatisfy { $0.count == 20 })
  }

  @Test("particles fall, stay in bounds and respawn at the top")
  func particlesRespawn() {
    var field = ParticleField(count: 50, width: 300, height: 600, seed: 5)
    let startY = field.particles.map(\.y)
    field.step(deltaTime: 0.1)
    #expect(zip(startY, field.particles.map(\.y)).contains { $1 > $0 })

    for _ in 0..<2000 {
      field.step(deltaTime: 0.05)
    }
    #expect(field.particles.allSatisfy { $0.x >= 0 && $0.x < 300 })
    #expect(field.particles.allSatisfy { $0.y < 600 + $0.size })
  }

  @Test("confetti with gravity does not respawn")
  func confettiNoRespawn() {
    var field = ParticleField(
      count: 5, width: 100, height: 100, gravity: 500, respawns: false, seed: 9)
    for _ in 0..<500 {
      field.step(deltaTime: 0.05)
    }
    #expect(field.particles.allSatisfy { $0.y > 100 })
  }
}
