package ru.tech.imageresizershrinker.delete_exif_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.resize_screen.components.BitmapInfo
import ru.tech.imageresizershrinker.resize_screen.components.compressFormat
import ru.tech.imageresizershrinker.resize_screen.components.extension
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.SavingFolder

class DeleteExifViewModel : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

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

    fun save(
        isExternalStorageWritable: Boolean,
        getSavingFolder: (bitmapInfo: BitmapInfo) -> SavingFolder,
        getBitmap: (Uri) -> Triple<Bitmap?, ExifInterface?, Int>,
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
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif, mimeInt) ->
                        bitmap?.let { result ->

                            val savingFolder = getSavingFolder(
                                BitmapInfo(
                                    mimeTypeInt = mimeInt,
                                    width = result.width.toString(),
                                    height = result.height.toString()
                                )
                            )

                            val fos = savingFolder.outputStream

                            result.compress(
                                mimeInt.extension.compressFormat,
                                100,
                                fos
                            )

                            fos!!.flush()
                            fos.close()
                        } ?: {
                            failed += 1
                        }
                    }

                    _done.value += 1
                }
                onSuccess(failed)
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

    fun setProgress(progress: Int) {
        _done.value = progress
    }

}