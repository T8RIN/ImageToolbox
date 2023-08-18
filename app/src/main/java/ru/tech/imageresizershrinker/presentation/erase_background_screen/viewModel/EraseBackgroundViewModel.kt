package ru.tech.imageresizershrinker.presentation.erase_background_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import javax.inject.Inject

@HiltViewModel
class EraseBackgroundViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _erasePaths = mutableStateOf(listOf<PathPaint>())
    val erasePaths: List<PathPaint> by _erasePaths

    val isSaving: Boolean = false
    val sizeInBytes: Long = 0L
    val isImageLoading: Boolean = false

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default())
    val imageFormat: ImageFormat by _imageFormat

    private val _uri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
        }
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
    }

    fun decodeBitmapByUri(
        uri: Uri,
        originalSize: Boolean = true,
        onGetMimeType: (ImageFormat) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetBitmap: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        imageManager.getImageAsync(
            uri = uri.toString(),
            originalSize = originalSize,
            onGetImage = {
                onGetBitmap(it.image)
                onGetExif(it.metadata)
                onGetMimeType(it.imageInfo.imageFormat)
            },
            onError = onError
        )
    }

    fun setMime(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    fun addErasePath(path: PathPaint) {
        _erasePaths.value = _erasePaths.value.toMutableList().apply {
            add(path)
        }
    }
}
