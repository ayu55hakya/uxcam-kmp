package com.uxcam.kmp

import android.os.SystemClock
import android.view.View
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import com.uxcam.UXCamKt

private const val PUSH_INTERVAL_MS = 100L

actual fun Modifier.uxcamOcclude(identifier: String, isInDialog: Boolean): Modifier = composed {
    val view = LocalView.current
    val reporter = remember(identifier, view, isInDialog) {
        ThrottledReporter(identifier, view, isInDialog)
    }
    onGloballyPositioned { reporter.onPositioned(it) }
}

// Plain holder, not MutableState: written on every layout pass, never read in composition.
// The capture draws the position recorded at registration time, so a node that moves must
// re-register; this class decides when a move actually becomes a native call.
private class ThrottledReporter(
    private val identifier: String,
    private val view: View,
    private val isInDialog: Boolean,
) : RememberObserver {

    // Acquired on successful remember, released on forget. Abandoned compositions
    // (e.g. cancelled lazy-layout prefetch) never acquire, so slots can't leak.
    private var slotId: String? = null
    private var coordinates: LayoutCoordinates? = null
    private var reportedBounds: androidx.compose.ui.geometry.Rect? = null
    private var lastPushUptimeMs = 0L
    private var pendingPush: Runnable? = null

    override fun onRemembered() {
        slotId = OcclusionSlots.acquire(identifier)
    }

    override fun onForgotten() {
        cancelPending()
        slotId?.let(OcclusionSlots::release)
        slotId = null
    }

    override fun onAbandoned() = Unit

    // Main thread only, like all Compose layout callbacks.
    fun onPositioned(latest: LayoutCoordinates) {
        coordinates = latest
        val bounds = latest.boundsInWindow()
        if (bounds == reportedBounds) return
        reportedBounds = bounds
        val sinceLastPush = SystemClock.uptimeMillis() - lastPushUptimeMs
        if (sinceLastPush >= PUSH_INTERVAL_MS) push() else schedulePush(PUSH_INTERVAL_MS - sinceLastPush)
    }

    private fun schedulePush(delayMs: Long) {
        if (pendingPush != null) return
        val runnable = Runnable {
            pendingPush = null
            push()
        }
        pendingPush = runnable
        view.postDelayed(runnable, delayMs)
    }

    private fun push() {
        val nativeId = slotId ?: return
        lastPushUptimeMs = SystemClock.uptimeMillis()
        val latest = coordinates?.takeIf { it.isAttached } ?: return
        UXCamKt.occludeSensitiveComposable(nativeId, view, latest, isInDialog)
    }

    private fun cancelPending() {
        pendingPush?.let(view::removeCallbacks)
        pendingPush = null
    }
}

/**
 * Maps user identifiers to unique native identities ("email#0", "email#1", …). The native
 * repository replaces occlusions by identity, so two live nodes sharing an identifier (one
 * per lazy-list row is common) would otherwise fight over one entry and only one would be
 * occluded. Freed slots are reused so the native list stays bounded by peak concurrency —
 * the SDK only prunes entries whose host View dies, and Compose's single host view never
 * does. Main-thread only, like the composition it serves.
 */
private object OcclusionSlots {
    private val freeSlots = HashMap<String, ArrayDeque<Int>>()
    private val nextSlot = HashMap<String, Int>()

    fun acquire(identifier: String): String {
        val slot = freeSlots[identifier]?.removeFirstOrNull()
            ?: (nextSlot[identifier] ?: 0).also { nextSlot[identifier] = it + 1 }
        return "$identifier#$slot"
    }

    fun release(slotId: String) {
        val sep = slotId.lastIndexOf('#')
        freeSlots.getOrPut(slotId.take(sep)) { ArrayDeque() }
            .addLast(slotId.substring(sep + 1).toInt())
    }
}