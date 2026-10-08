import SwiftUI

enum DesignTokens {
  static let spacing: CGFloat = 16
  static let cardCornerRadius: CGFloat = 24
}

extension View {
  /// Liquid Glass surface for cards.
  /// The deployment target is iOS 26, so there is no fallback material.
  func glassCard(interactive: Bool = false) -> some View {
    glassEffect(
      interactive ? Glass.regular.interactive() : Glass.regular,
      in: .rect(cornerRadius: DesignTokens.cardCornerRadius)
    )
  }
}

/// Soft gradient behind the content. Liquid Glass needs something behind it to refract,
/// otherwise cards look like flat white panels.
struct AppBackground: View {
  var body: some View {
    ZStack {
      Color(uiColor: .systemBackground)
      LinearGradient(
        colors: [
          Color("AppBackgroundStart"),
          Color("AppBackgroundMiddle"),
          Color("AppBackgroundEnd"),
        ],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
      )
    }
    .ignoresSafeArea()
  }
}
