@file:OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)

package com.uxcam.kmp

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import cocoapods.UXCam.UXCam as NativeUXCam
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIView
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * iOS implementation. Compose renders the whole hierarchy into a single Metal-backed view,
 * so the native SDK's per-UIView occlusion can't see individual composables. Instead each
 * occluded node's window rect is forwarded through the SDK's hybrid-wrapper rect API
 * (`occludeRectsOnNextFrame:withIdentity:`), whose per-identity list REPLACES the previous
 * push and persists until the next one — so rects only need pushing when layout changes.
 */
actual fun Modifier.uxcamOcclude(identifier: String, isInDialog: Boolean): Modifier = composed {
    val hostView = LocalUIViewController.current.view
    val density = LocalDensity.current.density
    val entry = remember { OccludedEntry() }
    entry.hostView = hostView
    entry.density = density
    DisposableEffect(entry) {
        onDispose { ComposeOcclusionRegistry.remove(entry) }
    }
    onGloballyPositioned { coordinates ->
        entry.coordinates = coordinates
        ComposeOcclusionRegistry.update(entry)
    }
}

internal class OccludedEntry {
    var coordinates: LayoutCoordinates? = null
    var hostView: UIView? = null
    var density: Float = 1f
}

/**
 * Aggregates the window rects of every currently-occluded composable and pushes them to the
 * native SDK under one shared identity. Pushes are coalesced per run-loop turn (N nodes
 * repositioning in one layout pass produce one native call) and skipped when nothing moved,
 * so a static screen costs nothing after its first push.
 *
 * Main-thread only: Compose layout callbacks and the SDK's frame capture both run there,
 * and the capture reads these rects in the same run-loop turn as the screenshot.
 */
internal object ComposeOcclusionRegistry {

    // Single native identity for all Compose rects: per-identity pushes replace the prior
    // list, so aggregating under one identity makes every push self-cleaning for moved or
    // disposed nodes. User-supplied identifiers may repeat across nodes (e.g. one per list
    // row), so they can't serve as native identities.
    private const val NATIVE_IDENTITY = "uxcam-kmp-compose"

    // The native API ignores empty lists and has no public remove-by-identity yet. A single
    // zero-size rect passes the SDK's input guard but is dropped during parsing, leaving the
    // identity mapped to an empty list — i.e. it clears our rects.
    private val CLEAR_PAYLOAD = listOf(listOf(0.0, 0.0, 0.0, 0.0))

    private val entries = linkedSetOf<OccludedEntry>()
    private var lastPushed: List<List<Double>>? = null
    private var flushScheduled = false

    fun update(entry: OccludedEntry) {
        entries.add(entry)
        scheduleFlush()
    }

    fun remove(entry: OccludedEntry) {
        entries.remove(entry)
        scheduleFlush()
    }

    private fun scheduleFlush() {
        if (flushScheduled) return
        flushScheduled = true
        dispatch_async(dispatch_get_main_queue()) {
            flushScheduled = false
            flush()
        }
    }

    private fun flush() {
        val rects = entries.mapNotNull { it.windowRectPoints() }
        if (rects == lastPushed) return
        lastPushed = rects
        NativeUXCam.occludeRectsOnNextFrame(rects.ifEmpty { CLEAR_PAYLOAD }, withIdentity = NATIVE_IDENTITY)
    }
}

/**
 * The entry's current window-space rect in iOS points: compose px → host-view points →
 * window coordinates. Null when the node is detached or scrolled fully out of the viewport
 * ([boundsInWindow] clips to the visible window, so off-screen nodes collapse to zero size).
 */
private fun OccludedEntry.windowRectPoints(): List<Double>? {
    val coords = coordinates?.takeIf { it.isAttached } ?: return null
    val view = hostView ?: return null
    val bounds = coords.boundsInWindow()
    if (bounds.width <= 0f || bounds.height <= 0f) return null
    val d = density.toDouble()
    val local = CGRectMake(
        bounds.left / d, bounds.top / d,
        bounds.width / d, bounds.height / d,
    )
    return view.convertRect(local, toView = null).useContents {
        listOf(origin.x, origin.y, size.width, size.height)
    }
}
