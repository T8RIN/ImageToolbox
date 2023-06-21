package ru.tech.imageresizershrinker.file_cipher_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.cipher.CipherUtils.decrypt
import ru.tech.imageresizershrinker.utils.cipher.CipherUtils.encrypt
import java.io.OutputStream
import java.security.InvalidKeyException

class FileCipherViewModel : ViewModel() {

    private val _uri = mutableStateOf<Uri?>(null)
    val uri by _uri

    private val _isEncrypt = mutableStateOf(true)
    val isEncrypt by _isEncrypt

    private val _byteArray = mutableStateOf<ByteArray?>(null)
    val byteArray by _byteArray

    fun setUri(newUri: Uri) {
        _uri.value = newUri
        resetCalculatedData()
    }

    fun startCryptography(
        key: String,
        onFileRequest: suspend (Uri) -> ByteArray?,
        onComplete: (Throwable?) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (_uri.value == null) {
                    onComplete(null)
                    return@withContext
                }
                val file = onFileRequest(_uri.value!!)
                runCatching {
                    if (isEncrypt) {
                        _byteArray.value = file?.encrypt(key)
                    } else {
                        _byteArray.value = file?.decrypt(key)
                    }
                }.exceptionOrNull().let {
                    onComplete(
                        if (it?.message?.contains("mac") == true && it.message?.contains("failed") == true) {
                            InvalidKeyException()
                        } else it
                    )
                }
            }
        }
    }

    fun setIsEncrypt(isEncrypt: Boolean) {
        _isEncrypt.value = isEncrypt
        resetCalculatedData()
    }

    fun resetCalculatedData() {
        _byteArray.value = null
    }

    fun saveCryptographyTo(outputStream: OutputStream?, onComplete: (Throwable?) -> Unit) {
        kotlin.runCatching {
            outputStream?.use {
                it.write(_byteArray.value)
            }
        }.exceptionOrNull().let(onComplete)

        /*TODO explicitly show password error to user*/
    }

}