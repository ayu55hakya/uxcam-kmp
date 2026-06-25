// swift-tools-version:5.9
import PackageDescription

// BEGIN KMMBRIDGE VARIABLES BLOCK (do not edit)
let remoteKotlinUrl = "https://www.example.com/"
let remoteKotlinChecksum = "0000000000000000000000000000000000000000000000000000000000000000"
let packageName = "UXCamKMP"
// END KMMBRIDGE BLOCK

// Custom Package.swift (KMMBridge spm(useCustomPackageFile = true)): KMMBridge only rewrites the
// url/checksum inside the marker block above on each publish. Everything below is hand-maintained.
//
// The KMP framework (UXCamKMP) is delivered as a prebuilt binaryTarget. binaryTargets cannot
// declare package dependencies, so the native UXCam iOS SDK is attached via a sibling
// `UXCamKMPDeps` target. Both targets are vended by the `UXCamKMP` library product, so a consumer
// adds ONE package, writes `import UXCamKMP`, and SPM resolves + links UXCam transitively.
let package = Package(
    name: packageName,
    platforms: [
        .iOS(.v12)
    ],
    products: [
        .library(
            name: packageName,
            targets: [packageName, "UXCamKMPDeps"]
        )
    ],
    dependencies: [
        // Native iOS UXCam SDK. Pinned to the exact version the wrapper's cinterop was built
        // against (the `pod("UXCam") 3.8.3` in uxcam/build.gradle.kts) to keep the ABI in sync.
        // NOTE: confirm "UXCam" is the library product name vended by uxcam-ios-sdk's Package.swift.
        .package(url: "https://github.com/uxcam/uxcam-ios-sdk", exact: "3.8.3")
    ],
    targets: [
        .binaryTarget(
            name: packageName,
            url: remoteKotlinUrl,
            checksum: remoteKotlinChecksum
        ),
        .target(
            name: "UXCamKMPDeps",
            dependencies: [
                .product(name: "UXCam", package: "uxcam-ios-sdk")
            ],
            path: "Sources/UXCamKMPDeps"
        )
    ]
)
