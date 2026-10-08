import Foundation
import ImageIO
import Testing

@testable import ImageToolboxKit

/// Records which types ImageIO reads and writes on the machine that runs the tests. The lists are
/// printed so the result can be copied into `docs/ios-port/FEATURES.md`.
@Suite("ImageIO capabilities")
struct ImageIOCapabilityTests {

  @Test("core formats can be written, and the printed lists show the rest")
  func recordsCapabilities() {
    let writable = ImageCodec.encodableTypeIdentifiers
    let readable = ImageCodec.decodableTypeIdentifiers

    print("IMAGEIO writable: \(writable.sorted().joined(separator: " "))")
    print("IMAGEIO readable: \(readable.sorted().joined(separator: " "))")

    #expect(writable.contains("public.jpeg"))
    #expect(writable.contains("public.png"))
    #expect(writable.contains("public.tiff"))
    #expect(writable.contains("com.compuserve.gif"))
    #expect(readable.contains("public.jpeg"))
    #expect(readable.contains("public.png"))
  }
}
