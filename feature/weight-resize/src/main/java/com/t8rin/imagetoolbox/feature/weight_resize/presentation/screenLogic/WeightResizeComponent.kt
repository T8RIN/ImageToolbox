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

package com.t8rin.imagetoolbox.feature.weight_resize.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.weight_resize.domain.WeightImageScaler
import com.t8rin.imagetoolbox.feature.weight_resize.presentation.screenLogic.WeightResizeComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job


class WeightResizeComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val filenameCreator: FilenameCreator,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageScaler: WeightImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    init {
        debounce {
            initialUris?.let(::setUris)
        }
    }

    private val _imageScaleMode: MutableState<ImageScaleMode> =
        mutableStateOf(ImageScaleMode.Default)
    val imageScaleMode by _imageScaleMode

    private val _imageSize: MutableState<Long> = mutableLongStateOf(0L)
    val imageSize by _imageSize

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

    private val _presetSelected: MutableState<Int> = mutableIntStateOf(-1)
    val presetSelected by _presetSelected

    private val _handMode = mutableStateOf(true)
    val handMode by _handMode

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _maxBytes: MutableState<Long> = mutableLongStateOf(0L)
    val maxBytes by _maxBytes

    private val _imageFormat = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageFormat.value != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            beginPendingHistoryTransaction(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            )
            _imageFormat.value = imageFormat
            updateImageSize()
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setUris(
        uris: List<Uri>?
    ) {
        clearHistory()
        registerChangesCleared()
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
        if (uris != null) {
            _isImageLoading.value = true
            imageGetter.getImageAsync(
                uri = uris[0].toString(),
                originalSize = true,
                onGetImage = ::setImageData,
                onFailure = {
                    _isImageLoading.value = false
                    AppToastHost.showFailureToast(it)
                }
            )
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            _uris.value = uris
            if (_selectedUri.value == removedUri) {
                val index = uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    uris?.getOrNull(1)?.let {
                        _selectedUri.value = it
                        _bitmap.value = imageGetter.getImage(it.toString())?.image
                    }
                } else {
                    uris?.getOrNull(index - 1)?.let {
                        _selectedUri.value = it
                        _bitmap.value = imageGetter.getImage(it.toString())?.image
                    }
                }
            }
            val u = _uris.value?.toMutableList()?.apply {
                remove(removedUri)
            }
            _uris.value = u
        }
    }


    private fun updateBitmap(bitmap: Bitmap?) {
        componentScope.launch {
            _isImageLoading.value = true
            _bitmap.value = bitmap
            _previewBitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _isImageLoading.value = false
        }
    }

    fun setKeepExif(boolean: Boolean) {
        if (_keepExif.value == boolean) return
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _keepExif.value = boolean
        commitHistoryFrom(beforeSnapshot)
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?
    ) {
        finalizePendingHistoryTransaction()
        savingJob = trackProgress {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runSuspendCatching {
                    imageGetter.getImage(uri.toString())
                }.getOrNull()?.image?.let { bitmap ->
                    runSuspendCatching {
                        if (handMode) {
                            imageScaler.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = maxBytes,
                                imageFormat = imageFormat,
                                imageScaleMode = imageScaleMode
                            )
                        } else {
                            imageScaler.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = (fileController.getSize(uri.toString()) ?: 0)
                                    .times(presetSelected / 100f)
                                    .toLong(),
                                imageFormat = imageFormat,
                                imageScaleMode = imageScaleMode
                            )
                        }
                    }.getOrNull()?.let { (data, imageInfo) ->

                        results.add(
                            fileController.save(
                                ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = data
                                ),
                                keepOriginalMetadata = keepExif,
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
            _isSaving.value = false
        }
    }

    fun updateSelectedUri(
        uri: Uri
    ) {
        runCatching {
            componentScope.launch {
                updateBitmap(
                    imageGetter.getImage(
                        uri = uri.toString(),
                        originalSize = false
                    )?.image
                )
                _selectedUri.value = uri
            }
        }.onFailure(AppToastHost::showFailureToast)
    }

    private fun updateCanSave(
        register: Boolean = true
    ) {
        _canSave.value =
            _bitmap.value != null && (_maxBytes.value != 0L && _handMode.value || !_handMode.value && _presetSelected.value != -1)

        if (register) {
            registerChanges()
        }
    }

    fun updateMaxBytes(newBytes: String) {
        val b = newBytes.toLongOrNull() ?: 0
        val maxBytes = b * 1024
        if (_maxBytes.value != maxBytes) {
            beginPendingHistoryTransaction()
            _maxBytes.value = maxBytes
            updateCanSave()
            schedulePendingHistoryCommit()
        }
    }

    fun selectPreset(preset: Preset) {
        preset.value()?.let {
            if (_presetSelected.value != it) {
                finalizePendingHistoryTransaction()
                val beforeSnapshot = currentHistorySnapshot()
                _presetSelected.value = it
                updateCanSave(register = false)
                commitHistoryFrom(beforeSnapshot)
            }
        }
    }

    fun updateHandMode() {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _handMode.value = !_handMode.value
        updateCanSave(register = false)
        commitHistoryFrom(beforeSnapshot)
    }

    fun shareBitmaps() {
        savingJob = trackProgress {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageGetter.getImage(uri)?.image?.let { bitmap ->
                        if (handMode) {
                            imageScaler.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = maxBytes,
                                imageFormat = imageFormat,
                                imageScaleMode = imageScaleMode
                            )
                        } else {
                            imageScaler.scaleByMaxBytes(
                                image = bitmap,
                                maxBytes = (fileController.getSize(uri) ?: 0)
                                    .times(presetSelected / 100f)
                                    .toLong(),
                                imageFormat = imageFormat,
                                imageScaleMode = imageScaleMode
                            )
                        }
                    }?.let { (data, imageInfo) ->
                        imageGetter.getImage(data)!! to imageInfo
                    }
                },
                onProgressChange = {
                    if (it == -1) {
                        AppToastHost.showConfetti()
                        _isSaving.value = false
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                    updateProgress(
                        done = done,
                        total = uris.orEmpty().size
                    )
                }
            )
        }
    }

    private var job: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private fun setImageData(imageData: ImageData<Bitmap>) {
        job = componentScope.launch {
            _isImageLoading.value = true
            imageScaler.scaleUntilCanShow(imageData.image)?.let {
                _bitmap.value = imageData.image
                _previewBitmap.value = it
                _imageFormat.value = imageData.imageInfo.imageFormat
                _imageSize.value = imageCompressor.calculateImageSize(
                    image = imageData.image,
                    imageInfo = ImageInfo(
                        width = imageData.image.width,
                        height = imageData.image.height,
                        imageFormat = imageFormat
                    )
                )
            }
            resetHistory()
            registerChangesCleared()
            _isImageLoading.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        if (_imageScaleMode.value != imageScaleMode) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _imageScaleMode.update { imageScaleMode }
            updateCanSave(register = false)
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            selectedUri?.toString()?.let { uri ->
                imageGetter.getImage(uri)?.image?.let { bitmap ->
                    if (handMode) {
                        imageScaler.scaleByMaxBytes(
                            image = bitmap,
                            maxBytes = maxBytes,
                            imageFormat = imageFormat,
                            imageScaleMode = imageScaleMode
                        )
                    } else {
                        imageScaler.scaleByMaxBytes(
                            image = bitmap,
                            maxBytes = (fileController.getSize(uri) ?: 0)
                                .times(presetSelected / 100f)
                                .toLong(),
                            imageFormat = imageFormat,
                            imageScaleMode = imageScaleMode
                        )
                    }
                }?.let { scaled ->
                    scaled.first to scaled.second.copy(imageFormat = imageFormat)
                }?.let { (image, imageInfo) ->
                    shareProvider.cacheByteArray(
                        byteArray = image,
                        filename = filenameCreator.constructImageFilename(
                            ImageSaveTarget(
                                imageInfo = imageInfo,
                                originalUri = uri,
                                sequenceNumber = _done.value + 1,
                                data = image
                            )
                        )
                    )?.let { uri ->
                        onComplete(uri.toUri())
                    }
                }
            }
            _isSaving.value = false
        }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            val list = mutableListOf<Uri>()
            uris?.forEach { uri ->
                imageGetter.getImage(uri.toString())?.image?.let { bitmap ->
                    if (handMode) {
                        imageScaler.scaleByMaxBytes(
                            image = bitmap,
                            maxBytes = maxBytes,
                            imageFormat = imageFormat,
                            imageScaleMode = imageScaleMode
                        )
                    } else {
                        imageScaler.scaleByMaxBytes(
                            image = bitmap,
                            maxBytes = (fileController.getSize(uri.toString()) ?: 0)
                                .times(presetSelected / 100f)
                                .toLong(),
                            imageFormat = imageFormat,
                            imageScaleMode = imageScaleMode
                        )
                    }
                }?.let { scaled ->
                    scaled.first to scaled.second.copy(imageFormat = imageFormat)
                }?.let { (image, imageInfo) ->
                    shareProvider.cacheByteArray(
                        byteArray = image,
                        filename = filenameCreator.constructImageFilename(
                            ImageSaveTarget(
                                imageInfo = imageInfo,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = image
                            )
                        )
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
            _isSaving.value = false
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

    fun getFormatForFilenameSelection(): ImageFormat? =
        if (uris?.size == 1) imageFormat
        else null

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        imageScaleMode = imageScaleMode,
        presetSelected = presetSelected,
        handMode = handMode,
        maxBytes = maxBytes,
        imageFormat = imageFormat,
        keepExif = keepExif,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _imageScaleMode.value = snapshot.imageScaleMode
        _presetSelected.value = snapshot.presetSelected
        _handMode.value = snapshot.handMode
        _maxBytes.value = snapshot.maxBytes
        _imageFormat.value = snapshot.imageFormat
        _keepExif.value = snapshot.keepExif
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        updateCanSave(register = false)
        updateImageSize()
    }

    private fun updateImageSize() {
        componentScope.launch {
            _bitmap.value?.let {
                _isImageLoading.value = true
                _imageSize.value = imageCompressor.calculateImageSize(
                    image = it,
                    imageInfo = ImageInfo(imageFormat = imageFormat)
                )
                _isImageLoading.value = false
            }
        }
    }

    data class HistorySnapshot(
        val imageScaleMode: ImageScaleMode = ImageScaleMode.Default,
        val presetSelected: Int = -1,
        val handMode: Boolean = true,
        val maxBytes: Long = 0L,
        val imageFormat: ImageFormat = ImageFormat.Default,
        val keepExif: Boolean = false,
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): WeightResizeComponent
    }
}
