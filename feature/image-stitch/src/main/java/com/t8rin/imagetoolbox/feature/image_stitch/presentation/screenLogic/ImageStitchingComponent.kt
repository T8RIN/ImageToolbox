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

package com.t8rin.imagetoolbox.feature.image_stitch.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseHistoryComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.ImageCombiner
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchAlignment
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchFadeSide
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import com.t8rin.imagetoolbox.feature.image_stitch.domain.toParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.toSavable
import com.t8rin.imagetoolbox.feature.image_stitch.presentation.screenLogic.ImageStitchingComponent.HistorySnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ImageStitchingComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageCombiner: ImageCombiner<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseHistoryComponent<HistorySnapshot>(
    dispatchersHolder = dispatchersHolder,
    componentContext = componentContext
) {

    private val _imageSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(0, 0))
    val imageSize by _imageSize

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _imageInfo = mutableStateOf(ImageInfo(imageFormat = ImageFormat.Png.Lossless))
    val imageInfo by _imageInfo

    private val _combiningParams = fileController.savable(
        scope = componentScope,
        initial = CombiningParams().toSavable()
    )
    val combiningParams: CombiningParams get() = _combiningParams.get().toParams()

    private val _imageByteSize: MutableState<Long?> = mutableStateOf(null)
    val imageByteSize by _imageByteSize

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageInfo.value.imageFormat != imageFormat) {
            if (pendingHistoryMode != PendingHistoryMode.FormatChange) {
                finalizePendingHistoryTransaction()
            }
            beginPendingHistoryTransaction(
                mode = PendingHistoryMode.FormatChange,
                commitDelayMillis = formatHistoryTransactionDebounce
            )
            _imageInfo.update { it.copy(imageFormat = imageFormat) }
            registerChanges()
            calculatePreview(true)
            schedulePendingHistoryCommit()
        }
    }

    fun updateUris(uris: List<Uri>?) {
        if (uris != _uris.value) {
            clearHistory()
            registerChangesCleared()
            _uris.value = uris
            if (!uris.isNullOrEmpty()) {
                resetHistory()
            }
            calculatePreview(true)
        }
    }

    fun reorderUris(uris: List<Uri>?) {
        if (uris != _uris.value) {
            finalizePendingHistoryTransaction()
            val beforeSnapshot = currentHistorySnapshot()
            _uris.value = uris
            commitHistoryFrom(beforeSnapshot)
            calculatePreview(true)
        }
    }

    private var calculationPreviewJob: Job? by smartJob {
        _isImageLoading.update { false }
    }

    private var previousParams: CombiningParams? = null

    private fun calculatePreview(force: Boolean = false) {
        if (previousParams == combiningParams && !force) return

        calculationPreviewJob = componentScope.launch {
            delay(300L)
            _isImageLoading.value = true
            uris?.let { uris ->
                registerChanges()
                imageCombiner.createCombinedImagesPreview(
                    imageUris = uris.map { it.toString() },
                    combiningParams = combiningParams,
                    imageFormat = imageInfo.imageFormat,
                    quality = imageInfo.quality,
                    onGetByteCount = {
                        _imageByteSize.value = it
                    }
                ).let { (image, size) ->
                    _previewBitmap.value = image
                    _imageSize.value = size
                    previousParams = combiningParams
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
            imageCombiner.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                onProgress = {
                    _done.value = it
                    updateProgress(
                        done = done,
                        total = uris.orEmpty().size
                    )
                }
            ).let { (image, info) ->
                val imageInfo = info.copy(
                    quality = imageInfo.quality,
                    imageFormat = imageInfo.imageFormat
                )
                parseSaveResult(
                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = imageInfo,
                            metadata = null,
                            originalUri = "Combined",
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
            imageCombiner.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                onProgress = {
                    _done.value = it
                    updateProgress(
                        done = done,
                        total = uris.orEmpty().size
                    )
                }
            ).let {
                it.copy(
                    second = it.second.copy(
                        quality = imageInfo.quality,
                        imageFormat = imageInfo.imageFormat
                    )
                )
            }.let { (image, imageInfo) ->
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
            _imageInfo.update { it.copy(quality = quality) }
            registerChanges()
            calculatePreview(true)
            schedulePendingHistoryCommit()
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun updateImageScale(newScale: Float) {
        updateCombiningParams(combiningParams.copy(outputScale = newScale))
    }

    fun setStitchMode(value: StitchMode) {
        updateCombiningParams(
            combiningParams.copy(
                stitchMode = value,
                scaleSmallImagesToLarge = false
            )
        )
        calculatePreview()
    }

    fun setFadingEdgesMode(mode: StitchFadeSide) {
        updateCombiningParams(
            combiningParams.copy(fadingEdgesMode = mode)
        )
        calculatePreview()
    }

    fun setFadeStrength(value: Float) {
        updateCombiningParams(combiningParams.copy(fadeStrength = value))
        calculatePreview()
    }

    fun updateImageSpacing(spacing: Int) {
        updateCombiningParams(
            combiningParams.copy(
                horizontalSpacing = spacing,
                verticalSpacing = spacing
            )
        )
        calculatePreview()
    }

    fun updateHorizontalImageSpacing(spacing: Int) {
        updateCombiningParams(
            combiningParams.copy(horizontalSpacing = spacing)
        )
        calculatePreview()
    }

    fun updateVerticalImageSpacing(spacing: Int) {
        updateCombiningParams(
            combiningParams.copy(verticalSpacing = spacing)
        )
        calculatePreview()
    }

    fun toggleScaleSmallImagesToLarge(checked: Boolean) {
        updateCombiningParams(
            combiningParams.copy(scaleSmallImagesToLarge = checked)
        )
        calculatePreview()
    }

    fun updateBackgroundSelector(color: Int) {
        updateCombiningParams(
            combiningParams.copy(backgroundColor = color)
        )
        calculatePreview()
    }

    fun addUrisToEnd(uris: List<Uri>) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _uris.update { list ->
            list?.plus(
                uris.filter { it !in list }
            )
        }
        commitHistoryFrom(beforeSnapshot)
        calculatePreview(true)
    }

    fun removeImageAt(index: Int) {
        finalizePendingHistoryTransaction()
        val beforeSnapshot = currentHistorySnapshot()
        _uris.update { list ->
            list?.toMutableList()?.apply {
                removeAt(index)
            }?.takeIf { it.size >= 2 }.also {
                if (it == null) _previewBitmap.value = null
            }
        }
        commitHistoryFrom(beforeSnapshot)
        calculatePreview(true)
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            imageCombiner.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                onProgress = {
                    _done.value = it
                    updateProgress(
                        done = done,
                        total = uris.orEmpty().size
                    )
                }
            ).let {
                it.copy(
                    second = it.second.copy(
                        quality = imageInfo.quality,
                        imageFormat = imageInfo.imageFormat
                    )
                )
            }.let { (image, imageInfo) ->
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

    fun setStitchAlignment(stitchAlignment: StitchAlignment) {
        updateCombiningParams(combiningParams.copy(alignment = stitchAlignment))
        calculatePreview()
    }

    fun setBlendingMode(blendingMode: BlendingMode) {
        updateCombiningParams(combiningParams.copy(blendingMode = blendingMode))
        calculatePreview()
    }

    private fun updateCombiningParams(params: CombiningParams) {
        if (combiningParams != params) {
            beginPendingHistoryTransaction()
            _combiningParams.update { params.toSavable() }
            registerChanges()
            schedulePendingHistoryCommit()
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageInfo.imageFormat

    override fun currentHistorySnapshot(): HistorySnapshot = HistorySnapshot(
        uris = uris,
        imageInfo = imageInfo.asHistoryImageInfo(),
        combiningParams = combiningParams,
        backgroundColorForNoAlphaFormats = settingsManager
            .settingsState
            .value
            .backgroundForNoAlphaImageFormats
    )

    override fun applyHistorySnapshot(snapshot: HistorySnapshot) {
        _uris.update { snapshot.uris }
        _imageInfo.update { current ->
            current.copy(
                imageFormat = snapshot.imageInfo.imageFormat,
                quality = snapshot.imageInfo.quality
            )
        }
        _combiningParams.update { snapshot.combiningParams.toSavable() }
        restoreBackgroundColorForNoAlphaFormats(
            settingsManager = settingsManager,
            backgroundColorForNoAlphaFormats = snapshot.backgroundColorForNoAlphaFormats
        )
        calculatePreview(true)
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
        val uris: List<Uri>? = null,
        val imageInfo: ImageInfo = ImageInfo(),
        val combiningParams: CombiningParams = CombiningParams(),
        val backgroundColorForNoAlphaFormats: ColorModel = ColorModel(-0x1000000)
    )

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ImageStitchingComponent
    }

}