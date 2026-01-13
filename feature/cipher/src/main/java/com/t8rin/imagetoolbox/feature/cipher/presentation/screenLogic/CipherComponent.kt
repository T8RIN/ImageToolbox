/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.cipher.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.model.CipherType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.cipher.domain.CryptographyManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class CipherComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUri: Uri?,
    @Assisted val onGoBack: () -> Unit,
    private val cryptographyManager: CryptographyManager,
    private val shareProvider: ShareProvider,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _cipherType = fileController.savable(
        scope = componentScope,
        initial = CipherType.entries.first()
    )
    val cipherType: CipherType by _cipherType

    private val _showTip: MutableState<Boolean> = mutableStateOf(false)
    val showTip by _showTip

    private val _key: MutableState<String> = mutableStateOf("")
    val key by _key

    val canGoBack: Boolean
        get() = uri == null || (key.isEmpty() && byteArray == null)

    fun showTip() = _showTip.update { true }
    fun hideTip() = _showTip.update { false }

    init {
        debounce {
            initialUri?.let(::setUri)
        }
    }

    fun updateKey(newKey: String) {
        _key.update { newKey }
        resetCalculatedData()
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
        onComplete: (Throwable?) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            val uri = uri

            if (uri == null) {
                onComplete(null)
                return@trackProgress
            }
            runSuspendCatching {
                _byteArray.update {
                    fileController.readBytes(uri.toString()).let { file ->
                        if (isEncrypt) {
                            cryptographyManager.encrypt(
                                data = file,
                                key = key,
                                type = cipherType
                            )
                        } else {
                            cryptographyManager.decrypt(
                                data = file,
                                key = key,
                                type = cipherType
                            )
                        }
                    }
                }
            }.exceptionOrNull().let(onComplete)
            _isSaving.value = false
        }
    }

    fun updateCipherType(type: CipherType) {
        _cipherType.update { type }
        resetCalculatedData()
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
        savingJob = trackProgress {
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
        savingJob = trackProgress {
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
            onGoBack: () -> Unit,
        ): CipherComponent
    }
}