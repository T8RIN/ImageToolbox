package ru.tech.imageresizershrinker.presentation.limits_resize_screen.viewModel


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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.core.android.ImageUtils.resizeBitmap
import ru.tech.imageresizershrinker.core.android.ImageUtils.scaleUntilCanShow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class LimitsResizeViewModel @Inject constructor(
    private val fileController: FileController
) : ViewModel() {

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

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _imageInfo = mutableStateOf(ImageInfo())
    val bitmapInfo by _imageInfo

    fun setMime(mimeType: MimeType) {
        _imageInfo.value = _imageInfo.value.copy(mimeType = mimeType)
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
        getBitmap: suspend (Uri) -> Bitmap?,
        onResult: (Int, String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var failed = 0
            if (!fileController.isExternalStorageWritable()) {
                onResult(-1, "")
                fileController.requestReadWritePermissions()
            } else {
                _done.value = 0
                uris?.forEach { uri ->
                    runCatching {
                        getBitmap(uri)
                    }.getOrNull()?.let { bitmap ->
                        var localBitmap = bitmap
                        if (localBitmap.height > bitmapInfo.height || localBitmap.width > bitmapInfo.width) {
                            if (localBitmap.aspectRatio > bitmapInfo.aspectRatio) {
                                localBitmap = localBitmap.resizeBitmap(
                                    bitmapInfo.width,
                                    bitmapInfo.width,
                                    ResizeType.Flexible
                                )
                            } else if (localBitmap.aspectRatio < bitmapInfo.aspectRatio) {
                                localBitmap = localBitmap.resizeBitmap(
                                    bitmapInfo.height,
                                    bitmapInfo.height,
                                    ResizeType.Flexible
                                )
                            } else {
                                localBitmap = localBitmap.resizeBitmap(
                                    bitmapInfo.width,
                                    bitmapInfo.height,
                                    ResizeType.Flexible
                                )
                            }
                        }

                        val out = ByteArrayOutputStream()
                        localBitmap.compress(
                            mimeType = bitmapInfo.mimeType,
                            quality = 100,
                            out = out
                        )

                        fileController.save(
                            ImageSaveTarget(
                                imageInfo = _imageInfo.value,
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = out.toByteArray()
                            ), keepMetadata = keepExif
                        )
                    } ?: {
                        failed += 1
                    }

                    _done.value += 1
                }
                onResult(failed, fileController.savingPath)
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
            _bitmap.value != null && (_imageInfo.value.height != 0 || _imageInfo.value.width != 0)
    }

    fun proceedBitmap(
        bitmapResult: Result<Bitmap?>,
    ): Pair<Bitmap, ImageInfo>? {
        return bitmapResult.getOrNull()?.let { bitmap ->
            var localBitmap = bitmap
            if (localBitmap.height > bitmapInfo.height || localBitmap.width > bitmapInfo.width) {
                if (localBitmap.aspectRatio > bitmapInfo.aspectRatio) {
                    localBitmap = localBitmap.resizeBitmap(
                        width_ = bitmapInfo.width,
                        height_ = bitmapInfo.width,
                        resizeType = ResizeType.Flexible
                    )
                } else if (localBitmap.aspectRatio < bitmapInfo.aspectRatio) {
                    localBitmap = localBitmap.resizeBitmap(
                        width_ = bitmapInfo.height,
                        height_ = bitmapInfo.height,
                        resizeType = ResizeType.Flexible
                    )
                } else {
                    localBitmap = localBitmap.resizeBitmap(
                        width_ = bitmapInfo.width,
                        height_ = bitmapInfo.height,
                        resizeType = ResizeType.Flexible
                    )
                }
            }
            localBitmap to _imageInfo.value
        }
    }

    fun setProgress(progress: Int) {
        _done.value = progress
    }

    fun updateWidth(i: Int) {
        _imageInfo.value = _imageInfo.value.copy(width = i)
        updateCanSave()
    }

    fun updateHeight(i: Int) {
        _imageInfo.value = _imageInfo.value.copy(height = i)
        updateCanSave()
    }

    private val Bitmap.aspectRatio: Float get() = width / height.toFloat()

    private val ImageInfo.aspectRatio: Float get() = width / height.toFloat()

}