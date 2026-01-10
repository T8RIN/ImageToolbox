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

package com.t8rin.imagetoolbox.feature.apng_tools.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
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
import com.t8rin.imagetoolbox.feature.apng_tools.domain.ApngConverter
import com.t8rin.imagetoolbox.feature.apng_tools.domain.ApngParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion

class ApngToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted initialType: Screen.ApngTools.Type?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val filenameCreator: FilenameCreator,
    private val apngConverter: ApngConverter,
    private val shareProvider: ShareProvider,
    defaultDispatchersHolder: DispatchersHolder
) : BaseComponent(defaultDispatchersHolder, componentContext) {

    init {
        debounce {
            initialType?.let(::setType)
        }
    }

    private val _type: MutableState<Screen.ApngTools.Type?> = mutableStateOf(null)
    val type by _type

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading by _isLoading

    private val _isLoadingApngImages: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingApngImages by _isLoadingApngImages

    private val _params: MutableState<ApngParams> = mutableStateOf(ApngParams.Default)
    val params by _params

    private val _convertedImageUris: MutableState<List<String>> = mutableStateOf(emptyList())
    val convertedImageUris by _convertedImageUris

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Png.Lossless)
    val imageFormat by _imageFormat

    private val _imageFrames: MutableState<ImageFrames> = mutableStateOf(ImageFrames.All)
    val imageFrames by _imageFrames

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _jxlQuality: MutableState<Quality.Jxl> = mutableStateOf(Quality.Jxl())
    val jxlQuality by _jxlQuality

    private var _outputApngUri: String? = null

    fun setType(type: Screen.ApngTools.Type) {
        when (type) {
            is Screen.ApngTools.Type.ApngToImage -> {
                type.apngUri?.let { setApngUri(it) } ?: _type.update { null }
            }

            is Screen.ApngTools.Type.ImageToApng -> {
                _type.update { type }
            }

            is Screen.ApngTools.Type.ApngToJxl -> {
                _type.update { type }
            }
        }
    }

    fun setImageUris(uris: List<Uri>) {
        clearAll()
        _type.update {
            Screen.ApngTools.Type.ImageToApng(uris)
        }
    }

    private var collectionJob: Job? by smartJob {
        _isLoading.update { false }
    }

    fun setApngUri(uri: Uri) {
        clearAll()
        _type.update {
            Screen.ApngTools.Type.ApngToImage(uri)
        }
        updateApngFrames(ImageFrames.All)
        collectionJob = componentScope.launch {
            _isLoading.update { true }
            _isLoadingApngImages.update { true }
            apngConverter.extractFramesFromApng(
                apngUri = uri.toString(),
                imageFormat = imageFormat,
                quality = params.quality
            ).onCompletion {
                _isLoading.update { false }
                _isLoadingApngImages.update { false }
            }.collect { nextUri ->
                if (isLoading) {
                    _isLoading.update { false }
                }
                _convertedImageUris.update { it + nextUri }
            }
        }
    }

    fun clearAll() {
        collectionJob = null
        _type.update { null }
        _convertedImageUris.update { emptyList() }
        _outputApngUri = null
        savingJob = null
        updateParams(ApngParams.Default)
        registerChangesCleared()
    }

    fun updateApngFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
        registerChanges()
    }

    fun clearConvertedImagesSelection() = updateApngFrames(ImageFrames.ManualSelection(emptyList()))

    fun selectAllConvertedImages() = updateApngFrames(ImageFrames.All)

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveApngTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _outputApngUri?.let { apngUri ->
                fileController.transferBytes(
                    fromUri = apngUri,
                    toUri = uri.toString(),
                ).also(onResult).onSuccess(::registerSave)
            }
            _isSaving.value = false
            _outputApngUri = null
        }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onApngSaveResult: (String) -> Unit,
        onResult: (List<SaveResult>) -> Unit
    ) {
        _isSaving.value = false
        savingJob = trackProgress {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.ApngTools.Type.ApngToImage -> {
                    val results = mutableListOf<SaveResult>()
                    type.apngUri?.toString()?.also { apngUri ->
                        _left.value = 0
                        apngConverter.extractFramesFromApng(
                            apngUri = apngUri,
                            imageFormat = imageFormat,
                            quality = params.quality
                        ).onCompletion {
                            onResult(results.onSuccess(::registerSave))
                        }.collect { uri ->
                            imageGetter.getImage(
                                data = uri,
                                originalSize = true
                            )?.let { localBitmap ->
                                if ((done + 1) in imageFrames.getFramePositions(convertedImageUris.size + 10)) {
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
                                }
                            } ?: results.add(
                                SaveResult.Error.Exception(Throwable())
                            )
                            _done.value++
                        }
                    }
                }

                is Screen.ApngTools.Type.ImageToApng -> {
                    _left.value = type.imageUris?.size ?: -1
                    _outputApngUri = type.imageUris?.map { it.toString() }?.let { list ->
                        apngConverter.createApngFromImageUris(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                            },
                            onFailure = {
                                onResult(listOf(SaveResult.Error.Exception(it)))
                            }
                        )?.also {
                            onApngSaveResult("APNG_${timestamp()}.png")
                            registerSave()
                        }
                    }
                }

                is Screen.ApngTools.Type.ApngToJxl -> {
                    val results = mutableListOf<SaveResult>()
                    val apngUris = type.apngUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = apngUris.size
                    apngConverter.convertApngToJxl(
                        apngUris = apngUris,
                        quality = jxlQuality
                    ) { uri, jxlBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = JxlSaveTarget(uri, jxlBytes),
                                keepOriginalMetadata = true,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.update { it + 1 }
                    }

                    onResult(results.onSuccess(::registerSave))
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    private fun JxlSaveTarget(
        uri: String,
        jxlBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = jxlFilename(uri),
        data = jxlBytes,
        imageFormat = ImageFormat.Jxl.Lossless
    )

    private fun jxlFilename(
        uri: String
    ): String = filenameCreator.constructImageFilename(
        ImageSaveTarget(
            imageInfo = ImageInfo(
                imageFormat = ImageFormat.Jxl.Lossless,
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

    fun reorderImageUris(uris: List<Uri>?) {
        if (type is Screen.ApngTools.Type.ImageToApng) {
            _type.update {
                Screen.ApngTools.Type.ImageToApng(uris)
            }
        }
        registerChanges()
    }

    fun addImageToUris(uris: List<Uri>) {
        val type = _type.value
        if (type is Screen.ApngTools.Type.ImageToApng) {
            _type.update {
                val newUris = type.imageUris?.plus(uris)?.toSet()?.toList()

                Screen.ApngTools.Type.ImageToApng(newUris)
            }
        }
        registerChanges()
    }

    fun removeImageAt(index: Int) {
        val type = _type.value
        if (type is Screen.ApngTools.Type.ImageToApng) {
            _type.update {
                val newUris = type.imageUris?.toMutableList()?.apply {
                    removeAt(index)
                }

                Screen.ApngTools.Type.ImageToApng(newUris)
            }
        }
        registerChanges()
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
        registerChanges()
    }

    fun setQuality(quality: Quality) {
        updateParams(params.copy(quality = quality))
    }

    fun updateParams(params: ApngParams) {
        _params.update { params }
        registerChanges()
    }

    fun performSharing(onComplete: () -> Unit) {
        cacheImages { uris ->
            componentScope.launch {
                shareProvider.shareUris(uris.map { it.toString() })
                onComplete()
            }
        }
    }

    fun setJxlQuality(quality: Quality) {
        _jxlQuality.update {
            (quality as? Quality.Jxl) ?: Quality.Jxl()
        }
        registerChanges()
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = trackProgress {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.ApngTools.Type.ApngToImage -> {
                    _left.value = -1
                    val positions =
                        imageFrames.getFramePositions(convertedImageUris.size).map { it - 1 }
                    val uris = convertedImageUris.filterIndexed { index, _ ->
                        index in positions
                    }
                    onComplete(uris.map { it.toUri() })
                }

                is Screen.ApngTools.Type.ImageToApng -> {
                    _left.value = type.imageUris?.size ?: -1
                    type.imageUris?.map { it.toString() }?.let { list ->
                        apngConverter.createApngFromImageUris(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                                updateProgress(
                                    done = done,
                                    total = left
                                )
                            },
                            onFailure = {}
                        )?.also { uri ->
                            onComplete(listOf(uri.toUri()))
                        }
                    }
                }

                is Screen.ApngTools.Type.ApngToJxl -> {
                    val results = mutableListOf<String?>()
                    val apngUris = type.apngUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = apngUris.size
                    apngConverter.convertApngToJxl(
                        apngUris = apngUris,
                        quality = jxlQuality
                    ) { uri, jxlBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = jxlBytes,
                                filename = jxlFilename(uri)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }

                    onComplete(results.mapNotNull { it?.toUri() })
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    val canSave: Boolean
        get() = (imageFrames == ImageFrames.All)
            .or(type is Screen.ApngTools.Type.ImageToApng)
            .or((imageFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.ApngTools.Type?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ApngToolsComponent
    }
}