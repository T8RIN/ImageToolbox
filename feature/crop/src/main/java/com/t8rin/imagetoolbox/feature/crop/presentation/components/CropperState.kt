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

package com.t8rin.imagetoolbox.feature.crop.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.t8rin.crop.advanced.compose.AdvancedCropperState
import com.t8rin.crop.advanced.compose.rememberAdvancedCropperState
import com.t8rin.cropper.ImageCropperState
import com.t8rin.cropper.rememberImageCropperState
import com.t8rin.opencv_tools.free_corners_crop.compose.FreeCornersCropperState
import com.t8rin.opencv_tools.free_corners_crop.compose.rememberFreeCornersCropperState

@Stable
class CropperState internal constructor(
    internal val advancedCropperState: AdvancedCropperState,
    internal val imageCropperState: ImageCropperState,
    internal val freeCornersCropperState: FreeCornersCropperState
) {
    var canUndo: Boolean by mutableStateOf(false)
        private set

    var canRedo: Boolean by mutableStateOf(false)
        private set

    private val undoHistory = ArrayDeque<HistoryAction>()
    private val redoHistory = ArrayDeque<HistoryAction>()
    private var pendingCombinedAction: HistoryAction.Combined? = null
    private var pendingAppliedCropperState: AppliedCropperState? = null
    private var activeCropType = CropType.Default

    fun undo() {
        finishActiveTransformation(recordAction = true)
        val action = undoHistory.removeLastOrNull() ?: return
        action.undo()
        redoHistory.addLast(action)
        redoHistory.trimToMaxSize()
        updateAvailability()
    }

    fun redo() {
        finishActiveTransformation(recordAction = true)
        val action = redoHistory.removeLastOrNull() ?: return
        action.redo()
        undoHistory.addLast(action)
        undoHistory.trimToMaxSize()
        updateAvailability()
    }

    fun recordExternalAction(
        previousCropType: CropType,
        currentCropType: CropType,
        undo: () -> Unit,
        redo: () -> Unit
    ) {
        finishTransformation(previousCropType, recordAction = true)
        if (previousCropType != currentCropType) {
            prepareForReattachment(previousCropType)
            activeCropType = currentCropType
        } else {
            finishReattachment(previousCropType)
        }
        recordAction(
            HistoryAction.External(
                undoAction = {
                    if (previousCropType != currentCropType) {
                        prepareForReattachment(currentCropType)
                    }
                    undo()
                    activeCropType = previousCropType
                },
                redoAction = {
                    if (previousCropType != currentCropType) {
                        prepareForReattachment(previousCropType)
                    }
                    redo()
                    activeCropType = currentCropType
                }
            )
        )
    }

    fun recordExternalCropperAction(
        cropType: CropType,
        undo: () -> Unit,
        redo: () -> Unit
    ) {
        finishActiveTransformation(recordAction = true)
        finishReattachment(cropType)
        val action = HistoryAction.Combined(
            cropType = cropType,
            undoExternalAction = undo,
            redoExternalAction = redo,
            undoCropperAction = { undoCropper(cropType) },
            redoCropperAction = { redoCropper(cropType) }
        )
        pendingCombinedAction = action
        beginCropperTransformation(cropType)
        recordAction(action)
    }

    fun finishExternalCropperAction(cropType: CropType) {
        val action = pendingCombinedAction ?: return
        if (action.cropType != cropType) return
        if (endCropperTransformation(cropType)) {
            action.includesCropperTransformation = true
        }
        pendingCombinedAction = null
    }

    fun recordAppliedAction(
        undo: () -> Unit,
        redo: () -> Unit
    ) {
        val appliedCropperState = pendingAppliedCropperState
        pendingAppliedCropperState = null
        finishActiveTransformation(recordAction = false)
        undoHistory.removeAll { it is HistoryAction.Cropper }
        undoHistory.filterIsInstance<HistoryAction.Combined>().forEach {
            it.includesCropperTransformation = false
        }
        redoHistory.clear()
        resetCropperStates()
        recordAction(
            HistoryAction.External(
                undoAction = {
                    appliedCropperState?.let(::prepareRestore)
                    undo()
                },
                redoAction = {
                    resetCropperStates()
                    redo()
                }
            )
        )
    }

    fun prepareAppliedAction() {
        finishActiveTransformation(recordAction = true)
        pendingAppliedCropperState = when (activeCropType) {
            CropType.Default -> advancedCropperState.saveState()?.let(AppliedCropperState::Advanced)
            CropType.NoRotation -> imageCropperState.saveState()?.let(AppliedCropperState::Image)
            CropType.FreeCorners -> freeCornersCropperState.saveState()
                ?.let(AppliedCropperState::FreeCorners)
        }
    }

    fun recordCropperTransformation(cropType: CropType) {
        if (cropType != activeCropType) {
            discardPendingTransformation(cropType)
            return
        }
        pendingCombinedAction?.let { action ->
            if (action.cropType == cropType) {
                action.includesCropperTransformation = true
                pendingCombinedAction = null
                return
            }
        }
        recordAction(
            HistoryAction.Cropper(
                undoAction = { undoCropper(cropType) },
                redoAction = { redoCropper(cropType) }
            )
        )
    }

    fun beginRotation() {
        advancedCropperState.beginTransformation()
    }

    fun endRotation() {
        if (advancedCropperState.endTransformation()) {
            recordCropperTransformation(CropType.Default)
        }
    }

    fun setActiveCropType(cropType: CropType) {
        if (activeCropType == cropType) return
        discardPendingTransformations()
        activeCropType = cropType
    }

    fun clearHistory() {
        pendingCombinedAction = null
        pendingAppliedCropperState = null
        undoHistory.clear()
        redoHistory.clear()
        advancedCropperState.clearHistory()
        imageCropperState.clearHistory()
        freeCornersCropperState.clearHistory()
        updateAvailability()
    }

    fun resetCropperStates() {
        pendingCombinedAction = null
        pendingAppliedCropperState = null
        advancedCropperState.reset()
        imageCropperState.reset()
        freeCornersCropperState.reset()
    }

    private fun finishActiveTransformation(recordAction: Boolean) {
        finishTransformation(activeCropType, recordAction)
    }

    private fun finishTransformation(
        cropType: CropType,
        recordAction: Boolean
    ) {
        if (endCropperTransformation(cropType) && recordAction) {
            recordCropperTransformation(cropType)
        }
        pendingCombinedAction = null
    }

    private fun discardPendingTransformations() {
        CropType.entries.forEach(::discardPendingTransformation)
        pendingCombinedAction = null
    }

    private fun discardPendingTransformation(cropType: CropType) {
        when (cropType) {
            CropType.Default -> advancedCropperState.discardPendingTransformation()
            CropType.NoRotation -> imageCropperState.discardPendingTransformation()
            CropType.FreeCorners -> freeCornersCropperState.discardPendingTransformation()
        }
    }

    private fun prepareForReattachment(cropType: CropType) {
        when (cropType) {
            CropType.Default -> advancedCropperState.prepareForReattachment()
            CropType.NoRotation -> imageCropperState.prepareForReattachment()
            CropType.FreeCorners -> freeCornersCropperState.prepareForReattachment()
        }
    }

    private fun finishReattachment(cropType: CropType) {
        when (cropType) {
            CropType.Default -> Unit
            CropType.NoRotation -> imageCropperState.finishReattachment()
            CropType.FreeCorners -> freeCornersCropperState.finishReattachment()
        }
    }

    private fun beginCropperTransformation(cropType: CropType) {
        when (cropType) {
            CropType.Default -> advancedCropperState.beginTransformation()
            CropType.NoRotation -> imageCropperState.beginTransformation()
            CropType.FreeCorners -> freeCornersCropperState.beginTransformation()
        }
    }

    private fun endCropperTransformation(cropType: CropType): Boolean = when (cropType) {
        CropType.Default -> advancedCropperState.endTransformation()
        CropType.NoRotation -> imageCropperState.endTransformation()
        CropType.FreeCorners -> freeCornersCropperState.endTransformation()
    }

    private fun undoCropper(cropType: CropType) {
        when (cropType) {
            CropType.Default -> advancedCropperState.undo()
            CropType.NoRotation -> imageCropperState.undo()
            CropType.FreeCorners -> freeCornersCropperState.undo()
        }
    }

    private fun redoCropper(cropType: CropType) {
        when (cropType) {
            CropType.Default -> advancedCropperState.redo()
            CropType.NoRotation -> imageCropperState.redo()
            CropType.FreeCorners -> freeCornersCropperState.redo()
        }
    }

    private fun prepareRestore(state: AppliedCropperState) {
        when (state) {
            is AppliedCropperState.Advanced -> {
                advancedCropperState.restoreStateOnNextImageChange(state.state)
            }

            is AppliedCropperState.Image -> {
                imageCropperState.restoreStateOnNextImageChange(state.state)
            }

            is AppliedCropperState.FreeCorners -> {
                freeCornersCropperState.restoreStateOnNextImageChange(state.state)
            }
        }
    }

    private fun recordAction(action: HistoryAction) {
        undoHistory.addLast(action)
        undoHistory.trimToMaxSize()
        redoHistory.clear()
        updateAvailability()
    }

    private fun updateAvailability() {
        canUndo = undoHistory.isNotEmpty()
        canRedo = redoHistory.isNotEmpty()
    }

    private fun ArrayDeque<HistoryAction>.trimToMaxSize() {
        while (size > MaxHistorySize) removeFirst()
    }

    private sealed interface HistoryAction {
        fun undo()
        fun redo()

        data class Cropper(
            val undoAction: () -> Unit,
            val redoAction: () -> Unit
        ) : HistoryAction {
            override fun undo() = undoAction()
            override fun redo() = redoAction()
        }

        data class External(
            val undoAction: () -> Unit,
            val redoAction: () -> Unit
        ) : HistoryAction {
            override fun undo() = undoAction()
            override fun redo() = redoAction()
        }

        class Combined(
            val cropType: CropType,
            val undoExternalAction: () -> Unit,
            val redoExternalAction: () -> Unit,
            val undoCropperAction: () -> Unit,
            val redoCropperAction: () -> Unit,
            var includesCropperTransformation: Boolean = false
        ) : HistoryAction {
            override fun undo() {
                undoExternalAction()
                if (includesCropperTransformation) undoCropperAction()
            }

            override fun redo() {
                redoExternalAction()
                if (includesCropperTransformation) redoCropperAction()
            }
        }
    }

    private sealed interface AppliedCropperState {
        data class Advanced(
            val state: AdvancedCropperState.SavedState
        ) : AppliedCropperState

        data class Image(
            val state: ImageCropperState.SavedState
        ) : AppliedCropperState

        data class FreeCorners(
            val state: FreeCornersCropperState.SavedState
        ) : AppliedCropperState
    }
}

private const val MaxHistorySize = 50

@Composable
fun rememberCropperState(): CropperState {
    val advancedCropperState = rememberAdvancedCropperState()
    val imageCropperState = rememberImageCropperState()
    val freeCornersCropperState = rememberFreeCornersCropperState()

    return remember(
        advancedCropperState,
        imageCropperState,
        freeCornersCropperState
    ) {
        CropperState(
            advancedCropperState = advancedCropperState,
            imageCropperState = imageCropperState,
            freeCornersCropperState = freeCornersCropperState
        )
    }
}
