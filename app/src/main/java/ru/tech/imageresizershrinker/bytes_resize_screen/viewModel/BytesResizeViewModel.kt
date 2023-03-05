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
import kotlinx.coroutines.*
import ru.tech.imageresizershrinker.resize_screen.components.compressFormat
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.BitmapUtils.flip
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.rotate
import ru.tech.imageresizershrinker.utils.BitmapUtils.scaleByMaxBytes
import ru.tech.imageresizershrinker.utils.SavingFolder
import java.text.SimpleDateFormat
import java.util.*

class BytesResizeViewModel : ViewModel() {

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

    private val _isFlipped: MutableState<Boolean> = mutableStateOf(false)
    private val isFlipped by _isFlipped

    private val _rotation: MutableState<Int> = mutableStateOf(0)
    private val rotation by _rotation

    private val _maxBytes = mutableStateOf(0L)
    val maxBytes by _maxBytes

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
            withContext(Dispatchers.IO) {
                delay(400)
                _isLoading.value = true
                _previewBitmap.value = _bitmap.value
                    ?.rotate(rotation.toFloat())
                    ?.flip(isFlipped)
                _isLoading.value = false
            }
        }
    }

    fun rotateLeft() {
        _rotation.value -= 90
        updatePreview()
    }

    fun rotateRight() {
        _rotation.value += 90
        updatePreview()
    }

    fun flip() {
        _isFlipped.value = !_isFlipped.value
        updatePreview()
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
            if (!isExternalStorageWritable) {
                onSuccess(false)
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    runCatching {
                        getBitmap(uri)
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
                        val ext = "jpg"

                        val timeStamp: String =
                            SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.getDefault()
                            ).format(Date())
                        val name =
                            "ResizedImage$timeStamp-${Date().hashCode()}.$ext"

                        val scaled = bitmap!!.scaleByMaxBytes(maxBytes = maxBytes)

                        val localBitmap = scaled.first
                            .rotate(rotation.toFloat())
                            .flip(isFlipped)
                        val savingFolder = getSavingFolder(name, ext)

                        val fos = savingFolder.outputStream

                        localBitmap.compress(ext.compressFormat, scaled.second, fos)

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
                updateBitmap(loader())
                _selectedUri.value = uri
            }
        }
    }

    fun updateMaxBytes(newBytes: String) {
        val b = newBytes.toLongOrNull() ?: 0
        _maxBytes.value = b * 1024
    }

}