package ru.tech.imageresizershrinker.presentation.bytes_resize_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.core.android.BitmapUtils.compress
import ru.tech.imageresizershrinker.core.android.BitmapUtils.scaleByMaxBytes
import ru.tech.imageresizershrinker.core.android.BitmapUtils.scaleUntilCanShow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class BytesResizeViewModel @Inject constructor(
    private val fileController: FileController
) : ViewModel() {

    private val _canSave = mutableStateOf(false)
    val canSave by _canSave

    private val _presetSelected: MutableState<Int> = mutableIntStateOf(-1)
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

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _maxBytes: MutableState<Long> = mutableLongStateOf(0L)
    val maxBytes by _maxBytes

    private val _mimeType = mutableStateOf(MimeType.Default())
    val mimeType by _mimeType

    fun setMime(mimeType: MimeType) {
        if (_mimeType.value != mimeType) {
            _mimeType.value = mimeType
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
            _previewBitmap.value = bitmap?.scaleUntilCanShow()
            _isLoading.value = false
        }
    }

    fun setKeepExif(boolean: Boolean) {
        _keepExif.value = boolean
    }

    fun saveBitmaps(
        getBitmap: suspend (Uri) -> Bitmap?,
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
                    }.getOrNull()?.let { bitmap ->
                        kotlin.runCatching {
                            if (handMode) {
                                bitmap.scaleByMaxBytes(
                                    maxBytes = maxBytes,
                                    mimeType = mimeType
                                )
                            } else {
                                bitmap.scaleByMaxBytes(
                                    maxBytes = (fileController.getSize(uri.toString()) ?: 0)
                                        .times(_presetSelected.value / 100f)
                                        .toLong(),
                                    mimeType = mimeType
                                )
                            }
                        }.let { result ->
                            if (result.isSuccess && result.getOrNull() != null) {
                                val scaled = result.getOrNull()!!
                                val localBitmap = scaled.first

                                val out = ByteArrayOutputStream()

                                localBitmap.compress(
                                    mimeType = mimeType,
                                    quality = scaled.second,
                                    out = out
                                )

                                fileController.save(
                                    ImageSaveTarget(
                                        imageInfo = ImageInfo(
                                            mimeType = mimeType,
                                            width = localBitmap.width,
                                            height = localBitmap.height
                                        ),
                                        originalUri = uri.toString(),
                                        sequenceNumber = _done.value + 1,
                                        data = out.toByteArray()
                                    ),
                                    keepMetadata = keepExif
                                )
                            } else failed += 1
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
    ): Pair<Bitmap, ImageInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            kotlin.runCatching {
                if (handMode) {
                    bitmap.scaleByMaxBytes(
                        maxBytes = maxBytes,
                        mimeType = mimeType
                    )
                } else {
                    bitmap.scaleByMaxBytes(
                        maxBytes = (getImageSize(uri) ?: 0)
                            .times(_presetSelected.value / 100f)
                            .toLong(),
                        mimeType = mimeType
                    )
                }
            }
        }?.let { result ->
            if (result.isSuccess && result.getOrNull() != null) {
                val scaled = result.getOrNull()!!
                scaled.first to ImageInfo(
                    mimeType = mimeType,
                    quality = scaled.second.toFloat(),
                    width = scaled.first.width,
                    height = scaled.first.height
                )
            } else null
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

}