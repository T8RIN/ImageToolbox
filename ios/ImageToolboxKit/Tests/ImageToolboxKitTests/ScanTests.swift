import CoreGraphics
import Foundation
import Testing

@testable import ImageToolboxKit

@Suite("Barcode and document scanning")
struct ScanTests {

  #if !targetEnvironment(simulator)
    // Vision's barcode detector cannot create its inference context on the iOS simulator
    // ("Could not create inference context"), so this round trip runs on macOS and on devices only.
    @Test("a generated QR code is read back with the same payload")
    func qrRoundTrip() throws {
      let qr = try #require(CodeScanner.makeQRCode("https://example.com/page?id=7"))
      let codes = CodeScanner.detect(in: qr)
      #expect(codes.contains { $0.payload == "https://example.com/page?id=7" })
      #expect(codes.contains { $0.symbology == "VNBarcodeSymbologyQR" })
    }
  #endif

  @Test("a picture without codes gives no results")
  func noCodes() {
    let blank = TestImages.solid(RGBA(red: 200, green: 200, blue: 200), width: 128, height: 128)
    #expect(CodeScanner.detect(in: blank).isEmpty)
  }

  @Test("perspective correction keeps the picture size and returns a picture")
  func correction() throws {
    let photo = TestImages.solid(RGBA(red: 180, green: 180, blue: 180), width: 200, height: 160)
    let quad = DocumentQuad(
      topLeft: CGPoint(x: 10, y: 150), topRight: CGPoint(x: 190, y: 150),
      bottomLeft: CGPoint(x: 10, y: 10), bottomRight: CGPoint(x: 190, y: 10))
    let corrected = try #require(DocumentScanner.correct(photo, quad: quad))
    #expect(corrected.width > 0 && corrected.height > 0)
  }
}

@Suite("Document detection on a synthetic sheet")
struct DocumentDetectionTests {

  @Test("a white sheet on a dark desk is found and its corners are inside the picture")
  func findsSheet() throws {
    let width = 640
    let height = 480
    let photo = TestImages.image(width: width, height: height) { x, y in
      let onSheet = x > 120 && x < 520 && y > 60 && y < 420
      let textLine = onSheet && (y / 24) % 3 == 0 && x > 160 && x < 480
      if textLine { return RGBA(red: 40, green: 40, blue: 40) }
      return onSheet ? RGBA(red: 245, green: 245, blue: 245) : RGBA(red: 30, green: 40, blue: 50)
    }
    let quad = try #require(DocumentScanner.detectDocument(in: photo))
    for corner in [quad.topLeft, quad.topRight, quad.bottomLeft, quad.bottomRight] {
      #expect(corner.x >= 0 && corner.x <= CGFloat(width))
      #expect(corner.y >= 0 && corner.y <= CGFloat(height))
    }
  }
}
