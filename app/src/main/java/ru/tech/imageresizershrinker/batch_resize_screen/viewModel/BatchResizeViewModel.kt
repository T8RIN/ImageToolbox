package ru.tech.imageresizershrinker.batch_resize_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
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
import ru.tech.imageresizershrinker.resize_screen.components.BitmapInfo
import ru.tech.imageresizershrinker.resize_screen.components.compressFormat
import ru.tech.imageresizershrinker.resize_screen.components.extension
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.BitmapUtils.flip
import ru.tech.imageresizershrinker.utils.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.rotate
import ru.tech.imageresizershrinker.utils.SavingFolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(false)
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
        loader: (Uri) -> Bitmap?
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
                    resetValues(saveMime = true)
                }
                val u = _uris.value?.toMutableList()?.apply {
                    remove(removedUri)
                }
                _uris.value = u
            }
        }
    }

    private fun checkBitmapAndUpdate(resetPreset: Boolean, resetTelegram: Boolean) {
        if (resetPreset) {
            _presetSelected.value = -1
        }
        if (resetTelegram) {
            _isTelegramSpecs.value = false
        }
        job?.cancel()
        job = viewModelScope.launch {
            delay(600)
            _isLoading.value = true
            _bitmap.value?.let { bmp ->
                val preview = updatePreview(bmp)
                _previewBitmap.value = null
                _shouldShowPreview.value = preview.canShow()
                if (shouldShowPreview) _previewBitmap.value = preview

                _bitmapInfo.value = _bitmapInfo.value.run {
                    if (resizeType == 2) copy(
                        height = preview.height.toString(),
                        width = preview.width.toString()
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
                width.toIntOrNull(),
                height.toIntOrNull(),
                mime,
                resizeType,
                rotation,
                isFlipped
            ) {
                _bitmapInfo.value = _bitmapInfo.value.copy(size = it)
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
            width = _bitmap.value?.width?.toString() ?: "",
            height = _bitmap.value?.height?.toString() ?: "",
            size = _bitmap.value?.byteCount ?: 0,
            mime = if (saveMime) bitmapInfo.mime else 0
        )
        checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
    }

    fun updateBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
        resetValues(saveMime = true)
    }

    fun rotateLeft() {
        _bitmapInfo.value = _bitmapInfo.value.run {
            copy(
                rotation = _bitmapInfo.value.rotation - 90f,
                height = width,
                width = height
            )
        }
        checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
    }

    fun rotateRight() {
        _bitmapInfo.value = _bitmapInfo.value.run {
            copy(
                rotation = _bitmapInfo.value.rotation + 90f,
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

    fun updateWidth(width: String) {
        if (_bitmapInfo.value.width != width) {
            _bitmapInfo.value = _bitmapInfo.value.copy(width = width)
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
        }
    }

    fun updateHeight(height: String) {
        if (_bitmapInfo.value.height != height) {
            _bitmapInfo.value = _bitmapInfo.value.copy(height = height)
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
        }
    }

    fun setQuality(quality: Float) {
        if (_bitmapInfo.value.quality != quality) {
            _bitmapInfo.value = _bitmapInfo.value.copy(quality = quality.coerceIn(0f, 100f))
            checkBitmapAndUpdate(resetPreset = true, resetTelegram = false)
        }
    }

    fun setMime(mime: Int) {
        if (_bitmapInfo.value.mime != mime) {
            _bitmapInfo.value = _bitmapInfo.value.copy(mime = mime)
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
            width = "512",
            height = "512",
            mime = 3,
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

    fun save(
        isExternalStorageWritable: Boolean,
        getSavingFolder: (name: String, ext: String) -> SavingFolder,
        getFileDescriptor: (Uri?) -> ParcelFileDescriptor?,
        getBitmap: (Uri) -> Pair<Bitmap?, ExifInterface?>,
        onSuccess: (Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmapInfo.apply {
                if (!isExternalStorageWritable) {
                    onSuccess(false)
                } else {
                    _done.value = 0
                    uris?.forEach { uri ->
                        runCatching {
                            getBitmap(uri)
                        }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
                            val ext = mime.extension

                            val tWidth = width.toIntOrNull() ?: bitmap!!.width
                            val tHeight = height.toIntOrNull() ?: bitmap!!.height

                            val timeStamp: String =
                                SimpleDateFormat(
                                    "yyyyMMdd_HHmmss",
                                    Locale.getDefault()
                                ).format(Date())
                            val name =
                                "ResizedImage$timeStamp-${Date().hashCode()}.$ext"
                            val localBitmap = bitmap!!.resizeBitmap(tWidth, tHeight, resizeType)
                                .rotate(rotation)
                                .flip(isFlipped)
                            val savingFolder = getSavingFolder(name, ext)

                            val fos = savingFolder.outputStream

                            localBitmap.compress(
                                mime.extension.compressFormat,
                                quality.toInt().coerceIn(0, 100),
                                fos
                            )

                            fos!!.flush()
                            fos.close()

                            if (keepExif) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val fd = getFileDescriptor(savingFolder.fileUri)
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
                    onSuccess(true)
                }
            }
        }
    }

    fun loadBitmapAsync(
        loader: suspend () -> Bitmap?,
        onGetBitmap: (Bitmap?) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                onGetBitmap(loader())
            }
        }
    }

    fun setBitmap(loader: () -> Bitmap?, uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _bitmap.value = loader()
                resetValues(saveMime = true)
                _selectedUri.value = uri
            }
        }
    }

    fun proceedBitmap(
        bitmapResult: Result<Bitmap?>
    ): Pair<Bitmap, BitmapInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            _bitmapInfo.value.run {
                val tWidth = width.toIntOrNull() ?: bitmap.width
                val tHeight = height.toIntOrNull() ?: bitmap.height

                bitmap.resizeBitmap(tWidth, tHeight, resizeType)
                    .rotate(rotation)
                    .flip(isFlipped) to _bitmapInfo.value
            }
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

}
