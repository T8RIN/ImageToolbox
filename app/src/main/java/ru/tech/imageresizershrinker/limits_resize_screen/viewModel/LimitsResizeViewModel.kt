package ru.tech.imageresizershrinker.limits_resize_screen.viewModel


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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.aspectRatio
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.scaleUntilCanShow
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.BitmapSaveTarget

class LimitsResizeViewModel : ViewModel() {

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

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

    private val _bitmapInfo = mutableStateOf(BitmapInfo())
    val bitmapInfo by _bitmapInfo

    fun setMime(mime: Int) {
        _bitmapInfo.value = _bitmapInfo.value.copy(mimeTypeInt = mime)
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

    fun updateBitmap(bitmap: Bitmap?, preview: Bitmap? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _bitmap.value = bitmap?.scaleUntilCanShow()
            _previewBitmap.value = preview ?: _bitmap.value
            _isLoading.value = false
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun saveBitmaps(
        fileController: FileController,
        getBitmap: suspend (Uri) -> Pair<Bitmap?, ExifInterface?>,
        onResult: (Int) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var failed = 0
            if (!fileController.isExternalStorageWritable()) {
                onResult(-1)
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    /*
                    the image aspect ratio >= WidthLimit/HeightLimit
then resize by WidthLimit.
the image aspect ratio <= WidthLimit/HeightLimit
then resize by HeighthLimit.
                     */
                    runCatching {
                        getBitmap(uri)
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
                        var localBitmap = bitmap
                        if (localBitmap != null) {
                            if (localBitmap.height > bitmapInfo.height || localBitmap.width > bitmapInfo.width) {
                                if (localBitmap.aspectRatio > bitmapInfo.aspectRatio) {
                                    localBitmap = localBitmap.resizeBitmap(
                                        bitmapInfo.width,
                                        bitmapInfo.width,
                                        1
                                    )
                                } else if (localBitmap.aspectRatio < bitmapInfo.aspectRatio) {
                                    localBitmap = localBitmap.resizeBitmap(
                                        bitmapInfo.height,
                                        bitmapInfo.height,
                                        1
                                    )
                                } else {
                                    localBitmap = localBitmap.resizeBitmap(
                                        bitmapInfo.width,
                                        bitmapInfo.height,
                                        1
                                    )
                                }
                            }
                            val savingFolder = fileController.getSavingFolder(
                                BitmapSaveTarget(
                                    bitmapInfo = _bitmapInfo.value,
                                    uri = uri,
                                    sequenceNumber = _done.value + 1
                                )
                            )

                            val fos = savingFolder.outputStream

                            localBitmap.compress(
                                _bitmapInfo.value.mimeTypeInt.extension.compressFormat,
                                100,
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
                        } else failed += 1
                    }
                    _done.value += 1
                }
                onResult(failed)
            }
        }
    }

    fun setBitmap(
        loader: suspend () -> Bitmap?,
        uri: Uri
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.value = true
                updateBitmap(loader())
                _selectedUri.value = uri
                _isLoading.value = false
            }
        }
    }


    private fun updateCanSave() {
        _canSave.value =
            _bitmap.value != null && (_bitmapInfo.value.height != 0 || _bitmapInfo.value.width != 0)
    }

    fun proceedBitmap(
        bitmapResult: Result<Bitmap?>,
    ): Pair<Bitmap, BitmapInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            var localBitmap = bitmap
            if (localBitmap.height > bitmapInfo.height || localBitmap.width > bitmapInfo.width) {
                if (localBitmap.aspectRatio > bitmapInfo.aspectRatio) {
                    localBitmap = localBitmap.resizeBitmap(
                        bitmapInfo.width,
                        bitmapInfo.width,
                        1
                    )
                } else if (localBitmap.aspectRatio < bitmapInfo.aspectRatio) {
                    localBitmap = localBitmap.resizeBitmap(
                        bitmapInfo.height,
                        bitmapInfo.height,
                        1
                    )
                } else {
                    localBitmap = localBitmap.resizeBitmap(
                        bitmapInfo.width,
                        bitmapInfo.height,
                        1
                    )
                }
            }
            localBitmap to _bitmapInfo.value
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

    fun updateWidth(i: Int) {
        _bitmapInfo.value = _bitmapInfo.value.copy(width = i)
        updateCanSave()
    }

    fun updateHeight(i: Int) {
        _bitmapInfo.value = _bitmapInfo.value.copy(height = i)
        updateCanSave()
    }

}