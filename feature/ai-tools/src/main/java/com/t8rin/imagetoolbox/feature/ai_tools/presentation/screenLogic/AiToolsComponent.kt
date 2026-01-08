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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProcessCallback
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiToolsRepository
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _total: MutableState<Int> = mutableIntStateOf(0)
    val total by _total

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    private var downloadJob: Job? by smartJob()


    val downloadedModels: StateFlow<List<NeuralModel>> = aiToolsRepository.downloadedModels

    val notDownloadedModels: StateFlow<List<NeuralModel>> = downloadedModels.map {
        NeuralModel.entries - it.toSet()
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.Eagerly,
        initialValue = NeuralModel.entries
    )

    val selectedModel: StateFlow<NeuralModel?> = aiToolsRepository.selectedModel

    private val _downloadProgress: MutableState<RemoteResourcesDownloadProgress?> =
        mutableStateOf(null)
    val downloadProgress by _downloadProgress

    private val _params: MutableState<NeuralParams> = mutableStateOf(NeuralParams.Default)
    val params by _params

    fun selectModel(model: NeuralModel) {
        componentScope.launch {
            aiToolsRepository.selectModel(model)
            registerChanges()
        }
    }

    fun downloadModel(model: NeuralModel) {
        downloadJob = componentScope.launch {
            delay(500)
            aiToolsRepository
                .downloadModel(model)
                .onStart {
                    _downloadProgress.update {
                        RemoteResourcesDownloadProgress(
                            currentPercent = 0f,
                            currentTotalSize = 0
                        )
                    }
                }
                .onCompletion {
                    _downloadProgress.update { null }
                    downloadJob = null
                }
                .catch {
                    _downloadProgress.update { null }
                    downloadJob = null
                }
                .collect { progress ->
                    _downloadProgress.update {
                        RemoteResourcesDownloadProgress(
                            currentPercent = progress.currentPercent,
                            currentTotalSize = progress.currentTotalSize
                        )
                    }
                }
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
        savingJob = componentScope.launch {
            _isSaving.value = true
            val results = mutableListOf<SaveResult>()
            _done.value = 0
            uris?.forEach { uri ->
                val totalDone = done

                runSuspendCatching {
                    val (image, imageInfo) = imageGetter.getImage(uri.toString())
                        ?: return@runSuspendCatching null

                    aiToolsRepository.processImage(
                        image = image,
                        callback = object : AiProcessCallback {
                            override fun onChunkProgress(
                                currentChunkIndex: Int,
                                totalChunks: Int
                            ) {
                                _total.value = totalChunks
                                _done.value = currentChunkIndex + 1
                            }
                        },
                        params = params
                    )?.let {
                        it to imageInfo
                    }
                }.getOrNull()?.let { (image, imageInfo) ->
                    results.add(
                        fileController.save(
                            ImageSaveTarget(
                                imageInfo = imageInfo.copy(
                                    width = image.width,
                                    height = image.height
                                ),
                                originalUri = uri.toString(),
                                sequenceNumber = _done.value + 1,
                                data = imageCompressor.compressAndTransform(
                                    image = image,
                                    imageInfo = imageInfo.copy(
                                        width = image.width,
                                        height = image.height
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

                _done.value = totalDone + 1
                _total.value = uris.orEmpty().size
            }
            onResult(results.onSuccess(::registerSave))
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun cacheImages(
        onComplete: (List<Uri>) -> Unit
    ) {
        savingJob = componentScope.launch {
            _isSaving.value = true
            _done.value = 0
            val list = mutableListOf<Uri>()
            uris?.forEach { uri ->
                val totalDone = done

                runSuspendCatching {
                    val (image, imageInfo) = imageGetter.getImage(uri.toString())
                        ?: return@runSuspendCatching null

                    aiToolsRepository.processImage(
                        image = image,
                        callback = object : AiProcessCallback {
                            override fun onChunkProgress(
                                currentChunkIndex: Int,
                                totalChunks: Int
                            ) {
                                _total.value = totalChunks
                                _done.value = currentChunkIndex + 1
                            }
                        },
                        params = params
                    )?.let {
                        it to imageInfo
                    }
                }.getOrNull()?.let { (image, imageInfo) ->
                    shareProvider.cacheImage(
                        image = image,
                        imageInfo = imageInfo.copy(originalUri = uri.toString())
                    )?.let { uri ->
                        list.add(uri.toUri())
                    }
                }

                _done.value = totalDone + 1
                _total.value = uris.orEmpty().size
            }
            onComplete(list)
            _isSaving.value = false
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