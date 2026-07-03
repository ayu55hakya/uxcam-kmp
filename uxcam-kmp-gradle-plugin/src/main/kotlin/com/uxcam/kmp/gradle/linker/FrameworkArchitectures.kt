package com.uxcam.kmp.gradle.linker

/**
 * Maps a Kotlin/Native Apple target name to the slice directory inside `UXCam.xcframework`.
 *
 * UXCam's iOS SDK ships device + simulator iOS slices only; the simulator slice is a fat binary
 * holding both `arm64` (Apple-silicon Macs) and `x86_64` (Intel Macs). Non-iOS Apple targets are
 * unsupported and return `null` so the linker can skip them with a clear warning rather than
 * pointing `-F` at a directory that doesn't exist.
 */
internal object FrameworkArchitectures {

    private val SLICE_BY_TARGET = mapOf(
        "iosArm64" to "ios-arm64",
        "iosSimulatorArm64" to "ios-arm64_x86_64-simulator",
        "iosX64" to "ios-arm64_x86_64-simulator",
    )

    // The single Apple arch a Kotlin/Native link for that target produces — what
    // [StaticFrameworkPrelinker] must thin the (possibly fat) slice archive down to.
    private val ARCH_BY_TARGET = mapOf(
        "iosArm64" to "arm64",
        "iosSimulatorArm64" to "arm64",
        "iosX64" to "x86_64",
    )

    // `ld` platform name (its `-platform_version` argument) for each target. `ld -r` refuses to
    // combine the SDK's objects without one, because not every archive member carries a build
    // version load command.
    private val LD_PLATFORM_BY_TARGET = mapOf(
        "iosArm64" to "ios",
        "iosSimulatorArm64" to "ios-simulator",
        "iosX64" to "ios-simulator",
    )

    /** Slice directory name for [targetName], or `null` if UXCam ships no slice for it. */
    fun sliceFor(targetName: String): String? = SLICE_BY_TARGET[targetName]

    /** Apple arch name for [targetName]'s link output, or `null` if unsupported. */
    fun archFor(targetName: String): String? = ARCH_BY_TARGET[targetName]

    /** `ld -platform_version` platform name for [targetName], or `null` if unsupported. */
    fun ldPlatformFor(targetName: String): String? = LD_PLATFORM_BY_TARGET[targetName]

    /** Apple target names UXCam supports (used for messaging). */
    val supportedTargets: Set<String> get() = SLICE_BY_TARGET.keys
}
