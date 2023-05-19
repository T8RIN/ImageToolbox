package ru.tech.imageresizershrinker.bytes_resize_screen.viewModel

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
import ru.tech.imageresizershrinker.single_resize_screen.components.BitmapInfo
import ru.tech.imageresizershrinker.single_resize_screen.components.compressFormat
import ru.tech.imageresizershrinker.single_resize_screen.components.extension
import ru.tech.imageresizershrinker.single_resize_screen.components.mimeTypeInt
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.scaleByMaxBytes
import ru.tech.imageresizershrinker.utils.SavingFolder

class BytesResizeViewModel : ViewModel() {

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

    private val _presetSelected = mutableStateOf(-1)
    val presetSelected by _presetSelected

    private val _handMode = mutableStateOf(true)
    val handMode by _handMode

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _keepExif = mutableStateOf(false)
    val keepExif by _keepExif

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _maxBytes = mutableStateOf(0L)
    val maxBytes by _maxBytes

    private val _mime = mutableStateOf(0)
    val mime by _mime

    fun setMime(mime: Int) {
        if (_mime.value != mime) {
            _mime.value = mime
            updatePreview()
        }
    }

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
                }
                val u = _uris.value?.toMutableList()?.apply {
                    remove(removedUri)
                }
                _uris.value = u
            }
        }
    }


    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isLoading.value = true
            _bitmap.value = bitmap
            var bmp: Bitmap?
            withContext(Dispatchers.IO) {
                bmp = if (bitmap?.canShow() == false) {
                    bitmap.resizeBitmap(
                        height_ = (bitmap.height * 0.9f).toInt(),
                        width_ = (bitmap.width * 0.9f).toInt(),
                        resize = 1
                    )
                } else bitmap

                while (bmp?.canShow() == false) {
                    bmp = bmp?.resizeBitmap(
                        height_ = (bmp!!.height * 0.9f).toInt(),
                        width_ = (bmp!!.width * 0.9f).toInt(),
                        resize = 1
                    )
                }
            }
            _previewBitmap.value = bmp
            _isLoading.value = false
        }
    }

    private var job: Job? = null

    private fun updatePreview() {
        job?.cancel()
        job = viewModelScope.launch {
            updateCanSave()
            withContext(Dispatchers.IO) {
                delay(400)
                _isLoading.value = true
                var bmp: Bitmap?
                withContext(Dispatchers.IO) {
                    val bitmap = _bitmap.value
                        ?.previewBitmap(
                            quality = 100f,
                            widthValue = null,
                            heightValue = null,
                            mime = mime,
                            resize = 0,
                            rotation = 0f,
                            isFlipped = false,
                            onByteCount = {}
                        )
                    bmp = if (bitmap?.canShow() == false) {
                        bitmap.resizeBitmap(
                            height_ = (bitmap.height * 0.9f).toInt(),
                            width_ = (bitmap.width * 0.9f).toInt(),
                            resize = 1
                        )
                    } else bitmap

                    while (bmp?.canShow() == false) {
                        bmp = bmp?.resizeBitmap(
                            height_ = (bmp!!.height * 0.9f).toInt(),
                            width_ = (bmp!!.width * 0.9f).toInt(),
                            resize = 1
                        )
                    }
                }
                _previewBitmap.value = bmp
                _isLoading.value = false
            }
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun save(
        isExternalStorageWritable: Boolean,
        getSavingFolder: (bitmapInfo: BitmapInfo) -> SavingFolder,
        getFileDescriptor: (Uri?) -> ParcelFileDescriptor?,
        getBitmap: suspend (Uri) -> Pair<Bitmap?, ExifInterface?>,
        getImageSize: (Uri) -> Long?,
        onSuccess: (Int) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var failed = 0
            if (!isExternalStorageWritable) {
                onSuccess(-1)
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    runCatching {
                        getBitmap(uri)
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
                        kotlin.runCatching {
                            if (handMode) {
                                bitmap?.scaleByMaxBytes(
                                    maxBytes = maxBytes,
                                    compressFormat = mime.extension.compressFormat
                                )
                            } else {
                                bitmap?.scaleByMaxBytes(
                                    maxBytes = (getImageSize(uri) ?: 0)
                                        .times(_presetSelected.value / 100f)
                                        .toLong(),
                                    compressFormat = mime.extension.compressFormat
                                )
                            }
                        }.let { result ->
                            if (result.isSuccess && result.getOrNull() != null) {
                                val scaled = result.getOrNull()!!
                                val localBitmap = scaled.first
                                val savingFolder = getSavingFolder(
                                    BitmapInfo(
                                        mimeTypeInt = mime.extension.mimeTypeInt,
                                        width = localBitmap.width.toString(),
                                        height = localBitmap.height.toString()
                                    )
                                )

                                val fos = savingFolder.outputStream

                                localBitmap.compress(
                                    mime.extension.compressFormat,
                                    scaled.second,
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
                            } else failed += 1
                        }
                    }
                    _done.value += 1
                }
                onSuccess(failed)
            }
        }
    }

    fun setBitmap(loader: suspend () -> Bitmap?, uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateBitmap(loader())
                _selectedUri.value = uri
            }
        }
    }

    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && (_maxBytes.value != 0L && _handMode.value || !_handMode.value && _presetSelected.value != -1)
    }

    fun updateMaxBytes(newBytes: String) {
        val b = newBytes.toLongOrNull() ?: 0
        _maxBytes.value = b * 1024
        updateCanSave()
    }

    fun selectPreset(preset: Int) {
        _presetSelected.value = preset
        updateCanSave()
    }

    fun updateHandMode() {
        _handMode.value = !_handMode.value
        updateCanSave()
    }

    fun proceedBitmap(
        uri: Uri,
        bitmapResult: Result<Bitmap?>,
        getImageSize: (Uri) -> Long?
    ): Pair<Bitmap, BitmapInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            kotlin.runCatching {
                if (handMode) {
                    bitmap.scaleByMaxBytes(
                        maxBytes = maxBytes,
                        compressFormat = mime.extension.compressFormat
                    )
                } else {
                    bitmap.scaleByMaxBytes(
                        maxBytes = (getImageSize(uri) ?: 0)
                            .times(_presetSelected.value / 100f)
                            .toLong(),
                        compressFormat = mime.extension.compressFormat
                    )
                }
            }
        }?.let { result ->
            if (result.isSuccess && result.getOrNull() != null) {
                val scaled = result.getOrNull()!!
                scaled.first to BitmapInfo(
                    mimeTypeInt = _mime.value,
                    quality = scaled.second.toFloat(),
                    width = scaled.first.width.toString(),
                    height = scaled.first.height.toString()
                )
            } else null
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

}