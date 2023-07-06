package ru.tech.imageresizershrinker.presentation.image_preview_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.MimeType
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
    }

    fun selectUri(uri: Uri?) {
        _selectedUri.value = uri
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

    suspend fun decodeSampledBitmapFromUri(
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? = imageManager.getSampledImage(uri.toString(), reqWidth, reqHeight)

}