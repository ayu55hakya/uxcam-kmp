plugins {
    // Loaded here once, applied in the :uxcam module — avoids classloading them twice.
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
