package ru.tech.imageresizershrinker.presentation.image_stitching_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.CombiningParams
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.ImageSize
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.root.utils.state.update
import javax.inject.Inject

@HiltViewModel
class ImageStitchingViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _imageSize: MutableState<ImageSize> = mutableStateOf(ImageSize(0, 0))
    val imageSize by _imageSize

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _imageInfo = mutableStateOf(ImageInfo(imageFormat = ImageFormat.Png))
    val imageInfo by _imageInfo

    private val _combiningParams: MutableState<CombiningParams> = mutableStateOf(CombiningParams())
    val combiningParams by _combiningParams

    private val _imageScale: MutableState<Float> = mutableFloatStateOf(0.5f)
    val imageScale by _imageScale

    fun setMime(imageFormat: ImageFormat) {
        _imageInfo.value = _imageInfo.value.copy(imageFormat = imageFormat)
        calculatePreview()
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris

        calculateImageSize()
        calculatePreview()
    }

    private fun calculateImageSize() {
        viewModelScope.launch {
            uris?.let { uris ->
                _imageSize.value = imageManager.calculateCombinedImageDimensions(
                    uris.map { it.toString() },
                    combiningParams
                )
            }
        }
    }

    private var calculationPreviewJob: Job? = null

    private fun calculatePreview() {
        calculationPreviewJob?.cancel()
        calculationPreviewJob = viewModelScope.launch {
            delay(300L)
            _isImageLoading.value = true
            uris?.let { uris ->
                _previewBitmap.value = imageManager.createCombinedImagesPreview(
                    imageUris = uris.map { it.toString() },
                    combiningParams = combiningParams,
                    imageFormat = imageInfo.imageFormat,
                    quality = imageInfo.quality,
                    onGetByteCount = {}
                )
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
            imageManager.combineImages(
                imageUris = uris?.map { it.toString() } ?: emptyList(),
                combiningParams = combiningParams,
                imageScale = imageScale
            ).let { (image, ii, _) ->
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
                            data = imageManager.compress(
                                ImageData(
                                    image = image,
                                    imageInfo = imageInfo
                                )
                            )
                        ),
                        keepMetadata = true
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

    fun shareBitmaps(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            imageManager.shareImage(
                imageData = imageManager.combineImages(
                    imageUris = uris?.map { it.toString() } ?: emptyList(),
                    combiningParams = combiningParams,
                    imageScale = imageScale
                ).let {
                    it.copy(
                        imageInfo = it.imageInfo.copy(
                            quality = imageInfo.quality,
                            imageFormat = imageInfo.imageFormat
                        )
                    )
                },
                onComplete = onComplete
            )
        }.also {
            _isSaving.value = false
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun setQuality(fl: Float) {
        _imageInfo.value = _imageInfo.value.copy(quality = fl.coerceIn(0f, 100f))
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

    fun toggleIsHorizontal(checked: Boolean) {
        _combiningParams.update {
            it.copy(isHorizontal = checked)
        }
        calculatePreview()
        calculateImageSize()
    }

    fun updateImageSpacing(spacing: Int) {
        _combiningParams.update {
            it.copy(spacing = spacing)
        }
        calculatePreview()
        calculateImageSize()
    }

    fun toggleScaleSmallImagesToLarge(checked: Boolean) {
        _combiningParams.update {
            it.copy(scaleSmallImagesToLarge = checked)
        }
        calculatePreview()
        calculateImageSize()
    }

    fun updateBackgroundSelector(color: Int) {
        _combiningParams.update {
            it.copy(backgroundColor = color)
        }
        calculatePreview()
        calculateImageSize()
    }

}