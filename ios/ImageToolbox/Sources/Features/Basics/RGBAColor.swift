import ImageToolboxKit
import SwiftUI

extension Color {
  /// SwiftUI colour for a package `RGBA`. Channels are scaled from 0...255 to 0...1.
  init(rgba: RGBA) {
    self.init(
      red: Double(rgba.red) / 255,
      green: Double(rgba.green) / 255,
      blue: Double(rgba.blue) / 255,
      opacity: Double(rgba.alpha) / 255
    )
  }
}
