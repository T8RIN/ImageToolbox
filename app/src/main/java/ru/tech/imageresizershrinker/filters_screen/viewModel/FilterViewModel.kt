package ru.tech.imageresizershrinker.filters_screen.viewModel


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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.helper.mimeTypeInt
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SaveTarget

class FilterViewModel : ViewModel() {

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

    private val _mime = mutableStateOf(0)
    val mime by _mime

    private val _filterList = mutableStateOf(listOf<FilterTransformation<*>>())
    val filterList by _filterList

    fun setMime(mime: Int) {
        _mime.value = mime
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
            _previewBitmap.value = preview ?: bmp
            _isLoading.value = false
        }
    }

    private var job: Job? = null

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
                    runCatching {
                        getBitmap(uri)
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, exif) ->
                        val localBitmap = bitmap
                        if (localBitmap != null) {
                            val savingFolder = fileController.getSavingFolder(
                                SaveTarget(
                                    bitmapInfo = BitmapInfo(
                                        mimeTypeInt = mime.extension.mimeTypeInt,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    uri = uri,
                                    sequenceNumber = _done.value + 1
                                )
                            )

                            val fos = savingFolder.outputStream

                            localBitmap.compress(
                                mime.extension.compressFormat,
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
        getPreview: suspend () -> Bitmap?,
        uri: Uri
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.value = true
                updateBitmap(loader(), getPreview())
                _selectedUri.value = uri
                _isLoading.value = false
            }
        }
    }

    private fun updateCanSave() {
        _canSave.value = _bitmap.value != null && _filterList.value.isNotEmpty()
    }

    fun proceedBitmap(
        uri: Uri,
        bitmapResult: Result<Bitmap?>,
        getImageSize: (Uri) -> Long?
    ): Pair<Bitmap, BitmapInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            runCatching<Pair<Bitmap, ExifInterface>> {
                error("COCK")
            }
        }?.let { result ->
            if (result.isSuccess && result.getOrNull() != null) {
                val scaled = result.getOrNull()!!
                scaled.first to BitmapInfo(
                    mimeTypeInt = _mime.value,
                    quality = 0f,
                    width = scaled.first.width,
                    height = scaled.first.height
                )
            } else null
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

    fun addFilter(filter: FilterTransformation<*>) {
        _filterList.value = _filterList.value + filter
        _filterList.value = _filterList.value.distinctBy { it::class.java.name }
        updateCanSave()
    }

    fun removeFilter(filter: FilterTransformation<*>) {
        _filterList.value = _filterList.value - filter
        updateCanSave()
    }

    fun setFilteredPreview(bitmap: Bitmap) {
        _previewBitmap.value = bitmap
    }

    fun <T : Any> updateFilter(filter: FilterTransformation<*>, value: T) {
        val list = _filterList.value.toMutableList()
        val index = list.indexOf(filter)
        list[index] = list[index].copy(value)
        _filterList.value = list
    }

}