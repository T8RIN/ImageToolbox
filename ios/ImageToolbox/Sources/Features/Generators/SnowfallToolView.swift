import ImageToolboxKit
import SwiftUI

struct SnowfallToolView: View {
  @State private var model = SnowfallToolModel()

  var body: some View {
    ToolScaffold(identifier: "snowfall") {
      TimelineView(.animation) { timeline in
        let particles = model.advance(to: timeline.date)
        Canvas { context, _ in
          for particle in particles {
            let diameter = CGFloat(particle.size)
            let rect = CGRect(
              x: CGFloat(particle.x) - diameter / 2,
              y: CGFloat(particle.y) - diameter / 2,
              width: diameter,
              height: diameter)
            let opacity = 0.3 + 0.7 * (particle.size - 2) / 4
            context.fill(Path(ellipseIn: rect), with: .color(.white.opacity(opacity)))
          }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 420)
        .background {
          LinearGradient(
            colors: [
              Color(red: 0.04, green: 0.06, blue: 0.16),
              Color(red: 0.2, green: 0.27, blue: 0.4),
            ],
            startPoint: .top,
            endPoint: .bottom)
        }
        .clipShape(.rect(cornerRadius: DesignTokens.cardCornerRadius))
        .onGeometryChange(for: CGSize.self, of: { $0.size }, action: { model.resize(to: $0) })
      }

      GlassSection("Settings") {
        HStack {
          Button("Undo") { model.undo() }
            .buttonStyle(.glass)
            .disabled(!model.canUndo)
            .accessibilityIdentifier("snowfall.undo")
          Button("Redo") { model.redo() }
            .buttonStyle(.glass)
            .disabled(!model.canRedo)
            .accessibilityIdentifier("snowfall.redo")
        }
        ParameterSlider(
          title: "Flakes", value: $model.flakes, onEditingEnded: model.commitParameters,
          range: 50...400)
      }
    }
    .onChange(of: model.flakes) { _, _ in
      model.rebuild()
    }
  }
}
