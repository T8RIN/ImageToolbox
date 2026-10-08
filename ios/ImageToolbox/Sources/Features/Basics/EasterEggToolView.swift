import CoreGraphics
import SwiftUI

struct EasterEggToolView: View {
  private static let palette: [Color] = [
    .red, .orange, .yellow, .green, .mint, .blue, .purple, .pink,
  ]

  @State private var model = EasterEggToolModel()

  var body: some View {
    ToolScaffold(identifier: "easterEgg") {
      Button {
        model.celebrate()
      } label: {
        Label("Celebrate", systemImage: "sparkles")
          .font(.title2.weight(.semibold))
          .frame(maxWidth: .infinity, minHeight: 56)
      }
      .buttonStyle(.glassProminent)
      .accessibilityIdentifier("easterEgg.celebrate")

      confetti
    }
  }

  private var confetti: some View {
    GeometryReader { proxy in
      TimelineView(.animation(paused: !model.isRunning)) { context in
        let snapshot = model.advance(to: context.date, width: Double(proxy.size.width))
        Canvas { canvas, _ in
          for (index, particle) in snapshot.particles.enumerated() {
            var piece = canvas
            let size = CGFloat(particle.size)
            piece.translateBy(x: CGFloat(particle.x), y: CGFloat(particle.y))
            piece.rotate(by: .radians(particle.spin * snapshot.elapsed))
            let rect = CGRect(x: -size, y: -size / 2, width: size * 2, height: size)
            piece.fill(Path(rect), with: .color(Self.palette[index % Self.palette.count]))
          }
        }
      }
    }
    .frame(height: EasterEggToolModel.canvasHeight)
    .clipped()
  }
}
