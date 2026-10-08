import Foundation

public enum RemoteImageError: Error, Equatable, Sendable {
  case invalidURL
  case unsupportedScheme
  case badStatus(Int)
  case tooLarge
  case notAnImage
}

/// Downloads an image over HTTP(S), with a size cap and a check that the body decodes.
public struct RemoteImageLoader: Sendable {
  public var session: URLSession
  public var maxBytes: Int

  public init(session: URLSession = .shared, maxBytes: Int = 50 * 1024 * 1024) {
    self.session = session
    self.maxBytes = maxBytes
  }

  /// Accepts `https://…`, `http://…`, or a bare host, which gets `https://` in front.
  public static func url(from text: String) -> URL? {
    let trimmed = text.trimmingCharacters(in: .whitespacesAndNewlines)
    guard !trimmed.isEmpty else { return nil }
    let candidate = trimmed.contains("://") ? trimmed : "https://" + trimmed
    guard let url = URL(string: candidate), let scheme = url.scheme?.lowercased(),
      scheme == "http" || scheme == "https", url.host != nil
    else { return nil }
    return url
  }

  public func load(_ url: URL) async throws -> Data {
    guard let scheme = url.scheme?.lowercased(), scheme == "http" || scheme == "https" else {
      throw RemoteImageError.unsupportedScheme
    }

    let (data, response) = try await session.data(from: url)
    if let http = response as? HTTPURLResponse, !(200..<300).contains(http.statusCode) {
      throw RemoteImageError.badStatus(http.statusCode)
    }
    guard data.count <= maxBytes else { throw RemoteImageError.tooLarge }
    guard ImageCodec.decode(data) != nil else { throw RemoteImageError.notAnImage }
    return data
  }
}
