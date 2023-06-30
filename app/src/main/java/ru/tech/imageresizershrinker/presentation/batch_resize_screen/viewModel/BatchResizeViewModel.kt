package ru.tech.imageresizershrinker.presentation.batch_resize_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import ru.tech.imageresizershrinker.core.android.BitmapUtils.applyPresetBy
import ru.tech.imageresizershrinker.core.android.BitmapUtils.canShow
import ru.tech.imageresizershrinker.core.android.BitmapUtils.compress
import ru.tech.imageresizershrinker.core.android.BitmapUtils.flip
import ru.tech.imageresizershrinker.core.android.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.core.android.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.core.android.BitmapUtils.rotate
import ru.tech.imageresizershrinker.core.android.BitmapUtils.scaleUntilCanShow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class BatchResizeViewModel @Inject constructor(
    private val fileController: FileController
) : ViewModel() {

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

    fun setMime(mimeType: MimeType) {
        if (_bitmapInfo.value.mimeType != mimeType) {
            _bitmapInfo.value = _bitmapInfo.value.copy(mimeType = mimeType)
            if (mimeType !is MimeType.Png) checkBitmapAndUpdate(
                resetPreset = false,
                resetTelegram = true
            )
            else checkBitmapAndUpdate(resetPreset = false, resetTelegram = false)
        }
    }

    fun setResizeType(type: ResizeType) {
        if (_bitmapInfo.value.resizeType != type) {
            _bitmapInfo.value = _bitmapInfo.value.copy(resizeType = type)
            if (type !is ResizeType.Ratio) checkBitmapAndUpdate(
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

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun saveBitamps(
        getBitmap: suspend (Uri) -> Bitmap?,
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
                        getBitmap(uri)
                    }.getOrNull()?.let { bitmap ->
                        bitmapInfo.let {
                            presetSelected.applyPresetBy(
                                bitmap = bitmap,
                                currentInfo = it
                            )
                        }.apply {
                            val out = ByteArrayOutputStream()
                            bitmap.rotate(rotationDegrees)
                                .resizeBitmap(width, height, resizeType)
                                .flip(isFlipped)
                                .compress(mimeType = mimeType, quality = quality, out)
                            fileController.save(
                                BitmapSaveTarget(
                                    bitmapInfo = this,
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value + 1,
                                    data = out.toByteArray()
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