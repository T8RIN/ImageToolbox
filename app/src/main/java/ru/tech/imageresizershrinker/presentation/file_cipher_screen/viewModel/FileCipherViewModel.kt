package ru.tech.imageresizershrinker.presentation.file_cipher_screen.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.coredomain.image.ImageManager
import ru.tech.imageresizershrinker.coredomain.use_case.decrypt_file.DecryptFileUseCase
import ru.tech.imageresizershrinker.coredomain.use_case.encrypt_file.EncryptFileUseCase
import ru.tech.imageresizershrinker.coredomain.use_case.generate_random_password.GenerateRandomPasswordUseCase
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

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    fun setUri(newUri: Uri) {
        _uri.value = newUri
        resetCalculatedData()
    }

    private var savingJob: Job? = null

    fun startCryptography(
        key: String,
        onFileRequest: suspend (Uri) -> ByteArray?,
        onComplete: (Throwable?) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
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
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun setIsEncrypt(isEncrypt: Boolean) {
        _isEncrypt.value = isEncrypt
        resetCalculatedData()
    }

    fun resetCalculatedData() {
        _byteArray.value = null
    }

    fun saveCryptographyTo(
        outputStream: OutputStream?,
        onComplete: (Throwable?) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            kotlin.runCatching {
                outputStream?.use {
                    it.write(_byteArray.value)
                }
            }.exceptionOrNull().let(onComplete)
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun generateRandomPassword(): String = generateRandomPasswordUseCase(18)

    fun shareFile(it: ByteArray, filename: String, onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _isSaving.value = true
            imageManager.shareFile(
                byteArray = it,
                filename = filename,
                onComplete = {
                    _isSaving.value = false
                    onComplete()
                }
            )
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

}