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

@file:Suppress("FunctionName")

package ru.tech.imageresizershrinker.feature.jxl_tools.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.SaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.FileSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.jxl_tools.domain.JxlTranscoder
import javax.inject.Inject

@HiltViewModel
class JxlToolsViewModel @Inject constructor(
    private val jxlTranscoder: JxlTranscoder,
    private val fileController: FileController,
    private val shareProvider: ShareProvider<Bitmap>
) : ViewModel() {

    private val _type: MutableState<Screen.JxlTools.Type?> = mutableStateOf(null)
    val type by _type

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading by _isLoading

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    fun setType(type: Screen.JxlTools.Type?) {
        _type.update { type }
    }

    private var savingJob: Job? = null

    fun save(
        onResult: (List<SaveResult>, String) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.JxlTools.Type.JpegToJxl -> {
                    val results = mutableListOf<SaveResult>()
                    val jpegUris = type.jpegImageUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = jpegUris.size
                    jxlTranscoder.jpegToJxl(jpegUris) { uri, jxlBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = JxlSaveTarget(uri, jxlBytes),
                                keepOriginalMetadata = true
                            )
                        )
                        _done.update { it + 1 }
                    }

                    onResult(results, fileController.savingPath)
                }

                is Screen.JxlTools.Type.JxlToJpeg -> {
                    val results = mutableListOf<SaveResult>()
                    val jxlUris = type.jxlImageUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = jxlUris.size
                    jxlTranscoder.jxlToJpeg(jxlUris) { uri, jpegBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = JpegSaveTarget(uri, jpegBytes),
                                keepOriginalMetadata = true
                            )
                        )
                        _done.update { it + 1 }
                    }

                    onResult(results, fileController.savingPath)
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    private fun JpegSaveTarget(
        uri: String,
        jpegBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = filename(
            uri = uri,
            format = ImageFormat.Jpg
        ),
        data = jpegBytes,
        imageFormat = ImageFormat.Jpg
    )

    private fun JxlSaveTarget(
        uri: String,
        jxlBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = filename(
            uri = uri,
            format = ImageFormat.Jxl.Lossless
        ),
        data = jxlBytes,
        imageFormat = ImageFormat.Jxl.Lossless
    )

    private fun filename(
        uri: String,
        format: ImageFormat
    ): String = fileController.constructImageFilename(
        ImageSaveTarget<ExifInterface>(
            imageInfo = ImageInfo(
                imageFormat = format,
                originalUri = uri
            ),
            originalUri = uri,
            sequenceNumber = done + 1,
            metadata = null,
            data = ByteArray(0)
        ),
        forceNotAddSizeInFilename = true
    )

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun performSharing(onComplete: () -> Unit) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = viewModelScope.launch(Dispatchers.IO) {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.JxlTools.Type.JpegToJxl -> {
                    val results = mutableListOf<String?>()
                    val jpegUris = type.jpegImageUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = jpegUris.size
                    jxlTranscoder.jpegToJxl(jpegUris) { uri, jxlBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = jxlBytes,
                                filename = filename(uri, ImageFormat.Jxl.Lossless)
                            )
                        )
                        _done.update { it + 1 }
                    }

                    shareProvider.shareUris(results.filterNotNull())
                    onComplete()
                }

                is Screen.JxlTools.Type.JxlToJpeg -> {
                    val results = mutableListOf<String?>()
                    val jxlUris = type.jxlImageUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = jxlUris.size
                    jxlTranscoder.jxlToJpeg(jxlUris) { uri, jpegBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = jpegBytes,
                                filename = filename(uri, ImageFormat.Jpg)
                            )
                        )
                        _done.update { it + 1 }
                    }

                    shareProvider.shareUris(results.filterNotNull())
                    onComplete()
                }

                null -> Unit
            }

            _isSaving.value = false
        }
    }

    fun clearAll() = setType(null)

    fun removeUri(uri: Uri) {
        _type.update {
            when (val type = it) {
                is Screen.JxlTools.Type.JpegToJxl -> type.copy(type.jpegImageUris?.minus(uri))
                is Screen.JxlTools.Type.JxlToJpeg -> type.copy(type.jxlImageUris?.minus(uri))
                null -> null
            }
        }
    }

}