/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.cropper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

private const val MaxHistorySize = 50

@Stable
class ImageCropperState {
    class SavedState internal constructor(
        internal val snapshot: ImageCropSnapshot
    )

    var canUndo: Boolean by mutableStateOf(false)
        private set

    var canRedo: Boolean by mutableStateOf(false)
        private set

    internal var resetVersion: Int by mutableIntStateOf(0)
        private set

    private var imageKey: Any? = null
    private var attachmentKey: Any? = null
    private var layoutKey: Any? = null
    private var configurationKey: Any? = null
    private var captureSnapshot: (() -> ImageCropSnapshot?)? = null
    private var captureSavedStateSnapshot: (() -> ImageCropSnapshot?)? = null
    private var restoreSnapshot: ((ImageCropSnapshot) -> Unit)? = null
    private var pendingSnapshot: ImageCropSnapshot? = null
    private var pendingLayoutSnapshot: ImageCropSnapshot? = null
    private var currentSnapshot: ImageCropSnapshot? = null
    private var restoreCurrentSnapshotOnCropperReady = false
    private var skipSnapshotSyncForAttachment: Any? = null
    private var restoredAttachmentKey: Any? = null
    private var snapshotToRestoreOnImageChange: ImageCropSnapshot? = null
    private var restoreGeneration = 0
    internal var isRestoring by mutableStateOf(false)
        private set
    private val undoHistory = ArrayDeque<ImageCropSnapshot>()
    private val redoHistory = ArrayDeque<ImageCropSnapshot>()

    fun undo() {
        endTransformation()
        val current = currentSnapshot ?: captureSnapshot?.invoke() ?: return
        val previous = undoHistory.removeLastOrNull() ?: return

        redoHistory.addLast(current)
        redoHistory.trimToMaxSize()
        currentSnapshot = previous
        restoreSnapshot?.invoke(previous)
        updateAvailability()
    }

    fun redo() {
        endTransformation()
        val current = currentSnapshot ?: captureSnapshot?.invoke() ?: return
        val next = redoHistory.removeLastOrNull() ?: return

        undoHistory.addLast(current)
        undoHistory.trimToMaxSize()
        currentSnapshot = next
        restoreSnapshot?.invoke(next)
        updateAvailability()
    }

    fun beginTransformation() {
        snapshotToRestoreOnImageChange = null
        restoreCurrentSnapshotOnCropperReady = false
        if (restoredAttachmentKey === attachmentKey) {
            restoredAttachmentKey = null
        }
        if (pendingSnapshot == null) {
            pendingSnapshot = captureSnapshot?.invoke()
        }
    }

    fun endTransformation(): Boolean {
        val before = pendingSnapshot ?: return false
        pendingSnapshot = null
        val after = captureSnapshot?.invoke() ?: return false
        currentSnapshot = after

        if (before != after) {
            undoHistory.addLast(before)
            undoHistory.trimToMaxSize()
            redoHistory.clear()
            updateAvailability()
            return true
        }
        return false
    }

    fun discardPendingTransformation() {
        pendingSnapshot = null
    }

    fun saveState(): SavedState? = (captureSavedStateSnapshot ?: captureSnapshot)
        ?.invoke()
        ?.copy(preferExactTransform = true)
        ?.let(::SavedState)

    fun restoreStateOnNextImageChange(savedState: SavedState) {
        snapshotToRestoreOnImageChange = savedState.snapshot
    }

    fun prepareForReattachment(attachmentKey: Any? = null) {
        if (attachmentKey != null && this.attachmentKey !== attachmentKey) return
        if (restoreCurrentSnapshotOnCropperReady) return

        if (pendingSnapshot != null || currentSnapshot == null) {
            captureSnapshot?.invoke()?.let { currentSnapshot = it }
        }
        restoreCurrentSnapshotOnCropperReady = currentSnapshot != null
    }

    fun finishReattachment() {
        restoreCurrentSnapshotOnCropperReady = false
    }

