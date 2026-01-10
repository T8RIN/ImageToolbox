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

package com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic

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
import com.t8rin.imagetoolbox.feature.gif_tools.domain.GifConverter
import com.t8rin.imagetoolbox.feature.gif_tools.domain.GifParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion

class GifToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialType: Screen.GifTools.Type?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val filenameCreator: FilenameCreator,
    private val gifConverter: GifConverter,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialType?.let(::setType)
        }
    }

    private val _type: MutableState<Screen.GifTools.Type?> = mutableStateOf(null)
    val type by _type

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading by _isLoading

    private val _isLoadingGifImages: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingGifImages by _isLoadingGifImages

    private val _params: MutableState<GifParams> = mutableStateOf(GifParams.Default)
    val params by _params

    private val _convertedImageUris: MutableState<List<String>> = mutableStateOf(emptyList())
    val convertedImageUris by _convertedImageUris

    private val _imageFormat: MutableState<ImageFormat> = mutableStateOf(ImageFormat.Default)
    val imageFormat by _imageFormat

    private val _imageFrames: MutableState<ImageFrames> = mutableStateOf(ImageFrames.All)
    val gifFrames by _imageFrames

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _jxlQuality: MutableState<Quality.Jxl> = mutableStateOf(Quality.Jxl())
    val jxlQuality by _jxlQuality

    private val _webpQuality: MutableState<Quality.Base> = mutableStateOf(Quality.Base())
    val webpQuality by _webpQuality

    private var gifData: ByteArray? = null

    fun setType(type: Screen.GifTools.Type) {
        when (type) {
            is Screen.GifTools.Type.GifToImage -> {
                type.gifUri?.let { setGifUri(it) } ?: _type.update { null }
            }

            is Screen.GifTools.Type.ImageToGif -> {
                _type.update { type }
            }

            is Screen.GifTools.Type.GifToJxl -> {
                _type.update { type }
            }

            is Screen.GifTools.Type.GifToWebp -> {
                _type.update { type }
            }
        }
    }

    fun setImageUris(uris: List<Uri>) {
        resetState()
        _type.update {
            Screen.GifTools.Type.ImageToGif(uris)
        }
    }

    private var collectionJob: Job? by smartJob {
        _isLoading.update { false }
    }

    fun setGifUri(uri: Uri) {
        resetState()
        _type.update {
            Screen.GifTools.Type.GifToImage(uri)
        }
        updateGifFrames(ImageFrames.All)

        collectionJob = componentScope.launch {
            _isLoading.update { true }
            _isLoadingGifImages.update { true }
            gifConverter.extractFramesFromGif(
                gifUri = uri.toString(),
                imageFormat = imageFormat,
                imageFrames = ImageFrames.All,
                quality = params.quality
            ).onCompletion {
                _isLoading.update { false }
                _isLoadingGifImages.update { false }
            }.collect { nextUri ->
                if (isLoading) {
                    _isLoading.update { false }
                }
                _convertedImageUris.update { it + nextUri }
            }
        }
    }

    override fun resetState() {
        collectionJob?.cancel()
        collectionJob = null
        _type.update { null }
        _convertedImageUris.update { emptyList() }
        gifData = null
        savingJob?.cancel()
        savingJob = null
        updateParams(GifParams.Default)
        registerChangesCleared()
    }

    fun updateGifFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
        registerChanges()
    }

    fun clearConvertedImagesSelection() = updateGifFrames(ImageFrames.ManualSelection(emptyList()))

    fun selectAllConvertedImages() = updateGifFrames(ImageFrames.All)

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveGifTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            gifData?.let { byteArray ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(byteArray) }
                ).also(onResult).onSuccess(::registerSave)
            }
            _isSaving.value = false
            gifData = null
        }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onGifSaveResult: (filename: String) -> Unit,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.GifTools.Type.GifToImage -> {
                    val results = mutableListOf<SaveResult>()
                    type.gifUri?.toString()?.also { gifUri ->
                        gifConverter.extractFramesFromGif(
                            gifUri = gifUri,
                            imageFormat = imageFormat,
                            imageFrames = gifFrames,
                            quality = params.quality,
                            onGetFramesCount = {
                                if (it == 0) {
                                    _isSaving.value = false
                                    savingJob?.cancel()
                                    onResult(
                                        listOf(SaveResult.Error.MissingPermissions)
                                    )
                                }
                                _left.value = gifFrames.getFramePositions(it).size
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

                is Screen.GifTools.Type.ImageToGif -> {
                    _left.value = type.imageUris?.size ?: -1
                    gifData = type.imageUris?.map { it.toString() }?.let { list ->
                        gifConverter.createGifFromImageUris(
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
                                onResult(listOf(SaveResult.Error.Exception(it)))
                            }
                        )?.also {
                            onGifSaveResult("GIF_${timestamp()}.gif")
                        }
                    }
                }

                is Screen.GifTools.Type.GifToJxl -> {
                    val results = mutableListOf<SaveResult>()
                    val gifUris = type.gifUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = gifUris.size
                    gifConverter.convertGifToJxl(
                        gifUris = gifUris,
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
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }

                    onResult(results.onSuccess(::registerSave))
                }

                is Screen.GifTools.Type.GifToWebp -> {
                    val results = mutableListOf<SaveResult>()
                    val gifUris = type.gifUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = gifUris.size
                    gifConverter.convertGifToWebp(
                        gifUris = gifUris,
                        quality = webpQuality
                    ) { uri, webpBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = WebpSaveTarget(uri, webpBytes),
                                keepOriginalMetadata = true,
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

    private fun WebpSaveTarget(
        uri: String,
        webpBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = webpFilename(uri),
        data = webpBytes,
        imageFormat = ImageFormat.Webp.Lossless
    )

    private fun webpFilename(
        uri: String
    ): String = filenameCreator.constructImageFilename(
        ImageSaveTarget(
            imageInfo = ImageInfo(
                imageFormat = ImageFormat.Webp.Lossless,
                originalUri = uri
            ),
            originalUri = uri,
            sequenceNumber = done + 1,
            metadata = null,
            data = ByteArray(0)
        ),
        forceNotAddSizeInFilename = true
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
        if (type is Screen.GifTools.Type.ImageToGif) {
            _type.update {
                Screen.GifTools.Type.ImageToGif(uris)
            }
            registerChanges()
        }
    }

    fun addImageToUris(uris: List<Uri>) {
        val type = _type.value
        if (type is Screen.GifTools.Type.ImageToGif) {
            _type.update {
                val newUris = type.imageUris?.plus(uris)?.toSet()?.toList()

                Screen.GifTools.Type.ImageToGif(newUris)
            }
            registerChanges()
        }
    }

    fun removeImageAt(index: Int) {
        val type = _type.value
        if (type is Screen.GifTools.Type.ImageToGif) {
            _type.update {
                val newUris = type.imageUris?.toMutableList()?.apply {
                    removeAt(index)
                }

                Screen.GifTools.Type.ImageToGif(newUris)
            }
            registerChanges()
        }
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
        registerChanges()
    }

    fun setQuality(quality: Quality) {
        updateParams(params.copy(quality = quality))
    }

    fun updateParams(params: GifParams) {
        _params.update { params }
        registerChanges()
    }

    fun performSharing(onComplete: () -> Unit) {
        savingJob = trackProgress {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.GifTools.Type.GifToImage -> {
                    _left.value = -1
                    val positions =
                        gifFrames.getFramePositions(convertedImageUris.size).map { it - 1 }
                    val uris = convertedImageUris.filterIndexed { index, _ ->
                        index in positions
                    }
                    shareProvider.shareUris(uris)
                    onComplete()
                }

                is Screen.GifTools.Type.ImageToGif -> {
                    _left.value = type.imageUris?.size ?: -1
                    type.imageUris?.map { it.toString() }?.let { list ->
                        gifConverter.createGifFromImageUris(
                            imageUris = list,
                            params = params,
                            onProgress = {
                                _done.update { it + 1 }
                                updateProgress(
                                    done = done,
                                    total = left
                                )
                            },
                            onFailure = { }
                        )?.also { byteArray ->
                            shareProvider.shareByteArray(
                                byteArray = byteArray,
                                filename = "GIF_${timestamp()}.gif",
                                onComplete = onComplete
                            )
                        }
                    }
                }

                is Screen.GifTools.Type.GifToJxl -> {
                    val results = mutableListOf<String?>()
                    val gifUris = type.gifUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = gifUris.size
                    gifConverter.convertGifToJxl(
                        gifUris = gifUris,
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

                    shareProvider.shareUris(results.filterNotNull())
                }

                is Screen.GifTools.Type.GifToWebp -> {
                    val results = mutableListOf<String?>()
                    val gifUris = type.gifUris?.map {
                        it.toString()
                    } ?: emptyList()

                    _left.value = gifUris.size
                    gifConverter.convertGifToWebp(
                        gifUris = gifUris,
                        quality = webpQuality
                    ) { uri, webpBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = webpBytes,
                                filename = webpFilename(uri)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(
                            done = done,
                            total = left
                        )
                    }

                    shareProvider.shareUris(results.filterNotNull())
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    fun setJxlQuality(quality: Quality) {
        _jxlQuality.update {
            (quality as? Quality.Jxl) ?: Quality.Jxl()
        }
        registerChanges()
    }

    fun setWebpQuality(quality: Quality) {
        _webpQuality.update {
            (quality as? Quality.Base) ?: Quality.Base()
        }
        registerChanges()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.GifTools.Type?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): GifToolsComponent
    }

}