import Foundation

/// Base64 helpers that mirror the Android behaviour in `feature/base64-tools`
/// and `core/domain/.../utils/KotlinUtils.kt`.
///
/// Android reference:
/// - `encode`: `Base64.encodeToString(bytes, DEFAULT | NO_WRAP)`, standard alphabet, padded, no line breaks.
/// - `trimToBase64`: drops all whitespace, then everything up to the first `,`,
///   so `data:image/png;base64,...` is accepted as input.
/// - `isBase64`: `^(?=(.{4})*$)[A-Za-z0-9+/]*={0,2}$`.
public enum Base64Codec {

  private static let alphabet = Set(
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
  )

  public static func encode(_ data: Data) -> String {
    data.base64EncodedString()
  }

  /// Trims whitespace and an optional `data:...,` prefix, then decodes.
  ///
  /// ASSUMPTION: missing `=` padding is accepted (it is re-added here).
  /// Android's decoder behaves this way for `DEFAULT` input; verify on a device.
  /// URL-safe characters (`-`, `_`) are rejected, as on Android.
  public static func decode(_ text: String) -> Data? {
    var payload = trimToBase64(text)
    guard !payload.isEmpty else { return nil }

    switch payload.utf8.count % 4 {
    case 0:
      break
    case 2:
      payload += "=="
    case 3:
      payload += "="
    default:
      return nil
    }

    guard isBase64(payload) else { return nil }
    return Data(base64Encoded: payload)
  }

  /// Port of `trimToBase64`: removes whitespace and everything up to the first comma.
  public static func trimToBase64(_ text: String) -> String {
    let stripped = String(text.filter { !$0.isWhitespace })
    guard let comma = stripped.firstIndex(of: ",") else { return stripped }
    return String(stripped[stripped.index(after: comma)...])
  }

  /// Port of `isBase64`: length is a multiple of 4, standard alphabet, at most two trailing `=`.
  public static func isBase64(_ text: String) -> Bool {
    guard !text.isEmpty, text.utf8.count % 4 == 0 else { return false }

    let padding = text.reversed().prefix(while: { $0 == "=" }).count
    guard padding <= 2 else { return false }

    return text.dropLast(padding).allSatisfy { alphabet.contains($0) }
  }
}
