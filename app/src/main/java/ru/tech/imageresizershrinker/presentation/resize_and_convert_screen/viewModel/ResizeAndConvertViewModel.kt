package ru.tech.imageresizershrinker.presentation.resize_and_convert_screen.viewModel

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Metadata
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.IntegerSize
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import javax.inject.Inject

@HiltViewModel
class ResizeAndConvertViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

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

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

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

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
        _presetSelected.value = Preset.None
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
                            setBitmap(it)
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
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
        job?.cancel()
        _isImageLoading.value = false
        job = viewModelScope.launch {
            _isImageLoading.value = true
            delay(600)
            _bitmap.value?.let { bmp ->
                val preview = updatePreview(bmp)
                _previewBitmap.value = null
                _shouldShowPreview.value = imageManager.canShow(preview)
                if (shouldShowPreview) _previewBitmap.value = preview
            }
            _isImageLoading.value = false
        }
    }

    private fun debouncedImageCalculation(
        onFinish: suspend () -> Unit = {},
        delay: Long = 600L,
        action: suspend () -> Unit
    ) {
        job?.cancel()
        _isImageLoading.value = false
        job = viewModelScope.launch {
            _isImageLoading.value = true
            delay(delay)
            action()
            _isImageLoading.value = false
            onFinish()
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap = withContext(Dispatchers.IO) {
        return@withContext imageInfo.run {
            _showWarning.value = width * height * 4L >= 10_000 * 10_000 * 3L
            imageManager.createFilteredPreview(
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
            _imageInfo.value = newInfo.copy(quality = imageInfo.quality)
            debouncedImageCalculation {
                checkBitmapAndUpdate()
            }
        }
    }

    fun resetValues(saveMime: Boolean = false, resetPreset: Boolean = true) {
        _imageInfo.value = ImageInfo(
            width = _bitmap.value?.width ?: 0,
            height = _bitmap.value?.height ?: 0,
            imageFormat = if (saveMime) {
                imageInfo.imageFormat
            } else ImageFormat.Default()
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
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
            resetValues(true)
            _imageInfo.value = imageData.imageInfo.copy(
                width = size.first,
                height = size.second
            )
            checkBitmapAndUpdate(
                resetPreset = _presetSelected.value == Preset.Telegram && imageData.imageInfo.imageFormat != ImageFormat.Png
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

    fun setQuality(quality: Float) {
        if (_imageInfo.value.quality != quality) {
            _imageInfo.value = _imageInfo.value.copy(quality = quality.coerceIn(0f, 100f))
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
                    resetPreset = _presetSelected.value == Preset.Telegram && imageFormat != ImageFormat.Png
                )
            }
        }
    }

    fun setResizeType(type: ResizeType) {
        if (_imageInfo.value.resizeType != type) {
            _imageInfo.value = _imageInfo.value.copy(resizeType = type)
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
        onComplete: (path: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            _done.value = 0
            uris?.forEach { uri ->
                runCatching {
                    imageManager.getImage(uri.toString())?.image
                }.getOrNull()?.let { bitmap ->
                    imageInfo.let {
                        imageManager.applyPresetBy(
                            image = bitmap,
                            preset = _presetSelected.value,
                            currentInfo = it
                        )
                    }.apply {
                        val result = fileController.save(
                            ImageSaveTarget(
                                imageInfo = this,
                                metadata = if (uris!!.size == 1) exif else null,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageManager.compress(
                                    ImageData(
                                        image = bitmap,
                                        imageInfo = imageInfo,
                                        metadata = if (uris!!.size == 1) exif else null
                                    )
                                )
                            ), if (uris!!.size == 1) true else keepExif
                        )
                        if (result is SaveResult.Error.MissingPermissions) {
                            return@withContext onComplete("")
                        }
                    }
                }
                _done.value += 1
            }
            onComplete(fileController.savingPath)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun setBitmap(
        uri: Uri
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val bitmap = imageManager.getImage(
                    uri = uri.toString(),
                    originalSize = false
                )?.image
                val size = bitmap?.let { it.width to it.height }
                _originalSize.value = size?.run { IntegerSize(width = first, height = second) }
                _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
                _imageInfo.value = _imageInfo.value.copy(
                    width = size?.first ?: 0,
                    height = size?.second ?: 0
                )
                setBitmapInfo(
                    newInfo = imageManager.applyPresetBy(
                        image = _bitmap.value,
                        preset = _presetSelected.value,
                        currentInfo = _imageInfo.value
                    )
                )
                _selectedUri.value = uri
            }
        }
    }

    fun updatePreset(preset: Preset) {
        setBitmapInfo(
            imageManager.applyPresetBy(
                image = _bitmap.value,
                preset = preset,
                currentInfo = _imageInfo.value
            )
        )
        _presetSelected.value = preset
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImage(uri)?.image?.let { ImageData(it, imageInfo) }
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
        }.also {
            _isSaving.value = false
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun canShow(): Boolean = bitmap?.let { imageManager.canShow(it) } ?: false

    fun decodeBitmapFromUri(uri: Uri, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            imageManager.getImageAsync(
                uri = uri.toString(),
                originalSize = true,
                onGetImage = ::setImageData,
                onError = onError
            )
        }
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

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

    fun updateExifByTag(tag: String, value: String) {
        val exifInterface = _exif.value
        exifInterface?.setAttribute(tag, value)
        updateExif(exifInterface)
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

}