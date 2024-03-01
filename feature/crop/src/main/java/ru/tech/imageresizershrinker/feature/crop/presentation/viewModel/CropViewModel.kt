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

package ru.tech.imageresizershrinker.feature.crop.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CropViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val _selectedAspectRatio: MutableState<DomainAspectRatio> =
        mutableStateOf(DomainAspectRatio.Free)
    val selectedAspectRatio by _selectedAspectRatio

    private val _cropProperties = mutableStateOf(
        CropDefaults.properties(
            cropOutlineProperty = CropOutlineProperty(
                OutlineType.Rect,
                RectCropShape(
                    id = 0,
                    title = OutlineType.Rect.name
                )
            ),
            fling = true
        )
    )
    val cropProperties by _cropProperties

    private val _uri = mutableStateOf(Uri.EMPTY)

    private var internalBitmap = mutableStateOf<Bitmap?>(null)

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    val isBitmapChanged get() = internalBitmap.value != _bitmap.value

    private val _imageFormat = mutableStateOf<ImageFormat>(ImageFormat.PngLossless)
    val imageFormat by _imageFormat

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    fun updateBitmap(
        bitmap: Bitmap?,
        newBitmap: Boolean = false
    ) {
        viewModelScope.launch {
            _isImageLoading.value = true
            val bmp = imageScaler.scaleUntilCanShow(bitmap)
            if (newBitmap) {
                internalBitmap.value = bmp
            }
            _bitmap.value = bmp
            _isImageLoading.value = false
        }
    }

    fun updateMimeType(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        bitmap: Bitmap? = _bitmap.value,
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            bitmap?.let { localBitmap ->
                val byteArray = imageCompressor.compressAndTransform(
                    image = localBitmap,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = localBitmap.width,
                        height = localBitmap.height
                    )
                )

                val decoded = imageGetter.getImage(data = byteArray)

                _bitmap.value = decoded

                onComplete(
                    fileController.save(
                        saveTarget = ImageSaveTarget<ExifInterface>(
                            imageInfo = ImageInfo(
                                imageFormat = imageFormat,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = _uri.value.toString(),
                            sequenceNumber = null,
                            data = byteArray
                        ),
                        keepOriginalMetadata = false
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

    fun setCropAspectRatio(
        domainAspectRatio: DomainAspectRatio,
        aspectRatio: AspectRatio
    ) {
        _cropProperties.value = _cropProperties.value.copy(
            aspectRatio = aspectRatio.takeIf {
                domainAspectRatio != DomainAspectRatio.Original
            } ?: _bitmap.value?.let {
                AspectRatio(it.width.toFloat() / it.height)
            } ?: aspectRatio,
            fixedAspectRatio = domainAspectRatio != DomainAspectRatio.Free
        )
        _selectedAspectRatio.update { domainAspectRatio }
    }

    fun setCropMask(cropOutlineProperty: CropOutlineProperty) {
        _cropProperties.value =
            _cropProperties.value.copy(cropOutlineProperty = cropOutlineProperty)
    }

    fun resetBitmap() {
        _bitmap.value = internalBitmap.value
    }

    fun imageCropStarted() {
        _isImageLoading.value = true
    }

    fun imageCropFinished() {
        _isImageLoading.value = false
    }

    fun setUri(
        uri: Uri,
        onError: (Throwable) -> Unit
    ) {
        _uri.value = uri
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = true,
            onGetImage = {
                updateBitmap(it.image, true)
                updateMimeType(it.imageInfo.imageFormat)
            },
            onError = onError
        )
    }

    fun shareBitmap(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            bitmap?.let { localBitmap ->
                shareProvider.shareImage(
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = localBitmap.width,
                        height = localBitmap.height
                    ),
                    image = localBitmap,
                    onComplete = {
                        _isSaving.value = false
                        onComplete()
                    }
                )
            }
        }
    }

    suspend fun loadImage(uri: Uri): Bitmap? = imageGetter.getImage(data = uri)

    fun cancelSaving() {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = null
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            bitmap?.let { image ->
                shareProvider.cacheImage(
                    image = image,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = image.width,
                        height = image.height
                    ),
                    name = Random.nextInt().toString()
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
            _isSaving.value = false
        }
    }

}
