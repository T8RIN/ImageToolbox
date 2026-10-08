import CoreTransferable
import Foundation
import UniformTypeIdentifiers

/// A file that the share sheet can export. It is written to a temporary folder on demand.
struct ExportFile: Transferable {
  let data: Data
  let fileName: String

  static var transferRepresentation: some TransferRepresentation {
    FileRepresentation(exportedContentType: .data) { file in
      let url = FileManager.default.temporaryDirectory.appendingPathComponent(file.fileName)
      try file.data.write(to: url, options: .atomic)
      return SentTransferredFile(url)
    }
  }
}
