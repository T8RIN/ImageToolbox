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

package ru.tech.imageresizershrinker.feature.image_stitch.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.image_stitch.domain.CombiningParams
import ru.tech.imageresizershrinker.feature.image_stitch.domain.ImageCombiner
import ru.tech.imageresizershrinker.feature.image_stitch.domain.StitchMode
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ImageStitchingViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageCombiner: ImageCombiner<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val _imageSize: MutableState<IntegerSize> = mutableStateOf(IntegerSize(0, 0))
    val imageSize by _imageSize

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _imageInfo = mutableStateOf(ImageInfo(imageFormat = ImageFormat.PngLossless))
    val imageInfo by _imageInfo

    private val _combiningParams: MutableState<CombiningParams> = mutableStateOf(CombiningParams())
    val combiningParams by _combiningParams

    private val _imageScale: MutableState<Float> = mutableFloatStateOf(0.5f)
    val imageScale by _imageScale

    private val _imageByteSize: MutableState<Int?> = mutableStateOf(null)
    val imageByteSize by _imageByteSize

    fun setMime(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
        calculatePreview()
    }

    fun updateUris(uris: List<Uri>?) {
        if (uris != _uris.value) {
            _uris.value = uris
            calculatePreview()
        }
    }

    private var calculationPreviewJob: Job? = null

    private fun calculatePreview() {
        calculationPreviewJob?.cancel()
        calculationPreviewJob = viewModelScope.launch {
            delay(300L)
            _isImageLoading.value = true
            uris?.let { uris ->
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
                }
            }
            _isImageLoading.value = false
        }
    }

    private var savingJob: Job? = null

    fun saveBitmaps(
        onComplete: (result: SaveResult) -> Unit,
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            imageCombiner.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                imageScale = imageScale
            ).let { (image, ii) ->
                val imageInfo = ii.copy(
                    quality = imageInfo.quality,
                    imageFormat = imageInfo.imageFormat
                )
                onComplete(
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
                        keepOriginalMetadata = true
                    )
                )
            }
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun shareBitmap(onComplete: () -> Unit) {
        _isSaving.value = false
        viewModelScope.launch {
            _isSaving.value = true
            imageCombiner.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                imageScale = imageScale
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
                    onComplete = onComplete
                )
            }
            _isSaving.value = false
        }.also {
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun setQuality(quality: Quality) {
        _imageInfo.value = _imageInfo.value.copy(quality = quality)
        calculatePreview()
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun updateImageScale(newScale: Float) {
        _imageScale.value = newScale
    }

    fun setStitchMode(value: StitchMode) {
        _combiningParams.update {
            it.copy(
                stitchMode = value,
                scaleSmallImagesToLarge = false
            )
        }
        calculatePreview()
    }

    fun setFadingEdgesMode(mode: Int?) {
        _combiningParams.update {
            it.copy(fadingEdgesMode = mode)
        }
        calculatePreview()
    }

    fun updateImageSpacing(spacing: Int) {
        _combiningParams.update {
            it.copy(spacing = spacing)
        }
        calculatePreview()
    }

    fun toggleScaleSmallImagesToLarge(checked: Boolean) {
        _combiningParams.update {
            it.copy(scaleSmallImagesToLarge = checked)
        }
        calculatePreview()
    }

    fun updateBackgroundSelector(color: Int) {
        _combiningParams.update {
            it.copy(backgroundColor = color)
        }
        calculatePreview()
    }

    fun addUrisToEnd(uris: List<Uri>) {
        _uris.update { list ->
            list?.plus(
                uris.filter { it !in list }
            )
        }
        calculatePreview()
    }

    fun removeImageAt(index: Int) {
        _uris.update { list ->
            list?.toMutableList()?.apply {
                removeAt(index)
            }?.takeIf { it.size >= 2 }.also {
                if (it == null) _previewBitmap.value = null
            }
        }
        calculatePreview()
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            imageCombiner.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                imageScale = imageScale
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
                    imageInfo = imageInfo,
                    name = Random.nextInt().toString()
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

}