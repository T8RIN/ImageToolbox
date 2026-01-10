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

package com.t8rin.imagetoolbox.feature.zip.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.zip.domain.ZipManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class ZipComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    private val zipManager: ZipManager,
    private val shareProvider: ShareProvider,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::setUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _compressedArchiveUri = mutableStateOf<String?>(null)
    val compressedArchiveUri by _compressedArchiveUri

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    fun setUris(newUris: List<Uri>) {
        _uris.update { newUris.distinct() }
        resetCalculatedData()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun startCompression(
        onFailure: (Throwable) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            if (uris.isEmpty()) {
                return@trackProgress
            }
            runSuspendCatching {
                _done.update { 0 }
                _left.update { uris.size }
                _compressedArchiveUri.value = zipManager.zip(
                    files = uris.map { it.toString() },
                    onProgress = {
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }
                )
            }.onFailure(onFailure)
            _isSaving.value = false
        }
    }

    private fun resetCalculatedData() {
        _compressedArchiveUri.value = null
    }

    fun saveResultTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _compressedArchiveUri.value?.let { byteArray ->
                fileController.transferBytes(
                    fromUri = byteArray,
                    toUri = uri.toString(),
                ).also(onResult).onSuccess(::registerSave)
            }
            _isSaving.value = false
        }
    }

    fun shareFile(
        onComplete: () -> Unit
    ) {
        compressedArchiveUri?.let { uri ->
            savingJob = trackProgress {
                _done.update { 0 }
                _left.update { 0 }

                _isSaving.value = true
                shareProvider.shareUri(
                    uri = uri,
                    onComplete = {
                        _isSaving.value = false
                        onComplete()
                    }
                )
            }
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun removeUri(uri: Uri) {
        _uris.update { it - uri }
    }

    fun addUris(list: List<Uri>) = setUris(uris + list)


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
        ): ZipComponent
    }
}