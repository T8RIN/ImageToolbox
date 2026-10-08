import CoreGraphics
import ImageToolboxKit
import SwiftUI

struct CodePreviewToolView: View {
  @State private var code = """
    func greet(name: String) -> String {
      // Say hello
      return "Hello, \\(name)! \\(42)"
    }
    """
  @State private var language: CodeHighlighter.Language = .swift
  @State private var theme: CodeThemeChoice = .dark
  @State private var fontSize: Double = 14
  @State private var showLineNumbers = true
  @State private var rendered: CGImage?
  @State private var pngData: Data?
  @State private var pngFileName = "code.png"
  @State private var failed = false
  @State private var history: UndoHistory<CodeSettings>?

  /// The settings the picture is rendered from. One undo step per render.
  private struct CodeSettings: Equatable, Sendable {
    var code: String
    var language: CodeHighlighter.Language
    var theme: CodeThemeChoice
    var fontSize: Double
    var showLineNumbers: Bool
  }

  private var currentSettings: CodeSettings {
    CodeSettings(
      code: code, language: language, theme: theme, fontSize: fontSize,
      showLineNumbers: showLineNumbers)
  }

  private var canUndo: Bool { history?.canUndo ?? false }
  private var canRedo: Bool { history?.canRedo ?? false }

  /// The first render sets the starting point, so undo returns to an earlier render.
  private func commitSettings() {
    if history == nil {
      history = UndoHistory(currentSettings)
    } else {
      history?.record(currentSettings)
    }
  }

  private func undoSettings() {
    guard history?.undo() == true, let settings = history?.current else { return }
    restore(settings)
  }

  private func redoSettings() {
    guard history?.redo() == true, let settings = history?.current else { return }
    restore(settings)
  }

  private func restore(_ settings: CodeSettings) {
    code = settings.code
    language = settings.language
    theme = settings.theme
    fontSize = settings.fontSize
    showLineNumbers = settings.showLineNumbers
  }

  var body: some View {
    ToolScaffold(identifier: "codePreview") {
      GlassSection("Code") {
        TextEditor(text: $code)
          .font(.system(.footnote, design: .monospaced))
          .frame(minHeight: 140)
          .accessibilityIdentifier("codePreview.input")
      }

      GlassSection("Style") {
        Picker("Language", selection: $language) {
          ForEach(CodeHighlighter.Language.all) { language in
            Text(language.title).tag(language)
          }
        }
        Picker("Theme", selection: $theme) {
          Text("Dark").tag(CodeThemeChoice.dark)
          Text("Light").tag(CodeThemeChoice.light)
        }
        .pickerStyle(.segmented)
        HStack {
          Button("Undo") { undoSettings() }
            .buttonStyle(.glass)
            .disabled(!canUndo)
            .accessibilityIdentifier("codePreview.undo")
          Button("Redo") { redoSettings() }
            .buttonStyle(.glass)
            .disabled(!canRedo)
            .accessibilityIdentifier("codePreview.redo")
        }
        ParameterSlider(title: "Font size", value: $fontSize, range: 10...28)
        Toggle("Line numbers", isOn: $showLineNumbers)
      }

      Button("Render") {
        commitSettings()
        render()
      }
      .buttonStyle(.glassProminent)
      .accessibilityIdentifier("codePreview.render")

      if failed {
        ErrorText(message: "Could not render the code.")
      }

      if let rendered, let pngData {
        PictureView(image: rendered)
        ShareFileButton(title: "Share PNG", data: pngData, fileName: pngFileName)
      }
    }
  }

  private func render() {
    let theme = theme.colors
    guard
      let image = CodeImageRenderer.render(
        code: code,
        language: language,
        fontSize: CGFloat(fontSize),
        theme: theme,
        showLineNumbers: showLineNumbers),
      let data = ImageCodec.encode(image, as: .png)
    else {
      failed = true
      rendered = nil
      pngData = nil
      return
    }
    failed = false
    rendered = image
    pngFileName = ExportNaming.fileName(stem: "code", fileExtension: "png", data: data)
    pngData = data
  }
}

private enum CodeThemeChoice: Hashable {
  case dark
  case light

  var colors: CodeTheme {
    switch self {
    case .dark: .dark
    case .light: .light
    }
  }
}
