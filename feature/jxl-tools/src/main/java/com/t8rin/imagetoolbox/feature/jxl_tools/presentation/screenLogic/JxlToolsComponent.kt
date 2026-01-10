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

@file:Suppress("FunctionName")

package com.t8rin.imagetoolbox.feature.jxl_tools.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.jxl_tools.domain.AnimatedJxlParams
import com.t8rin.imagetoolbox.feature.jxl_tools.domain.JxlConverter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion

class JxlToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialType: Screen.JxlTools.Type?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val jxlConverter: JxlConverter,
    private val fileController: FileController,
    private val filenameCreator: FilenameCreator,
    private val shareProvider: ShareProvider,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialType?.let(::setType)
        }
    }

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

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _imageFrames: MutableState<ImageFrames> = mutableStateOf(ImageFrames.All)
    val imageFrames by _imageFrames

    private val _isLoadingJxlImages: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingJxlImages by _isLoadingJxlImages

    private val _params: MutableState<AnimatedJxlParams> = mutableStateOf(AnimatedJxlParams.Default)
    val params by _params

    private val _convertedImageUris: MutableState<List<String>> = mutableStateOf(emptyList())
    val convertedImageUris by _convertedImageUris

    fun setType(
        type: Screen.JxlTools.Type?,
        onFailure: (Throwable) -> Unit = {}
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
                type.jxlUri?.let { setJxlUri(it, onFailure) } ?: _type.update { null }
            }

            else -> _type.update { type }
        }
        registerChanges()
        if (_type.value == null) {
            clearAll()
        } else if (!_type.value!!::class.isInstance(type)) {
            clearAll()
        }
    }

    private var collectionJob: Job? by smartJob {
        _isLoading.update { false }
    }

    private fun setJxlUri(
        uri: Uri,
        onFailure: (Throwable) -> Unit,
    ) {
        clearAll()
        _type.update {
            Screen.JxlTools.Type.JxlToImage(uri)
        }
        updateJxlFrames(ImageFrames.All)
        collectionJob = componentScope.launch {
            _isLoading.update { true }
            _isLoadingJxlImages.update { true }
            jxlConverter.extractFramesFromJxl(
                jxlUri = uri.toString(),
                imageFormat = imageFormat,
                quality = params.quality,
                imageFrames = imageFrames,
                onFailure = onFailure
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

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun save(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
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
                        onFailure = {
                            results.add(SaveResult.Error.Exception(it))
                        }
                    ) { uri, jxlBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = JxlSaveTarget(uri, jxlBytes),
                                keepOriginalMetadata = false,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }

                    onResult(results.onSuccess(::registerSave))
                }

                is Screen.JxlTools.Type.JxlToJpeg -> {
                    val results = mutableListOf<SaveResult>()
                    val jxlUris = type.jxlImageUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = jxlUris.size
                    jxlConverter.jxlToJpeg(
                        jxlUris = jxlUris,
                        onFailure = {
                            results.add(SaveResult.Error.Exception(it))
                        }
                    ) { uri, jpegBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = JpegSaveTarget(uri, jpegBytes),
                                keepOriginalMetadata = false,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }

                    onResult(results.onSuccess(::registerSave))
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
                            onFailure = {
                                results.add(SaveResult.Error.Exception(it))
                            },
                            onGetFramesCount = {
                                if (it == 0) {
                                    _isSaving.value = false
                                    savingJob?.cancel()
                                    onResult(
                                        listOf(SaveResult.Error.MissingPermissions)
                                    )
                                }
                                _left.value = imageFrames.getFramePositions(it).size
                                updateProgress(
                                    done = done,
                                    total = left
                                )
                            }
                        ).onCompletion {
                            onResult(results.onSuccess(::registerSave))
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
                                        saveTarget = ImageSaveTarget(
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
                                        keepOriginalMetadata = false,
                                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                                    )
                                )
                            } ?: results.add(
                                SaveResult.Error.Exception(Throwable())
                            )
                            _done.value++
                            updateProgress(
                                done = done,
                                total = left
                            )
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
                                updateProgress(
                                    done = done,
                                    total = left
                                )
                            },
                            onFailure = {
                                onResult(
                                    listOf(
                                        SaveResult.Error.Exception(it)
                                    )
                                )
                            },
                        )?.also { jxlBytes ->
                            val result = fileController.save(
                                saveTarget = JxlSaveTarget("", jxlBytes),
                                keepOriginalMetadata = false,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            ).onSuccess(::registerSave)
                            onResult(listOf(result))
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
    ): String = filenameCreator.constructImageFilename(
        ImageSaveTarget(
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
        onFailure: (Throwable) -> Unit,
        onComplete: () -> Unit
    ) {
        savingJob = trackProgress {
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
                        onFailure = onFailure
                    ) { uri, jxlBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = jxlBytes,
                                filename = filename(uri, ImageFormat.Jxl.Lossless)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
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
                        onFailure = onFailure
                    ) { uri, jpegBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = jpegBytes,
                                filename = filename(uri, ImageFormat.Jpg)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
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
                                updateProgress(
                                    done = done,
                                    total = left
                                )
                            },
                            onFailure = {
                                _isSaving.value = false
                                savingJob?.cancel()
                                onFailure(it)
                            }
                        )?.also { byteArray ->
                            shareProvider.shareByteArray(
                                byteArray = byteArray,
                                filename = "JXL_${timestamp()}.jxl",
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
        updateParams(AnimatedJxlParams.Default)
        registerChangesCleared()
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
        registerChanges()
    }

    fun updateJxlFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
        registerChanges()
    }

    fun updateParams(params: AnimatedJxlParams) {
        _params.update { params }
        registerChanges()
    }

    fun clearConvertedImagesSelection() = updateJxlFrames(ImageFrames.ManualSelection(emptyList()))

    fun selectAllConvertedImages() = updateJxlFrames(ImageFrames.All)

    val canSave: Boolean
        get() = (imageFrames == ImageFrames.All)
            .or(type !is Screen.JxlTools.Type.JxlToImage)
            .or((imageFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.JxlTools.Type?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): JxlToolsComponent
    }

}