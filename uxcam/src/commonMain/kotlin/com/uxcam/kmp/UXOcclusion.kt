package com.uxcam.kmp

/**
 * Occlusion categories recognised by the native UXCam SDKs. [nativeValue] matches the
 * SDK's `UXOcclusionType` ordinal used on the native side (see the Flutter plugin's
 * `UXOcclusionType`).
 */
enum class OcclusionType(val nativeValue: Int) {
    OccludeAllTextFields(1),
    Overlay(2),
    Blur(3),
    Unknown(4),
    AITextOcclusion(5),
}

/**
 * Blur styles supported by the native SDKs (see [KMPUXCamBlur.blurType]). [id] is the
 * name the native SDK uses to identify the algorithm. Note the iOS SDK only honours a
 * subset (gaussian / box / bokeh); unsupported values fall back to the SDK default.
 */
enum class BlurType(val id: String) {
    Gaussian("gaussianBlur")
}

/**
 * Base model describing a UXCam occlusion rule: *what* to hide (the concrete subtype) and
 * *where* to apply it ([screens] + [excludeMentionedScreens]). Mirrors the native
 * `UXCamOcclusionSetting` hierarchy and the Flutter plugin's `FlutterUXOcclusion`.
 *
 * Subclass via [KMPUXCamOverlay] or [KMPUXCamBlur] and pass an instance to
 * [UXCamKMP.applyOcclusion].
 *
 * @property type the occlusion category, fixed per subtype.
 * @property screens screen names this rule applies to. Empty means "all screens".
 * @property excludeMentionedScreens when true, [screens] is treated as a block-list —
 *   occlude everywhere *except* those screens — instead of an allow-list.
 */
sealed class Occlusion {
    abstract val type: OcclusionType
    abstract val screens: List<String>?
    abstract val excludeMentionedScreens: Boolean
}

/**
 * Hides the matching screens behind a solid-colour overlay.
 *
 * @property color overlay colour packed as `0xAARRGGBB`. Defaults to opaque red
 *   (`0xFFFF0000`), matching the Flutter plugin's `FlutterUXOverlay`.
 * @property hideGestures also suppress gesture capture on the occluded screen(s).
 */
data class KMPUXCamOverlay(
    val color: Int = 0xFFFF0000.toInt(),
    val hideGestures: Boolean = true,
    override val screens: List<String>? = null,
    override val excludeMentionedScreens: Boolean = false,
) : Occlusion() {
    override val type: OcclusionType get() = OcclusionType.Overlay
}

/**
 * Blurs the matching screens.
 *
 * @property blurRadius blur strength; higher is blurrier (native default ~15).
 * @property blurType blur algorithm to use — see [BlurType].
 * @property hideGestures also suppress gesture capture on the occluded screen(s).
 */
data class KMPUXCamBlur(
    val blurRadius: Int = 15,
    val blurType: BlurType = BlurType.Gaussian,
    val hideGestures: Boolean = true,
    override val screens: List<String>? = null,
    override val excludeMentionedScreens: Boolean = false,
) : Occlusion() {
    override val type: OcclusionType get() = OcclusionType.Blur
}
