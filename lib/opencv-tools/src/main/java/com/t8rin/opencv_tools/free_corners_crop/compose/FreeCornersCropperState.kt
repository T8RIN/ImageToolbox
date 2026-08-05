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

package com.t8rin.opencv_tools.free_corners_crop.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

private const val MaxHistorySize = 50

@Stable
class FreeCornersCropperState {
    class SavedState internal constructor(
        internal val snapshot: FreeCornersCropSnapshot
    )

    var canUndo: Boolean by mutableStateOf(false)
        private set

    var canRedo: Boolean by mutableStateOf(false)
        private set

    private var imageKey: Any? = null
    private var attachmentKey: Any? = null
    private var captureSnapshot: (() -> FreeCornersCropSnapshot?)? = null
    private var restoreSnapshot: ((FreeCornersCropSnapshot) -> Unit)? = null
    private var pendingSnapshot: FreeCornersCropSnapshot? = null
    private var currentSnapshot: FreeCornersCropSnapshot? = null
    private var restoreCurrentSnapshotOnCropperReady = false
    private var snapshotToRestoreOnImageChange: FreeCornersCropSnapshot? = null
    private val undoHistory = ArrayDeque<FreeCornersCropSnapshot>()
    private val redoHistory = ArrayDeque<FreeCornersCropSnapshot>()

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

    fun saveState(): SavedState? = captureSnapshot?.invoke()?.let(::SavedState)

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

    internal fun snapshotForLayoutChange(): FreeCornersCropSnapshot? {
        if (pendingSnapshot != null) {
            captureSnapshot?.invoke()?.let { currentSnapshot = it }
        }
        if (currentSnapshot == null) {
            currentSnapshot = captureSnapshot?.invoke()
        }
        return currentSnapshot
    }

    internal fun attach(
        attachmentKey: Any,
        imageKey: Any?,
        captureSnapshot: () -> FreeCornersCropSnapshot?,
        restoreSnapshot: (FreeCornersCropSnapshot) -> Unit
    ) {
        val imageChanged = this.imageKey != imageKey
        if (!imageChanged && this.attachmentKey != null && this.attachmentKey !== attachmentKey) {
            if (pendingSnapshot != null || currentSnapshot == null) {
                this.captureSnapshot?.invoke()?.let { currentSnapshot = it }
            }
            restoreCurrentSnapshotOnCropperReady = currentSnapshot != null
            pendingSnapshot = null
        }
        if (imageChanged) {
            val snapshotToRestore = snapshotToRestoreOnImageChange
            this.imageKey = imageKey
            pendingSnapshot = null
            currentSnapshot = snapshotToRestore
            restoreCurrentSnapshotOnCropperReady = snapshotToRestore != null
            undoHistory.clear()
            redoHistory.clear()
            updateAvailability()
        }
        this.attachmentKey = attachmentKey
        this.captureSnapshot = captureSnapshot
        this.restoreSnapshot = restoreSnapshot
    }

    internal fun detach(attachmentKey: Any) {
        if (this.attachmentKey !== attachmentKey) return
        this.attachmentKey = null
        restoreCurrentSnapshotOnCropperReady = currentSnapshot != null
        captureSnapshot = null
        restoreSnapshot = null
        pendingSnapshot = null
    }

    internal fun onCropperReady() {
        if (restoreCurrentSnapshotOnCropperReady) {
            currentSnapshot?.let { restoreSnapshot?.invoke(it) }
            restoreCurrentSnapshotOnCropperReady = false
        } else if (currentSnapshot == null) {
            currentSnapshot = captureSnapshot?.invoke()
        }
    }

    fun clearHistory() {
        pendingSnapshot = null
        currentSnapshot = captureSnapshot?.invoke()
        snapshotToRestoreOnImageChange = null
        restoreCurrentSnapshotOnCropperReady = false
        undoHistory.clear()
        redoHistory.clear()
        updateAvailability()
    }

    fun reset() {
        attachmentKey = null
        pendingSnapshot = null
        currentSnapshot = null
        snapshotToRestoreOnImageChange = null
        restoreCurrentSnapshotOnCropperReady = false
        undoHistory.clear()
        redoHistory.clear()
        updateAvailability()
    }

    private fun updateAvailability() {
        canUndo = undoHistory.isNotEmpty()
        canRedo = redoHistory.isNotEmpty()
    }

    private fun ArrayDeque<FreeCornersCropSnapshot>.trimToMaxSize() {
        while (size > MaxHistorySize) removeFirst()
    }
}

@Composable
fun rememberFreeCornersCropperState(): FreeCornersCropperState = remember {
    FreeCornersCropperState()
}

internal data class FreeCornersCropSnapshot(
    val normalizedPoints: List<Offset>,
    val normalizedViewportCenter: Offset,
    val viewportFillFraction: Float,
    val exactPoints: List<Offset> = emptyList(),
    val exactImageBounds: Rect = Rect.Zero,
    val exactViewportBounds: Rect = Rect.Zero,
    val exactImageScale: Float? = null,
    val exactImageTranslation: Offset? = null
)
