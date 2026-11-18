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

package com.t8rin.imagetoolbox.core.settings.data

import android.content.Context
import android.system.Os
import com.t8rin.imagetoolbox.core.domain.HF_BASE_URL
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.saving.FailureNotifier
import com.t8rin.imagetoolbox.core.settings.domain.FontLibrary
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Category
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Script
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

internal class AndroidFontLibrary @Inject constructor(
    @ApplicationContext context: Context,
    private val downloadManager: DownloadManager,
    private val failureNotifier: FailureNotifier,
    dispatchersHolder: DispatchersHolder
) : FontLibrary {
    private val ioDispatcher = dispatchersHolder.ioDispatcher
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val directory = File(context.filesDir, "fontPack")
    private val staging = File(context.filesDir, "fontPack.download")
    private var downloadJob: Job? = null

    private val _fonts = MutableStateFlow<List<FontPackFont>>(emptyList())
    override val fonts = _fonts.asStateFlow()

    private val _progress = MutableStateFlow<DownloadProgress?>(null)
    override val progress = _progress.asStateFlow()

    private val initialization = scope.launch {
        staging.deleteRecursively()
        if (directory.isDirectory) {
            runCatching { readManifest(directory) }
                .onSuccess { _fonts.value = it }
        }
    }

    override suspend fun getFonts(): List<FontPackFont> {
        initialization.join()
        return fonts.value
    }

    override fun download(force: Boolean) {
        if (progress.value != null || (!force && fonts.value.isNotEmpty())) return
        _progress.value = DownloadProgress(0f, 0L)
        downloadJob = scope.launch {
            try {
                initialization.join()
                if (!force && fonts.value.isNotEmpty()) return@launch
                staging.deleteRecursively()
                var failure: Throwable? = null
                downloadManager.downloadZip(
                    url = HF_BASE_URL.replace("*", "ImageToolbox-Fonts.zip"),
                    destinationPath = staging.absolutePath,
                    onProgress = { _progress.value = it },
                    onFailure = { failure = it },
                    downloadOnlyNewData = false
                )
                if (failure != null) return@launch
                val imported = readManifest(staging).map {
                    it.copy(
                        filePath = File(
                            directory,
                            File(it.filePath).relativeTo(staging.canonicalFile).path
                        ).canonicalPath
                    )
                }
                ensureActive()
                directory.deleteRecursively()
                Os.rename(staging.absolutePath, directory.absolutePath)
                _fonts.value = imported
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                failureNotifier.send(e)
            } finally {
                staging.deleteRecursively()
                _progress.value = null
            }
        }
    }

    override suspend fun remove(): Unit = withContext(ioDispatcher) {
        downloadJob?.cancelAndJoin()
        initialization.join()
        check(!directory.exists() || directory.deleteRecursively())
        _fonts.value = emptyList()
    }

    override fun cancelDownload() {
        downloadJob?.cancel()
    }

    private suspend fun readManifest(root: File): List<FontPackFont> {
        val entries = JSONObject(File(root, "manifest.json").readText()).getJSONArray("fonts")
        return List(entries.length()) { index ->
            currentCoroutineContext().ensureActive()
            val entry = entries.getJSONObject(index)
            val coverage = entry.getJSONObject("coverage")
            FontPackFont(
                name = entry.getString("name"),
                filePath = File(root, entry.getString("file")).canonicalPath,
                variable = entry.getBoolean("variable"),
                category = Category.valueOf(entry.getString("category")),
                coverage = coverage.keys().asSequence().associate { key ->
                    Script.valueOf(key) to coverage.getDouble(key).toFloat()
                }
            )
        }.sortedBy { it.name.lowercase() }
    }
}
