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
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProcessCallback
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiToolsRepository
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralDownloadProgress
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class AndroidAiToolsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient,
    private val dataStore: DataStore<Preferences>,
    private val appScope: AppScope,
    private val processor: AiProcessor,
    dispatchersHolder: DispatchersHolder
) : AiToolsRepository<Bitmap>, DispatchersHolder by dispatchersHolder {

    private val directory: File get() = File(context.filesDir, "ai_models").apply(File::mkdirs)

    override val downloadedModels: MutableStateFlow<List<NeuralModel>> =
        MutableStateFlow(emptyList())

    override val selectedModel: StateFlow<NeuralModel?> =
        dataStore.data.map { NeuralModel.find(it[SELECTED_MODEL]) }
            .stateIn(
                scope = appScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    private var session: OrtSession? = null

    init {
        appScope.launch { updateDownloadedModels() }
    }

    override fun downloadModel(
        model: NeuralModel
    ): Flow<NeuralDownloadProgress> = callbackFlow {
        val modelFile = model.file

        client.prepareGet(model.downloadLink).execute { response ->
            val total = response.contentLength() ?: -1L

            val tmp = File(modelFile.parentFile, modelFile.name + ".tmp")

            val channel = response.bodyAsChannel()
            var downloaded = 0L

            FileOutputStream(tmp).use { fos ->
                try {
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
                            downloaded += bytes.size
                            fos.write(bytes)
                            trySend(
                                NeuralDownloadProgress(
                                    currentPercent = if (total > 0) downloaded.toFloat() / total else 0f,
                                    currentTotalSize = downloaded
                                )
                            )
                        }
                    }

                    tmp.renameTo(modelFile)
                    updateDownloadedModels()
                    selectModel(model)
                    close()
                } catch (e: Throwable) {
                    tmp.delete()
                    close(e)
                }
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun processImage(
        image: Bitmap,
        callback: AiProcessCallback,
        params: NeuralParams
    ): Bitmap? {
        "start processing".makeLog()
        return processor.processImage(
            session = session.makeLog("Held session")
                ?: createSession(selectedModel.value).makeLog("New session")
                ?: return null,
            inputBitmap = image,
            strength = params.strength,
            callback = callback,
            chunkSize = params.chunkSize,
            overlap = params.overlap
        )
    }

    override suspend fun selectModel(model: NeuralModel): Boolean {
        if (downloadedModels.value.none { it.name == model.name }) return false

        dataStore.edit {
            it[SELECTED_MODEL] = model.name
        }

        closeSession()

        System.gc()

        createSession(model)

        return true
    }

    private fun createSession(model: NeuralModel?): OrtSession? {
        val options = OrtSession.SessionOptions().apply {
            runCatching { addCUDA() }
            runCatching { setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT) }
            runCatching { setInterOpNumThreads(8) }
            runCatching { setIntraOpNumThreads(8) }
            runCatching { setMemoryPatternOptimization(true) }
        }

        return OrtEnvironment.getEnvironment()
            .createSession((model ?: return null).file.absolutePath, options)
            .also { session = it }
    }

    private fun closeSession() {
        session?.close()
        session = null
    }

    private suspend fun updateDownloadedModels() = withContext(ioDispatcher) {
        downloadedModels.update {
            directory.listFiles().orEmpty().mapNotNull {
                NeuralModel.find(it.name)
            }
        }
    }

    private val NeuralModel.file: File get() = File(directory, name)

}

private val SELECTED_MODEL = stringPreferencesKey("SELECTED_MODEL")