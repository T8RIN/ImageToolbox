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

package com.t8rin.imagetoolbox.feature.image_stacking.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.image_stacking.domain.ImageStacker
import com.t8rin.imagetoolbox.feature.image_stacking.domain.StackImage
import com.t8rin.imagetoolbox.feature.image_stacking.domain.StackingParams
import com.t8rin.imagetoolbox.feature.image_stacking.domain.toStackImage
import com.t8rin.imagetoolbox.feature.image_stacking.presentation.screenLogic.ImageStackingComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ImageStackingComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageStacker: ImageStacker<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _stackImages: MutableState<List<StackImage>> = mutableStateOf(emptyList())
    val stackImages by _stackImages

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    fun updateUris(uris: List<Uri>) {
        clearHistory()
        registerChangesCleared()
        _stackImages.value = uris.map { it.toString().toStackImage() }
        if (uris.isNotEmpty()) {
            resetHistory()
        }
        if (uris.isNotEmpty()) {
            calculatePreview()
        }
    }

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _stackingParams: MutableState<StackingParams> =
        mutableStateOf(StackingParams.Default)
    val stackingParams by _stackingParams

    private val _imageInfo = mutableStateOf(ImageInfo(imageFormat = ImageFormat.Png.Lossless))
    val imageInfo by _imageInfo

    private val _imageByteSize: MutableState<Int?> = mutableStateOf(null)
    val imageByteSize by _imageByteSize

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageInfo.value.imageFormat != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            beginPendingHistoryTransaction(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            )
            _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
            registerChanges()
            calculatePreview()
            schedulePendingHistoryCommit()
        }
    }

    private var calculationPreviewJob: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private fun calculatePreview() {
        calculationPreviewJob = componentScope.launch {
            delay(300L)
            _isImageLoading.value = true
            stackImages.takeIf { it.isNotEmpty() }?.let {
                registerChanges()
                imageStacker.stackImagesPreview(
                    stackImages = stackImages,
                    stackingParams = stackingParams,
                    imageFormat = imageInfo.imageFormat,
                    quality = imageInfo.quality,
                    onGetByteCount = {
                        _imageByteSize.update { it }
                    }
                ).let { image ->
                    _previewBitmap.value = image
                }
            }
            _isImageLoading.value = false
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            imageStacker.stackImages(
                stackImages = stackImages,
                stackingParams = stackingParams,
                onFailure = {
                    parseSaveResult(SaveResult.Error.Exception(it))
                },
                onProgress = {
                    _done.value = it
                    updateProgress(
                        done = done,
                        total = stackImages.size
                    )
                }
            )?.let { image ->
                val imageInfo = ImageInfo(
                    height = image.height,
                    width = image.width,
                    quality = imageInfo.quality,
                    imageFormat = imageInfo.imageFormat
                )
                parseSaveResult(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            metadata = null,
                            originalUri = "Stacked",
                            sequenceNumber = null,
                            data = imageCompressor.compressAndTransform(
                                image = image,
                                imageInfo = imageInfo
                            )
                        ),
                        keepOriginalMetadata = true,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    ).onSuccess(::registerSave)
                )
            }
            _isSaving.value = false
        }
    }

    fun shareBitmap() {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            imageStacker.stackImages(
                stackImages = stackImages,
                stackingParams = stackingParams,
                onProgress = {
                    _done.value = it
                    updateProgress(
                        done = done,
                        total = stackImages.size
                    )
                },
                onFailure = AppToastHost::showFailureToast
            )?.let { image ->
                val imageInfo = ImageInfo(
                    height = image.height,
                    width = image.width,
                    quality = imageInfo.quality,
                    imageFormat = imageInfo.imageFormat
                )
                shareProvider.shareImage(
                    image = image,
                    imageInfo = imageInfo,
                    onComplete = AppToastHost::showConfetti
                )
            }
            _isSaving.value = false
        }
    }

    fun setQuality(quality: Quality) {
        if (_imageInfo.value.quality != quality) {
            beginPendingHistoryTransaction()
            _imageInfo.value = _imageInfo.value.copy(quality = quality)
            registerChanges()
            calculatePreview()
            schedulePendingHistoryCommit()
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun addUrisToEnd(uris: List<Uri>) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _stackImages.update { list ->
            list + uris.map { it.toString().toStackImage() }.filter { it !in list }
        }
        commitHistoryFrom(beforeSnapshot)
        calculatePreview()
    }

    fun removeImageAt(index: Int) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _stackImages.update { list ->
            list.toMutableList().apply {
                removeAt(index)
            }.takeIf { it.size >= 2 }.also {
                if (it == null) _previewBitmap.value = null
            } ?: emptyList()
        }
        commitHistoryFrom(beforeSnapshot)
        calculatePreview()
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            imageStacker.stackImages(
                stackImages = stackImages,
                stackingParams = stackingParams,
                onProgress = {
                    _done.value = it
                    updateProgress(
                        done = done,
                        total = stackImages.size
                    )
                },
                onFailure = {}
            )?.let { image ->
                val imageInfo = ImageInfo(
                    height = image.height,
                    width = image.width,
                    quality = imageInfo.quality,
                    imageFormat = imageInfo.imageFormat
                )

                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

    fun updateParams(
        newParams: StackingParams
    ) {
        if (_stackingParams.value != newParams) {
            beginPendingHistoryTransaction()
            _stackingParams.update { newParams }
            registerChanges()
            calculatePreview()
            schedulePendingHistoryCommit()
        }
    }

    fun updateStackImage(
        value: StackImage,
        index: Int
    ) {
        beginPendingHistoryTransaction()
        val list = stackImages.toMutableList()
        runCatching {
            list[index] = value
            _stackImages.update { list }
        }.onFailure(AppToastHost::showFailureToast)

        registerChanges()
        calculatePreview()
        schedulePendingHistoryCommit()
    }

    fun reorderUris(uris: List<Uri>) {
        if (stackImages.map { it.uri } != uris) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _stackImages.update { stack ->
                val stackOrder = uris.map { it.toString() }
                val data = stack.associateBy { it.uri }
                val leftStack = stack.filter { it.uri !in stackOrder }
                (leftStack + stackOrder.mapNotNull { data[it] }).distinct()
            }
            commitHistoryFrom(beforeSnapshot)
            calculatePreview()
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageInfo.imageFormat

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        stackImages = stackImages,
        stackingParams = stackingParams,
        imageInfo = imageInfo.asHistoryImageInfo(),
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _stackImages.update { snapshot.stackImages }
        _stackingParams.update { snapshot.stackingParams }
        _imageInfo.update { current ->
            current.copy(
                imageFormat = snapshot.imageInfo.imageFormat,
                quality = snapshot.imageInfo.quality
            )
        }
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        calculatePreview()
    }

    override fun hasSameUndoState(
        first: HistorySnapshot,
        second: HistorySnapshot
    ): Boolean = first.normalized() == second.normalized()

    private fun ImageInfo.asHistoryImageInfo(): ImageInfo = ImageInfo(
        imageFormat = imageFormat,
        quality = quality
    )

    private fun HistorySnapshot.normalized(): HistorySnapshot = copy(
        imageInfo = imageInfo.asHistoryImageInfo()
    )

    data class HistorySnapshot(
        val stackImages: List<StackImage> = emptyList(),
        val stackingParams: StackingParams = StackingParams.Default,
        val imageInfo: ImageInfo = ImageInfo(),
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ImageStackingComponent
    }

}