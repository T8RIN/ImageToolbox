package ru.tech.imageresizershrinker.presentation.single_resize_screen.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import ru.tech.imageresizershrinker.domain.model.BitmapInfo
import ru.tech.imageresizershrinker.domain.saving.model.BitmapSaveTarget
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.core.android.BitmapUtils
import ru.tech.imageresizershrinker.core.android.BitmapUtils.canShow
import ru.tech.imageresizershrinker.core.android.BitmapUtils.compress
import ru.tech.imageresizershrinker.core.android.BitmapUtils.flip
import ru.tech.imageresizershrinker.core.android.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.core.android.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.core.android.BitmapUtils.rotate
import ru.tech.imageresizershrinker.core.android.BitmapUtils.scaleUntilCanShow
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class SingleResizeViewModel @Inject constructor(
    private val fileController: FileController
) : ViewModel() {

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

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

    private val _showWarning: MutableState<Boolean> = mutableStateOf(false)
    val showWarning: Boolean by _showWarning

    private val _shouldShowPreview: MutableState<Boolean> = mutableStateOf(true)
    val shouldShowPreview by _shouldShowPreview

    private val _presetSelected: MutableState<Int> = mutableIntStateOf(-1)
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
                    if (resizeType !is ResizeType.Ratio) copy(
                        height = preview.height,
                        width = preview.width
                    ) else this
                }
            }
            _isLoading.value = false
        }
    }

    fun saveBitmap(
        onComplete: (path: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmap?.let { bitmap ->
                bitmapInfo.apply {
                    if (!fileController.isExternalStorageWritable()) {
                        onComplete("")
                        fileController.requestReadWritePermissions()
                    } else {
                        val tWidth = width
                        val tHeight = height

                        val localBitmap =
                            bitmap
                                .rotate(rotationDegrees)
                                .resizeBitmap(tWidth, tHeight, resizeType)
                                .flip(isFlipped)


                        val out = ByteArrayOutputStream()
                        localBitmap.compress(
                            mimeType,
                            quality.toInt().coerceIn(0, 100),
                            out
                        )
                        val decoded =
                            BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

                        fileController.save(
                            saveTarget = BitmapSaveTarget(
                                bitmapInfo = this,
                                originalUri = uri.toString(),
                                sequenceNumber = null,
                                data = out.toByteArray()
                            ),
                            keepMetadata = true
                        )

                        out.flush()
                        out.close()

                        _bitmap.value = decoded
                        _bitmapInfo.value = _bitmapInfo.value.copy(
                            isFlipped = false,
                            rotationDegrees = 0f
                        )
                        onComplete(fileController.savingPath)
                    }
                }
            }
        }
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap = withContext(Dispatchers.IO) {
        return@withContext bitmapInfo.run {
            _showWarning.value = width * height * 4L >= 10_000 * 10_000 * 3L
            bitmap.previewBitmap(
                bitmapInfo = this,
                onByteCount = {
                    _bitmapInfo.value = _bitmapInfo.value.copy(sizeInBytes = it)
                }
            )
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
            width = _bitmap.value?.width ?: 0,
            height = _bitmap.value?.height ?: 0,
            mimeType = if (saveMime) bitmapInfo.mimeType else MimeType.Default()
        )
        checkBitmapAndUpdate(resetPreset = true, resetTelegram = true)
    }

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            val size = bitmap?.let { bitmap.width to bitmap.height }
            _bitmap.value = bitmap?.scaleUntilCanShow()
            resetValues(saveMime = true)
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

    fun setMime(mimeType: MimeType) {
        if (_bitmapInfo.value.mimeType != mimeType) {
            _bitmapInfo.value = _bitmapInfo.value.copy(mimeType = mimeType)
            if (mimeType != MimeType.Png) checkBitmapAndUpdate(
                resetPreset = false,
                resetTelegram = true
            )
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
        }
    }

    fun setResizeType(type: ResizeType) {
        if (_bitmapInfo.value.resizeType != type) {
            _bitmapInfo.value = _bitmapInfo.value.copy(resizeType = type)
            if (type != ResizeType.Ratio) checkBitmapAndUpdate(
                resetPreset = false,
                resetTelegram = false
            )
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = true)
        }
    }

    fun setTelegramSpecs() {
        val new = _bitmapInfo.value.copy(
            width = 512,
            height = 512,
            mimeType = MimeType.Png,
            resizeType = ResizeType.Flexible,
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

    fun setUri(uri: Uri) {
        _uri.value = uri
    }
}
