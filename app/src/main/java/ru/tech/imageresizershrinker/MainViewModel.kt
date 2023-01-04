package ru.tech.imageresizershrinker

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.tech.imageresizershrinker.BitmapUtils.flip
import ru.tech.imageresizershrinker.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.BitmapUtils.rotate
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class MainViewModel : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _bitmapInfo: MutableState<BitmapInfo> = mutableStateOf(BitmapInfo())
    val bitmapInfo: BitmapInfo by _bitmapInfo

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private var job: Job? = null

    private fun checkBitmapAndUpdate() {
        job?.cancel()
        job = viewModelScope.launch {
            delay(400)
            _isLoading.value = true
            _bitmap.value?.let { bmp ->
                _previewBitmap.value = updatePreview(bmp)
            }
            _isLoading.value = false
        }
    }

    fun saveBitmap(
        isExternalStorageWritable: Boolean,
        contentResolver: ContentResolver,
        onSuccess: (Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _bitmap.value?.let { bitmap ->
                bitmapInfo.apply {
                    if (!isExternalStorageWritable) {
                        onSuccess(false)
                        cancel()
                    }

                    val ext = if (mime == 1) "webp" else if (mime == 0) "png" else "jpg"
                    val explicit = resizeType == 0

                    val tWidth = width.toIntOrNull() ?: bitmap.width
                    val tHeight = height.toIntOrNull() ?: bitmap.height

                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val name = "ResizedImage$timeStamp.$ext"
                    val localBitmap = if (explicit) {
                        Bitmap.createScaledBitmap(
                            bitmap,
                            tWidth,
                            tHeight,
                            false
                        )
                    } else {
                        bitmap.resizeBitmap(max(tWidth, tHeight))
                    }.rotate(rotation).flip(isFlipped)

                    val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val resolver: ContentResolver = contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/$ext")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/ResizedImages")
                        }
                        val imageUri =
                            resolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                        resolver.openOutputStream(imageUri!!)
                    } else {
                        val imagesDir =
                            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}${File.separator}ResizedImages"
                        val file = File(imagesDir)
                        if (!file.exists()) {
                            file.mkdir()
                        }
                        val image = File(imagesDir, "$name.$ext")
                        FileOutputStream(image)
                    }
                    localBitmap.compress(
                        if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 0) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                        quality.toInt(),
                        fos
                    )
                    val out = ByteArrayOutputStream()
                    localBitmap.compress(
                        if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 0) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                        quality.toInt(), out
                    )
                    val decoded =
                        BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
                    out.flush()
                    out.close()
                    fos!!.flush()
                    fos.close()

                    _bitmap.value = decoded
                    _bitmapInfo.value = _bitmapInfo.value.copy(
                        isFlipped = false,
                        rotation = 0f
                    )
                }
                onSuccess(true)
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
        _bitmapInfo.value = _bitmapInfo.value.copy(rotation = _bitmapInfo.value.rotation - 90f)
        checkBitmapAndUpdate()
    }

    fun rotateRight() {
        _bitmapInfo.value = _bitmapInfo.value.copy(rotation = _bitmapInfo.value.rotation + 90f)
        checkBitmapAndUpdate()
    }

    fun flip() {
        _bitmapInfo.value = _bitmapInfo.value.copy(isFlipped = !_bitmapInfo.value.isFlipped)
        checkBitmapAndUpdate()
    }

    fun updateWidth(width: String) {
        _bitmapInfo.value = _bitmapInfo.value.copy(width = width)
        checkBitmapAndUpdate()
    }

    fun updateHeight(height: String) {
        _bitmapInfo.value = _bitmapInfo.value.copy(height = height)
        checkBitmapAndUpdate()
    }

    fun setQuality(quality: Float) {
        _bitmapInfo.value = _bitmapInfo.value.copy(quality = quality)
        checkBitmapAndUpdate()
    }

    fun setMime(mime: Int) {
        _bitmapInfo.value = _bitmapInfo.value.copy(mime = mime)
        checkBitmapAndUpdate()
    }

    fun setResizeType(type: Int) {
        _bitmapInfo.value = _bitmapInfo.value.copy(resizeType = type)
        checkBitmapAndUpdate()
    }

    fun setTelegramSpecs() {
        _bitmapInfo.value = _bitmapInfo.value.copy(
            width = "512",
            height = "512",
            mime = 0,
            resizeType = 1
        )
        checkBitmapAndUpdate()
    }

    companion object {
        fun String.restrict(`by`: Int = 6000): String {
            if (isEmpty()) return this

            return if ((this.toIntOrNull() ?: 0) > `by`) `by`.toString()
            else if (this.isDigitsOnly() && (this.toIntOrNull() ?: 0) == 0) ""
            else this.trim().filter {
                !listOf('-', '.', ',', ' ', "\n").contains(it)
            }
        }
    }

}