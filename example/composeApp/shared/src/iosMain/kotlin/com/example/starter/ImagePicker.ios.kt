@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("DEPRECATION") // keyWindow — deprecated since iOS 13 but the simplest root accessor.

package com.example.starter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import com.uxcam.kmp.UXCam

/**
 * Presents the system photo picker (PHPickerViewController), which runs out-of-process — so
 * iOS treats it as "another app" and UXCam's short-break handling fires. Mirrors the native
 * sample's SwiftUI `.photosPicker`. [onResult] fires when the picker is dismissed, picked or not.
 */
@Composable
actual fun rememberImagePicker(onResult: () -> Unit): () -> Unit {
    // Retain the delegate for the composable's lifetime; PHPickerViewController holds its
    // delegate weakly, so a local would be collected before the user finishes picking.
    val delegate = remember { ImagePickerDelegate() }
    delegate.onResult = onResult
    return {
        // Compose-on-iOS is captured via a pixel snapshot, not the schematic view-hierarchy
        // walk, so the picker's out-of-process content is NOT auto-blacked the way it is in
        // native UIKit. Pause recording explicitly while the picker is up.
        UXCam.pauseScreenRecording()
        val config = PHPickerConfiguration().apply { setSelectionLimit(1) }
        val picker = PHPickerViewController(configuration = config)
        picker.delegate = delegate
        topmostViewController()?.presentViewController(picker, animated = true, completion = null)
    }
}

private class ImagePickerDelegate : NSObject(), PHPickerViewControllerDelegateProtocol {
    var onResult: () -> Unit = {}

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        // Fires whether the user picked an image or tapped Cancel — either way they've returned.
        picker.dismissViewControllerAnimated(flag = true, completion = null)
        UXCam.resumeScreenRecording()
        onResult()
    }
}

private fun topmostViewController(): UIViewController? {
    var top = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) {
        top = top.presentedViewController
    }
    return top
}
