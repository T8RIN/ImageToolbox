import CoreGraphics
import Foundation

/// A finished picture with the encoded bytes that its share button exports.
struct EncodedPicture: Sendable {
  let image: CGImage
  let data: Data
  let fileName: String
}