    internal fun attach(
        attachmentKey: Any,
        imageKey: Any?,
        layoutKey: Any? = null,
        configurationKey: Any? = null,
        captureSnapshot: () -> ImageCropSnapshot?,
        restoreSnapshot: (ImageCropSnapshot) -> Unit,
        captureSavedStateSnapshot: (() -> ImageCropSnapshot?)? = null
    ) {
        val imageChanged = this.imageKey != imageKey
        val attachmentChanged = this.attachmentKey != null &&
                this.attachmentKey !== attachmentKey
        val layoutChanged = !imageChanged &&
                this.layoutKey != null &&
                layoutKey != null &&
                this.layoutKey != layoutKey
        val canRestorePreviousLayout = !imageChanged &&
                this.configurationKey == configurationKey &&
                (attachmentChanged || layoutChanged)
        if (canRestorePreviousLayout) {
            pendingLayoutSnapshot?.let { currentSnapshot = it }
            // A pending transformation belongs to the outgoing CropState, so capture its live
            // geometry. Otherwise keep the committed snapshot: the old layout can already be in
            // a temporary measure state while the replacement CropState is being attached.
            if (pendingLayoutSnapshot == null && (pendingSnapshot != null || currentSnapshot == null)) {
                this.captureSnapshot?.invoke()?.let { currentSnapshot = it }
            }
            restoreCurrentSnapshotOnCropperReady = currentSnapshot != null
            isRestoring = restoreCurrentSnapshotOnCropperReady
            pendingSnapshot = null
            pendingLayoutSnapshot = null
        }
        val configurationChanged = !imageChanged &&
                this.configurationKey != null &&
                this.configurationKey != configurationKey
        if (imageChanged) {
            val snapshotToRestore = snapshotToRestoreOnImageChange
            this.imageKey = imageKey
            pendingSnapshot = null
            pendingLayoutSnapshot = null
            currentSnapshot = snapshotToRestore
            restoreCurrentSnapshotOnCropperReady = snapshotToRestore != null
            skipSnapshotSyncForAttachment = null
            restoredAttachmentKey = null
            isRestoring = restoreCurrentSnapshotOnCropperReady
            undoHistory.clear()
            redoHistory.clear()
            updateAvailability()
        } else if (configurationChanged) {
            pendingSnapshot = null
            pendingLayoutSnapshot = null
            currentSnapshot = null
            restoreCurrentSnapshotOnCropperReady = false
            skipSnapshotSyncForAttachment = null
            restoredAttachmentKey = null
            isRestoring = false
        }
        this.attachmentKey = attachmentKey
        this.layoutKey = layoutKey
        this.configurationKey = configurationKey
        this.captureSnapshot = captureSnapshot
        this.captureSavedStateSnapshot = captureSavedStateSnapshot
        this.restoreSnapshot = restoreSnapshot
    }

    internal fun detach(attachmentKey: Any) {
        if (this.attachmentKey !== attachmentKey) return
        if (skipSnapshotSyncForAttachment === attachmentKey) {
            skipSnapshotSyncForAttachment = null
        }
        this.attachmentKey = null
        captureSnapshot = null
        captureSavedStateSnapshot = null
        restoreSnapshot = null
        pendingSnapshot = null
    }

    /**
     * Stores geometry from the outgoing CropState before Compose replaces it for a new viewport.
     * Unlike a callback captured during attachment, this state still uses the old immutable
     * container and draw-area sizes, so it cannot contain a partially measured frame.
     */
    internal fun captureLayoutSnapshot(
        attachmentKey: Any?,
        snapshot: ImageCropSnapshot?
    ) {
        if (attachmentKey != null && restoredAttachmentKey === attachmentKey) return
        if (snapshot != null) {
            pendingLayoutSnapshot = snapshot
        }
    }

    internal fun onCropperReady() {
        if (restoreCurrentSnapshotOnCropperReady) {
            currentSnapshot?.let { restoreSnapshot?.invoke(it) }
            restoreCurrentSnapshotOnCropperReady = false
            skipSnapshotSyncForAttachment = attachmentKey
            restoredAttachmentKey = attachmentKey
        } else if (currentSnapshot == null) {
            currentSnapshot = captureSnapshot?.invoke()
        }
    }

