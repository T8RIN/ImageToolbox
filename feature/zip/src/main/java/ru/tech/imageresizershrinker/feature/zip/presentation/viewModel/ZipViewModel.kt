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

package ru.tech.imageresizershrinker.feature.zip.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.zip.domain.ZipManager
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class ZipViewModel @Inject constructor(
    private val zipManager: ZipManager,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _byteArray = mutableStateOf<ByteArray?>(null)
    val byteArray by _byteArray

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

    private var savingJob: Job? = null

    fun startCompression(
        onComplete: (Throwable?) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSaving.value = true
                if (uris.isEmpty()) {
                    onComplete(null)
                    return@withContext
                }
                runCatching {
                    _done.update { 0 }
                    _left.update { uris.size }
                    _byteArray.value = zipManager.zip(
                        files = uris.map { it.toString() },
                        onProgress = {
                            _done.update { it + 1 }
                        }
                    )
                }.onFailure(onComplete)
                _isSaving.value = false
            }
        }
    }

    private fun resetCalculatedData() {
        _byteArray.value = null
    }

    fun saveResultTo(
        outputStream: OutputStream?,
        onComplete: (Throwable?) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSaving.value = true
                kotlin.runCatching {
                    outputStream?.use {
                        it.write(_byteArray.value)
                    }
                }.exceptionOrNull().let(onComplete)
                _isSaving.value = false
            }
        }
    }

    fun shareFile(
        it: ByteArray,
        filename: String,
        onComplete: () -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch {
            _done.update { 0 }
            _left.update { 0 }

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

    fun removeUri(uri: Uri) {
        _uris.update { it - uri }
    }

    fun addUris(list: List<Uri>) = setUris(uris + list)

}