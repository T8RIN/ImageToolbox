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

package com.t8rin.imagetoolbox.feature.resize_convert.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.clearAttribute
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.ui.transformation.ImageInfoTransformation
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class ResizeAndConvertComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
    private val settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::setUris)
        }
    }

    private val _originalSize: MutableState<IntegerSize?> = mutableStateOf(null)
    val originalSize by _originalSize

    private val _exif: MutableState<Metadata?> = mutableStateOf(null)
    val exif by _exif

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageInfo: ImageInfo by _imageInfo

    private val _history: MutableState<List<HistorySnapshot>> = mutableStateOf(emptyList())
    private val history: List<HistorySnapshot> by _history

    private val _redoHistory: MutableState<List<HistorySnapshot>> = mutableStateOf(emptyList())
    private val redoHistory: List<HistorySnapshot> by _redoHistory

    val canUndo: Boolean get() = history.size > 1
    val canRedo: Boolean get() = redoHistory.isNotEmpty()

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(true)
    val shouldShowPreview by _shouldShowPreview

    private val _showWarning: MutableState<Boolean> = mutableStateOf(false)
    val showWarning: Boolean by _showWarning

    private val _presetSelected: MutableState<Preset> = mutableStateOf(Preset.None)
    val presetSelected by _presetSelected

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val isAlwaysClearExif: Boolean get() = settingsProvider.settingsState.value.isAlwaysClearExif

    init {
        componentScope.launch {
            val settingsState = settingsProvider.getSettingsState()
            _imageInfo.update {
                it.copy(resizeType = settingsState.defaultResizeType)
            }
        }
    }

    private var job: Job? by smartJob {
        _isImageLoading.update { false }
    }

    fun setUris(
        uris: List<Uri>?
    ) {
        _history.value = emptyList()
        _redoHistory.value = emptyList()
        registerChangesCleared()
        _uris.update { null }
        _uris.update { uris }
        _selectedUri.update { uris?.firstOrNull() }
        _presetSelected.update { Preset.None }
        uris?.firstOrNull()?.let { uri ->
            componentScope.launch {
                _imageInfo.update {
                    it.copy(originalUri = uri.toString())
                }
                imageGetter.getImageAsync(
                    uri = uri.toString(),
                    originalSize = true,
                    onGetImage = ::setImageData,
                    onFailure = AppToastHost::showFailureToast
                )
            }
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            _uris.update { uris }
            if (_selectedUri.value == removedUri) {
                val index = uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    uris?.getOrNull(1)?.let {
                        updateSelectedUri(it)
                    }
                } else {
                    uris?.getOrNull(index - 1)?.let {
                        updateSelectedUri(it)
                    }
                }
            }
            _uris.update {
                it?.toMutableList()?.apply {
                    remove(removedUri)
                }
            }
        }
    }

    private suspend fun checkBitmapAndUpdate(
        resetPreset: Boolean = false
    ) {
        if (resetPreset) {
            _presetSelected.update { Preset.None }
        }
        _bitmap.value?.let { bmp ->
            val preview = updatePreview(bmp)
            _previewBitmap.update { null }
            _shouldShowPreview.update { imagePreviewCreator.canShow(preview) }
            if (shouldShowPreview) _previewBitmap.update { preview }
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap? = withContext(defaultDispatcher) {
        return@withContext imageInfo.run {
            _showWarning.update { width * height * 4L >= 10_000 * 10_000 * 3L }
            imagePreviewCreator.createPreview(
                image = bitmap,
                imageInfo = this,
                onGetByteCount = { sizeInBytes ->
                    _imageInfo.update { it.copy(sizeInBytes = sizeInBytes) }
                }
            )
        }
    }

    private fun setBitmapInfo(newInfo: ImageInfo) {
        if (_imageInfo.value != newInfo) {
            _imageInfo.update { newInfo }
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
        }
    }

    fun resetValues(
        saveFormat: Boolean = false,
        resetPreset: Boolean = true
    ) {
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            ImageInfo(
                width = _originalSize.value?.width ?: 0,
                height = _originalSize.value?.height ?: 0,
                imageFormat = if (saveFormat) {
                    imageInfo.imageFormat
                } else ImageFormat.Default,
                originalUri = selectedUri?.toString()
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate(
                resetPreset = resetPreset
            )
        }
        commitHistoryFrom(beforeSnapshot)
    }

    private fun setImageData(imageData: ImageData<Bitmap>) {
        job = componentScope.launch {
            _isImageLoading.update { true }
            _exif.update { imageData.metadata.takeIf { !isAlwaysClearExif } }
            val bitmap = imageData.image
            val size = bitmap.width to bitmap.height
            _originalSize.update { size.run { IntegerSize(width = first, height = second) } }
            _bitmap.update { imageScaler.scaleUntilCanShow(bitmap) }
            resetValues(true)
            _imageInfo.update {
                imageData.imageInfo.copy(
                    width = size.first,
                    height = size.second
                )
            }
            checkBitmapAndUpdate(
                resetPreset = _presetSelected.value == Preset.Telegram && imageData.imageInfo.imageFormat != ImageFormat.Png.Lossless
            )
            resetHistory()
            registerChangesCleared()
            _isImageLoading.update { false }
        }
    }

    fun rotateLeft() {
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(
                rotationDegrees = it.rotationDegrees - 90f,
                height = it.width,
                width = it.height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun rotateRight() {
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(
                rotationDegrees = it.rotationDegrees + 90f,
                height = it.width,
                width = it.height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun flip() {
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(isFlipped = !it.isFlipped)
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun updateWidth(width: Int) {
        if (imageInfo.width != width) {
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(width = width)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(true)
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun updateHeight(height: Int) {
        if (imageInfo.height != height) {
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(height = height)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(true)
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun setQuality(quality: Quality) {
        if (imageInfo.quality != quality) {
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(quality = quality)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (imageInfo.imageFormat != imageFormat) {
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(imageFormat = imageFormat)
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = _presetSelected.value == Preset.Telegram && imageFormat != ImageFormat.Png.Lossless
                )
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun setResizeType(type: ResizeType) {
        if (imageInfo.resizeType != type) {
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(
                    resizeType = type.withOriginalSizeIfCrop(originalSize)
                )
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun setKeepExif(boolean: Boolean) {
        val beforeSnapshot = currentHistorySnapshot()
        _keepExif.update { boolean }
        commitHistoryFrom(beforeSnapshot)
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            val results = mutableListOf<SaveResult>()
            _done.update { 0 }
            uris?.forEach { uri ->
                runSuspendCatching {
                    imageGetter.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    imageInfo.copy(
                        originalUri = uri.toString()
                    ).let {
                        imageTransformer.applyPresetBy(
                            image = bitmap,
                            preset = presetSelected,
                            currentInfo = it
                        )
                    }.let { imageInfo ->
                        results.add(
                            fileController.save(
                                saveTarget = ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    metadata = if (uris!!.size == 1) exif else null,
                                    originalUri = uri.toString(),
                                    sequenceNumber = done + 1,
                                    data = imageCompressor.compressAndTransform(
                                        image = bitmap,
                                        imageInfo = imageInfo
                                    ),
                                    presetInfo = presetSelected,
                                    canSkipIfLarger = true
                                ),
                                keepOriginalMetadata = if (uris!!.size == 1) true else keepExif,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                    }
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
                updateProgress(
                    done = done,
                    total = uris.orEmpty().size
                )
            }
            parseSaveResults(results.onSuccess(::registerSave))
            _isSaving.update { false }
        }
    }

    fun updateSelectedUri(uri: Uri) {
        runCatching {
            _selectedUri.update { uri }
            componentScope.launch {
                _isImageLoading.update { true }
                val bitmap = imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = true
                )?.image
                val size = bitmap?.let { it.width to it.height }
                _originalSize.update {
                    size?.run {
                        IntegerSize(
                            width = first,
                            height = second
                        )
                    }
                }
                _bitmap.update { imageScaler.scaleUntilCanShow(bitmap) }
                _imageInfo.update {
                    it.copy(
                        width = size?.first ?: 0,
                        height = size?.second ?: 0,
                        originalUri = uri.toString()
                    )
                }
                _imageInfo.update {
                    imageTransformer.applyPresetBy(
                        image = _bitmap.value,
                        preset = presetSelected,
                        currentInfo = it
                    )
                }
                checkBitmapAndUpdate()
                _isImageLoading.update { false }
            }
        }.onFailure(AppToastHost::showFailureToast)
    }

    fun updatePreset(preset: Preset) {
        componentScope.launch {
            val beforeSnapshot = currentHistorySnapshot()
            if (preset is Preset.AspectRatio && preset.ratio != 1f) {
                _imageInfo.update { it.copy(rotationDegrees = 0f) }
            }
            setBitmapInfo(
                imageTransformer.applyPresetBy(
                    image = bitmap,
                    preset = preset,
                    currentInfo = imageInfo.copy(
                        originalUri = selectedUri?.toString()
                    )
                )
            )
            _presetSelected.update { preset }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun selectLeftUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let {
                uris?.leftFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 }
            ?.let {
                uris?.rightFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun performSharing() {
        cacheImages { uris ->
            componentScope.launch {
                shareProvider.shareUris(uris.map { it.toString() })
                AppToastHost.showConfetti()
            }
        }
    }

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } == true

    fun clearExif() {
        updateExif(_exif.value?.clearAllAttributes())
    }

    fun updateExif(metadata: Metadata?) {
        val beforeSnapshot = currentHistorySnapshot()
        _exif.update { metadata }
        commitHistoryFrom(beforeSnapshot)
    }

    fun removeExifTag(tag: MetadataTag) {
        updateExif(_exif.value?.clearAttribute(tag))
    }

    fun updateExifByTag(
        tag: MetadataTag,
        value: String
    ) {
        updateExif(_exif.value?.setAttribute(tag, value))
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.update { false }
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        val beforeSnapshot = currentHistorySnapshot()
        _imageInfo.update {
            it.copy(
                imageScaleMode = imageScaleMode
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
        commitHistoryFrom(beforeSnapshot)
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.update { true }
            imageGetter.getImage(selectedUri.toString())?.image?.let { bmp ->
                bmp to imageInfo.copy(
                    originalUri = selectedUri.toString()
                ).let {
                    imageTransformer.applyPresetBy(
                        image = bitmap,
                        preset = presetSelected,
                        currentInfo = it
                    )
                }
            }?.let { (image, imageInfo) ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = selectedUri.toString())
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.update { false }
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.update { true }
            _done.update { 0 }
            val list = mutableListOf<Uri>()
            uris?.forEach { uri ->
                imageGetter.getImage(uri.toString())?.image?.let { bmp ->
                    bmp to imageInfo.copy(
                        originalUri = uri.toString()
                    ).let {
                        imageTransformer.applyPresetBy(
                            image = bitmap,
                            preset = presetSelected,
                            currentInfo = it
                        )
                    }
                }?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo.copy(originalUri = uri.toString())
                    )?.let { uri ->
                        list.add(uri.toUri())
                    }
                }
                _done.value += 1
                updateProgress(
                    done = done,
                    total = uris.orEmpty().size
                )
            }
            onComplete(list)
            _isSaving.update { false }
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat? =
        if (uris?.size == 1) imageInfo.imageFormat
        else null

    fun getTransformations() = listOf(
        imageInfoTransformationFactory(
            imageInfo = imageInfo,
            preset = presetSelected
        )
    )

    fun undo() {
        if (!canUndo) return

        val current = history.last()
        val previous = history[history.lastIndex - 1]

        _history.value = history.dropLast(1)
        _redoHistory.update { (it + current).takeLast(MAX_HISTORY_SIZE) }
        applyHistorySnapshot(previous)
        registerChanges()
    }

    fun redo() {
        if (!canRedo) return

        val snapshot = redoHistory.last()

        _redoHistory.value = redoHistory.dropLast(1)
        _history.update { (it + snapshot).takeLast(MAX_HISTORY_SIZE) }
        applyHistorySnapshot(snapshot)
        registerChanges()
    }

    private fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        imageInfo = imageInfo,
        preset = presetSelected,
        keepExif = keepExif,
        exif = exif
    )

    private fun commitHistoryFrom(beforeSnapshot: HistorySnapshot) {
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

    private fun resetHistory() {
        _history.value = listOf(currentHistorySnapshot())
        _redoHistory.value = emptyList()
    }

    private fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _imageInfo.value = snapshot.imageInfo
        _presetSelected.value = snapshot.preset
        _keepExif.value = snapshot.keepExif
        _exif.value = snapshot.exif
        _previewBitmap.value = null
        debouncedImageCalculation {
            bitmap?.let {
                checkBitmapAndUpdate()
            }
        }
    }

    private data class HistorySnapshot(
        val imageInfo: ImageInfo = ImageInfo(),
        val preset: Preset = Preset.None,
        val keepExif: Boolean = false,
        val exif: Metadata? = null
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ResizeAndConvertComponent
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 25
    }
}