package ru.tech.imageresizershrinker.batch_resize_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.applyPresetBy
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.flip
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.rotate
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.scaleUntilCanShow
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SaveTarget

class BatchResizeViewModel : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _bitmapInfo: MutableState<BitmapInfo> = mutableStateOf(BitmapInfo())
    val bitmapInfo: BitmapInfo by _bitmapInfo

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(true)
    val shouldShowPreview by _shouldShowPreview

    private val _presetSelected: MutableState<Int> = mutableStateOf(-1)
    val presetSelected by _presetSelected

    private val _isTelegramSpecs: MutableState<Boolean> = mutableStateOf(false)
    val isTelegramSpecs by _isTelegramSpecs

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableStateOf(0)
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

    fun updateUrisSilently(
        removedUri: Uri,
        loader: suspend (Uri) -> Bitmap?
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uris.value = uris
                if (_selectedUri.value == removedUri) {
                    val index = uris?.indexOf(removedUri) ?: -1
                    if (index == 0) {
                        uris?.getOrNull(1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = loader(it)
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            _bitmap.value = loader(it)
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
                _shouldShowPreview.value = preview.canShow()
                if (shouldShowPreview) _previewBitmap.value = preview

                _bitmapInfo.value = _bitmapInfo.value.run {
                    if (resizeType == 2) copy(
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
        return@withContext bitmapInfo.run {
            bitmap.previewBitmap(
                quality,
                width,
                height,
                mimeTypeInt,
                resizeType,
                rotationDegrees,
                isFlipped
            ) {
                _bitmapInfo.value = _bitmapInfo.value.copy(sizeInBytes = it)
            }
        }
    }

    fun setBitmapInfo(newInfo: BitmapInfo) {
        if (_bitmapInfo.value != newInfo || _bitmapInfo.value.quality == 100f) {
            _bitmapInfo.value = newInfo
            checkBitmapAndUpdate(resetPreset = false, resetTelegram = true)
            _presetSelected.value = newInfo.quality.toInt()
        }
    }

    fun resetValues(saveMime: Boolean = false) {
        _bitmapInfo.value = BitmapInfo(
            width = _bitmap.value?.width ?: 0,
            height = _bitmap.value?.height ?: 0,
            mimeTypeInt = if (saveMime) bitmapInfo.mimeTypeInt else 0
        )
        checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
    }

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            val size = bitmap?.let { bitmap.width to bitmap.height }
            _bitmap.value = bitmap?.scaleUntilCanShow()
            resetValues(true)
            _bitmapInfo.value = _bitmapInfo.value.copy(
                width = size?.first ?: 0,
                height = size?.second ?: 0
            )
        }
    }

    fun rotateLeft() {
        _bitmapInfo.value = _bitmapInfo.value.run {
            copy(
                rotationDegrees = _bitmapInfo.value.rotationDegrees - 90f,
                height = width,
                width = height
            )
        }
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun rotateRight() {
        _bitmapInfo.value = _bitmapInfo.value.run {
            copy(
                rotationDegrees = _bitmapInfo.value.rotationDegrees + 90f,
                height = width,
                width = height
            )
        }
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun flip() {
        _bitmapInfo.value = _bitmapInfo.value.copy(isFlipped = !_bitmapInfo.value.isFlipped)
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun updateWidth(width: Int) {
        if (_bitmapInfo.value.width != width) {
            _bitmapInfo.value = _bitmapInfo.value.copy(width = width)
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
        }
    }

    fun updateHeight(height: Int) {
        if (_bitmapInfo.value.height != height) {
            _bitmapInfo.value = _bitmapInfo.value.copy(height = height)
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
        }
    }

    fun setQuality(quality: Float) {
        if (_bitmapInfo.value.quality != quality) {
            _bitmapInfo.value = _bitmapInfo.value.copy(quality = quality.coerceIn(0f, 100f))
            checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
        }
    }

    fun setMime(mime: Int) {
        if (_bitmapInfo.value.mimeTypeInt != mime) {
            _bitmapInfo.value = _bitmapInfo.value.copy(mimeTypeInt = mime)
            if (mime.extension != "png") checkBitmapAndUpdate(
                resetPreset = false,
                resetTelegram = true
            )
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
        }
    }

    fun setResizeType(type: Int) {
        if (_bitmapInfo.value.resizeType != type) {
            _bitmapInfo.value = _bitmapInfo.value.copy(resizeType = type)
            if (type != 2) checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = true)
        }
    }

    fun setTelegramSpecs() {
        val new = _bitmapInfo.value.copy(
            width = 512,
            height = 512,
            mimeTypeInt = 3,
            resizeType = 1,
            quality = 100f
        )
        if (new != _bitmapInfo.value) {
            _bitmapInfo.value = new
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = false)
        }
        _isTelegramSpecs.value = true
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun saveBitamps(
        fileController: FileController,
        getBitmap: suspend (Uri) -> Pair<Bitmap?, ExifInterface?>,
        onComplete: (success: Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmapInfo.apply {
                if (!fileController.isExternalStorageWritable()) {
                    onComplete(false)
                } else {
                    _done.value = 0
                    uris?.forEach { uri ->
                        runCatching {
                            getBitmap(uri)
                        }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
                            val tWidth = width
                            val tHeight = height

                            val localBitmap = bitmap!!.rotate(rotationDegrees)
                                .resizeBitmap(tWidth, tHeight, resizeType)
                                .flip(isFlipped)
                            val savingFolder = fileController.getSavingFolder(
                                SaveTarget(
                                    bitmapInfo = bitmapInfo,
                                    uri = uri,
                                    sequenceNumber = _done.value + 1
                                )
                            )

                            val fos = savingFolder.outputStream

                            localBitmap.compress(
                                mimeTypeInt.extension.compressFormat,
                                quality.toInt().coerceIn(0, 100),
                                fos
                            )

                            fos!!.flush()
                            fos.close()

                            if (keepExif) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val fd =
                                        fileController.getFileDescriptorFor(savingFolder.fileUri)
                                    fd?.fileDescriptor?.let {
                                        val ex = ExifInterface(it)
                                        exif?.copyTo(ex)
                                        ex.saveAttributes()
                                    }
                                    fd?.close()
                                } else {
                                    val image = savingFolder.file!!
                                    val ex = ExifInterface(image)
                                    exif?.copyTo(ex)
                                    ex.saveAttributes()
                                }
                            }

                        }
                        _done.value += 1
                    }
                    onComplete(true)
                }
            }
        }
    }

    fun setBitmap(loader: suspend () -> Bitmap?, uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _bitmap.value = loader()
                if (_presetSelected.value != -1) {
                    setBitmapInfo(
                        _presetSelected.value.applyPresetBy(
                            _bitmap.value,
                            _bitmapInfo.value
                        )
                    )
                }
                checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
                _selectedUri.value = uri
            }
        }
    }

    fun proceedBitmap(
        bitmapResult: Result<Bitmap?>
    ): Pair<Bitmap, BitmapInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            _bitmapInfo.value.run {
                val tWidth = width
                val tHeight = height

                bitmap
                    .rotate(rotationDegrees)
                    .resizeBitmap(tWidth, tHeight, resizeType)
                    .flip(isFlipped) to _bitmapInfo.value
            }
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

}