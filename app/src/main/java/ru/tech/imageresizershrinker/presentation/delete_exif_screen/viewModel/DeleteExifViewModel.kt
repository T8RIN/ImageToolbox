package ru.tech.imageresizershrinker.presentation.delete_exif_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.model.BitmapInfo
import ru.tech.imageresizershrinker.domain.model.BitmapSaveTarget
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.compress
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.scaleUntilCanShow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class DeleteExifViewModel @Inject constructor(
    private val fileController: FileController
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
            _previewBitmap.value = bitmap?.scaleUntilCanShow()
            _isLoading.value = false
        }
    }

    fun saveBitmaps(
        getBitmap: suspend (Uri) -> Pair<Bitmap?, MimeType>,
        onResult: (Int, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var failed = 0
            if (!fileController.isExternalStorageWritable()) {
                onResult(-1, "")
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    runCatching {
                        getBitmap(uri)
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, mimeType) ->
                        bitmap?.let { result ->
                            val out = ByteArrayOutputStream()
                            result.compress(
                                mimeType = mimeType,
                                quality = 100,
                                out = out
                            )
                            fileController.save(
                                BitmapSaveTarget(
                                    bitmapInfo = BitmapInfo(
                                        mimeType = mimeType,
                                        width = result.width,
                                        height = result.height
                                    ),
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value,
                                    data = out.toByteArray()
                                ),
                                keepMetadata = false
                            )
                        } ?: {
                            failed += 1
                        }
                    }

                    _done.value += 1
                }
                onResult(failed, fileController.savingPath)
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