    /**
     * Refreshes the reattachment snapshot after a non-gesture geometry change, such as choosing
     * another aspect ratio. Without this, a later configuration change restores the frame that
     * existed before the aspect-ratio change.
     */
    internal fun syncSnapshot() {
        if (skipSnapshotSyncForAttachment === attachmentKey) {
            skipSnapshotSyncForAttachment = null
            return
        }
        if (pendingSnapshot == null && !restoreCurrentSnapshotOnCropperReady) {
            currentSnapshot = captureSnapshot?.invoke() ?: currentSnapshot
        }
    }

    internal fun beginRestore(): Int {
        restoreGeneration++
        isRestoring = true
        return restoreGeneration
    }

    internal fun restoreCompleted(generation: Int) {
        if (restoreGeneration == generation) {
            isRestoring = false
        }
    }

    fun clearHistory() {
        pendingSnapshot = null
        pendingLayoutSnapshot = null
        currentSnapshot = captureSnapshot?.invoke()
        snapshotToRestoreOnImageChange = null
        restoreCurrentSnapshotOnCropperReady = false
        skipSnapshotSyncForAttachment = null
        restoredAttachmentKey = null
        isRestoring = false
        undoHistory.clear()
        redoHistory.clear()
        resetVersion++
        updateAvailability()
    }

    fun reset() {
        attachmentKey = null
        layoutKey = null
        configurationKey = null
        pendingSnapshot = null
        pendingLayoutSnapshot = null
        currentSnapshot = null
        snapshotToRestoreOnImageChange = null
        restoreCurrentSnapshotOnCropperReady = false
        skipSnapshotSyncForAttachment = null
        restoredAttachmentKey = null
        isRestoring = false
        undoHistory.clear()
        redoHistory.clear()
        updateAvailability()
    }

    internal fun needsRestoreForLayout(
        attachmentKey: Any,
        imageKey: Any?,
        layoutKey: Any?,
        configurationKey: Any?
    ): Boolean {
        if (this.imageKey != imageKey || this.configurationKey != configurationKey) return false
        if (currentSnapshot == null) return false

        val attachmentChanged = this.attachmentKey != null && this.attachmentKey !== attachmentKey
        val layoutChanged =
            this.layoutKey != null && layoutKey != null && this.layoutKey != layoutKey
        return restoreCurrentSnapshotOnCropperReady || attachmentChanged || layoutChanged
    }

    internal fun preferredContainerSize(
        imageKey: Any?,
        measuredSize: IntSize
    ): IntSize {
        // An adaptive parent can report a taller max constraint after an orientation round trip
        // while the visible viewport keeps its old size. Reuse that viewport only when its width
        // still matches; a real orientation change must use the newly measured dimensions.
        val snapshotSize = currentSnapshot?.containerSize ?: return measuredSize
        if (this.imageKey != imageKey || snapshotSize.height <= 0) return measuredSize

        return if (snapshotSize.width == measuredSize.width) snapshotSize else measuredSize
    }

    private fun updateAvailability() {
        canUndo = undoHistory.isNotEmpty()
        canRedo = redoHistory.isNotEmpty()
    }

    private fun ArrayDeque<ImageCropSnapshot>.trimToMaxSize() {
        while (size > MaxHistorySize) removeFirst()
    }
}

@Composable
fun rememberImageCropperState(): ImageCropperState = remember {
    ImageCropperState()
}

internal data class ImageCropSnapshot(
    val normalizedCropRect: Rect,
    val normalizedOverlayCenter: Offset = Offset(0.5f, 0.5f),
    val overlaySizeFraction: Float = 0.8f,
    val containerSize: IntSize = IntSize.Zero,
    val rotation: Float,
    val imageSize: IntSize = IntSize.Zero,
    val drawAreaSize: IntSize = IntSize.Zero,
    val exactOverlayRect: Rect? = null,
    val exactZoom: Float? = null,
    val exactPan: Offset? = null,
    val preferExactTransform: Boolean = false
)
