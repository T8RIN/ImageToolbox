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

package com.t8rin.imagetoolbox.feature.format_conversion.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.image.model.ImageData
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
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
import com.t8rin.imagetoolbox.core.ui.transformation.ImageInfoTransformation
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class FormatConversionComponent @AssistedInject internal constructor(
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
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let {
                updateUris(
                    uris = it,
                    onFailure = {}
                )
            }
        }
    }

    private val _originalSize: MutableState<IntegerSize?> = mutableStateOf(null)

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageInfo: ImageInfo by _imageInfo

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(true)
    val shouldShowPreview by _shouldShowPreview

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private var job: Job? by smartJob {
        _isImageLoading.update { false }
    }

    fun updateUris(
        uris: List<Uri>?,
        onFailure: (Throwable) -> Unit
    ) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
        uris?.firstOrNull()?.let { uri ->
            _imageInfo.update {
                it.copy(originalUri = uri.toString())
            }
            imageGetter.getImageAsync(
                uri = uri.toString(),
                originalSize = false,
                onGetImage = ::setImageData,
                onFailure = onFailure
            )
        }
    }

    fun updateUrisSilently(
        removedUri: Uri,
        onFailure: (Throwable) -> Unit
    ) {
        componentScope.launch {
            _uris.value = uris
            if (_selectedUri.value == removedUri) {
                val index = uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    uris?.getOrNull(1)?.let {
                        updateSelectedUri(it, onFailure)
                    }
                } else {
                    uris?.getOrNull(index - 1)?.let {
                        updateSelectedUri(it, onFailure)
                    }
                }
            }
            val u = _uris.value?.toMutableList()?.apply {
                remove(removedUri)
            }
            _uris.value = u

            registerChanges()
        }
    }

    private suspend fun checkBitmapAndUpdate() {
        _bitmap.value?.let { bmp ->
            val preview = updatePreview(bmp)
            _previewBitmap.value = null
            _shouldShowPreview.value = imagePreviewCreator.canShow(preview)
            if (shouldShowPreview) _previewBitmap.value = preview
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap? = imagePreviewCreator.createPreview(
        image = bitmap,
        imageInfo = imageInfo,
        onGetByteCount = { size ->
            _imageInfo.update { it.copy(sizeInBytes = size) }
        }
    )

    private fun resetValues() {
        _imageInfo.value = ImageInfo(
            width = _originalSize.value?.width ?: 0,
            height = _originalSize.value?.height ?: 0,
            imageFormat = imageInfo.imageFormat,
            originalUri = selectedUri?.toString()
        )
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    private fun setImageData(imageData: ImageData<Bitmap>) {
        job = componentScope.launch {
            _isImageLoading.update { true }
            val bitmap = imageData.image
            val size = bitmap.width to bitmap.height
            _originalSize.update {
                size.run { IntegerSize(width = first, height = second) }
            }
            _bitmap.update {
                imageScaler.scaleUntilCanShow(bitmap)
            }
            resetValues()
            _imageInfo.update {
                imageData.imageInfo.copy(
                    width = size.first,
                    height = size.second
                )
            }
            checkBitmapAndUpdate()
            _isImageLoading.update { false }
        }
    }

    fun setQuality(quality: Quality) {
        if (_imageInfo.value.quality != quality) {
            _imageInfo.value = _imageInfo.value.copy(quality = quality)
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            registerChanges()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageInfo.value.imageFormat != imageFormat) {
            _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
            registerChanges()
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
        registerChanges()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runSuspendCatching {
                    imageGetter.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    imageInfo.copy(
                        originalUri = uri.toString()
                    ).let {
                        imageTransformer.applyPresetBy(
                            image = bitmap,
                            preset = Preset.Original,
                            currentInfo = it
                        )
                    }.let { imageInfo ->
                        results.add(
                            fileController.save(
                                saveTarget = ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    metadata = null,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageCompressor.compressAndTransform(
                                        image = bitmap,
                                        imageInfo = imageInfo
                                    ),
                                    canSkipIfLarger = true
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
            onResult(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun updateSelectedUri(
        uri: Uri,
        onFailure: (Throwable) -> Unit = {}
    ) {
        _selectedUri.value = uri
        componentScope.launch {
            runSuspendCatching {
                _isImageLoading.update { true }
                val bitmap = imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = true
                )?.image
                val size = bitmap?.let { it.width to it.height }
                _originalSize.value = size?.run { IntegerSize(width = first, height = second) }
                _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
                _imageInfo.value = _imageInfo.value.copy(
                    width = size?.first ?: 0,
                    height = size?.second ?: 0,
                    originalUri = uri.toString()
                )
                _imageInfo.value = imageTransformer.applyPresetBy(
                    image = _bitmap.value,
                    preset = Preset.Original,
                    currentInfo = _imageInfo.value
                )
                checkBitmapAndUpdate()
                _isImageLoading.update { false }
            }.onFailure {
                _isImageLoading.update { false }
                onFailure(it)
            }
        }
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            shareProvider.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageGetter.getImage(uri)?.image?.let { bmp ->
                        bmp to imageInfo.copy(
                            originalUri = uri
                        ).let {
                            imageTransformer.applyPresetBy(
                                image = bitmap,
                                preset = Preset.Original,
                                currentInfo = it
                            )
                        }
                    }
                },
                onProgressChange = {
                    if (it == -1) {
                        onComplete()
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

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            imageGetter.getImage(selectedUri.toString())?.image?.let { bmp ->
                bmp to imageInfo.copy(
                    originalUri = selectedUri.toString()
                ).let {
                    imageTransformer.applyPresetBy(
                        image = bitmap,
                        preset = Preset.Original,
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
            uris?.forEach {
                imageGetter.getImage(it.toString())?.image?.let { bmp ->
                    bmp to imageInfo.copy(
                        originalUri = it.toString()
                    ).let { info ->
                        imageTransformer.applyPresetBy(
                            image = bitmap,
                            preset = Preset.Original,
                            currentInfo = info
                        )
                    }
                }?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo.copy(originalUri = it.toString())
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

    fun getConversionTransformation() = listOf(
        imageInfoTransformationFactory(
            imageInfo = imageInfo,
            preset = Preset.Original
        )
    )


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): FormatConversionComponent
    }
}