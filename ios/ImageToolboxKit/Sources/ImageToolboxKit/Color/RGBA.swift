import Foundation

/// 8-bit straight-alpha colour with conversions and simple colour-theory helpers.
public struct RGBA: Hashable, Sendable {
  public var red: UInt8
  public var green: UInt8
  public var blue: UInt8
  public var alpha: UInt8

  public init(red: UInt8, green: UInt8, blue: UInt8, alpha: UInt8 = 255) {
    self.red = red
    self.green = green
    self.blue = blue
    self.alpha = alpha
  }

  /// Parses `RGB`, `RRGGBB` or `RRGGBBAA`, with or without a leading `#`.
  public init?(hex: String) {
    var text = hex.trimmingCharacters(in: .whitespaces)
    if text.hasPrefix("#") { text.removeFirst() }
    guard [3, 6, 8].contains(text.count), let value = UInt64(text, radix: 16) else { return nil }

    switch text.count {
    case 3:
      let r = (value >> 8) & 0xF
      let g = (value >> 4) & 0xF
      let b = value & 0xF
      self.init(red: UInt8(r * 17), green: UInt8(g * 17), blue: UInt8(b * 17))
    case 6:
      self.init(
        red: UInt8((value >> 16) & 0xFF),
        green: UInt8((value >> 8) & 0xFF),
        blue: UInt8(value & 0xFF))
    default:
      self.init(
        red: UInt8((value >> 24) & 0xFF),
        green: UInt8((value >> 16) & 0xFF),
        blue: UInt8((value >> 8) & 0xFF),
        alpha: UInt8(value & 0xFF))
    }
  }

  /// `#RRGGBB`, or `#RRGGBBAA` when alpha is not fully opaque. Uppercase.
  public var hexString: String {
    let base = String(format: "#%02X%02X%02X", red, green, blue)
    return alpha == 255 ? base : base + String(format: "%02X", alpha)
  }

  public var hsl: (hue: Double, saturation: Double, lightness: Double) {
    let (r, g, b) = unitComponents
    let maxValue = max(r, g, b)
    let minValue = min(r, g, b)
    let lightness = (maxValue + minValue) / 2
    let delta = maxValue - minValue
    guard delta > 0 else { return (0, 0, lightness * 100) }

    let saturation =
      lightness > 0.5 ? delta / (2 - maxValue - minValue) : delta / (maxValue + minValue)
    return (Self.hue(r, g, b, maxValue, delta), saturation * 100, lightness * 100)
  }

  public var hsv: (hue: Double, saturation: Double, value: Double) {
    let (r, g, b) = unitComponents
    let maxValue = max(r, g, b)
    let minValue = min(r, g, b)
    let delta = maxValue - minValue
    let saturation = maxValue == 0 ? 0 : delta / maxValue
    guard delta > 0 else { return (0, 0, maxValue * 100) }
    return (Self.hue(r, g, b, maxValue, delta), saturation * 100, maxValue * 100)
  }

  public var cmyk: (cyan: Double, magenta: Double, yellow: Double, key: Double) {
    let (r, g, b) = unitComponents
    let key = 1 - max(r, g, b)
    guard key < 1 else { return (0, 0, 0, 100) }
    let scale = 1 - key
    return (
      (1 - r - key) / scale * 100,
      (1 - g - key) / scale * 100,
      (1 - b - key) / scale * 100,
      key * 100
    )
  }

  /// Relative luminance per WCAG 2.x, 0...1.
  public var relativeLuminance: Double {
    func linear(_ channel: Double) -> Double {
      channel <= 0.03928 ? channel / 12.92 : pow((channel + 0.055) / 1.055, 2.4)
    }
    let (r, g, b) = unitComponents
    return 0.2126 * linear(r) + 0.7152 * linear(g) + 0.0722 * linear(b)
  }

  /// WCAG contrast ratio against another colour, 1...21.
  public func contrastRatio(with other: RGBA) -> Double {
    let first = relativeLuminance
    let second = other.relativeLuminance
    return (max(first, second) + 0.05) / (min(first, second) + 0.05)
  }

  /// Linear interpolation in sRGB. `t` is clamped to 0...1.
  public func mixed(with other: RGBA, fraction: Double) -> RGBA {
    let t = min(max(fraction, 0), 1)
    func blend(_ a: UInt8, _ b: UInt8) -> UInt8 {
      UInt8((Double(a) + (Double(b) - Double(a)) * t).rounded())
    }
    return RGBA(
      red: blend(red, other.red),
      green: blend(green, other.green),
      blue: blend(blue, other.blue),
      alpha: blend(alpha, other.alpha))
  }

  /// Colours at the given hue offsets (degrees) from this colour, keeping saturation and value.
  public func rotatedHues(_ offsets: [Double]) -> [RGBA] {
    let base = hsv
    return offsets.map { offset in
      RGBA(hue: base.hue + offset, saturation: base.saturation, value: base.value, alpha: alpha)
    }
  }

  public var complementary: RGBA { rotatedHues([180])[0] }
  public var analogous: [RGBA] { rotatedHues([-30, 30]) }
  public var triadic: [RGBA] { rotatedHues([120, 240]) }
  public var splitComplementary: [RGBA] { rotatedHues([150, 210]) }
  public var tetradic: [RGBA] { rotatedHues([90, 180, 270]) }

  /// Creates a colour from HSV, where hue is in degrees and saturation and value are 0...100.
  public init(hue: Double, saturation: Double, value: Double, alpha: UInt8 = 255) {
    let h = ((hue.truncatingRemainder(dividingBy: 360)) + 360).truncatingRemainder(dividingBy: 360)
    let s = min(max(saturation, 0), 100) / 100
    let v = min(max(value, 0), 100) / 100
    let chroma = v * s
    let sector = h / 60
    let x = chroma * (1 - abs(sector.truncatingRemainder(dividingBy: 2) - 1))
    let m = v - chroma
    let (r, g, b): (Double, Double, Double)
    switch sector {
    case 0..<1: (r, g, b) = (chroma, x, 0)
    case 1..<2: (r, g, b) = (x, chroma, 0)
    case 2..<3: (r, g, b) = (0, chroma, x)
    case 3..<4: (r, g, b) = (0, x, chroma)
    case 4..<5: (r, g, b) = (x, 0, chroma)
    default: (r, g, b) = (chroma, 0, x)
    }
    self.init(
      red: UInt8(((r + m) * 255).rounded()),
      green: UInt8(((g + m) * 255).rounded()),
      blue: UInt8(((b + m) * 255).rounded()),
      alpha: alpha)
  }

  private var unitComponents: (Double, Double, Double) {
    (Double(red) / 255, Double(green) / 255, Double(blue) / 255)
  }

  private static func hue(
    _ r: Double, _ g: Double, _ b: Double, _ maxValue: Double, _ delta: Double
  )
    -> Double
  {
    var hue: Double
    if maxValue == r {
      hue = ((g - b) / delta).truncatingRemainder(dividingBy: 6)
    } else if maxValue == g {
      hue = (b - r) / delta + 2
    } else {
      hue = (r - g) / delta + 4
    }
    hue *= 60
    return hue < 0 ? hue + 360 : hue
  }
}
