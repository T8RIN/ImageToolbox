package ru.tech.imageresizershrinker.resize_screen.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.tech.imageresizershrinker.resize_screen.components.BitmapInfo
import ru.tech.imageresizershrinker.utils.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.BitmapUtils.flip
import ru.tech.imageresizershrinker.utils.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.rotate
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

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

    private var job: Job? = null

    private fun checkBitmapAndUpdate() {
        job?.cancel()
        job = viewModelScope.launch {
            delay(400)
            _isLoading.value = true
            _bitmap.value?.let { bmp ->
                _shouldShowPreview.value = (bitmapInfo.height.toIntOrNull() ?: 0)
                    .plus(bitmapInfo.width.toIntOrNull() ?: 0) <= 10000
                if (shouldShowPreview) _previewBitmap.value = updatePreview(bmp)
            }
            _isLoading.value = false
        }
    }

    fun saveBitmap(
        bitmap: Bitmap? = _bitmap.value,
        isExternalStorageWritable: Boolean,
        getFileOutputStream: (name: String, ext: String) -> OutputStream?,
        getFileDescriptor: (name: String) -> ParcelFileDescriptor?,
        getExternalStorageDir: () -> File?,
        onSuccess: (Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmap?.let { bitmap ->
                bitmapInfo.apply {
                    if (!isExternalStorageWritable) {
                        onSuccess(false)
                    } else {
                        val ext = if (mime == 1) "webp" else if (mime == 2) "png" else "jpg"
                        val explicit = resizeType == 0

                        val tWidth = width.toIntOrNull() ?: bitmap.width
                        val tHeight = height.toIntOrNull() ?: bitmap.height

                        val timeStamp: String =
                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                        val name = "ResizedImage$timeStamp.$ext"
                        val localBitmap =
                            bitmap.resizeBitmap(tWidth, tHeight, !explicit).rotate(rotation)
                                .flip(isFlipped)
                        val fos: OutputStream? =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                getFileOutputStream(name, ext)
                            } else {
                                val imagesDir = getExternalStorageDir()
                                if (imagesDir?.exists() == false) imagesDir.mkdir()
                                val image = File(imagesDir, name)
                                FileOutputStream(image)
                            }
                        localBitmap.compress(
                            if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 2) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                            quality.toInt(),
                            fos
                        )
                        val out = ByteArrayOutputStream()
                        localBitmap.compress(
                            if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 2) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                            quality.toInt(), out
                        )
                        val decoded =
                            BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

                        out.flush()
                        out.close()
                        fos!!.flush()
                        fos.close()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val fd = getFileDescriptor(name)
                            fd?.fileDescriptor?.let {
                                val ex = ExifInterface(it)
                                exif?.copyTo(ex)
                                ex.saveAttributes()
                            }
                            fd?.close()
                        } else {
                            val dir = getExternalStorageDir()
                            val image = File(dir, name)
                            val ex = ExifInterface(image)
                            exif?.copyTo(ex)
                            ex.saveAttributes()
                        }

                        _bitmap.value = decoded
                        _bitmapInfo.value = _bitmapInfo.value.copy(
                            isFlipped = false,
                            rotation = 0f
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
                mime,
                resizeType,
                rotation,
                isFlipped
            ) {
                _bitmapInfo.value = _bitmapInfo.value.copy(size = it)
            }
        }
    }

    fun resetValues() {
        _bitmapInfo.value = BitmapInfo(
            width = _bitmap.value?.width?.toString() ?: "",
            height = _bitmap.value?.height?.toString() ?: "",
            size = _bitmap.value?.byteCount ?: 0
        )
        checkBitmapAndUpdate()
    }

    fun updateBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
        resetValues()
    }

    fun rotateLeft() {
        _bitmapInfo.value = _bitmapInfo.value.run {
            copy(
                rotation = _bitmapInfo.value.rotation - 90f,
                height = width,
                width = height
            )
        }
        checkBitmapAndUpdate()
    }

    fun rotateRight() {
        _bitmapInfo.value = _bitmapInfo.value.run {
            copy(
                rotation = _bitmapInfo.value.rotation + 90f,
                height = width,
                width = height
            )
        }
        checkBitmapAndUpdate()
    }

    fun flip() {
        _bitmapInfo.value = _bitmapInfo.value.copy(isFlipped = !_bitmapInfo.value.isFlipped)
        checkBitmapAndUpdate()
    }

    fun updateWidth(width: String) {
        if (_bitmapInfo.value.width != width) {
            _bitmapInfo.value = _bitmapInfo.value.copy(width = width)
            checkBitmapAndUpdate()
        }
    }

    fun updateHeight(height: String) {
        if (_bitmapInfo.value.height != height) {
            _bitmapInfo.value = _bitmapInfo.value.copy(height = height)
            checkBitmapAndUpdate()
        }
    }

    fun setQuality(quality: Float) {
        if (_bitmapInfo.value.quality != quality) {
            _bitmapInfo.value = _bitmapInfo.value.copy(quality = quality)
            checkBitmapAndUpdate()
        }
    }

    fun setMime(mime: Int) {
        if (_bitmapInfo.value.mime != mime) {
            _bitmapInfo.value = _bitmapInfo.value.copy(mime = mime)
            checkBitmapAndUpdate()
        }
    }

    fun setResizeType(type: Int) {
        if (_bitmapInfo.value.resizeType != type) {
            _bitmapInfo.value = _bitmapInfo.value.copy(resizeType = type)
            checkBitmapAndUpdate()
        }
    }

    fun setTelegramSpecs() {
        val new = _bitmapInfo.value.copy(
            width = "512",
            height = "512",
            mime = 2,
            resizeType = 1,
            quality = 100f
        )
        if (new != _bitmapInfo.value) {
            _bitmapInfo.value = new
            checkBitmapAndUpdate()
        }
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

    companion object {
        fun String.restrict(`by`: Int = 20000): String {
            if (isEmpty()) return this

            return if ((this.toIntOrNull() ?: 0) > `by`) `by`.toString()
            else if (this.isDigitsOnly() && (this.toIntOrNull() ?: 0) == 0) ""
            else this.trim().filter {
                !listOf('-', '.', ',', ' ', "\n").contains(it)
            }
        }
    }

}