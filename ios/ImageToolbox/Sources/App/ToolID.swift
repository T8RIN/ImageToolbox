import SwiftUI

/// Every tool in the app. The raw value is also the accessibility identifier and the usage key.
enum ToolID: String, CaseIterable, Identifiable, Hashable {
  // Phase 1: simple tools
  case base64
  case deleteExif
  case editExif
  case rotateFlip
  case loadFromURL
  case colorPicker
  case colorLibrary
  case perlinNoise
  case asciiArt
  case imagePreview
  case snowfall
  case libraries
  case help
  case appLogs
  case usageStatistics
  case easterEgg
  // Phase 2: medium tools
  case resizeConvert
  case weightResize
  case limitsResize
  case crop
  case imageCutting
  case imageSplitting
  case imageStitch
  case imageStacking
  case compare
  case watermarking
  case singleEdit
  case batchRename
  case duplicateFinder
  case checksumTools
  case colorTools
  case gifTools
  case apngTools
  case webpTools
  case documentScanner
  case scanQRCode
  case wallpapersExport
  case audioCoverExtractor
  case screenshotFraming
  case codePreview
  case gradientMaker
  case paletteToPDF
  case photomosaic

  var id: Self { self }

  var title: LocalizedStringResource {
    switch self {
    case .base64: "Base64"
    case .deleteExif: "Delete EXIF"
    case .editExif: "Edit EXIF"
    case .rotateFlip: "Rotate and flip"
    case .loadFromURL: "Load image from URL"
    case .colorPicker: "Color picker"
    case .colorLibrary: "Color library"
    case .perlinNoise: "Perlin noise"
    case .asciiArt: "ASCII art"
    case .imagePreview: "Image preview"
    case .snowfall: "Snowfall"
    case .libraries: "Libraries"
    case .help: "Help"
    case .appLogs: "App logs"
    case .usageStatistics: "Usage statistics"
    case .easterEgg: "Easter egg"
    case .resizeConvert: "Resize and convert"
    case .weightResize: "Resize to file size"
    case .limitsResize: "Resize within limits"
    case .crop: "Crop"
    case .imageCutting: "Image cutting"
    case .imageSplitting: "Image splitting"
    case .imageStitch: "Image stitching"
    case .imageStacking: "Image stacking"
    case .compare: "Compare images"
    case .watermarking: "Watermarks"
    case .singleEdit: "Single edit and export profiles"
    case .batchRename: "Batch rename"
    case .duplicateFinder: "Duplicate finder"
    case .checksumTools: "Checksums"
    case .colorTools: "Color tools"
    case .gifTools: "GIF tools"
    case .apngTools: "APNG tools"
    case .webpTools: "WebP tools"
    case .documentScanner: "Document scanner"
    case .scanQRCode: "Scan QR and barcodes"
    case .wallpapersExport: "Wallpapers export"
    case .audioCoverExtractor: "Audio cover extractor"
    case .screenshotFraming: "Screenshot framing"
    case .codePreview: "Code preview"
    case .gradientMaker: "Gradients and mesh"
    case .paletteToPDF: "Palette to PDF"
    case .photomosaic: "Photomosaic"
    }
  }

  var systemImage: String {
    switch self {
    case .base64: "textformat"
    case .deleteExif: "trash"
    case .editExif: "pencil"
    case .rotateFlip: "rotate.right"
    case .loadFromURL: "link"
    case .colorPicker: "eyedropper"
    case .colorLibrary: "swatchpalette"
    case .perlinNoise: "waveform"
    case .asciiArt: "text.alignleft"
    case .imagePreview: "photo"
    case .snowfall: "snowflake"
    case .libraries: "doc.text"
    case .help: "questionmark.circle"
    case .appLogs: "list.bullet.rectangle"
    case .usageStatistics: "chart.bar"
    case .easterEgg: "sparkles"
    case .resizeConvert: "arrow.up.left.and.arrow.down.right"
    case .weightResize: "scalemass"
    case .limitsResize: "rectangle.compress.vertical"
    case .crop: "crop"
    case .imageCutting: "scissors"
    case .imageSplitting: "square.split.2x1"
    case .imageStitch: "rectangle.split.3x1"
    case .imageStacking: "square.stack.3d.up"
    case .compare: "rectangle.on.rectangle"
    case .watermarking: "signature"
    case .singleEdit: "slider.horizontal.3"
    case .batchRename: "textformat.abc"
    case .duplicateFinder: "doc.on.doc"
    case .checksumTools: "number"
    case .colorTools: "paintpalette"
    case .gifTools: "film"
    case .apngTools: "film.stack"
    case .webpTools: "photo.badge.arrow.down"
    case .documentScanner: "doc.viewfinder"
    case .scanQRCode: "qrcode.viewfinder"
    case .wallpapersExport: "iphone"
    case .audioCoverExtractor: "music.note"
    case .screenshotFraming: "rectangle.inset.filled"
    case .codePreview: "chevron.left.forwardslash.chevron.right"
    case .gradientMaker: "paintbrush"
    case .paletteToPDF: "doc.richtext"
    case .photomosaic: "square.grid.3x3"
    }
  }
}
