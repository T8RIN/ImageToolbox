package ru.tech.imageresizershrinker.presentation.delete_exif_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import javax.inject.Inject

@HiltViewModel
class DeleteExifViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
    }

    fun updateUrisSilently(removedUri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uris.value = uris
                if (_selectedUri.value == removedUri) {
                    val index = uris?.indexOf(removedUri) ?: -1
                    if (index == 0) {
                        uris?.getOrNull(1)?.let {
                            _selectedUri.value = it
                            _bitmap.value =
                                imageManager.getImage(it.toString(), originalSize = false)
                        }
                    } else {
                        uris?.getOrNull(index - 1)?.let {
                            _selectedUri.value = it
                            _bitmap.value =
                                imageManager.getImage(it.toString(), originalSize = false)
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
            _previewBitmap.value = imageManager.scaleUntilCanShow(bitmap)
            _isLoading.value = false
        }
    }

    fun saveBitmaps(
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
                        imageManager.getImageWithMime(uri.toString())
                    }.getOrNull()?.takeIf { it.first != null }?.let { (bitmap, mimeType) ->
                        bitmap?.let { result ->
                            fileController.save(
                                ImageSaveTarget(
                                    imageInfo = ImageInfo(
                                        mimeType = mimeType,
                                        width = result.width,
                                        height = result.height
                                    ),
                                    originalUri = uri.toString(),
                                    sequenceNumber = _done.value,
                                    data = imageManager.compress(
                                        result,
                                        ImageInfo(mimeType = mimeType)
                                    )
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

    fun setBitmap(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateBitmap(imageManager.getImage(uri.toString(), originalSize = false))
                _selectedUri.value = uri
            }
        }
    }

    fun decodeBitmapByUri(
        uri: Uri,
        originalSize: Boolean = true,
        onGetMimeType: (MimeType) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetBitmap: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        imageManager.getImageAsync(
            uri = uri.toString(),
            originalSize = originalSize,
            onGetImage = onGetBitmap,
            onGetMetadata = onGetExif,
            onGetMimeType = onGetMimeType,
            onError = onError
        )
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        viewModelScope.launch {
            imageManager.shareImages(
                uris = uris?.map { it.toString() } ?: emptyList(),
                imageLoader = { uri ->
                    imageManager.getImageWithMime(uri)
                        .takeIf { it.first != null }?.let {
                            it.first!! to ImageInfo(
                                mimeType = it.second,
                                width = it.first!!.width,
                                height = it.first!!.height
                            )
                        }
                },
                onProgressChange = {
                    if (it == -1) {
                        onComplete()
                        _done.value = 0
                    } else {
                        _done.value = it
                    }
                }
            )
        }
    }

    fun getImageManager(): ImageManager<Bitmap, ExifInterface> = imageManager

}