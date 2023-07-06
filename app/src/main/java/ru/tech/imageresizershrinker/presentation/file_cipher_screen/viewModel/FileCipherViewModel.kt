package ru.tech.imageresizershrinker.presentation.file_cipher_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.use_case.decrypt_file.DecryptFileUseCase
import ru.tech.imageresizershrinker.domain.use_case.encrypt_file.EncryptFileUseCase
import ru.tech.imageresizershrinker.domain.use_case.generate_random_password.GenerateRandomPasswordUseCase
import java.io.OutputStream
import java.security.InvalidKeyException
import javax.inject.Inject

@HiltViewModel
class FileCipherViewModel @Inject constructor(
    private val encryptFileUseCase: EncryptFileUseCase,
    private val decryptFileUseCase: DecryptFileUseCase,
    private val generateRandomPasswordUseCase: GenerateRandomPasswordUseCase,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

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
                        _byteArray.value = file?.let { encryptFileUseCase(it, key) }
                    } else {
                        _byteArray.value = file?.let { decryptFileUseCase(it, key) }
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
    }

    fun generateRandomPassword(): String = generateRandomPasswordUseCase(18)

    fun getMimeType(uri: Uri): String? = imageManager.getMimeTypeString(uri.toString())

    fun shareFile(it: ByteArray, filename: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            imageManager.shareFile(it, filename, onComplete)
        }
    }

}