package ru.tech.imageresizershrinker.presentation.generate_palette_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.android.ImageUtils.scaleUntilCanShow

class GeneratePaletteViewModel : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    fun setUri(uri: Uri) {
        _uri.value = uri
    }

    fun updateBitmap(bitmap: Bitmap?) {
        viewModelScope.launch {
            _isLoading.value = true
            _bitmap.value = bitmap?.scaleUntilCanShow()
            _isLoading.value = false
        }
    }

}