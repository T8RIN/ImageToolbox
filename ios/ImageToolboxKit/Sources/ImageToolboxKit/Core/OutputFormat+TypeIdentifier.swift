import Foundation

extension OutputFormat {
  /// Maps an ImageIO type identifier back to a supported format. Returns nil for other types.
  public init?(typeIdentifier: String) {
    guard let match = OutputFormat.allCases.first(where: { $0.typeIdentifier == typeIdentifier })
    else { return nil }
    self = match
  }
}
