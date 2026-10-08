// swift-tools-version: 6.1

import PackageDescription

// Platform-agnostic image logic shared by the iOS app. No UIKit/SwiftUI here,
// so it builds and tests with `swift test` on macOS without Xcode.
let package = Package(
    name: "ImageToolboxKit",
    platforms: [
        .iOS("26.0"),
        .macOS("15.0"),
    ],
    products: [
        .library(name: "ImageToolboxKit", targets: ["ImageToolboxKit"]),
    ],
    dependencies: [
        // libwebp (BSD-3-Clause). ImageIO on iOS can read WebP but cannot write it.
        .package(url: "https://github.com/SDWebImage/libwebp-Xcode", from: "1.5.0"),
    ],
    targets: [
        .target(
            name: "ImageToolboxKit",
            dependencies: [
                .product(name: "libwebp", package: "libwebp-Xcode"),
            ],
            resources: [.copy("Resources/color_names.json")]
        ),
        .testTarget(
            name: "ImageToolboxKitTests",
            dependencies: ["ImageToolboxKit"]
        ),
    ]
)
