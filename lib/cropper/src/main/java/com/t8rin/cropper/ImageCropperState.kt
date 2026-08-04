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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

private const val MaxHistorySize = 50

@Stable
class ImageCropperState {
    var canUndo: Boolean by mutableStateOf(false)
        private set

    var canRedo: Boolean by mutableStateOf(false)
        private set

    private var imageKey: Any? = null
    private var attachmentKey: Any? = null
    private var layoutKey: Any? = null
    private var configurationKey: Any? = null
    private var captureSnapshot: (() -> ImageCropSnapshot?)? = null
    private var restoreSnapshot: ((ImageCropSnapshot) -> Unit)? = null
    private var pendingSnapshot: ImageCropSnapshot? = null
    private var currentSnapshot: ImageCropSnapshot? = null
    private var restoreCurrentSnapshotOnCropperReady = false
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
        restoreSnapshot: (ImageCropSnapshot) -> Unit
    ) {
        val imageChanged = this.imageKey != imageKey
        val attachmentChanged = this.attachmentKey != null &&
                this.attachmentKey !== attachmentKey
        if (
            !imageChanged &&
            attachmentChanged &&
            this.configurationKey == configurationKey
        ) {
            if (pendingSnapshot != null || currentSnapshot == null) {
                this.captureSnapshot?.invoke()?.let { currentSnapshot = it }
            }
            restoreCurrentSnapshotOnCropperReady = currentSnapshot != null
            pendingSnapshot = null
        }
        val layoutChanged = !imageChanged &&
                this.layoutKey != null &&
                layoutKey != null &&
                this.layoutKey != layoutKey &&
                this.configurationKey == configurationKey
        if (layoutChanged) {
            restoreCurrentSnapshotOnCropperReady = currentSnapshot != null
        }
        if (imageChanged) {
            this.imageKey = imageKey
            pendingSnapshot = null
            currentSnapshot = null
            restoreCurrentSnapshotOnCropperReady = false
            undoHistory.clear()
            redoHistory.clear()
            updateAvailability()
        }
        this.attachmentKey = attachmentKey
        this.layoutKey = layoutKey
        this.configurationKey = configurationKey
        this.captureSnapshot = captureSnapshot
        this.restoreSnapshot = restoreSnapshot
    }

    internal fun detach(attachmentKey: Any) {
        if (this.attachmentKey !== attachmentKey) return
        this.attachmentKey = null
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
        restoreCurrentSnapshotOnCropperReady = false
        undoHistory.clear()
        redoHistory.clear()
        updateAvailability()
    }

    fun reset() {
        attachmentKey = null
        layoutKey = null
        configurationKey = null
        pendingSnapshot = null
        currentSnapshot = null
        restoreCurrentSnapshotOnCropperReady = false
        undoHistory.clear()
        redoHistory.clear()
        updateAvailability()
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
    val rotation: Float
)
