import ImageToolboxKit
import SwiftUI
import UIKit

/// Converts between SwiftUI colours and the package's 8-bit `RGBA`.
@MainActor
enum ColorConversion {
  static func rgba(_ color: Color) -> RGBA {
    let uiColor = UIColor(color)
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var alpha: CGFloat = 0
    uiColor.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
    return RGBA(
      red: channel(red),
      green: channel(green),
      blue: channel(blue),
      alpha: channel(alpha))
  }

  static func color(_ rgba: RGBA) -> Color {
    Color(
      red: Double(rgba.red) / 255,
      green: Double(rgba.green) / 255,
      blue: Double(rgba.blue) / 255,
      opacity: Double(rgba.alpha) / 255)
  }

  private static func channel(_ value: CGFloat) -> UInt8 {
    UInt8((min(max(value, 0), 1) * 255).rounded())
  }
}

/// Colour square with its hex code. Tapping copies the code to the pasteboard.
struct ColorSwatch: View {
  let color: RGBA

  var body: some View {
    Button {
      UIPasteboard.general.string = color.hexString
    } label: {
      VStack(spacing: 6) {
        RoundedRectangle(cornerRadius: 12)
          .fill(ColorConversion.color(color))
          .frame(height: 56)
        Text(color.hexString)
          .font(.caption.monospaced())
      }
      .frame(maxWidth: .infinity)
    }
    .buttonStyle(.plain)
  }
}
