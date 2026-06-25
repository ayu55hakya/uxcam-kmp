// This target exists solely to attach the native UXCam SPM dependency to the UXCamKMP package.
// SwiftPM binaryTargets cannot declare package dependencies, so this sibling target carries the
// dependency on uxcam-ios-sdk; because it is vended by the UXCamKMP library product, linking the
// product pulls in (and links) the native UXCam framework that the KMP binary needs at runtime.
import UXCam
