import CoreGraphics
import CoreImage
import Foundation
import Vision

/// Four corners of a detected document, in pixels, origin at the top-left of the picture.
public struct DocumentQuad: Equatable, Sendable {
  public let topLeft: CGPoint
  public let topRight: CGPoint
  public let bottomLeft: CGPoint
  public let bottomRight: CGPoint
}

/// Finds a sheet of paper in a photo and straightens it (perspective correction).
public enum DocumentScanner {

  public static func detectDocument(in image: CGImage) -> DocumentQuad? {
    let request = VNDetectDocumentSegmentationRequest()
    let handler = VNImageRequestHandler(cgImage: image, options: [:])
    do {
      try handler.perform([request])
    } catch {
      return nil
    }
    guard let observation = request.results?.first else { return nil }

    // Vision returns normalised points with the origin at the bottom-left, the same origin Core Image uses.
    // Scaling by the picture size gives pixel corners that Core Image can consume directly.
    let width = CGFloat(image.width)
    let height = CGFloat(image.height)
    func pixel(_ point: CGPoint) -> CGPoint {
      CGPoint(x: point.x * width, y: point.y * height)
    }
    return DocumentQuad(
      topLeft: pixel(observation.topLeft),
      topRight: pixel(observation.topRight),
      bottomLeft: pixel(observation.bottomLeft),
      bottomRight: pixel(observation.bottomRight))
  }

  /// Warps the quad to a rectangle. The output has the picture's full size; the document fills the frame.
  public static func correct(_ image: CGImage, quad: DocumentQuad) -> CGImage? {
    let input = CIImage(cgImage: image)
    guard let filter = CIFilter(name: "CIPerspectiveCorrection") else { return nil }
    filter.setValue(input, forKey: kCIInputImageKey)
    filter.setValue(CIVector(cgPoint: quad.topLeft), forKey: "inputTopLeft")
    filter.setValue(CIVector(cgPoint: quad.topRight), forKey: "inputTopRight")
    filter.setValue(CIVector(cgPoint: quad.bottomLeft), forKey: "inputBottomLeft")
    filter.setValue(CIVector(cgPoint: quad.bottomRight), forKey: "inputBottomRight")
    guard let output = filter.outputImage else { return nil }
    return CIContext().createCGImage(output, from: output.extent)
  }
}
