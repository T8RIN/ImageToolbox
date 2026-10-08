import CoreGraphics
import CoreImage
import Foundation
import Vision

/// A barcode or QR code found in a picture.
public struct ScannedCode: Equatable, Sendable {
  public let payload: String?
  public let symbology: String
}

/// Finds barcodes and QR codes with Vision. Works on still pictures; live camera scanning is separate.
public enum CodeScanner {

  public static func detect(in image: CGImage) -> [ScannedCode] {
    let request = VNDetectBarcodesRequest()
    let handler = VNImageRequestHandler(cgImage: image, options: [:])
    do {
      try handler.perform([request])
    } catch {
      return []
    }
    return (request.results ?? []).map { observation in
      ScannedCode(
        payload: observation.payloadStringValue, symbology: observation.symbology.rawValue)
    }
  }

  /// Renders a QR code for `text` with Core Image, on an opaque white background with the quiet zone
  /// the QR standard requires. Without the white margin, some decoders (including Vision on iOS) fail.
  public static func makeQRCode(_ text: String, scale: Int = 8) -> CGImage? {
    guard let filter = CIFilter(name: "CIQRCodeGenerator") else { return nil }
    filter.setValue(Data(text.utf8), forKey: "inputMessage")
    filter.setValue("M", forKey: "inputCorrectionLevel")
    // Render at module size first, then scale with nearest-neighbour so module edges stay sharp.
    guard let output = filter.outputImage,
      let modules = CIContext().createCGImage(output, from: output.extent)
    else { return nil }

    let margin = scale * 4
    let codeSide = modules.width * scale
    let side = codeSide + margin * 2
    guard let context = ImageGeometry.makeContext(width: side, height: side) else { return nil }
    context.setFillColor(ImageStitcher.cgColor(RGBA(red: 255, green: 255, blue: 255)))
    context.fill(CGRect(x: 0, y: 0, width: side, height: side))
    context.interpolationQuality = .none
    context.draw(modules, in: CGRect(x: margin, y: margin, width: codeSide, height: codeSide))
    return context.makeImage()
  }
}
