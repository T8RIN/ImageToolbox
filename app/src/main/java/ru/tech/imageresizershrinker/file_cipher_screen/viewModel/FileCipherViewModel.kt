package ru.tech.imageresizershrinker.file_cipher_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FileCipherViewModel : ViewModel() {

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    fun setUri(newUri: Uri) {
        _uri.value = newUri
    }

}