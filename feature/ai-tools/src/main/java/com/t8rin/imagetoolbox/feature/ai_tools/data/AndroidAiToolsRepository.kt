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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiToolsRepository
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralDownloadProgress
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class AndroidAiToolsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient,
    private val dataStore: DataStore<Preferences>,
    private val appScope: AppScope
) : AiToolsRepository {

    private val directory: File = File(context.filesDir, "ai_models").apply(File::mkdirs)

    override val downloadedModels: MutableStateFlow<List<NeuralModel>> =
        MutableStateFlow(emptyList())

    override val selectedModel: StateFlow<NeuralModel?>
        get() = dataStore.data.map { NeuralModel.find(it[SELECTED_MODEL]) }.stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    init {
        updateDownloadedModels()
    }

    override fun downloadModel(
        model: NeuralModel
    ): Flow<NeuralDownloadProgress> = callbackFlow {
        val modelFile = File(directory, model.name)

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
                    selectModel(model)
                    updateDownloadedModels()
                    close()
                } catch (e: Throwable) {
                    tmp.delete()
                    close(e)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun selectModel(model: NeuralModel): Boolean {
        if (model !in downloadedModels.value) return false

        dataStore.edit {
            it[SELECTED_MODEL] = model.name
        }

        return true
    }

    private fun updateDownloadedModels() {
        downloadedModels.update {
            directory.listFiles().orEmpty().mapNotNull {
                NeuralModel.find(it.name)
            }
        }
    }
}

private val SELECTED_MODEL = stringPreferencesKey("SELECTED_MODEL")