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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectExtension
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.isMarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayersApplier
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectResult
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ProjectBackground
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.EditBoxState
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.BackgroundBehavior
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayerSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.applyGroupGlobalChanges
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.asDomain
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.asUi
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.coerceGroupToBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.combinedBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.composeToParentSpace
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.deepDuplicate
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.defaultGroupPlaceholderType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.effectiveCoerceToBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.flattenToDomain
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.groupChildAt
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.setGroupScalePrecisely
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.toSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.toUi
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.uiCornerRadiusPercent
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.withCoerceToBoundsRecursively
import com.t8rin.logger.makeLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext


class MarkupLayersComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val markupLayersApplier: MarkupLayersApplier<Bitmap>,
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    private val _isOptionsExpanded = fileController.savable(
        scope = componentScope,
        initial = false
    )
    val isOptionsExpanded: Boolean get() = _isOptionsExpanded.get()

    private val _backgroundBehavior: MutableState<BackgroundBehavior> =
        mutableStateOf(BackgroundBehavior.None)
    val backgroundBehavior: BackgroundBehavior by _backgroundBehavior

    private val _layers: MutableState<List<UiMarkupLayer>> = mutableStateOf(emptyList())
    val layers: List<UiMarkupLayer> by _layers

    private val _groupingSelectionIds: MutableState<Set<Long>> = mutableStateOf(emptySet())
    val groupingSelectionIds: Set<Long> by _groupingSelectionIds
    val isGroupingSelectionMode: Boolean get() = groupingSelectionIds.isNotEmpty()
    val groupingSelectionCount: Int get() = groupingSelectionIds.size

    private val _history: MutableState<List<HistorySnapshot>> = mutableStateOf(
        listOf(HistorySnapshot())
    )
    private val history: List<HistorySnapshot> by _history

    private val _redoHistory: MutableState<List<HistorySnapshot>> = mutableStateOf(emptyList())
    private val redoHistory: List<HistorySnapshot> by _redoHistory

    private var pendingHistorySnapshot: HistorySnapshot? = null

    val canUndo: Boolean get() = history.size > 1
    val canRedo: Boolean get() = redoHistory.isNotEmpty()

    fun toggleExpandOptions() {
        _isOptionsExpanded.update { !it }
    }

    fun undo() {
        finalizePendingHistoryTransaction()
        if (!canUndo) return

        val current = history.last()
        val previous = history[history.lastIndex - 1]

        _history.value = history.dropLast(1)
        _redoHistory.update { (it + current).takeLast(MAX_HISTORY_SIZE) }
        applyHistorySnapshot(previous)
        registerChanges()
    }

    fun redo() {
        finalizePendingHistoryTransaction()
        if (!canRedo) return

        val snapshot = redoHistory.last()

        _redoHistory.value = redoHistory.dropLast(1)
        _history.update { (it + snapshot).takeLast(MAX_HISTORY_SIZE) }
        applyHistorySnapshot(snapshot)
        registerChanges()
    }

    fun clearLayers() {
        if (layers.isEmpty()) return

        cancelGroupingSelection()
        runEditorChange {
            _layers.value = emptyList()
        }
    }

    fun addLayer(layer: UiMarkupLayer) {
        cancelGroupingSelection()
        deactivateAllLayers()
        runEditorChange {
            _layers.update { it + layer }
        }
    }

    fun deactivateAllLayers() {
        _layers.value.forEach { it.state.deactivate() }
    }

    fun activateLayer(layer: UiMarkupLayer) {
        if (layer.isLocked) return
        deactivateAllLayers()
        layer.state.activate()
    }

    fun copyLayer(layer: UiMarkupLayer) {
        cancelGroupingSelection()
        runEditorChange {
            val copied = layer.deepDuplicate()
            _layers.update {
                it.toMutableList().apply {
                    add(indexOf(layer), copied)
                }
            }
            activateLayer(copied)
        }
    }

    fun updateLayerAt(
        index: Int,
        layer: UiMarkupLayer,
        commitToHistory: Boolean = true
    ) {
        val currentLayer = layers.getOrNull(index) ?: return
        if (currentLayer == layer) return
        val metadataOnlyUpdate = currentLayer.copy(
            visibleLineCount = layer.visibleLineCount
        ) == layer
        if (currentLayer.isLocked && !metadataOnlyUpdate) return

        val replaceLayer: () -> Unit = {
            _layers.update {
                it.toMutableList().apply {
                    set(index, layer)
                }
            }
        }

        val shouldTrackHistory = commitToHistory && !metadataOnlyUpdate

        if (shouldTrackHistory) {
            runEditorChange(replaceLayer)
        } else {
            replaceLayer()
        }
    }

    fun updateLayerState(
        layer: UiMarkupLayer,
        commitToHistory: Boolean = true,
        allowLocked: Boolean = false,
        block: EditBoxState.() -> Unit
    ) {
        if (layer.isLocked && !allowLocked) return

        if (commitToHistory) {
            runEditorChange {
                layer.state.block()
                layer.coerceGroupToBounds()
            }
        } else {
            layer.state.block()
            layer.coerceGroupToBounds()
        }
    }

    fun toggleLayerLock(layer: UiMarkupLayer) {
        cancelGroupingSelection()
        runEditorChange {
            val copied = layer.copy(
                isLocked = !layer.isLocked,
                state = layer.state.copy(
                    isActive = false,
                    isInEditMode = false
                )
            )

            _layers.update {
                it.toMutableList().apply {
                    set(
                        index = indexOf(layer),
                        element = copied
                    )
                }
            }
        }
    }

    fun removeLayer(layer: UiMarkupLayer) {
        cancelGroupingSelection()
        runEditorChange {
            _layers.update { it - layer }
        }
    }

    fun reorderLayers(layers: List<UiMarkupLayer>) {
        runEditorChange {
            _layers.update { layers }
        }
    }

    fun beginHistoryTransaction() {
        if (pendingHistorySnapshot == null) {
            pendingHistorySnapshot = currentHistorySnapshot()
        }
    }

    fun commitHistoryTransaction() {
        pendingHistorySnapshot?.let(::commitHistoryFrom)
    }

    fun moveLayerBy(
        layer: UiMarkupLayer,
        offsetChange: Offset,
        commitToHistory: Boolean = true
    ) {
        updateLayerState(
            layer = layer,
            commitToHistory = commitToHistory
        ) {
            if (layer.isGroup) {
                layer.applyGroupGlobalChanges(
                    offsetChange = offsetChange
                )
            } else {
                moveBy(
                    offsetChange = offsetChange,
                    cornerRadiusPercent = layer.uiCornerRadiusPercent()
                )
            }
        }
    }

    fun setLayerScale(
        layer: UiMarkupLayer,
        scale: Float,
        commitToHistory: Boolean = true
    ) {
        updateLayerState(
            layer = layer,
            commitToHistory = commitToHistory
        ) {
            if (layer.isGroup) {
                layer.setGroupScalePrecisely(scale)
            } else {
                setScalePrecisely(
                    targetScale = scale,
                    cornerRadiusPercent = layer.uiCornerRadiusPercent()
                )
            }
        }
    }

    fun resetLayerPosition(layer: UiMarkupLayer) {
        updateLayerState(layer = layer) {
            resetPosition()
        }
    }

    fun setLayerNormalizedPosition(
        layer: UiMarkupLayer,
        x: Float? = null,
        y: Float? = null,
        commitToHistory: Boolean = true
    ) {
        updateLayerState(
            layer = layer,
            commitToHistory = commitToHistory
        ) {
            setNormalizedPosition(
                x = x,
                y = y,
                cornerRadiusPercent = layer.uiCornerRadiusPercent()
            )
        }
    }

    fun toggleGroupingSelection(layer: UiMarkupLayer) {
        if (layer.isLocked) return
        if (layers.none { it.id == layer.id }) return

        deactivateAllLayers()
        _groupingSelectionIds.update { ids ->
            if (layer.id in ids) ids - layer.id else ids + layer.id
        }
    }

    fun startGroupingSelection(layer: UiMarkupLayer) {
        if (layer.isLocked) return
        if (layers.none { it.id == layer.id }) return

        val activeLayerId = layers.firstOrNull { it.state.isActive && !it.isLocked }?.id

        deactivateAllLayers()
        _groupingSelectionIds.update { ids ->
            val seededIds = if (ids.isEmpty()) {
                buildSet {
                    activeLayerId?.let(::add)
                    add(layer.id)
                }
            } else {
                ids
            }

            when {
                seededIds.isEmpty() -> setOf(layer.id)
                ids.isEmpty() -> seededIds
                layer.id in seededIds -> seededIds - layer.id
                else -> seededIds + layer.id
            }
        }
    }

    fun cancelGroupingSelection() {
        _groupingSelectionIds.value = emptySet()
    }

    fun clearSelections() {
        cancelGroupingSelection()
        deactivateAllLayers()
    }

    fun groupSelectedLayers() {
        val selectedEntries = layers.withIndex()
            .filter { it.value.id in groupingSelectionIds }
        if (selectedEntries.size < 2) return

        val selectedLayers = selectedEntries.map { it.value }
        val bounds = selectedLayers.combinedBounds() ?: return
        val center = bounds.center
        val canvasSize = selectedLayers.firstNotNullOfOrNull { layer ->
            layer.state.canvasSize.takeIf { it.width > 0 && it.height > 0 }
        } ?: return
        val groupCoerceToBounds = selectedLayers.all(UiMarkupLayer::effectiveCoerceToBounds)

        val groupedLayer = UiMarkupLayer(
            type = defaultGroupPlaceholderType(),
            groupedLayers = selectedLayers.map { layer ->
                layer.groupChildAt(center)
            },
            state = EditBoxState(
                isActive = true,
                canvasSize = canvasSize,
                contentSize = bounds.toIntSize(),
                offset = center,
                coerceToBounds = groupCoerceToBounds
            )
        ).withCoerceToBoundsRecursively(groupCoerceToBounds)

        runEditorChange {
            val selectedIds = selectedEntries.map { it.value.id }.toSet()
            val firstSelectedIndex = selectedEntries.minOf { it.index }

            _layers.update { current ->
                buildList {
                    current.forEachIndexed { index, currentLayer ->
                        if (index == firstSelectedIndex) add(groupedLayer)
                        if (currentLayer.id !in selectedIds) add(currentLayer)
                    }
                }
            }
        }
        cancelGroupingSelection()
    }

    fun ungroupLayer(layer: UiMarkupLayer) {
        if (!layer.isGroup) return

        cancelGroupingSelection()
        runEditorChange {
            val restoredLayers = layer.groupedLayers.map { child ->
                child.composeToParentSpace(layer)
            }

            _layers.update { current ->
                current.toMutableList().apply {
                    val index = indexOf(layer)
                    if (index >= 0) {
                        removeAt(index)
                        addAll(index, restoredLayers)
                    }
                }
            }

            restoredLayers.firstOrNull()?.let(::activateLayer)
        }
    }

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _uri = mutableStateOf(Uri.EMPTY)

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            renderLayers()?.let { localBitmap ->
                parseSaveResult(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = _uri.value.toString(),
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = localBitmap,
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )
                            )
                        ),
                        keepOriginalMetadata = _saveExif.value,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.value = false
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
        registerChanges()
    }

    fun setSaveExif(bool: Boolean) {
        _saveExif.value = bool
        registerChanges()
    }

    private fun updateBitmap(bitmap: Bitmap?) {
        componentScope.launch {
            updateBitmapSync(bitmap)
        }
    }

    private suspend fun updateBitmapSync(bitmap: Bitmap?) {
        _isImageLoading.value = true
        _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
        _isImageLoading.value = false
    }

    fun setUri(uri: Uri) {
        if (uri.isMarkupProject()) {
            loadProject(uri)
        } else {
            setImageUri(uri)
        }
    }

    fun saveProject(uri: Uri) {
        savingJob = trackProgress {
            _isSaving.value = true

            fileController.writeBytes(uri.toString()) { output ->
                markupLayersApplier.saveProject(
                    destination = output,
                    project = createProject()
                )
            }.onSuccess {
                registerSave()
                AppToastHost.showConfetti()
            }.let(::parseSaveResult)

            _isSaving.value = false
        }
    }

    fun createProjectFilename(): String {
        val baseName = when (backgroundBehavior) {
            is BackgroundBehavior.Image -> {
                _uri.value.filename()?.substringBeforeLast('.')?.takeIf(String::isNotBlank)
            }

            is BackgroundBehavior.Color -> "Markup"
            BackgroundBehavior.None -> null
        } ?: "Markup"

        return "${baseName}_${timestamp()}.$MarkupProjectExtension"
    }

    private fun setImageUri(uri: Uri) {
        componentScope.launch {
            cancelGroupingSelection()
            _layers.update { emptyList() }
            _isImageLoading.value = true

            _uri.value = uri
            imageGetter.getImageAsync(
                uri = uri.toString(),
                originalSize = false,
                onGetImage = { data ->
                    _backgroundBehavior.update { BackgroundBehavior.Image }
                    updateBitmap(data.image)
                    _imageFormat.update { data.imageInfo.imageFormat }
                    resetHistory()
                    registerChangesCleared()
                },
                onFailure = {
                    _isImageLoading.value = false

                    if (bitmap == null) resetState()

                    AppToastHost.showFailureToast(it)
                }
            )
        }
    }

    private fun loadProject(uri: Uri) {
        componentScope.launch {
            _isImageLoading.value = true
            when (val result = markupLayersApplier.openProject(uri.toString())) {
                is MarkupProjectResult.Success -> {
                    applyProject(
                        project = result.project
                    )
                    registerChangesCleared()
                }

                is MarkupProjectResult.Error -> {
                    AppToastHost.showFailureToast(result.message)
                }
            }
            _isImageLoading.value = false
        }
    }

    private suspend fun renderLayers(): Bitmap? = withContext(defaultDispatcher) {
        deactivateAllLayers()

        runCatching {
            markupLayersApplier.applyToImage(
                image = imageGetter.getImage(data = _uri.value)
                    ?: (backgroundBehavior as? BackgroundBehavior.Color)?.run {
                        color.toDrawable().toBitmap(width, height)
                    } ?: run {
                        val w =
                            layers.firstOrNull()?.state?.canvasSize?.width?.takeIf { it > 0 } ?: 1
                        val h =
                            layers.firstOrNull()?.state?.canvasSize?.height?.takeIf { it > 0 } ?: 1
                        ImageBitmap(w, h).asAndroidBitmap()
                    },
                layers = flattenLayers(layers)
            )
        }.onFailure {
            it.makeLog()
        }.getOrNull()
    }

    override fun resetState() {
        markupLayersApplier.clearProjectCache()
        _bitmap.value = null
        _backgroundBehavior.update {
            BackgroundBehavior.None
        }
        _uri.value = Uri.EMPTY
        _layers.update { emptyList() }
        cancelGroupingSelection()
        resetHistory()
        registerChangesCleared()
    }

    fun startDrawOnBackground(
        reqWidth: Int,
        reqHeight: Int,
        color: Color,
    ) {
        val width = reqWidth.takeIf { it > 0 } ?: 1
        val height = reqHeight.takeIf { it > 0 } ?: 1
        width / height.toFloat()
        _backgroundBehavior.update {
            BackgroundBehavior.Color(
                width = width,
                height = height,
                color = color.toArgb()
            )
        }
        _uri.value = Uri.EMPTY
        _layers.value = emptyList()
        cancelGroupingSelection()
        updateBitmap(null)
        resetHistory()
        registerChangesCleared()
    }

    fun shareBitmap() {
        savingJob = trackProgress {
            _isSaving.value = true
            renderLayers()?.let {
                shareProvider.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    ),
                    onComplete = AppToastHost::showConfetti
                )
            }
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            renderLayers()?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = image.width,
                        height = image.height
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat

    fun updateBackgroundColor(color: Color) {
        runEditorChange {
            _backgroundBehavior.update {
                if (it is BackgroundBehavior.Color) {
                    it.copy(color = color.toArgb())
                } else it
            }
        }
    }

    private fun createProject(): MarkupProject = MarkupProject(
        background = when (val behavior = backgroundBehavior) {
            is BackgroundBehavior.Color -> ProjectBackground.Color(
                width = behavior.width,
                height = behavior.height,
                color = behavior.color
            )

            is BackgroundBehavior.Image -> ProjectBackground.Image(
                uri = _uri.value.toString()
            )

            BackgroundBehavior.None -> ProjectBackground.None
        },
        layers = layers.toProjectLayers(),
        lastLayers = history.dropLast(1).lastOrNull()?.projectLayers() ?: emptyList(),
        undoneLayers = redoHistory.lastOrNull()?.projectLayers() ?: emptyList()
    )

    private suspend fun applyProject(
        project: MarkupProject
    ) {
        cancelGroupingSelection()
        _layers.value = emptyList()

        when (val background = project.background) {
            is ProjectBackground.Image -> {
                _uri.value = background.uri.toUri()
                _backgroundBehavior.value = BackgroundBehavior.Image
                updateBitmapSync(
                    bitmap = imageGetter.getImage(
                        data = background.uri,
                        originalSize = false
                    )
                )
            }

            is ProjectBackground.Color -> {
                _uri.value = Uri.EMPTY
                _backgroundBehavior.value = BackgroundBehavior.Color(
                    width = background.width,
                    height = background.height,
                    color = background.color
                )
                updateBitmapSync(null)
            }

            ProjectBackground.None -> {
                _uri.value = Uri.EMPTY
                _backgroundBehavior.value = BackgroundBehavior.None
                updateBitmapSync(null)
            }
        }

        _layers.value = project.layers.map { it.asUi() }
        restoreHistory(
            previousLayers = project.lastLayers,
            redoneLayers = project.undoneLayers
        )
    }

    private fun runEditorChange(
        block: () -> Unit
    ) {
        val beforeSnapshot = pendingHistorySnapshot ?: currentHistorySnapshot()
        pendingHistorySnapshot = null
        block()
        normalizeGroupingSelection()
        commitHistoryFrom(beforeSnapshot)
    }

    private fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        backgroundBehavior = backgroundBehavior,
        layers = layers.map(UiMarkupLayer::toSnapshot)
    )

    private fun commitHistoryFrom(beforeSnapshot: HistorySnapshot) {
        pendingHistorySnapshot = null

        val afterSnapshot = currentHistorySnapshot()
        if (afterSnapshot == beforeSnapshot) return

        _history.update { states ->
            val normalizedStates = when {
                states.isEmpty() -> listOf(beforeSnapshot)
                states.last() == beforeSnapshot -> states
                else -> (states + beforeSnapshot).takeLast(MAX_HISTORY_SIZE)
            }

            (normalizedStates + afterSnapshot).takeLast(MAX_HISTORY_SIZE)
        }
        _redoHistory.value = emptyList()
        registerChanges()
    }

    private fun finalizePendingHistoryTransaction() {
        pendingHistorySnapshot?.let(::commitHistoryFrom)
    }

    private fun resetHistory() {
        restoreHistory()
    }

    private fun restoreHistory(
        previousLayers: List<MarkupLayer> = emptyList(),
        redoneLayers: List<MarkupLayer> = emptyList()
    ) {
        pendingHistorySnapshot = null

        val currentSnapshot = currentHistorySnapshot()
        val previousSnapshot = HistorySnapshot(
            backgroundBehavior = currentSnapshot.backgroundBehavior,
            layers = previousLayers.map { it.asUi().toSnapshot() }
        ).takeIf { it != currentSnapshot }
        val redoneSnapshot = HistorySnapshot(
            backgroundBehavior = currentSnapshot.backgroundBehavior,
            layers = redoneLayers.map { it.asUi().toSnapshot() }
        ).takeIf { it != currentSnapshot }

        _history.value = listOfNotNull(previousSnapshot, currentSnapshot)
        _redoHistory.value = listOfNotNull(redoneSnapshot)
    }

    private fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _backgroundBehavior.value = snapshot.backgroundBehavior
        _layers.value = snapshot.layers.map(UiMarkupLayerSnapshot::toUi)
        cancelGroupingSelection()
    }

    private data class HistorySnapshot(
        val backgroundBehavior: BackgroundBehavior = BackgroundBehavior.None,
        val layers: List<UiMarkupLayerSnapshot> = emptyList()
    )

    private fun HistorySnapshot.projectLayers(): List<MarkupLayer> = layers.map(
        UiMarkupLayerSnapshot::toUi
    ).toProjectLayers()

    private fun flattenLayers(
        layers: List<UiMarkupLayer>
    ): List<MarkupLayer> = layers.flatMap(UiMarkupLayer::flattenToDomain)

    private fun List<UiMarkupLayer>.toProjectLayers(): List<MarkupLayer> = map(
        UiMarkupLayer::asDomain
    )

    private fun normalizeGroupingSelection() {
        if (groupingSelectionIds.isEmpty()) return

        val validIds = layers.mapTo(mutableSetOf()) { it.id }
        _groupingSelectionIds.update { ids ->
            ids.filterTo(mutableSetOf()) { it in validIds }
        }
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 100
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): MarkupLayersComponent
    }

}

private fun EditBoxState.moveBy(
    offsetChange: Offset,
    cornerRadiusPercent: Int
) {
    val contentSize = contentSize
    if (contentSize.width <= 0 || contentSize.height <= 0) {
        offset += offsetChange
        return
    }

    val canvasWidth = canvasSize.width.takeIf { it > 0 } ?: contentSize.width
    val canvasHeight = canvasSize.height.takeIf { it > 0 } ?: contentSize.height

    applyGlobalChanges(
        parentMaxWidth = canvasWidth,
        parentMaxHeight = canvasHeight,
        contentSize = contentSize,
        cornerRadiusPercent = cornerRadiusPercent,
        zoomChange = 1f,
        offsetChange = offsetChange,
        rotationChange = 0f
    )
}

private fun EditBoxState.resetPosition() {
    offset = Offset.Zero
}
