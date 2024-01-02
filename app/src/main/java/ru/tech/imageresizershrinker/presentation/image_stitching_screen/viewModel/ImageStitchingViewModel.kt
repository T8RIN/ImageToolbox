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
import ru.tech.imageresizershrinker.coredomain.image.ImageManager
import ru.tech.imageresizershrinker.coredomain.model.CombiningParams
import ru.tech.imageresizershrinker.coredomain.model.ImageData
import ru.tech.imageresizershrinker.coredomain.model.ImageFormat
import ru.tech.imageresizershrinker.coredomain.model.ImageInfo
import ru.tech.imageresizershrinker.coredomain.model.IntegerSize
import ru.tech.imageresizershrinker.coredomain.model.StitchMode
import ru.tech.imageresizershrinker.coredomain.saving.FileController
import ru.tech.imageresizershrinker.coredomain.saving.SaveResult
import ru.tech.imageresizershrinker.coredomain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.coreui.utils.state.update
import javax.inject.Inject

@HiltViewModel
class ImageStitchingViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
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

    private val _imageInfo = mutableStateOf(ImageInfo(imageFormat = ImageFormat.Png))
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
                imageManager.createCombinedImagesPreview(
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
        _isSaving.value = false
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
            _isSaving.value = false
        }.also {
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

}