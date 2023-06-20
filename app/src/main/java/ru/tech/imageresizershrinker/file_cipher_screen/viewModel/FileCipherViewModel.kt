package ru.tech.imageresizershrinker.file_cipher_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class FileCipherViewModel : ViewModel() {

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _isEncrypt = mutableStateOf(true)
    val isEncrypt by _isEncrypt

    fun setUri(newUri: Uri) {
        _uri.value = newUri
    }

    fun startCryptography(onComplete: () -> Unit) {

    }

    fun setIsEncrypt(isEncrypt: Boolean) {
        _isEncrypt.value = isEncrypt
    }

}