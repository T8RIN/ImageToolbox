import CoreGraphics
import Foundation

/// A preset target size for social networks, websites and messengers.
public struct ExportProfile: Identifiable, Hashable, Sendable {
  public let group: String
  public let name: String
  /// Exact output size. Nil for profiles that only limit the longest side.
  public let size: CGSize?
  /// Longest side limit used when `size` is nil.
  public let maxSide: Int?

  public var id: String { group + "/" + name }

  public init(group: String, name: String, width: Int, height: Int) {
    self.group = group
    self.name = name
    size = CGSize(width: width, height: height)
    maxSide = nil
  }

  public init(group: String, name: String, maxSide: Int) {
    self.group = group
    self.name = name
    size = nil
    self.maxSide = maxSide
  }

  /// Renders the picture for this profile. Exact sizes are cropped to fill, limits keep the whole picture.
  public func apply(to image: CGImage) -> CGImage? {
    if let size {
      return ImageResize.resize(image, to: size, mode: .fill)
    }
    guard let maxSide else { return nil }
    let target = CGSize(width: maxSide, height: maxSide)
    return ImageResize.resize(image, to: target, mode: .fit)
  }

  /// The 61 built-in profiles, from the project README.
  public static let builtIn: [ExportProfile] = [
    ExportProfile(group: "Web", name: "Optimized", maxSide: 1920),
    ExportProfile(group: "Web", name: "Thumbnail", maxSide: 640),
    ExportProfile(group: "Instagram", name: "Square Post", width: 1080, height: 1080),
    ExportProfile(group: "Instagram", name: "Portrait Post", width: 1080, height: 1440),
    ExportProfile(group: "Instagram", name: "Landscape Post", width: 1080, height: 566),
    ExportProfile(group: "Instagram", name: "Story", width: 1080, height: 1920),
    ExportProfile(group: "Instagram", name: "Reel Cover", width: 1080, height: 1920),
    ExportProfile(group: "Instagram", name: "Profile Picture", width: 320, height: 320),
    ExportProfile(group: "Facebook", name: "Landscape Post", width: 1200, height: 630),
    ExportProfile(group: "Facebook", name: "Square Post", width: 1080, height: 1080),
    ExportProfile(group: "Facebook", name: "Story", width: 1080, height: 1920),
    ExportProfile(group: "Facebook", name: "Page Cover", width: 851, height: 315),
    ExportProfile(group: "Facebook", name: "Event Cover", width: 1920, height: 1005),
    ExportProfile(group: "Facebook", name: "Profile Picture", width: 320, height: 320),
    ExportProfile(group: "X", name: "Landscape Post", width: 1600, height: 900),
    ExportProfile(group: "X", name: "Square Post", width: 1080, height: 1080),
    ExportProfile(group: "X", name: "Portrait Post", width: 1080, height: 1350),
    ExportProfile(group: "X", name: "Header", width: 1500, height: 500),
    ExportProfile(group: "X", name: "Profile Picture", width: 400, height: 400),
    ExportProfile(group: "YouTube", name: "Video Thumbnail", width: 3840, height: 2160),
    ExportProfile(group: "YouTube", name: "Channel Banner", width: 2560, height: 1440),
    ExportProfile(group: "YouTube", name: "Channel Picture", width: 800, height: 800),
    ExportProfile(group: "YouTube", name: "Community Post", width: 1200, height: 1200),
    ExportProfile(group: "TikTok", name: "Portrait Post", width: 1080, height: 1920),
    ExportProfile(group: "TikTok", name: "Landscape Post", width: 1200, height: 628),
    ExportProfile(group: "TikTok", name: "Square Post", width: 640, height: 640),
    ExportProfile(group: "TikTok", name: "Profile Picture", width: 200, height: 200),
    ExportProfile(group: "Threads", name: "Square Post", width: 1080, height: 1080),
    ExportProfile(group: "Threads", name: "Portrait Post", width: 1080, height: 1440),
    ExportProfile(group: "Threads", name: "Landscape Post", width: 1080, height: 566),
    ExportProfile(group: "Bluesky", name: "Landscape Post", width: 1200, height: 675),
    ExportProfile(group: "Bluesky", name: "Square Post", width: 1080, height: 1080),
    ExportProfile(group: "Bluesky", name: "Portrait Post", width: 1080, height: 1350),
    ExportProfile(group: "LinkedIn", name: "Landscape Post", width: 1200, height: 627),
    ExportProfile(group: "LinkedIn", name: "Portrait Post", width: 1080, height: 1350),
    ExportProfile(group: "LinkedIn", name: "Profile Cover", width: 1584, height: 396),
    ExportProfile(group: "LinkedIn", name: "Article Cover", width: 1920, height: 1080),
    ExportProfile(group: "LinkedIn", name: "Company Page Cover", width: 4200, height: 700),
    ExportProfile(group: "LinkedIn", name: "Profile Picture", width: 400, height: 400),
    ExportProfile(group: "Pinterest", name: "Portrait Pin", width: 1000, height: 1500),
    ExportProfile(group: "Pinterest", name: "Square Pin", width: 1000, height: 1000),
    ExportProfile(group: "Pinterest", name: "Full-screen Pin", width: 1080, height: 1920),
    ExportProfile(group: "VK", name: "Community Cover", width: 1590, height: 400),
    ExportProfile(group: "VK", name: "Profile Picture", width: 500, height: 500),
    ExportProfile(group: "Reddit", name: "Community Banner", width: 1080, height: 128),
    ExportProfile(group: "Reddit", name: "Community Icon", width: 300, height: 300),
    ExportProfile(group: "Snapchat", name: "Single Image Ad", width: 720, height: 1280),
    ExportProfile(group: "Snapchat", name: "Story Ad", width: 720, height: 1560),
    ExportProfile(group: "Behance", name: "Project Cover", width: 808, height: 632),
    ExportProfile(group: "Behance", name: "Project Image", maxSide: 1400),
    ExportProfile(group: "Behance", name: "Lightbox Image", maxSide: 2800),
    ExportProfile(group: "GitHub", name: "Repository Social Preview", width: 1280, height: 640),
    ExportProfile(group: "Telegram", name: "Sticker", width: 512, height: 512),
    ExportProfile(group: "Telegram", name: "Custom Emoji", width: 100, height: 100),
    ExportProfile(group: "Discord", name: "Profile Banner", width: 680, height: 240),
    ExportProfile(group: "Discord", name: "Server Banner", width: 960, height: 540),
    ExportProfile(group: "Discord", name: "Invite Splash", width: 1920, height: 1080),
    ExportProfile(group: "Discord", name: "Avatar", width: 512, height: 512),
    ExportProfile(group: "Twitch", name: "Profile Banner", width: 1200, height: 480),
    ExportProfile(group: "Twitch", name: "Video Thumbnail", width: 1280, height: 720),
    ExportProfile(group: "Twitch", name: "Profile Picture", width: 256, height: 256),
  ]
}

/// Phone wallpaper sizes for the wallpaper export tool.
public struct WallpaperPreset: Identifiable, Hashable, Sendable {
  public let name: String
  public let width: Int
  public let height: Int

  public var id: String { name }

  public static let all: [WallpaperPreset] = [
    WallpaperPreset(name: "iPhone 17 Pro Max", width: 1320, height: 2868),
    WallpaperPreset(name: "iPhone 17 Pro", width: 1206, height: 2622),
    WallpaperPreset(name: "iPhone 17", width: 1179, height: 2556),
    WallpaperPreset(name: "iPhone 16e", width: 1170, height: 2532),
    WallpaperPreset(name: "Full HD", width: 1920, height: 1080),
    WallpaperPreset(name: "4K", width: 3840, height: 2160),
  ]
}
