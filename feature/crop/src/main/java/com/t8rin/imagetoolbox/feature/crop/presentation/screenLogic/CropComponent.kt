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

package com.t8rin.imagetoolbox.feature.crop.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.IntSize
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.cropper.model.AspectRatio
import com.t8rin.cropper.model.OutlineType
import com.t8rin.cropper.model.RectCropShape
import com.t8rin.cropper.settings.CropDefaults
import com.t8rin.cropper.settings.CropOutlineProperty
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.crop.presentation.components.CropType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class CropComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    private val _selectedAspectRatio: MutableState<DomainAspectRatio> =
        mutableStateOf(DomainAspectRatio.Free)
    val selectedAspectRatio by _selectedAspectRatio

    private val defaultOutline = CropOutlineProperty(
        OutlineType.Rect,
        RectCropShape(
            id = 0,
            title = OutlineType.Rect.name
        )
    )

    private val _cropProperties = mutableStateOf(
        CropDefaults.properties(
            cropOutlineProperty = defaultOutline,
            fling = true
        )
    )
    val cropProperties by _cropProperties

    private val _cropType: MutableState<CropType> = mutableStateOf(CropType.Default)
    val cropType: CropType by _cropType

    private val _uri = mutableStateOf(Uri.EMPTY)
    private val _currentUri = mutableStateOf(Uri.EMPTY)
    val currentUri: Uri?
        get() = _currentUri.value.takeIf { it != Uri.EMPTY }

    private val _originalImageSize = mutableStateOf(IntSize.Zero)

    private val _imageSize = mutableStateOf(IntSize.Zero)
    val imageSize: IntSize by _imageSize

    private var internalBitmap = mutableStateOf<Bitmap?>(null)

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    val isBitmapChanged get() = _currentUri.value != _uri.value

    private val _imageFormat = mutableStateOf<ImageFormat>(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _saveExif: MutableState<Boolean> = mutableStateOf(false)
    val saveExif: Boolean by _saveExif

    private var isBitmapLoading = false
    private var isCropperLoading = false

    private fun updateImageLoadingState() {
        _isImageLoading.value = isBitmapLoading || isCropperLoading
    }

    fun updateBitmap(
        bitmap: Bitmap?,
        newBitmap: Boolean = false
    ) {
        componentScope.launch {
            isBitmapLoading = true
            updateImageLoadingState()
            try {
                val bmp = imageScaler.scaleUntilCanShow(bitmap)
                if (newBitmap) {
                    internalBitmap.value = bmp
                }
                _bitmap.value = bmp
            } finally {
                isBitmapLoading = false
                updateImageLoadingState()
            }
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    fun setSaveExif(saveExif: Boolean) {
        _saveExif.value = saveExif
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmap(
        oneTimeSaveLocationUri: String?
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            currentUri?.let { uri ->
                imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = true
                )?.image?.let { localBitmap ->
                    val imageInfo = ImageInfo(
                        originalUri = _uri.value.toString(),
                        imageFormat = imageFormat,
                        width = localBitmap.width,
                        height = localBitmap.height
                    )
                    val byteArray = imageCompressor.compressAndTransform(
                        image = localBitmap,
                        imageInfo = imageInfo
                    )

                    parseSaveResult(
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = imageInfo,
                                originalUri = _uri.value.toString(),
                                sequenceNumber = null,
                                data = byteArray
                            ),
                            keepOriginalMetadata = saveExif,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                }
            }
            _isSaving.value = false
        }
    }

    fun setCropAspectRatio(
        domainAspectRatio: DomainAspectRatio,
        aspectRatio: AspectRatio
    ) {
        _cropProperties.update { properties ->
            properties.copy(
                aspectRatio = aspectRatio.takeIf {
                    domainAspectRatio != DomainAspectRatio.Original
                } ?: _bitmap.value?.let {
                    AspectRatio(it.safeAspectRatio)
                } ?: aspectRatio,
                fixedAspectRatio = domainAspectRatio != DomainAspectRatio.Free
            )
        }
        _selectedAspectRatio.update { domainAspectRatio }
    }

    fun setCropMask(cropOutlineProperty: CropOutlineProperty) {
        _cropProperties.update { it.copy(cropOutlineProperty = cropOutlineProperty) }

        if (cropOutlineProperty.cropOutline.id == 0) {
            _cropType.update { CropType.Default }
        } else {
            _cropType.update { CropType.NoRotation }
        }
    }

    fun toggleFreeCornersCrop() {
        _cropType.update {
            if (it != CropType.FreeCorners) {
                CropType.FreeCorners
            } else if (cropProperties.cropOutlineProperty.cropOutline.id != defaultOutline.cropOutline.id) {
                CropType.NoRotation
            } else {
                CropType.Default
            }
        }
    }

    fun resetBitmap() {
        _currentUri.value = _uri.value
        _imageSize.value = _originalImageSize.value
        _bitmap.value = internalBitmap.value
    }

    fun imageCropStarted() {
        isCropperLoading = true
        updateImageLoadingState()
    }

    fun imageCropFinished() {
        isCropperLoading = false
        updateImageLoadingState()
    }

    fun setUri(
        uri: Uri
    ) {
        _uri.value = uri
        _currentUri.value = uri
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = true,
            onGetImage = {
                val size = IntSize(it.imageInfo.width, it.imageInfo.height)
                _originalImageSize.value = size
                _imageSize.value = size
                updateBitmap(it.image, true)
                setImageFormat(it.imageInfo.imageFormat)
            },
            onFailure = AppToastHost::showFailureToast
        )
    }

    fun updateImageUri(uri: Uri) {
        _currentUri.value = uri
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = true,
            onGetImage = {
                _imageSize.value = IntSize(it.imageInfo.width, it.imageInfo.height)
                updateBitmap(it.image)
            },
            onFailure = AppToastHost::showFailureToast
        )
    }

    fun shareBitmap() {
        savingJob = trackProgress {
            _isSaving.value = true
            currentUri?.let { uri ->
                imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = true
                )?.image?.let { image ->
                    shareProvider.shareImage(
                        imageInfo = ImageInfo(
                            originalUri = _uri.value.toString(),
                            imageFormat = imageFormat,
                            width = image.width,
                            height = image.height
                        ),
                        image = image,
                        onComplete = {
                            _isSaving.value = false
                            AppToastHost.showConfetti()
                        }
                    )
                }
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
        savingJob = trackProgress {
            _isSaving.value = true
            currentUri?.let { uri ->
                imageGetter.getImage(
                    uri = uri.toString(),
                    originalSize = true
                )?.image?.let { image ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = ImageInfo(
                            originalUri = _uri.value.toString(),
                            imageFormat = imageFormat,
                            width = image.width,
                            height = image.height
                        )
                    )?.let { cachedUri ->
                        onComplete(cachedUri.toUri())
                    }
                }
            }
            _isSaving.value = false
        }
    }

    fun getFormatForFilenameSelection(): ImageFormat = imageFormat


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): CropComponent
    }
}
