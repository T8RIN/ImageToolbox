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

package ru.tech.imageresizershrinker.feature.bytes_resize.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.bytes_resize.domain.BytesImageScaler
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class BytesResizeViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageScaler: BytesImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
) : ViewModel() {

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

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

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

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    fun setImageFormat(imageFormat: ImageFormat) {
        if (_imageFormat.value != imageFormat) {
            _imageFormat.value = imageFormat
            viewModelScope.launch {
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
    }

    fun updateUris(
        uris: List<Uri>?,
        onError: (Throwable) -> Unit
    ) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
        if (uris != null) {
            _isImageLoading.value = true
            imageGetter.getImageAsync(
                uri = uris[0].toString(),
                originalSize = true,
                onGetImage = ::setImageData,
                onError = {
                    _isImageLoading.value = false
                    onError(it)
                }
            )
        }
    }

    fun updateUrisSilently(removedUri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
    }


    private fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _bitmap.value = bitmap
            _previewBitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _isImageLoading.value = false
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    private var savingJob: Job? = null

    fun saveBitmaps(
        onResult: (List<SaveResult>, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageGetter.getImage(uri.toString())
                }.getOrNull()?.image?.let { bitmap ->
                    kotlin.runCatching {
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
                                    .times(_presetSelected.value / 100f)
                                    .toLong(),
                                imageFormat = imageFormat,
                                imageScaleMode = imageScaleMode
                            )
                        }
                    }.getOrNull()?.let { scaled ->
                        val localBitmap = scaled.first

                        results.add(
                            fileController.save(
                                ImageSaveTarget<ExifInterface>(
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageCompressor.compressAndTransform(
                                        image = localBitmap,
                                        imageInfo = ImageInfo(
                                            imageFormat = imageFormat,
                                            quality = scaled.second.quality
                                        )
                                    )
                                ),
                                keepOriginalMetadata = keepExif
                            )
                        )
                    }
                } ?: results.add(
                    SaveResult.Error.Exception(Throwable())
                )
                _done.value += 1
            }
            onResult(results, fileController.savingPath)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun setBitmap(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateBitmap(
                    imageGetter.getImage(
                        uri = uri.toString(),
                        originalSize = false
                    )?.image
                )
                _selectedUri.value = uri
            }
        }
    }

    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && (_maxBytes.value != 0L && _handMode.value || !_handMode.value && _presetSelected.value != -1)
    }

    fun updateMaxBytes(newBytes: String) {
        val b = newBytes.toLongOrNull() ?: 0
        _maxBytes.value = b * 1024
        updateCanSave()
    }

    fun selectPreset(preset: Preset) {
        preset.value()?.let { _presetSelected.value = it }
        updateCanSave()
    }

    fun updateHandMode() {
        _handMode.value = !_handMode.value
        updateCanSave()
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
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
                                    .times(_presetSelected.value / 100f)
                                    .toLong(),
                                imageFormat = imageFormat,
                                imageScaleMode = imageScaleMode
                            )
                        }
                    }?.let { scaled ->
                        scaled.first to scaled.second.copy(imageFormat = imageFormat)
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

    private var job: Job? = null

    private fun setImageData(imageData: ImageData<Bitmap, ExifInterface>) {
        job?.cancel()
        _isImageLoading.value = false
        job = viewModelScope.launch {
            _isImageLoading.value = true
            imageScaler.scaleUntilCanShow(imageData.image)?.let {
                _bitmap.value = imageData.image
                _previewBitmap.value = it
                _imageFormat.value = imageData.imageInfo.imageFormat
                _imageSize.value = imageCompressor.calculateImageSize(
                    image = imageData.image,
                    imageInfo = ImageInfo(imageFormat = imageFormat)
                )
            }
            _isImageLoading.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        _imageScaleMode.update { imageScaleMode }
        updateCanSave()
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
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
                                .times(_presetSelected.value / 100f)
                                .toLong(),
                            imageFormat = imageFormat,
                            imageScaleMode = imageScaleMode
                        )
                    }
                }?.let { scaled ->
                    scaled.first to scaled.second.copy(imageFormat = imageFormat)
                }?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo.copy(originalUri = uri),
                        name = Random.nextInt().toString()
                    )?.let { uri ->
                        onComplete(uri.toUri())
                    }
                }
            }
            _isSaving.value = false
        }
    }

}