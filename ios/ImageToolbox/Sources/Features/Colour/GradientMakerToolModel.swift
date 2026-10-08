import CoreGraphics
import ImageToolboxKit
import Observation
import SwiftUI

/// A colour stop as edited on screen. It is converted to `GradientStop` for rendering.
struct GradientDraftStop: Identifiable {
  let id = UUID()
  var color: Color
  var position: Double
}

enum GradientKind {
  case linear
  case radial
}

@MainActor
@Observable
final class GradientMakerToolModel {

  struct Stop: Equatable, Sendable {
    var color: Color
    var position: Double
  }

  /// Settings that the undo history keeps. A step is recorded when a render starts.
  struct Parameters: Equatable, Sendable {
    var stops: [Stop]
    var kind: GradientKind
    var angle: Double
    var width: Double
    var height: Double
  }

  private(set) var history = UndoHistory(
    Parameters(
      stops: [
        Stop(color: Color(red: 1, green: 0.35, blue: 0.37), position: 0),
        Stop(color: Color(red: 0.35, green: 0.6, blue: 0.9), position: 1),
      ],
      kind: .linear, angle: 0, width: 1024, height: 1024))

  var canUndo: Bool { history.canUndo }
  var canRedo: Bool { history.canRedo }

  func commitParameters() {
    history.record(currentParameters)
  }

  func undo() {
    guard history.undo() else { return }
    restoreParameters(history.current)
  }

  func redo() {
    guard history.redo() else { return }
    restoreParameters(history.current)
  }

  private var currentParameters: Parameters {
    Parameters(
      stops: stops.map { Stop(color: $0.color, position: $0.position) },
      kind: kind, angle: angle, width: width, height: height)
  }

  private func restoreParameters(_ parameters: Parameters) {
    stops = parameters.stops.map { GradientDraftStop(color: $0.color, position: $0.position) }
    kind = parameters.kind
    angle = parameters.angle
    width = parameters.width
    height = parameters.height
  }
  private static let minimumStops = 2
  private static let maximumStops = 4

  var stops = [
    GradientDraftStop(color: Color(red: 1, green: 0.35, blue: 0.37), position: 0),
    GradientDraftStop(color: Color(red: 0.35, green: 0.6, blue: 0.9), position: 1),
  ]
  var kind = GradientKind.linear
  var angle = 0.0
  var width = 1024.0
  var height = 1024.0

  private(set) var rendered: LoadedImage?
  private(set) var renderFailed = false
  private(set) var meshPNG: Data?
  private(set) var meshFailed = false
  private(set) var renderedFileName = "gradient.png"
  private(set) var meshFileName = "mesh.png"

  var canAddStop: Bool { stops.count < GradientMakerToolModel.maximumStops }
  var canRemoveStop: Bool { stops.count > GradientMakerToolModel.minimumStops }

  func addStop() {
    guard canAddStop else { return }
    stops.append(GradientDraftStop(color: .gray, position: 0.5))
  }

  func removeStop() {
    guard canRemoveStop else { return }
    stops.removeLast()
  }

  /// Renders the gradient off the main actor. The result keeps both the PNG bytes and the pixels.
  func render() async {
    let gradientStops = stops.map { stop in
      GradientStop(color: ColorConversion.rgba(stop.color), location: stop.position)
    }
    let gradientKind = kind
    let gradientAngle = angle
    let pixelWidth = Int(width)
    let pixelHeight = Int(height)

    let image = await Task.detached(priority: .userInitiated) { () -> LoadedImage? in
      let cgImage: CGImage?
      switch gradientKind {
      case .linear:
        cgImage = GradientRenderer.linear(
          stops: gradientStops, angle: gradientAngle, width: pixelWidth, height: pixelHeight)
      case .radial:
        cgImage = GradientRenderer.radial(
          stops: gradientStops, width: pixelWidth, height: pixelHeight)
      }
      guard let cgImage, let png = ImageCodec.encode(cgImage, as: .png) else { return nil }
      return LoadedImage(data: png, image: cgImage)
    }.value

    rendered = image
    renderedFileName =
      image.map { ExportNaming.fileName(stem: "gradient", fileExtension: "png", data: $0.data) }
      ?? "gradient.png"
    renderFailed = image == nil
  }

  /// The sample mesh is rendered on the main actor, as `ImageRenderer` requires.
  func exportMesh() {
    let renderer = ImageRenderer(content: MeshSampleView().frame(width: 512, height: 512))
    renderer.scale = 2
    guard let cgImage = renderer.cgImage, let png = ImageCodec.encode(cgImage, as: .png) else {
      meshPNG = nil
      meshFailed = true
      return
    }
    meshPNG = png
    meshFileName = ExportNaming.fileName(stem: "mesh", fileExtension: "png", data: png)
    meshFailed = false
  }
}
