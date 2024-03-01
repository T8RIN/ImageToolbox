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

package ru.tech.imageresizershrinker.feature.limits_resize.presentation.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.transformation.ImageInfoTransformation
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.limits_resize.domain.LimitsImageScaler
import ru.tech.imageresizershrinker.feature.limits_resize.domain.LimitsResizeType
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LimitsResizeViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: LimitsImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    val imageInfoTransformationFactory: ImageInfoTransformation.Factory,
) : ViewModel() {

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

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageInfo by _imageInfo

    private val _resizeType: MutableState<LimitsResizeType> =
        mutableStateOf(LimitsResizeType.Recode())
    val resizeType by _resizeType

    fun setMime(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
    }

    fun updateUris(
        uris: List<Uri>?,
        onError: (Throwable) -> Unit
    ) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
        if (uris != null) {
            viewModelScope.launch {
                imageGetter.getImageAsync(
                    uri = uris[0].toString(),
                    originalSize = true,
                    onGetImage = {
                        updateBitmap(it.image)
                        setMime(it.imageInfo.imageFormat)
                    },
                    onError = onError
                )
            }
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

    private fun updateBitmap(
        bitmap: Bitmap?,
        preview: Bitmap? = null
    ) {
        viewModelScope.launch {
            _isImageLoading.value = true
            val size = bitmap?.let { it.width to it.height }
            _originalSize.value = size?.run { IntegerSize(width = first, height = second) }
            _bitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            _previewBitmap.value = preview ?: _bitmap.value
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
                            ImageSaveTarget<ExifInterface>(
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
                            ), keepOriginalMetadata = keepExif
                        )
                    )
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
                _isImageLoading.value = true
                updateBitmap(imageGetter.getImage(uri.toString())?.image)
                _selectedUri.value = uri
                _isImageLoading.value = false
            }
        }
    }


    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && (_imageInfo.value.height != 0 && _imageInfo.value.width != 0)
    }

    fun updateWidth(i: Int) {
        _imageInfo.value = _imageInfo.value.copy(width = i)
        updateCanSave()
    }

    fun updateHeight(i: Int) {
        _imageInfo.value = _imageInfo.value.copy(height = i)
        updateCanSave()
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        _isSaving.value = false
        viewModelScope.launch {
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
                        onComplete()
                        _done.value = 0
                        _isSaving.value = false
                    } else {
                        _done.value = it
                    }
                }
            )
        }.also {
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun setQuality(quality: Quality) {
        _imageInfo.value = _imageInfo.value.copy(quality = quality)
    }

    fun setResizeType(resizeType: LimitsResizeType) {
        _resizeType.value = resizeType
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun toggleAutoRotateLimitBox() {
        _resizeType.update { it.copy(!it.autoRotateLimitBox) }
    }

    fun setImageScaleMode(imageScaleMode: ImageScaleMode) {
        _imageInfo.update {
            it.copy(
                imageScaleMode = imageScaleMode
            )
        }
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
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