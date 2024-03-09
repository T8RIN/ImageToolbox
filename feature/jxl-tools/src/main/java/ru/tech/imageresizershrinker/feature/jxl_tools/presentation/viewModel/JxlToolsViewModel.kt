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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageFrames
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.SaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.FileSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.feature.jxl_tools.domain.JxlConverter
import ru.tech.imageresizershrinker.feature.jxl_tools.domain.JxlParams
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class JxlToolsViewModel @Inject constructor(
    private val jxlConverter: JxlConverter,
    private val fileController: FileController,
    private val shareProvider: ShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val imageCompressor: ImageCompressor<Bitmap>
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

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.PngLossless)
    val imageFormat by _imageFormat

    private val _quality: MutableState<Quality> = mutableStateOf(Quality.Base())
    val quality by _quality

    private val _imageFrames: MutableState<ImageFrames> = mutableStateOf(ImageFrames.All)
    val imageFrames by _imageFrames

    private val _isLoadingJxlImages: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingJxlImages by _isLoadingJxlImages

    private val _params: MutableState<JxlParams> = mutableStateOf(JxlParams.Default)
    val params by _params

    private val _convertedImageUris: MutableState<List<String>> = mutableStateOf(emptyList())
    val convertedImageUris by _convertedImageUris

    fun setType(
        type: Screen.JxlTools.Type?,
        onError: (Throwable) -> Unit = {}
    ) {
        when (type) {
            is Screen.JxlTools.Type.JpegToJxl -> {
                if (!type.jpegImageUris.isNullOrEmpty()) _type.update { type }
                else _type.update { null }
            }

            is Screen.JxlTools.Type.JxlToJpeg -> {
                if (!type.jxlImageUris.isNullOrEmpty()) _type.update { type }
                else _type.update { null }
            }

            is Screen.JxlTools.Type.JxlToImage -> {
                type.jxlUri?.let { setJxlUri(it, onError) } ?: _type.update { null }
            }

            else -> _type.update { type }
        }
    }

    private var collectionJob: Job? = null
    private fun setJxlUri(
        uri: Uri,
        onError: (Throwable) -> Unit,
    ) {
        clearAll()
        _type.update {
            Screen.JxlTools.Type.JxlToImage(uri)
        }
        updateJxlFrames(ImageFrames.All)
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            _isLoadingJxlImages.update { true }
            jxlConverter.extractFramesFromJxl(
                jxlUri = uri.toString(),
                imageFormat = imageFormat,
                quality = params.quality,
                imageFrames = imageFrames,
                onError = onError
            ).onCompletion {
                _isLoading.update { false }
                _isLoadingJxlImages.update { false }
            }.collect { nextUri ->
                if (isLoading) {
                    _isLoading.update { false }
                }
                _convertedImageUris.update { it + nextUri }
            }
        }
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
                    jxlConverter.jpegToJxl(
                        jpegUris = jpegUris,
                        onError = {
                            results.add(SaveResult.Error.Exception(it))
                        }
                    ) { uri, jxlBytes ->
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
                    jxlConverter.jxlToJpeg(
                        jxlUris = jxlUris,
                        onError = {
                            results.add(SaveResult.Error.Exception(it))
                        }
                    ) { uri, jpegBytes ->
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

                is Screen.JxlTools.Type.JxlToImage -> {
                    val results = mutableListOf<SaveResult>()
                    type.jxlUri?.toString()?.also { jxlUri ->
                        _left.value = 0
                        jxlConverter.extractFramesFromJxl(
                            jxlUri = jxlUri,
                            imageFormat = imageFormat,
                            quality = params.quality,
                            imageFrames = imageFrames,
                            onError = {
                                results.add(SaveResult.Error.Exception(it))
                            },
                            onGetFramesCount = {
                                if (it == 0) {
                                    _isSaving.value = false
                                    savingJob?.cancel()
                                    onResult(
                                        listOf(SaveResult.Error.MissingPermissions), ""
                                    )
                                }
                                _left.value = imageFrames.getFramePositions(it).size
                            }
                        ).onCompletion {
                            onResult(results, fileController.savingPath)
                        }.collect { uri ->
                            imageGetter.getImage(
                                data = uri,
                                originalSize = true
                            )?.let { localBitmap ->
                                val imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                )

                                results.add(
                                    fileController.save(
                                        saveTarget = ImageSaveTarget<ExifInterface>(
                                            imageInfo = imageInfo,
                                            originalUri = uri,
                                            sequenceNumber = _done.value + 1,
                                            data = imageCompressor.compressAndTransform(
                                                image = localBitmap,
                                                imageInfo = ImageInfo(
                                                    imageFormat = imageFormat,
                                                    quality = params.quality,
                                                    width = localBitmap.width,
                                                    height = localBitmap.height
                                                )
                                            )
                                        ),
                                        keepOriginalMetadata = false
                                    )
                                )
                            } ?: results.add(
                                SaveResult.Error.Exception(Throwable())
                            )
                            _done.value++
                        }
                    }
                }

                is Screen.JxlTools.Type.ImageToJxl -> {
                    _left.value = type.imageUris?.size ?: -1
                    type.imageUris?.map { it.toString() }?.let { list ->
                        jxlConverter.createJxlAnimation(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                            },
                            onError = {
                                onResult(
                                    listOf(
                                        SaveResult.Error.Exception(it)
                                    ),
                                    fileController.savingPath
                                )
                            },
                        ).also { jxlBytes ->
                            val result = fileController.save(
                                saveTarget = JxlSaveTarget("", jxlBytes),
                                keepOriginalMetadata = true
                            )
                            onResult(listOf(result), fileController.savingPath)
                        }
                    }
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

    fun performSharing(
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ) {
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
                    jxlConverter.jpegToJxl(
                        jpegUris = jpegUris,
                        onError = {}
                    ) { uri, jxlBytes ->
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
                    jxlConverter.jxlToJpeg(
                        jxlUris = jxlUris,
                        onError = {}
                    ) { uri, jpegBytes ->
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

                is Screen.JxlTools.Type.JxlToImage -> {
                    _left.value = -1
                    val positions =
                        imageFrames.getFramePositions(convertedImageUris.size).map { it - 1 }
                    val uris = convertedImageUris.filterIndexed { index, _ ->
                        index in positions
                    }
                    shareProvider.shareUris(uris)
                    onComplete()
                }

                is Screen.JxlTools.Type.ImageToJxl -> {
                    _left.value = type.imageUris?.size ?: -1
                    type.imageUris?.map { it.toString() }?.let { list ->
                        jxlConverter.createJxlAnimation(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                            },
                            onError = {
                                _isSaving.value = false
                                savingJob?.cancel()
                                onError(it)
                            }
                        ).also { byteArray ->
                            val timeStamp = SimpleDateFormat(
                                "yyyy-MM-dd_HH-mm-ss",
                                Locale.getDefault()
                            ).format(Date())
                            val jxlName = "JXL_$timeStamp"
                            shareProvider.shareByteArray(
                                byteArray = byteArray,
                                filename = "$jxlName.jxl",
                                onComplete = onComplete
                            )
                        }
                    }
                }

                null -> Unit
            }

            _isSaving.value = false
        }
    }

    fun clearAll() {
        collectionJob?.cancel()
        collectionJob = null
        _type.update { null }
        _convertedImageUris.update { emptyList() }
        savingJob?.cancel()
        savingJob = null
        _params.update { JxlParams.Default }
    }

    fun removeUri(uri: Uri) {
        setType(
            when (val type = _type.value) {
                is Screen.JxlTools.Type.JpegToJxl -> type.copy(type.jpegImageUris?.minus(uri))
                is Screen.JxlTools.Type.JxlToJpeg -> type.copy(type.jxlImageUris?.minus(uri))
                is Screen.JxlTools.Type.ImageToJxl -> type.copy(type.imageUris?.minus(uri))
                is Screen.JxlTools.Type.JxlToImage -> type
                null -> null
            }
        )
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
    }

    fun setQuality(quality: Quality) {
        _quality.update { quality }
    }

    fun updateJxlFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
    }

    fun updateParams(params: JxlParams) {
        _params.update { params }
    }

    fun setUseOriginalSize(value: Boolean) {
        _params.update {
            it.copy(size = if (value) null else IntegerSize(1000, 1000))
        }
    }

    fun clearConvertedImagesSelection() = updateJxlFrames(ImageFrames.ManualSelection(emptyList()))

    fun selectAllConvertedImages() = updateJxlFrames(ImageFrames.All)

}