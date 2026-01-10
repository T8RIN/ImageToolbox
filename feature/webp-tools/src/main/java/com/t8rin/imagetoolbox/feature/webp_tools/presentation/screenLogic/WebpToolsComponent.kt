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

package com.t8rin.imagetoolbox.feature.webp_tools.presentation.screenLogic

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
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.webp_tools.domain.WebpConverter
import com.t8rin.imagetoolbox.feature.webp_tools.domain.WebpParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion

class WebpToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialType: Screen.WebpTools.Type?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val webpConverter: WebpConverter,
    private val shareProvider: ShareProvider,
    defaultDispatchersHolder: DispatchersHolder
) : BaseComponent(defaultDispatchersHolder, componentContext) {

    init {
        debounce {
            initialType?.let(::setType)
        }
    }

    private val _type: MutableState<Screen.WebpTools.Type?> = mutableStateOf(null)
    val type by _type

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading by _isLoading

    private val _isLoadingWebpImages: MutableState<Boolean> = mutableStateOf(false)
    val isLoadingWebpImages by _isLoadingWebpImages

    private val _params: MutableState<WebpParams> = mutableStateOf(WebpParams.Default)
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

    private var webpData: ByteArray? = null

    fun setType(type: Screen.WebpTools.Type) {
        when (type) {
            is Screen.WebpTools.Type.WebpToImage -> {
                type.webpUri?.let { setWebpUri(it) } ?: _type.update { null }
            }

            is Screen.WebpTools.Type.ImageToWebp -> {
                _type.update { type }
            }
        }
    }

    fun setImageUris(uris: List<Uri>) {
        clearAll()
        _type.update {
            Screen.WebpTools.Type.ImageToWebp(uris)
        }
    }

    private var collectionJob: Job? by smartJob {
        _isLoading.update { false }
    }

    fun setWebpUri(uri: Uri) {
        clearAll()
        _type.update {
            Screen.WebpTools.Type.WebpToImage(uri)
        }
        updateWebpFrames(ImageFrames.All)
        collectionJob = componentScope.launch {
            _isLoading.update { true }
            _isLoadingWebpImages.update { true }
            webpConverter.extractFramesFromWebp(
                webpUri = uri.toString(),
                imageFormat = imageFormat,
                quality = params.quality
            ).onCompletion {
                _isLoading.update { false }
                _isLoadingWebpImages.update { false }
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
        webpData = null
        savingJob = null
        updateParams(WebpParams.Default)
        registerChangesCleared()
    }

    fun updateWebpFrames(imageFrames: ImageFrames) {
        _imageFrames.update { imageFrames }
        registerChanges()
    }

    fun clearConvertedImagesSelection() = updateWebpFrames(ImageFrames.ManualSelection(emptyList()))

    fun selectAllConvertedImages() = updateWebpFrames(ImageFrames.All)

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveWebpTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        savingJob = trackProgress {
            _isSaving.value = true
            webpData?.let { byteArray ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(byteArray) }
                ).also(onResult).onSuccess(::registerSave)
            }
            _isSaving.value = false
            webpData = null
        }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onWebpSaveResult: (String) -> Unit,
        onResult: (List<SaveResult>) -> Unit
    ) {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = trackProgress {
            _isSaving.value = true
            _left.value = 1
            _done.value = 0
            when (val type = _type.value) {
                is Screen.WebpTools.Type.WebpToImage -> {
                    val results = mutableListOf<SaveResult>()
                    type.webpUri?.toString()?.also { webpUri ->
                        _left.value = 0
                        webpConverter.extractFramesFromWebp(
                            webpUri = webpUri,
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
                            updateProgress(
                                done = done,
                                total = left
                            )
                        }
                    }
                }

                is Screen.WebpTools.Type.ImageToWebp -> {
                    _left.value = type.imageUris?.size ?: -1
                    webpData = type.imageUris?.map { it.toString() }?.let { list ->
                        webpConverter.createWebpFromImageUris(
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
                            onWebpSaveResult("WEBP_${timestamp()}.webp")
                            registerSave()
                        }
                    }
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun reorderImageUris(uris: List<Uri>?) {
        if (type is Screen.WebpTools.Type.ImageToWebp) {
            _type.update {
                Screen.WebpTools.Type.ImageToWebp(uris)
            }
        }
        registerChanges()
    }

    fun addImageToUris(uris: List<Uri>) {
        val type = _type.value
        if (type is Screen.WebpTools.Type.ImageToWebp) {
            _type.update {
                val newUris = type.imageUris?.plus(uris)?.toSet()?.toList()

                Screen.WebpTools.Type.ImageToWebp(newUris)
            }
        }
        registerChanges()
    }

    fun removeImageAt(index: Int) {
        val type = _type.value
        if (type is Screen.WebpTools.Type.ImageToWebp) {
            _type.update {
                val newUris = type.imageUris?.toMutableList()?.apply {
                    removeAt(index)
                }

                Screen.WebpTools.Type.ImageToWebp(newUris)
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

    fun updateParams(params: WebpParams) {
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
                is Screen.WebpTools.Type.WebpToImage -> {
                    _left.value = -1
                    val positions =
                        imageFrames.getFramePositions(convertedImageUris.size).map { it - 1 }
                    val uris = convertedImageUris.filterIndexed { index, _ ->
                        index in positions
                    }
                    onComplete(uris.map { it.toUri() })
                }

                is Screen.WebpTools.Type.ImageToWebp -> {
                    _left.value = type.imageUris?.size ?: -1
                    type.imageUris?.map { it.toString() }?.let { list ->
                        webpConverter.createWebpFromImageUris(
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
                        )?.also { byteArray ->
                            shareProvider.cacheByteArray(
                                byteArray = byteArray,
                                filename = "WEBP_${timestamp()}.webp",
                            )?.let {
                                onComplete(listOf(it.toUri()))
                            }
                        }
                    }
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    val canSave: Boolean
        get() = (imageFrames == ImageFrames.All)
            .or(type is Screen.WebpTools.Type.ImageToWebp)
            .or((imageFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialType: Screen.WebpTools.Type?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): WebpToolsComponent
    }

}