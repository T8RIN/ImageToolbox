package ru.tech.imageresizershrinker.image_preview_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ImagePreviewViewModel : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
        _selectedUri.value = uris?.firstOrNull()
    }

    fun selectUri(uri: Uri?) {
        _selectedUri.value = uri
    }

}