/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.feature.cipher.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.cipher.domain.CryptographyManager
import java.security.InvalidKeyException

class CipherComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    private val cryptographyManager: CryptographyManager,
    private val shareProvider: ShareProvider<Bitmap>,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

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

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun startCryptography(
        key: String,
        onFileRequest: suspend (Uri) -> ByteArray?,
        onComplete: (Throwable?) -> Unit
    ) {
        savingJob = componentScope.launch(defaultDispatcher) {
            _isSaving.value = true
            if (_uri.value == null) {
                onComplete(null)
                return@launch
            }
            val file = onFileRequest(_uri.value!!)
            runCatching {
                if (isEncrypt) {
                    _byteArray.value = file?.let { cryptographyManager.encrypt(it, key) }
                } else {
                    _byteArray.value = file?.let { cryptographyManager.decrypt(it, key) }
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
    }

    fun setIsEncrypt(isEncrypt: Boolean) {
        _isEncrypt.value = isEncrypt
        resetCalculatedData()
    }

    fun resetCalculatedData() {
        _byteArray.value = null
    }

    fun saveCryptographyTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = componentScope.launch(defaultDispatcher) {
            _isSaving.value = true
            byteArray?.let { byteArray ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(byteArray) }
                ).also(onResult).onSuccess(::registerSave)
            }
            _isSaving.value = false
        }
    }

    fun generateRandomPassword(): String = cryptographyManager.generateRandomString(18)

    fun shareFile(
        it: ByteArray,
        filename: String,
        onComplete: () -> Unit
    ) {
        savingJob = componentScope.launch {
            _isSaving.value = true
            shareProvider.shareByteArray(
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


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUri: Uri?,
        ): CipherComponent
    }
}