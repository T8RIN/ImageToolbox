package ru.tech.imageresizershrinker.resize_screen.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import ru.tech.imageresizershrinker.utils.BitmapUtils
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.BitmapUtils.flip
import ru.tech.imageresizershrinker.utils.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.rotate
import ru.tech.imageresizershrinker.utils.SavingFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SingleResizeViewModel : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _exif: MutableState<ExifInterface?> = mutableStateOf(null)
    val exif by _exif

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

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

    private var job: Job? = null

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

    fun saveBitmap(
        bitmap: Bitmap? = _bitmap.value,
        isExternalStorageWritable: Boolean,
        getSavingFolder: (ext: String) -> SavingFolder,
        getFileDescriptor: (Uri?) -> ParcelFileDescriptor?,
        onSuccess: (Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmap?.let { bitmap ->
                bitmapInfo.apply {
                    if (!isExternalStorageWritable) {
                        onSuccess(false)
                    } else {
                        val tWidth = width.toIntOrNull() ?: bitmap.width
                        val tHeight = height.toIntOrNull() ?: bitmap.height

                        val localBitmap =
                            bitmap
                                .rotate(rotationDegrees)
                                .resizeBitmap(tWidth, tHeight, resizeType)
                                .flip(isFlipped)

                        val savingFolder = getSavingFolder(mimeTypeInt.extension)

                        val fos = savingFolder.outputStream
                        localBitmap.compress(
                            mimeTypeInt.extension.compressFormat,
                            quality.toInt().coerceIn(0, 100),
                            fos
                        )

                        val out = ByteArrayOutputStream()
                        localBitmap.compress(
                            mimeTypeInt.extension.compressFormat,
                            quality.toInt().coerceIn(0, 100),
                            out
                        )
                        val decoded =
                            BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

                        out.flush()
                        out.close()
                        fos!!.flush()
                        fos.close()

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

                        _bitmap.value = decoded
                        _bitmapInfo.value = _bitmapInfo.value.copy(
                            isFlipped = false,
                            rotationDegrees = 0f
                        )
                        onSuccess(true)
                    }
                }
            }
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
                mimeTypeInt,
                resizeType,
                rotationDegrees,
                isFlipped
            ) {
                _bitmapInfo.value = _bitmapInfo.value.copy(sizeInBytes = it)
            }
        }
    }

    fun clearExif() {
        val t = _exif.value
        BitmapUtils.tags.forEach {
            t?.setAttribute(it, null)
        }
        _exif.value = t
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
            sizeInBytes = _bitmap.value?.byteCount ?: 0,
            mimeTypeInt = if (saveMime) bitmapInfo.mimeTypeInt else 0
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
            width = "512",
            height = "512",
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

}
