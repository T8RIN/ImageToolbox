import SwiftUI

struct UsageStatisticsToolView: View {
  @State private var model = UsageStatisticsToolModel()

  var body: some View {
    ToolScaffold(identifier: "usageStatistics") {
      GlassSection {
        HStack {
          Text("Total opens")
          Spacer()
          Text(model.statistics.total, format: .number)
            .monospacedDigit()
            .foregroundStyle(.secondary)
        }

        HStack {
          Button("Refresh") {
            model.load()
          }
          .accessibilityIdentifier("usageStatistics.refresh")

          Spacer()

          Button("Reset", role: .destructive) {
            model.reset()
          }
          .accessibilityIdentifier("usageStatistics.reset")
        }
        .buttonStyle(.glass)
      }

      GlassSection("Top tools") {
        if model.topItems.isEmpty {
          Text("No tools have been opened yet.")
            .foregroundStyle(.secondary)
        } else {
          ForEach(model.topItems) { item in
            UsageRow(item: item, maxCount: model.maxCount)
          }
        }
      }
    }
    .onAppear {
      model.load()
    }
  }
}

private struct UsageRow: View {
  let item: UsageItem
  let maxCount: Int

  var body: some View {
    VStack(alignment: .leading, spacing: 6) {
      HStack {
        name
        Spacer()
        Text(item.count, format: .number)
          .monospacedDigit()
          .foregroundStyle(.secondary)
      }

      GeometryReader { proxy in
        ZStack(alignment: .leading) {
          Capsule()
            .fill(.quaternary)
          Capsule()
            .fill(.tint)
            .frame(width: proxy.size.width * fraction)
        }
      }
      .frame(height: 8)
    }
  }

  private var fraction: CGFloat {
    CGFloat(item.count) / CGFloat(max(1, maxCount))
  }

  @ViewBuilder
  private var name: some View {
    if let tool = ToolID(rawValue: item.key) {
      Text(tool.title)
    } else {
      Text(verbatim: item.key)
    }
  }
}
