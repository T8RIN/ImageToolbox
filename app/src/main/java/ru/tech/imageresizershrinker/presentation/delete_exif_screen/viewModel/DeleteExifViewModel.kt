package ru.tech.imageresizershrinker.presentation.delete_exif_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.SAVE_FOLDER
import ru.tech.imageresizershrinker.domain.model.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.storage.BitmapSaveTarget
import ru.tech.imageresizershrinker.utils.storage.FileController
import javax.inject.Inject

@HiltViewModel
class DeleteExifViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

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
                        resizeType = 1
                    )
                } else bitmap

                while (bmp?.canShow() == false) {
                    bmp = bmp?.resizeBitmap(
                        height_ = (bmp!!.height * 0.9f).toInt(),
                        width_ = (bmp!!.width * 0.9f).toInt(),
                        resizeType = 1
                    )
                }
            }
            _previewBitmap.value = bmp
            _isLoading.value = false
        }
    }

    fun saveBitmaps(
        fileController: FileController,
        getBitmap: suspend (Uri) -> Triple<Bitmap?, ExifInterface?, Int>,
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
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, _, mimeInt) ->
                        bitmap?.let { result ->

                            fileController.getSavingFolder(
                                BitmapSaveTarget(
                                    bitmapInfo = BitmapInfo(
                                        mimeTypeInt = mimeInt,
                                        width = result.width,
                                        height = result.height
                                    ),
                                    uri = uri,
                                    sequenceNumber = _done.value
                                )
                            ).getOrNull()?.outputStream?.use {
                                result.compress(
                                    mimeInt.extension.compressFormat,
                                    100,
                                    it
                                )
                            } ?: dataStore.edit { it[SAVE_FOLDER] = "" }
                        } ?: {
                            failed += 1
                        }
                    }

                    _done.value += 1
                }
                onResult(failed)
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

    fun setProgress(progress: Int) {
        _done.value = progress
    }

}