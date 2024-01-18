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
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject

@HiltViewModel
class LimitsResizeViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
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

    private val _resizeType: MutableState<ResizeType.Limits> =
        mutableStateOf(ResizeType.Limits.Recode())
    val resizeType by _resizeType

    fun setMime(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
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
                            _bitmap.value = imageManager.getImage(it.toString())?.image
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = imageManager.getImage(it.toString())?.image
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

    private fun updateBitmap(bitmap: Bitmap?, preview: Bitmap? = null) {
        viewModelScope.launch {
            _isImageLoading.value = true
            val size = bitmap?.let { it.width to it.height }
            _originalSize.value = size?.run { IntegerSize(width = first, height = second) }
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
            _previewBitmap.value = preview ?: _bitmap.value
            _isImageLoading.value = false
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    private var savingJob: Job? = null

    fun saveBitmaps(
        onResult: (Int, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            var failed = 0
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageManager.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    imageManager.resize(
                        image = bitmap,
                        width = imageInfo.width,
                        height = imageInfo.height,
                        resizeType = resizeType,
                        imageScaleMode = imageInfo.imageScaleMode
                    )
                }?.let { localBitmap ->
                    val result = fileController.save(
                        ImageSaveTarget<ExifInterface>(
                            imageInfo = imageInfo.copy(
                                width = localBitmap.width,
                                height = localBitmap.height,
                                resizeType = resizeType
                            ),
                            originalUri = uri.toString(),
                            sequenceNumber = _done.value + 1,
                            data = imageManager.compress(
                                ImageData(
                                    image = localBitmap,
                                    imageInfo = imageInfo.copy(
                                        width = localBitmap.width,
                                        height = localBitmap.height,
                                        resizeType = resizeType
                                    )
                                )
                            )
                        ), keepMetadata = keepExif
                    )
                    if (result is SaveResult.Error.MissingPermissions) {
                        return@withContext onResult(-1, "")
                    }
                } ?: {
                    failed += 1
                }


                _done.value += 1
            }
            onResult(failed, fileController.savingPath)
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
                updateBitmap(imageManager.getImage(uri.toString())?.image)
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
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImage(uri)?.image?.let { bitmap: Bitmap ->
                        imageManager.resize(
                            image = bitmap,
                            width = imageInfo.width,
                            height = imageInfo.height,
                            resizeType = resizeType,
                            imageScaleMode = imageInfo.imageScaleMode
                        )
                    }?.let {
                        ImageData(
                            image = it,
                            imageInfo = imageInfo.copy(
                                width = it.width,
                                height = it.height,
                                resizeType = resizeType
                            )
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

    fun decodeBitmapFromUri(uri: Uri, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            imageManager.getImageAsync(
                uri = uri.toString(),
                originalSize = true,
                onGetImage = {
                    updateBitmap(it.image)
                    setMime(it.imageInfo.imageFormat)
                },
                onError = onError
            )
        }
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

    fun setQuality(fl: Float) {
        _imageInfo.value = _imageInfo.value.copy(quality = fl.coerceIn(0f, 100f))
    }

    fun setResizeType(resizeType: ResizeType.Limits) {
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

}