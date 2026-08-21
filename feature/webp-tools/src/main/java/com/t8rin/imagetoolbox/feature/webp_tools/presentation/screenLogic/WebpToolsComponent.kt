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
import com.t8rin.imagetoolbox.core.domain.image.model.AnimationMergeItem
import com.t8rin.imagetoolbox.core.domain.image.model.AnimationMergeParams
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.FileSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenameSelectionData
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
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
    private val filenameCreator: FilenameCreator,
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

    private val _gifQuality: MutableState<Quality.Base> = mutableStateOf(Quality.Base(50))
    val gifQuality by _gifQuality

    private val _jxlQuality: MutableState<Quality.Jxl> = mutableStateOf(Quality.Jxl())
    val jxlQuality by _jxlQuality

    private val _mergeParams = mutableStateOf(
        AnimationMergeParams(quality = Quality.Base())
    )
    val mergeParams by _mergeParams

    private val _mergeItems = mutableStateOf<Map<String, AnimationMergeItem>>(emptyMap())

    private var webpData: ByteArray? = null

    fun setType(type: Screen.WebpTools.Type) {
        when (type) {
            is Screen.WebpTools.Type.WebpToImage -> {
                type.webpUri?.let { setWebpUri(it) } ?: _type.update { null }
            }

            is Screen.WebpTools.Type.ImageToWebp -> {
                _type.update { type }
            }

            is Screen.WebpTools.Type.WebpToGif -> {
                _type.update { type }
            }

            is Screen.WebpTools.Type.WebpToApng -> {
                _type.update { type }
            }

            is Screen.WebpTools.Type.WebpToJxl -> {
                _type.update { type }
            }

            is Screen.WebpTools.Type.MergeWebp -> {
                _type.update { type }
                _mergeItems.update { current ->
                    type.webpUris.orEmpty().associate { uri ->
                        uri.toString() to (current[uri.toString()]
                            ?: AnimationMergeItem(uri.toString()))
                    }
                }
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
        _mergeParams.value = AnimationMergeParams(quality = Quality.Base())
        _mergeItems.value = emptyMap()
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

    fun saveWebpTo(uri: Uri) {
        savingJob = trackProgress {
            _isSaving.value = true
            webpData?.let { byteArray ->
                fileController.writeBytes(
                    uri = uri.toString(),
                    block = { it.writeBytes(byteArray) }
                ).also(::parseFileSaveResult).onSuccess(::registerSave)
            }
            _isSaving.value = false
            webpData = null
        }
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onWebpSaveResult: (String) -> Unit
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
                            parseSaveResults(results.onSuccess(::registerSave))
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
                                parseSaveResults(listOf(SaveResult.Error.Exception(it)))
                            }
                        )?.also {
                            onWebpSaveResult("WEBP_${timestamp()}.webp")
                            registerSave()
                        }
                    }
                }

                is Screen.WebpTools.Type.WebpToGif -> {
                    val results = mutableListOf<SaveResult>()
                    val webpUris = type.webpUris?.map(Uri::toString).orEmpty()

                    _left.value = webpUris.size
                    webpConverter.convertWebpToGif(
                        webpUris = webpUris,
                        quality = gifQuality
                    ) { uri, gifBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = gifSaveTarget(uri, gifBytes),
                                keepOriginalMetadata = true,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(done = done, total = left)
                    }

                    parseSaveResults(results.onSuccess(::registerSave))
                }

                is Screen.WebpTools.Type.WebpToApng -> {
                    val results = mutableListOf<SaveResult>()
                    val webpUris = type.webpUris?.map(Uri::toString).orEmpty()

                    _left.value = webpUris.size
                    webpConverter.convertWebpToApng(
                        webpUris = webpUris
                    ) { uri, apngBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = apngSaveTarget(uri, apngBytes),
                                keepOriginalMetadata = true,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(done = done, total = left)
                    }

                    parseSaveResults(results.onSuccess(::registerSave))
                }

                is Screen.WebpTools.Type.WebpToJxl -> {
                    val results = mutableListOf<SaveResult>()
                    val webpUris = type.webpUris?.map(Uri::toString).orEmpty()

                    _left.value = webpUris.size
                    webpConverter.convertWebpToJxl(
                        webpUris = webpUris,
                        quality = jxlQuality
                    ) { uri, jxlBytes ->
                        results.add(
                            fileController.save(
                                saveTarget = jxlSaveTarget(uri, jxlBytes),
                                keepOriginalMetadata = true,
                                oneTimeSaveLocationUri = oneTimeSaveLocationUri
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(done = done, total = left)
                    }

                    parseSaveResults(results.onSuccess(::registerSave))
                }

                is Screen.WebpTools.Type.MergeWebp -> {
                    _left.value = type.webpUris.orEmpty().size
                    val results = mutableListOf<SaveResult>()
                    webpConverter.mergeWebps(
                        items = type.mergeItems(),
                        params = mergeParams,
                        onFailure = { results += SaveResult.Error.Exception(it) },
                        onProgress = {
                            _done.update { it + 1 }
                            updateProgress(done = done, total = left)
                        }
                    )?.let { bytes ->
                        results += fileController.save(
                            saveTarget = FileSaveTarget(
                                originalUri = type.webpUris.orEmpty().first().toString(),
                                filename = mergedWebpFilename(),
                                data = bytes,
                                imageFormat = ImageFormat.Webp.Lossy
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    }
                    parseSaveResults(results.onSuccess(::registerSave))
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    private fun gifSaveTarget(
        uri: String,
        gifBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = gifFilename(uri),
        data = gifBytes,
        imageFormat = ImageFormat.Gif
    )

    private fun apngSaveTarget(
        uri: String,
        apngBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = apngFilename(uri),
        data = apngBytes,
        imageFormat = ImageFormat.Png.Lossless
    )

    private fun jxlSaveTarget(
        uri: String,
        jxlBytes: ByteArray
    ): SaveTarget = FileSaveTarget(
        originalUri = uri,
        filename = jxlFilename(uri),
        data = jxlBytes,
        imageFormat = ImageFormat.Jxl.Lossy
    )

    private fun gifFilename(uri: String): String = filenameCreator.constructImageFilename(
        ImageSaveTarget(
            imageInfo = ImageInfo(
                imageFormat = ImageFormat.Gif,
                originalUri = uri
            ),
            originalUri = uri,
            sequenceNumber = done + 1,
            metadata = null,
            data = ByteArray(0)
        ),
        forceNotAddSizeInFilename = true
    )

    private fun apngFilename(uri: String): String = filenameCreator.constructImageFilename(
        ImageSaveTarget(
            imageInfo = ImageInfo(
                imageFormat = ImageFormat.Png.Lossless,
                originalUri = uri
            ),
            originalUri = uri,
            sequenceNumber = done + 1,
            metadata = null,
            data = ByteArray(0)
        ),
        forceNotAddSizeInFilename = true
    )

    private fun jxlFilename(uri: String): String = filenameCreator.constructImageFilename(
        ImageSaveTarget(
            imageInfo = ImageInfo(
                imageFormat = ImageFormat.Jxl.Lossy,
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

    fun reorderMergeUris(uris: List<Uri>) {
        _type.update { Screen.WebpTools.Type.MergeWebp(uris) }
        registerChanges()
    }

    fun addMergeUris(uris: List<Uri>) {
        val current = (type as? Screen.WebpTools.Type.MergeWebp)?.webpUris.orEmpty()
        setType(Screen.WebpTools.Type.MergeWebp((current + uris).distinct()))
        registerChanges()
    }

    fun removeMergeUriAt(index: Int) {
        val current = (type as? Screen.WebpTools.Type.MergeWebp)?.webpUris.orEmpty()
        setType(Screen.WebpTools.Type.MergeWebp(current.toMutableList().apply { removeAt(index) }))
        registerChanges()
    }

    fun mergeItem(uri: Uri): AnimationMergeItem =
        _mergeItems.value[uri.toString()] ?: AnimationMergeItem(uri.toString())

    fun updateMergeItem(uri: Uri, reverse: Boolean?, boomerang: Boolean?) {
        _mergeItems.update { items ->
            val item = items[uri.toString()] ?: AnimationMergeItem(uri.toString())
            items + (uri.toString() to item.copy(
                reverse = reverse ?: item.reverse,
                boomerang = boomerang ?: item.boomerang
            ))
        }
        registerChanges()
    }

    fun updateMergeParams(params: AnimationMergeParams) {
        _mergeParams.value = params
        registerChanges()
    }

    fun setImageFormat(imageFormat: ImageFormat) {
        _imageFormat.update { imageFormat }
        registerChanges()
    }

    fun getFilenameSelectionData(): FilenameSelectionData? = when (val type = type) {
        is Screen.WebpTools.Type.WebpToImage -> imageFormat
            .takeIf {
                imageFrames.getFramePositions(convertedImageUris.size).size == 1
            }
            ?.let {
                FilenameSelectionData(
                    mimeType = it.mimeType,
                    extension = it.extension
                )
            }

        is Screen.WebpTools.Type.WebpToGif -> FilenameSelectionData(
            mimeType = MimeType.Gif,
            extension = "gif"
        ).takeIf { type.webpUris?.size == 1 }

        is Screen.WebpTools.Type.WebpToApng -> FilenameSelectionData(
            mimeType = MimeType.Apng,
            extension = "png"
        ).takeIf { type.webpUris?.size == 1 }

        is Screen.WebpTools.Type.WebpToJxl -> FilenameSelectionData(
            mimeType = MimeType.Jxl,
            extension = "jxl"
        ).takeIf { type.webpUris?.size == 1 }

        is Screen.WebpTools.Type.MergeWebp -> FilenameSelectionData(
            mimeType = MimeType.Webp,
            extension = "webp"
        )

        else -> null
    }

    fun setQuality(quality: Quality) {
        updateParams(params.copy(quality = quality))
    }

    fun updateParams(params: WebpParams) {
        _params.update { params }
        registerChanges()
    }

    fun setGifQuality(quality: Quality) {
        _gifQuality.update {
            (quality as? Quality.Base) ?: Quality.Base(50)
        }
        registerChanges()
    }

    fun setJxlQuality(quality: Quality) {
        _jxlQuality.update {
            (quality as? Quality.Jxl) ?: Quality.Jxl()
        }
        registerChanges()
    }

    fun performSharing() {
        cacheImages { uris ->
            componentScope.launch {
                shareProvider.shareUris(uris.map { it.toString() })
                AppToastHost.showConfetti()
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
                            onFailure = AppToastHost::showFailureToast
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

                is Screen.WebpTools.Type.WebpToGif -> {
                    val results = mutableListOf<String?>()
                    val webpUris = type.webpUris?.map(Uri::toString).orEmpty()

                    _left.value = webpUris.size
                    webpConverter.convertWebpToGif(
                        webpUris = webpUris,
                        quality = gifQuality
                    ) { uri, gifBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = gifBytes,
                                filename = gifFilename(uri)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(done = done, total = left)
                    }

                    onComplete(results.mapNotNull { it?.toUri() })
                }

                is Screen.WebpTools.Type.WebpToApng -> {
                    val results = mutableListOf<String?>()
                    val webpUris = type.webpUris?.map(Uri::toString).orEmpty()

                    _left.value = webpUris.size
                    webpConverter.convertWebpToApng(
                        webpUris = webpUris
                    ) { uri, apngBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = apngBytes,
                                filename = apngFilename(uri)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(done = done, total = left)
                    }

                    onComplete(results.mapNotNull { it?.toUri() })
                }

                is Screen.WebpTools.Type.WebpToJxl -> {
                    val results = mutableListOf<String?>()
                    val webpUris = type.webpUris?.map(Uri::toString).orEmpty()

                    _left.value = webpUris.size
                    webpConverter.convertWebpToJxl(
                        webpUris = webpUris,
                        quality = jxlQuality
                    ) { uri, jxlBytes ->
                        results.add(
                            shareProvider.cacheByteArray(
                                byteArray = jxlBytes,
                                filename = jxlFilename(uri)
                            )
                        )
                        _done.update { it + 1 }
                        updateProgress(done = done, total = left)
                    }

                    onComplete(results.mapNotNull { it?.toUri() })
                }

                is Screen.WebpTools.Type.MergeWebp -> {
                    _left.value = type.webpUris.orEmpty().size
                    webpConverter.mergeWebps(
                        items = type.mergeItems(),
                        params = mergeParams,
                        onFailure = AppToastHost::showFailureToast,
                        onProgress = {
                            _done.update { it + 1 }
                            updateProgress(done = done, total = left)
                        }
                    )?.let { bytes ->
                        shareProvider.cacheByteArray(
                            byteArray = bytes,
                            filename = mergedWebpFilename()
                        )?.toUri()?.let { onComplete(listOf(it)) }
                    }
                }

                null -> Unit
            }
            _isSaving.value = false
        }
    }

    val canSave: Boolean
        get() = when (val type = type) {
            is Screen.WebpTools.Type.WebpToGif -> type.webpUris.orEmpty().isNotEmpty()
            is Screen.WebpTools.Type.WebpToApng -> type.webpUris.orEmpty().isNotEmpty()
            is Screen.WebpTools.Type.WebpToJxl -> type.webpUris.orEmpty().isNotEmpty()
            is Screen.WebpTools.Type.MergeWebp -> type.webpUris.orEmpty().size >= 2
            is Screen.WebpTools.Type.ImageToWebp -> type.imageUris.orEmpty().isNotEmpty()
            is Screen.WebpTools.Type.WebpToImage -> (imageFrames == ImageFrames.All)
                .or((imageFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)

            null -> false
        }

    private fun Screen.WebpTools.Type.MergeWebp.mergeItems(): List<AnimationMergeItem> =
        webpUris.orEmpty().map(::mergeItem)

    private fun mergedWebpFilename(): String = "Merged_WEBP_${timestamp()}.webp"

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
