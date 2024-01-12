package ru.tech.imageresizershrinker.feature.pick_color.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.logger.makeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import javax.inject.Inject

@HiltViewModel
class PickColorViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _color: MutableState<Color> = mutableStateOf(Color.Unspecified)
    val color: Color by _color

    private val _isImageLoading: MutableState<Boolean> = mutableStateOf(false)
    val isImageLoading: Boolean by _isImageLoading

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    fun setUri(uri: Uri) {
        uri.makeLog()
        _uri.value = uri
    }

    private fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isImageLoading.value = true
            _bitmap.value = imageManager.scaleUntilCanShow(bitmap)
            _isImageLoading.value = false
        }
    }

    fun updateColor(color: Color) {
        _color.value = color
    }

    fun decodeBitmapByUri(
        uri: Uri,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                updateBitmap(imageManager.getImage(uri, false))
            }.exceptionOrNull()?.let(onError)
        }
    }
}