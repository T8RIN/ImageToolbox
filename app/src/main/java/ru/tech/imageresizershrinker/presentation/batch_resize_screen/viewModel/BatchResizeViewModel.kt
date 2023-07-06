package ru.tech.imageresizershrinker.presentation.batch_resize_screen.viewModel

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
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import javax.inject.Inject

@HiltViewModel
class BatchResizeViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _imageInfo: MutableState<ImageInfo> = mutableStateOf(ImageInfo())
    val imageInfo: ImageInfo by _imageInfo

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(true)
    val shouldShowPreview by _shouldShowPreview

    private val _showWarning: MutableState<Boolean> = mutableStateOf(false)
    val showWarning: Boolean by _showWarning

    private val _presetSelected: MutableState<Int> = mutableIntStateOf(-1)
    val presetSelected by _presetSelected

    private val _isTelegramSpecs: MutableState<Boolean> = mutableStateOf(false)
    val isTelegramSpecs by _isTelegramSpecs

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

//    private val _cropRect: MutableState<Rect?> = mutableStateOf(null)
//    val cropRect by _cropRect
//    TODO: Create Batch Cropping mode
//    fun updateCropRect(rect: Rect) {
//        _cropRect.value = rect
//    }

    private var job: Job? = null

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
                            _bitmap.value = imageManager.getImage(it.toString())
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = imageManager.getImage(it.toString())
                        }
                    }
                    resetValues(true)
                }
                val u = _uris.value?.toMutableList()?.apply {
                    remove(removedUri)
                }
                _uris.value = u
            }
        }
    }

    private fun checkBitmapAndUpdate(
        resetPreset: Boolean,
        resetTelegram: Boolean
    ) {
        if (resetPreset) {
            _presetSelected.value = -1
        }
        if (resetTelegram) {
            _isTelegramSpecs.value = false
        }
        job?.cancel()
        _isLoading.value = false
        job = viewModelScope.launch {
            _isLoading.value = true
            delay(600)
            _bitmap.value?.let { bmp ->
                val preview = updatePreview(bmp)
                _previewBitmap.value = null
                _shouldShowPreview.value = imageManager.canShow(preview)
                if (shouldShowPreview) _previewBitmap.value = preview

                _imageInfo.value = _imageInfo.value.run {
                    if (resizeType is ResizeType.Ratio) copy(
                        height = preview.height,
                        width = preview.width
                    ) else this
                }
            }
            _isLoading.value = false
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap = withContext(Dispatchers.IO) {
        return@withContext imageInfo.run {
            _showWarning.value = width * height * 4L >= 10_000 * 10_000 * 3L
            imageManager.createPreview(
                image = bitmap,
                imageInfo = this,
                onGetByteCount = {
                    _imageInfo.value = _imageInfo.value.copy(sizeInBytes = it)
                }
            )
        }
    }

    private fun setBitmapInfo(newInfo: ImageInfo) {
        if (_imageInfo.value != newInfo || _imageInfo.value.quality == 100f) {
            _imageInfo.value = newInfo
            checkBitmapAndUpdate(resetPreset = false, resetTelegram = true)
            _presetSelected.value = newInfo.quality.toInt()
        }
    }

    fun resetValues(saveMime: Boolean = false) {
        _imageInfo.value = ImageInfo(
            width = _bitmap.value?.width ?: 0,
            height = _bitmap.value?.height ?: 0,
            mimeType = if (saveMime) imageInfo.mimeType else MimeType.Default()
        )
        checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
    }

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            val size = bitmap?.let { bitmap.width to bitmap.height }
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
            resetValues(true)
            _imageInfo.value = _imageInfo.value.copy(
                width = size?.first ?: 0,
                height = size?.second ?: 0
            )
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
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun rotateRight() {
        _imageInfo.value = _imageInfo.value.run {
            copy(
                rotationDegrees = _imageInfo.value.rotationDegrees + 90f,
                height = width,
                width = height
            )
        }
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun flip() {
        _imageInfo.value = _imageInfo.value.copy(isFlipped = !_imageInfo.value.isFlipped)
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun updateWidth(width: Int) {
        if (_imageInfo.value.width != width) {
            _imageInfo.value = _imageInfo.value.copy(width = width)
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
        }
    }

    fun updateHeight(height: Int) {
        if (_imageInfo.value.height != height) {
            _imageInfo.value = _imageInfo.value.copy(height = height)
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
        }
    }

    fun setQuality(quality: Float) {
        if (_imageInfo.value.quality != quality) {
            _imageInfo.value = _imageInfo.value.copy(quality = quality.coerceIn(0f, 100f))
            checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
        }
    }

    fun setMime(mimeType: MimeType) {
        if (_imageInfo.value.mimeType != mimeType) {
            _imageInfo.value = _imageInfo.value.copy(mimeType = mimeType)
            if (mimeType !is MimeType.Png) checkBitmapAndUpdate(
                resetPreset = false,
                resetTelegram = true
            )
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
        }
    }

    fun setResizeType(type: ResizeType) {
        if (_imageInfo.value.resizeType != type) {
            _imageInfo.value = _imageInfo.value.copy(resizeType = type)
            if (type !is ResizeType.Ratio) checkBitmapAndUpdate(
                resetPreset = false,
                resetTelegram = false
            )
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = true)
        }
    }

    fun setTelegramSpecs() {
        val new = _imageInfo.value.copy(
            width = 512,
            height = 512,
            mimeType = MimeType.Png,
            resizeType = ResizeType.Flexible,
            quality = 100f
        )
        if (new != _imageInfo.value) {
            _imageInfo.value = new
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = false)
        }
        _isTelegramSpecs.value = true
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun saveBitamps(
        onComplete: (path: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (!fileController.isExternalStorageWritable()) {
                onComplete("")
                fileController.requestReadWritePermissions()
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    runCatching {
                        imageManager.getImage(uri.toString())
                    }.getOrNull()?.let { bitmap ->
                        imageInfo.let {
                            imageManager.applyPresetBy(
                                bitmap = bitmap,
                                preset = _presetSelected.value,
                                currentInfo = it
                            )
                        }.apply {
                            fileController.save(
                                ImageSaveTarget(
                                    imageInfo = this,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = imageManager.compress(bitmap, imageInfo)
                                ), keepExif
                            )
                        }
                    }
                    _done.value += 1
                }
                onComplete(fileController.savingPath)
            }
        }
    }

    fun setBitmap(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateBitmap(imageManager.getImage(uri = uri.toString(), originalSize = false))
                if (_presetSelected.value != -1) {
                    setBitmapInfo(
                        imageManager.applyPresetBy(
                            bitmap = _bitmap.value,
                            preset = _presetSelected.value,
                            currentInfo = _imageInfo.value
                        )
                    )
                }
                checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
                _selectedUri.value = uri
            }
        }
    }

    fun updatePreset(preset: Int) {
        setBitmapInfo(
            imageManager.applyPresetBy(
                bitmap = _bitmap.value,
                preset = preset,
                currentInfo = _imageInfo.value
            )
        )
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        viewModelScope.launch {
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImage(uri)?.let { it to imageInfo }
                },
                onProgressChange = {
                    if (it == -1) {
                        onComplete()
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                }
            )
        }
    }

    fun canShow(): Boolean = bitmap?.let { imageManager.canShow(it) } ?: false

    fun decodeBitmapFromUri(uri: Uri, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            imageManager.getImageAsync(
                uri = uri.toString(),
                originalSize = true,
                onGetMimeType = ::setMime,
                onGetMetadata = {},
                onGetImage = ::updateBitmap,
                onError = onError
            )
        }
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

}