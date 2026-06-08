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

package com.t8rin.imagetoolbox.feature.limits_resize.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
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
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.limits_resize.domain.LimitsImageScaler
import com.t8rin.imagetoolbox.feature.limits_resize.domain.LimitsResizeType
import com.t8rin.imagetoolbox.feature.limits_resize.presentation.screenLogic.LimitsResizeComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class LimitsResizeComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: LimitsImageScaler<Bitmap>,
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

    private val _originalSize: MutableState<IntegerSize?> = mutableStateOf(null)
    val originalSize by _originalSize

    private val _canSave: MutableState<Boolean> = mutableStateOf(false)
    val canSave by _canSave

    private val _uris: MutableState<List<Uri>?> = mutableStateOf(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif: MutableState<Boolean> = mutableStateOf(false)
    val keepExif by _keepExif

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _resizeType: MutableState<LimitsResizeType> =
        mutableStateOf(LimitsResizeType.Recode())
    val resizeType by _resizeType

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageInfo.value.imageFormat != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            beginPendingHistoryTransaction(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            )
            _imageInfo.value = _imageInfo.value.copy(
                imageFormat = imageFormat,
                quality = imageInfo.quality.coerceIn(imageFormat)
            )
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
            componentScope.launch {
                imageGetter.getImageAsync(
                    uri = uris[0].toString(),
                    originalSize = true,
                    onGetImage = {
                        _imageInfo.value = _imageInfo.value.copy(
                            imageFormat = it.imageInfo.imageFormat,
                            quality = imageInfo.quality.coerceIn(it.imageInfo.imageFormat)
                        )
                        updateBitmap(it.image)
                        resetHistory()
                        registerChangesCleared()
                    },
                    onFailure = AppToastHost::showFailureToast
                )
            }
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

    private fun updateBitmap(
        bitmap: Bitmap?,
        preview: Bitmap? = null
    ) {
        componentScope.launch {
            _isImageLoading.value = true
            val size = bitmap?.let { it.width to it.height }
            _originalSize.value = size?.run { IntegerSize(width = first, height = second) }
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _previewBitmap.value = preview ?: _bitmap.value
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
                    imageGetter.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    imageScaler.scaleImage(
                        image = bitmap,
                        width = imageInfo.width,
                        height = imageInfo.height,
                        resizeType = resizeType,
                        imageScaleMode = imageInfo.imageScaleMode
                    )
                }?.let { localBitmap ->
                    results.add(
                        fileController.save(
                            ImageSaveTarget(
                                imageInfo = imageInfo.copy(
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                ),
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageCompressor.compressAndTransform(
                                    image = localBitmap,
                                    imageInfo = imageInfo.copy(
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            ),
                            keepOriginalMetadata = keepExif,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
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
                _isImageLoading.value = true
                updateBitmap(imageGetter.getImage(uri.toString())?.image)
                _selectedUri.value = uri
                _isImageLoading.value = false
            }
        }.onFailure(AppToastHost::showFailureToast)
    }


    private fun updateCanSave(
        register: Boolean = true
    ) {
        _canSave.update {
            _bitmap.value != null && (_imageInfo.value.height != 0 || _imageInfo.value.width != 0)
        }

        if (register) {
            registerChanges()
        }
    }

    fun updateWidth(i: Int) {
        if (_imageInfo.value.width != i) {
            beginPendingHistoryTransaction()
            _imageInfo.value = _imageInfo.value.copy(width = i)
            updateCanSave()
            schedulePendingHistoryCommit()
        }
    }

    fun updateHeight(i: Int) {
        if (_imageInfo.value.height != i) {
            beginPendingHistoryTransaction()
            _imageInfo.value = _imageInfo.value.copy(height = i)
            updateCanSave()
            schedulePendingHistoryCommit()
        }
    }

    fun shareBitmaps() {
        _isSaving.value = false
        savingJob = trackProgress {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageGetter.getImage(uri)?.image?.let { bitmap: Bitmap ->
                        imageScaler.scaleImage(
                            image = bitmap,
                            width = imageInfo.width,
                            height = imageInfo.height,
                            resizeType = resizeType,
                            imageScaleMode = imageInfo.imageScaleMode
                        )
                    }?.let {
                        it to imageInfo.copy(
                            width = it.width,
                            height = it.height
                        )
                    }
                },
                onProgressChange = {
                    if (it == -1) {
                        AppToastHost.showConfetti()
                        _done.value = 0
                        _isSaving.value = false
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

    fun setQuality(quality: Quality) {
        val coercedQuality = quality.coerceIn(imageInfo.imageFormat)
        if (_imageInfo.value.quality != coercedQuality) {
            beginPendingHistoryTransaction()
            _imageInfo.value = _imageInfo.value.copy(quality = coercedQuality)
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun setResizeType(resizeType: LimitsResizeType) {
        if (!_resizeType.value.isSameType(resizeType)) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _resizeType.value = resizeType
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun toggleAutoRotateLimitBox() {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _resizeType.update { it.copy(!it.autoRotateLimitBox) }
        commitHistoryFrom(beforeSnapshot)
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        if (_imageInfo.value.imageScaleMode != imageScaleMode) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _imageInfo.update {
                it.copy(
                    imageScaleMode = imageScaleMode
                )
            }
            commitHistoryFrom(beforeSnapshot)
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            imageGetter.getImage(
                uri = selectedUri.toString()
            )?.image?.let { bitmap ->
                imageScaler.scaleImage(
                    image = bitmap,
                    width = imageInfo.width,
                    height = imageInfo.height,
                    resizeType = resizeType,
                    imageScaleMode = imageInfo.imageScaleMode
                )
            }?.let {
                it to imageInfo.copy(
                    width = it.width,
                    height = it.height
                )
            }?.let { (image, imageInfo) ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = selectedUri.toString())
                )?.let { uri ->
                    onComplete(uri.toUri())
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
                imageGetter.getImage(
                    uri = uri.toString()
                )?.image?.let { bitmap ->
                    imageScaler.scaleImage(
                        image = bitmap,
                        width = imageInfo.width,
                        height = imageInfo.height,
                        resizeType = resizeType,
                        imageScaleMode = imageInfo.imageScaleMode
                    )
                }?.let {
                    it to imageInfo.copy(
                        width = it.width,
                        height = it.height
                    )
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
        if (uris?.size == 1) imageInfo.imageFormat
        else null

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        imageInfo = imageInfo.asHistoryImageInfo(),
        resizeType = resizeType,
        keepExif = keepExif,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _imageInfo.value = snapshot.imageInfo
        _resizeType.value = snapshot.resizeType
        _keepExif.value = snapshot.keepExif
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        updateCanSave(register = false)
    }

    override fun hasSameUndoState(
        first: HistorySnapshot,
        second: HistorySnapshot
    ): Boolean = first.imageInfo == second.imageInfo &&
            first.keepExif == second.keepExif &&
            first.backgroundColorForNoAlphaFormats == second.backgroundColorForNoAlphaFormats &&
            first.resizeType.isSameType(second.resizeType)

    private fun ImageInfo.asHistoryImageInfo(): ImageInfo = copy(
        sizeInBytes = 0,
        originalUri = selectedUri?.toString()
    )

    private fun LimitsResizeType.isSameType(
        other: LimitsResizeType
    ): Boolean = this::class == other::class &&
            autoRotateLimitBox == other.autoRotateLimitBox

    data class HistorySnapshot(
        val imageInfo: ImageInfo = ImageInfo(),
        val resizeType: LimitsResizeType = LimitsResizeType.Recode(),
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
        ): LimitsResizeComponent
    }
}
