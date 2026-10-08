import CoreGraphics
import CoreText
import Foundation

/// Renders a colour palette as a one-page PDF with swatches and hex labels.
public enum PalettePDF {

  public static func render(_ colors: [RGBA], columns: Int = 4, swatchSize: CGFloat = 120) -> Data?
  {
    guard !colors.isEmpty, columns > 0 else { return nil }
    let rows = Int((Double(colors.count) / Double(columns)).rounded(.up))
    let labelHeight: CGFloat = 28
    let margin: CGFloat = 24
    let width = CGFloat(columns) * swatchSize + margin * 2
    let height = CGFloat(rows) * (swatchSize + labelHeight) + margin * 2

    let output = NSMutableData()
    var box = CGRect(x: 0, y: 0, width: width, height: height)
    guard let consumer = CGDataConsumer(data: output as CFMutableData),
      let context = CGContext(consumer: consumer, mediaBox: &box, nil)
    else { return nil }

    context.beginPDFPage(nil)
    context.setFillColor(CGColor(red: 1, green: 1, blue: 1, alpha: 1))
    context.fill(box)

    let font = CTFontCreateWithName("Menlo-Regular" as CFString, 12, nil)
    for (index, color) in colors.enumerated() {
      let column = CGFloat(index % columns)
      let row = CGFloat(index / columns)
      let x = margin + column * swatchSize
      // PDF origin is bottom-left, so rows are counted from the top.
      let y = height - margin - (row + 1) * (swatchSize + labelHeight) + labelHeight
      context.setFillColor(ImageStitcher.cgColor(color))
      context.fill(CGRect(x: x + 4, y: y, width: swatchSize - 8, height: swatchSize - 8))
      drawLabel(color.hexString, font: font, at: CGPoint(x: x + 4, y: y - 18), in: context)
    }

    context.endPDFPage()
    context.closePDF()
    return output as Data
  }

  private static func drawLabel(
    _ text: String, font: CTFont, at point: CGPoint, in context: CGContext
  ) {
    let attributes: [NSAttributedString.Key: Any] = [
      NSAttributedString.Key(kCTFontAttributeName as String): font,
      NSAttributedString.Key(kCTForegroundColorAttributeName as String): CGColor(
        gray: 0.1, alpha: 1),
    ]
    let line = CTLineCreateWithAttributedString(
      NSAttributedString(string: text, attributes: attributes))
    context.textPosition = point
    CTLineDraw(line, context)
  }
}
