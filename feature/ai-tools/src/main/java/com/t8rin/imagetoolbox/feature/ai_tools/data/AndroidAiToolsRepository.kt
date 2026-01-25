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

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtException
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.data.image.utils.healAlpha
import com.t8rin.imagetoolbox.core.data.saving.io.FileReadable
import com.t8rin.imagetoolbox.core.data.saving.io.FileWriteable
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.data.utils.getFilename
import com.t8rin.imagetoolbox.core.data.utils.observeHasChanges
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiToolsRepository
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralConstants
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import com.t8rin.logger.makeLog
import com.t8rin.neural_tools.bgremover.BgRemover
import com.t8rin.neural_tools.bgremover.GenericBackgroundRemover
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class AndroidAiToolsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val appScope: AppScope,
    private val processor: AiProcessor,
    private val keepAliveService: KeepAliveService,
    dispatchersHolder: DispatchersHolder,
    resourceManager: ResourceManager,
    private val downloadManager: DownloadManager,
    private val fileController: FileController
) : AiToolsRepository<Bitmap>,
    DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager {

    init {
        appScope.launch { extractU2NetP() }
    }

    private var isProcessingImage = false

    private val modelsDir: File
        get() = File(
            context.filesDir,
            NeuralConstants.DIR
        ).apply(File::mkdirs)

    private val updateFlow: MutableSharedFlow<Unit> = MutableSharedFlow()

    override val occupiedStorageSize: MutableStateFlow<Long> = MutableStateFlow(0)

    override val downloadedModels: StateFlow<List<NeuralModel>> =
        merge(
            modelsDir.observeHasChanges().debounce(100),
            updateFlow
        ).map {
            fetchDownloadedModels { files ->
                occupiedStorageSize.update { files.sumOf { it.length() } }
            }.sortedWith(
                compareBy(
                    { NeuralModel.entries.indexOfFirst { e -> e.name == it.name } },
                    { it.title },
                )
            )
        }.stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override val selectedModel: StateFlow<NeuralModel?> = combine(
        downloadedModels,
        dataStore.data
    ) { downloaded, data ->
        downloaded.find { it.name == data[SELECTED_MODEL] }
    }.stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private var session: OrtSession? = null

    override fun downloadModel(
        model: NeuralModel
    ): Flow<DownloadProgress> = callbackFlow {
        ensureActive()

        if (model.name.contains("u2netp")) {
            extractU2NetP()
            selectModelForced(model)
            close()
        } else {
            downloadManager.download(
                url = model.downloadLink,
                onStart = {
                    trySend(
                        DownloadProgress(
                            currentPercent = 0f,
                            currentTotalSize = model.downloadSize
                        )
                    )
                },
                onProgress = ::trySend,
                destinationPath = model.file.absolutePath,
                onFinish = {
                    if (it == null) selectModelForced(model)
                    close(it)
                }
            )
        }
    }.flowOn(ioDispatcher)

    private suspend fun CoroutineScope.selectModelForced(model: NeuralModel) {
        updateFlow.emit(Unit)

        ensureActive()

        selectModel(
            model = model,
            forced = true
        )
        model.asBgRemover()?.checkModel()
    }

    override suspend fun importModel(
        uri: String
    ): SaveResult = withContext(ioDispatcher) {
        val modelToImport = UriReadable(
            uri = uri.toUri(),
            context = context
        )
        val modelChecksum = HashingType.SHA_256.computeFromReadable(modelToImport)

        val possibleModel = NeuralModel.entries.find {
            it.checksum == modelChecksum
        }

        val modelName = possibleModel?.name
            ?: uri.toUri().getFilename().orEmpty().ifEmpty {
                "imported_model_($modelChecksum).onnx"
            }

        val alreadyDownloaded = downloadedModels.value.find {
            it.checksum == modelChecksum
        }

        if (alreadyDownloaded != null) {
            selectModelForced(alreadyDownloaded)
            return@withContext SaveResult.Skipped
        }

        fileController.transferBytes(
            fromUri = uri,
            to = FileWriteable(
                File(
                    modelsDir,
                    modelName
                ).apply(File::createNewFile)
            )
        ).also {
            selectModelForced(
                NeuralModel.Imported(
                    name = modelName,
                    checksum = modelChecksum
                )
            )
        }
    }

    override suspend fun processImage(
        image: Bitmap,
        listener: AiProgressListener,
        params: NeuralParams
    ): Bitmap? = withContext(defaultDispatcher) {
        "start processing".makeLog()

        val model = selectedModel.value

        when {
            model == null -> return@withContext listener.failedSession()

            model.type == NeuralModel.Type.REMOVE_BG -> {
                processImage {
                    withClosedSession(listener) {
                        model.asBgRemover()?.removeBackground(image = image)!!.healAlpha(image)
                    }
                }
            }

            else -> {
                processImage {
                    val ortSession = session.makeLog("Held session")
                        ?: createSession(selectedModel.value).makeLog("New session")
                        ?: return@withContext null.also {
                            listener.onError(getString(R.string.failed_to_open_session))
                        }

                    processor.processImage(
                        session = ortSession,
                        inputBitmap = image,
                        params = params,
                        listener = listener,
                        model = selectedModel.value!!
                    )
                }
            }
        }
    }

    private suspend fun withClosedSession(
        listener: AiProgressListener,
        function: suspend () -> Bitmap?
    ): Bitmap? {
        closeSession()

        return keepAliveService.track(
            onFailure = {
                listener.onError(it.message ?: it::class.simpleName.orEmpty())
            },
            action = {
                function()
            }
        )
    }

    private fun <T> AiProgressListener.failedSession(): T? = null.also {
        onError(getString(R.string.failed_to_open_session))
    }

    override suspend fun deleteModel(model: NeuralModel) = withContext(ioDispatcher) {
        model.file.delete()
        if (selectedModel.value?.name == model.name) selectModel(null)
        updateFlow.emit(Unit)
    }

    override fun cleanup() {
        BgRemover.closeAll()
        closeSession()
        System.gc()
    }

    override suspend fun selectModel(
        model: NeuralModel?,
        forced: Boolean
    ): Boolean = withContext(ioDispatcher) {
        if (isProcessingImage) return@withContext false
        if (!forced && model != null && downloadedModels.value.none { it.name == model.name }) return@withContext false
        if (model != null && model.name == selectedModel.value?.name) return@withContext false

        dataStore.edit {
            it[SELECTED_MODEL] = model?.name.orEmpty()
        }

        cleanup()

        return@withContext true
    }

    private fun createSession(model: NeuralModel?): OrtSession? {
        return runCatching {
            val modelName = model?.name.orEmpty()
            val options = OrtSession.SessionOptions().apply {
                val processors = Runtime.getRuntime().availableProcessors()
                try {
                    setIntraOpNumThreads(if (processors <= 2) 1 else (processors * 3) / 4)
                } catch (e: OrtException) {
                    "Error setting IntraOpNumThreads: ${e.message}".makeLog("ModelManager")
                }
                try {
                    setInterOpNumThreads(4)
                } catch (e: OrtException) {
                    "Error setting InterOpNumThreads: ${e.message}".makeLog("ModelManager")
                }
                try {
                    when {
                        modelName.endsWith(".ort") -> { // prevent double optimizations (.ort models are already optimized)
                            setOptimizationLevel(
                                OrtSession.SessionOptions.OptLevel.NO_OPT
                            )
                        }

                        modelName.startsWith("fbcnn_") -> setOptimizationLevel(
                            OrtSession.SessionOptions.OptLevel.EXTENDED_OPT
                        )

                        modelName.startsWith("scunet_") -> setOptimizationLevel(
                            OrtSession.SessionOptions.OptLevel.NO_OPT
                        )
                    }
                } catch (e: OrtException) {
                    "Error setting OptimizationLevel: ${e.message}".makeLog("ModelManager")
                }
            }

            OrtEnvironment.getEnvironment()
                .createSession((model ?: return null).file.absolutePath, options)
                .also { session = it }
        }.onFailure { e ->
            e.makeLog("createSession")
            model?.let {
                appScope.launch { deleteModel(it) }
            }
        }.getOrNull()
    }

    private fun closeSession() {
        session?.close()
        session = null
    }

    private suspend fun fetchDownloadedModels(
        onGetFiles: (List<File>) -> Unit
    ) = withContext(ioDispatcher) {
        modelsDir.listFiles().orEmpty().toList().filter {
            !it.name.orEmpty().endsWith(".tmp") && !it.name.isNullOrEmpty() && it.length() > 0
        }.also(onGetFiles).mapNotNull {
            val name = it.name

            if (name.isNullOrEmpty() || it.length() <= 0) return@mapNotNull null

            NeuralModel.find(name) ?: NeuralModel.Imported(
                name = name,
                checksum = HashingType.SHA_256.computeFromReadable(FileReadable(it))
            )
        }
    }

    private val NeuralModel.file: File get() = File(modelsDir, name)

    private fun NeuralModel.asBgRemover(): GenericBackgroundRemover? {
        return BgRemover.getRemover(
            when {
                name.startsWith("u2netp") -> BgRemover.Type.U2NetP
                name.startsWith("u2net") -> BgRemover.Type.U2Net
                name.startsWith("inspyrenet") -> BgRemover.Type.InSPyReNet
                name.startsWith("RMBG_1.4") -> BgRemover.Type.RMBG1_4
                name.startsWith("birefnet_swin_tiny") -> BgRemover.Type.BiRefNetTiny
                name.startsWith("isnet") -> BgRemover.Type.ISNet
                else -> return null
            }
        )
    }

    private suspend fun extractU2NetP() {
        //Extraction from assets
        BgRemover.downloadModel(BgRemover.Type.U2NetP).collect()
    }

    private inline fun <T> processImage(action: () -> T): T = try {
        isProcessingImage = true
        action()
    } finally {
        isProcessingImage = false
    }

}

private val SELECTED_MODEL = stringPreferencesKey("SELECTED_MODEL")