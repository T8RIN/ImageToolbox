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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.update
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.utils.state.updateNotNull
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiToolsRepository
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.components.NeuralSaveProgress
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class AiToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val aiToolsRepository: AiToolsRepository<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _saveProgress: MutableState<NeuralSaveProgress?> = mutableStateOf(null)
    val saveProgress by _saveProgress

    private var savingJob: Job? by smartJob {
        _saveProgress.update { null }
    }

    private var downloadJobs: MutableMap<String, Job> = mutableMapOf()

    val occupiedStorageSize: StateFlow<Long> = aiToolsRepository.occupiedStorageSize

    val downloadedModels: StateFlow<List<NeuralModel>> = aiToolsRepository.downloadedModels

    val notDownloadedModels: StateFlow<List<NeuralModel>> = downloadedModels.map {
        NeuralModel.entries - it.toSet()
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.Eagerly,
        initialValue = NeuralModel.entries
    )

    val selectedModel: StateFlow<NeuralModel?> = aiToolsRepository.selectedModel

    private val _downloadProgresses: SnapshotStateMap<String, DownloadProgress> =
        mutableStateMapOf()
    val downloadProgresses: Map<String, DownloadProgress> = _downloadProgresses

    private val _params = fileController.savable(
        scope = componentScope,
        initial = NeuralParams.Default
    )
    val params by _params

    private val errorsChannel: Channel<String> = Channel(Channel.BUFFERED)
    val errors: Flow<String> = errorsChannel.receiveAsFlow()

    private val _imageFormat: MutableState<ImageFormat?> = mutableStateOf(null)
    val imageFormat by _imageFormat

    private val aiProgressListener = object : AiProgressListener {
        override fun onError(error: String) {
            errorsChannel.trySend(error)
        }

        override fun onProgress(
            currentChunkIndex: Int,
            totalChunks: Int
        ) {
            _saveProgress.updateNotNull {
                it.copy(
                    doneChunks = currentChunkIndex,
                    totalChunks = totalChunks
                )
            }
        }
    }

    fun selectModel(model: NeuralModel) {
        componentScope.launch {
            aiToolsRepository.selectModel(model)
            registerChanges()
        }
    }

    fun downloadModel(model: NeuralModel) {
        if (downloadJobs.contains(model.name)) return

        downloadJobs[model.name] = componentScope.launch {
            aiToolsRepository
                .downloadModel(model)
                .onCompletion {
                    _downloadProgresses.remove(model.name)
                    downloadJobs.remove(model.name)
                }
                .catch {
                    _downloadProgresses.remove(model.name)
                    downloadJobs.remove(model.name)
                }
                .collect { progress ->
                    _downloadProgresses[model.name] = progress
                }
        }
    }

    fun importModel(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {
        componentScope.launch {
            _isImageLoading.update { true }
            onResult(aiToolsRepository.importModel(uri.toString()))
            _isImageLoading.update { false }
        }
    }

    fun deleteModel(model: NeuralModel) {
        componentScope.launch {
            aiToolsRepository.deleteModel(model)
        }
    }

    fun updateParams(
        action: NeuralParams.() -> NeuralParams
    ) {
        _params.update { action(it) }
        registerChanges()
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris
    }

    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onResult: (List<SaveResult>) -> Unit
    ) {
        savingJob = trackProgress {
            if (selectedModel.value?.type == NeuralModel.Type.REMOVE_BG && imageFormat == null) {
                setImageFormat(ImageFormat.Png.Lossless)
            }
            delay(400)
            _saveProgress.update {
                NeuralSaveProgress(
                    doneImages = 0,
                    totalImages = uris.orEmpty().size,
                    doneChunks = 0,
                    totalChunks = 0
                )
            }

            val results = mutableListOf<SaveResult>()
            uris?.forEach { uri ->
                runSuspendCatching {
                    val (image, imageInfo) = imageGetter.getImage(uri.toString())
                        ?: return@runSuspendCatching null

                    aiToolsRepository.processImage(
                        image = image,
                        listener = aiProgressListener,
                        params = params
                    )?.let {
                        it to imageInfo.copy(
                            width = it.width,
                            height = it.height,
                            originalUri = uri.toString(),
                            imageFormat = imageFormat ?: imageInfo.imageFormat
                        )
                    }
                }.onFailure {
                    results.add(
                        SaveResult.Error.Exception(it)
                    )
                }.getOrNull()?.let { (image, imageInfo) ->
                    results.add(
                        fileController.save(
                            ImageSaveTarget(
                                imageInfo = imageInfo,
                                originalUri = uri.toString(),
                                sequenceNumber = _saveProgress.value?.doneImages?.plus(1),
                                data = imageCompressor.compressAndTransform(
                                    image = image,
                                    imageInfo = imageInfo
                                )
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri
                        )
                    )
                }

                _saveProgress.updateNotNull {
                    it.copy(
                        doneImages = it.doneImages + 1
                    )
                }
            }
            onResult(results.onSuccess(::registerSave))
            _saveProgress.update { null }

            aiToolsRepository.cleanup()
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _saveProgress.update { null }
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = trackProgress {
            if (selectedModel.value?.type == NeuralModel.Type.REMOVE_BG && imageFormat == null) {
                setImageFormat(ImageFormat.Png.Lossless)
            }
            delay(400)
            _saveProgress.update {
                NeuralSaveProgress(
                    doneImages = 0,
                    totalImages = uris.orEmpty().size,
                    doneChunks = 0,
                    totalChunks = 0
                )
            }
            val list = mutableListOf<Uri>()
            uris?.forEach { uri ->
                runSuspendCatching {
                    val (image, imageInfo) = imageGetter.getImage(uri.toString())
                        ?: return@runSuspendCatching null

                    aiToolsRepository.processImage(
                        image = image,
                        listener = aiProgressListener,
                        params = params
                    )?.let {
                        it to imageInfo.copy(
                            width = it.width,
                            height = it.height,
                            originalUri = uri.toString(),
                            imageFormat = imageFormat ?: imageInfo.imageFormat
                        )
                    }
                }.getOrNull()?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo
                    )?.let { uri ->
                        list.add(uri.toUri())
                    }
                }

                _saveProgress.updateNotNull {
                    it.copy(
                        doneImages = it.doneImages + 1
                    )
                }
            }
            onComplete(list)
            _saveProgress.update { null }

            aiToolsRepository.cleanup()
        }
    }

    fun shareBitmaps(onComplete: () -> Unit) {
        cacheImages { uris ->
            componentScope.launch {
                shareProvider.shareUris(uris.map { it.toString() })
                onComplete()
            }
        }
    }

    fun removeUri(uri: Uri) {
        _uris.update { it.orEmpty() - uri }
    }

    fun addUris(uris: List<Uri>) {
        _uris.update { it.orEmpty() + uris }
    }

    fun setImageFormat(imageFormat: ImageFormat?) {
        _imageFormat.update { imageFormat }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): AiToolsComponent
    }


}