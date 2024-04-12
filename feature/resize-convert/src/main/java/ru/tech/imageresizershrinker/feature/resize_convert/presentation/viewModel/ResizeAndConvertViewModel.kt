/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.resize_convert.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImagePreviewCreator
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageData
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.Metadata
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.ui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ResizeAndConvertViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imagePreviewCreator: ImagePreviewCreator<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
    dispatchersHolder: DispatchersHolder
) : BaseViewModel(dispatchersHolder) {

    private val _originalSize: MutableState<IntegerSize?> = mutableStateOf(null)
    val originalSize by _originalSize

    private val _exif: MutableState<ExifInterface?> = mutableStateOf(null)
    val exif by _exif

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

    private var job: Job? = null

    fun updateUris(
        uris: List<Uri>?,
        onError: (Throwable) -> Unit
    ) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
        _presetSelected.value = Preset.None
        uris?.firstOrNull()?.let { uri ->
            viewModelScope.launch {
                _imageInfo.update {
                    it.copy(originalUri = uri.toString())
                }
                imageGetter.getImageAsync(
                    uri = uri.toString(),
                    originalSize = true,
                    onGetImage = ::setImageData,
                    onError = onError
                )
            }
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        viewModelScope.launch {
            withContext(defaultDispatcher) {
                _uris.value = uris
                if (_selectedUri.value == removedUri) {
                    val index = uris?.indexOf(removedUri) ?: -1
                    if (index == 0) {
                        uris?.getOrNull(1)?.let {
                            setBitmap(it)
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            setBitmap(it)
                        }
                    }
                }
                val u = _uris.value?.toMutableList()?.apply {
                    remove(removedUri)
                }
                _uris.value = u
            }
        }
    }

    private suspend fun checkBitmapAndUpdate(
        resetPreset: Boolean = false
    ) {
        if (resetPreset) {
            _presetSelected.value = Preset.None
        }
        _bitmap.value?.let { bmp ->
            val preview = updatePreview(bmp)
            _previewBitmap.value = null
            _shouldShowPreview.value = imagePreviewCreator.canShow(preview)
            if (shouldShowPreview) _previewBitmap.value = preview
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap? = withContext(defaultDispatcher) {
        return@withContext imageInfo.run {
            _showWarning.value = width * height * 4L >= 10_000 * 10_000 * 3L
            imagePreviewCreator.createPreview(
                image = bitmap,
                imageInfo = this,
                onGetByteCount = {
                    _imageInfo.value = _imageInfo.value.copy(sizeInBytes = it)
                }
            )
        }
    }

    private fun setBitmapInfo(newInfo: ImageInfo) {
        if (_imageInfo.value != newInfo) {
            _imageInfo.value = newInfo
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
        }
    }

    fun resetValues(
        saveMime: Boolean = false,
        resetPreset: Boolean = true
    ) {
        _imageInfo.value = ImageInfo(
            width = _originalSize.value?.width ?: 0,
            height = _originalSize.value?.height ?: 0,
            imageFormat = if (saveMime) {
                imageInfo.imageFormat
            } else ImageFormat.Default(),
            originalUri = selectedUri?.toString()
        )
        debouncedImageCalculation {
            checkBitmapAndUpdate(
                resetPreset = resetPreset
            )
        }
    }

    private fun setImageData(imageData: ImageData<Bitmap, ExifInterface>) {
        job?.cancel()
        _isImageLoading.value = false
        job = viewModelScope.launch {
            _isImageLoading.value = true
            updateExif(imageData.metadata)
            val bitmap = imageData.image
            val size = bitmap.width to bitmap.height
            _originalSize.value = size.run { IntegerSize(width = first, height = second) }
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            resetValues(true)
            _imageInfo.value = imageData.imageInfo.copy(
                width = size.first,
                height = size.second
            )
            checkBitmapAndUpdate(
                resetPreset = _presetSelected.value == Preset.Telegram && imageData.imageInfo.imageFormat != ImageFormat.Png.Lossless
            )
            _isImageLoading.value = false
        }
    }

    fun rotateLeft() {
        _imageInfo.value = _imageInfo.value.run {
            copy(
                rotationDegrees = _imageInfo.value.rotationDegrees - 90f,
                height = width,
                width = height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    fun rotateRight() {
        _imageInfo.value = _imageInfo.value.run {
            copy(
                rotationDegrees = _imageInfo.value.rotationDegrees + 90f,
                height = width,
                width = height
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    fun flip() {
        _imageInfo.value = _imageInfo.value.copy(isFlipped = !_imageInfo.value.isFlipped)
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    fun updateWidth(width: Int) {
        if (_imageInfo.value.width != width) {
            _imageInfo.value = _imageInfo.value.copy(width = width)
            debouncedImageCalculation {
                checkBitmapAndUpdate(true)
            }
        }
    }

    fun updateHeight(height: Int) {
        if (_imageInfo.value.height != height) {
            _imageInfo.value = _imageInfo.value.copy(height = height)
            debouncedImageCalculation {
                checkBitmapAndUpdate(true)
            }
        }
    }

    fun setQuality(quality: Quality) {
        if (_imageInfo.value.quality != quality) {
            _imageInfo.value = _imageInfo.value.copy(quality = quality)
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageInfo.value.imageFormat != imageFormat) {
            _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
            debouncedImageCalculation {
                checkBitmapAndUpdate(
                    resetPreset = _presetSelected.value == Preset.Telegram && imageFormat != ImageFormat.Png.Lossless
                )
            }
        }
    }

    fun setResizeType(type: ResizeType) {
        if (_imageInfo.value.resizeType != type) {
            _imageInfo.update {
                it.copy(
                    resizeType = type.withOriginalSizeIfCrop(originalSize)
                )
            }
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    private var savingJob: Job? = null

    fun saveBitmaps(
        onComplete: (List<SaveResult>, path: String) -> Unit
    ) = viewModelScope.launch {
        withContext(defaultDispatcher) {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageGetter.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    imageInfo.copy(
                        originalUri = uri.toString()
                    ).let {
                        imageTransformer.applyPresetBy(
                            image = bitmap,
                            preset = _presetSelected.value,
                            currentInfo = it
                        )
                    }.let { imageInfo ->
                        results.add(
                            fileController.save(
                                saveTarget = ImageSaveTarget(
                                    imageInfo = imageInfo,
                                    metadata = if (uris!!.size == 1) exif else null,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageCompressor.compressAndTransform(
                                        image = bitmap,
                                        imageInfo = imageInfo
                                    )
                                ),
                                keepOriginalMetadata = if (uris!!.size == 1) true else keepExif
                            )
                        )
                    }
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )

                _done.value += 1
            }
            onComplete(results, fileController.savingPath)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun setBitmap(uri: Uri) {
        _selectedUri.value = uri
        viewModelScope.launch {
            withContext(defaultDispatcher) {
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
                    preset = _presetSelected.value,
                    currentInfo = _imageInfo.value
                )
                checkBitmapAndUpdate()
                _isImageLoading.update { false }
            }
        }
    }

    fun updatePreset(preset: Preset) {
        viewModelScope.launch {
            setBitmapInfo(
                imageTransformer.applyPresetBy(
                    image = _bitmap.value,
                    preset = preset,
                    currentInfo = _imageInfo.value
                )
            )
            _presetSelected.value = preset
        }
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
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
                                preset = _presetSelected.value,
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
                }
            )
        }
    }

    fun canShow(): Boolean = bitmap?.let { imagePreviewCreator.canShow(it) } ?: false

    fun clearExif() {
        val t = _exif.value
        Metadata.metaTags.forEach {
            t?.setAttribute(it, null)
        }
        _exif.value = t
    }

    fun updateExif(exifInterface: ExifInterface?) {
        _exif.value = exifInterface
    }

    fun removeExifTag(tag: String) {
        val exifInterface = _exif.value
        exifInterface?.setAttribute(tag, null)
        updateExif(exifInterface)
    }

    fun updateExifByTag(
        tag: String,
        value: String
    ) {
        val exifInterface = _exif.value
        exifInterface?.setAttribute(tag, value)
        updateExif(exifInterface)
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        _imageInfo.update {
            it.copy(
                imageScaleMode = imageScaleMode
            )
        }
        debouncedImageCalculation {
            checkBitmapAndUpdate()
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            imageGetter.getImage(selectedUri.toString())?.image?.let { bmp ->
                bmp to imageInfo.copy(
                    originalUri = selectedUri.toString()
                ).let {
                    imageTransformer.applyPresetBy(
                        image = bitmap,
                        preset = _presetSelected.value,
                        currentInfo = it
                    )
                }
            }?.let { (image, imageInfo) ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = imageInfo.copy(originalUri = selectedUri.toString()),
                    name = Random.nextInt().toString()
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

}