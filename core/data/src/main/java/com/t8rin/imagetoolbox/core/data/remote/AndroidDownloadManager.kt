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

package com.t8rin.imagetoolbox.core.data.remote

import android.content.Context
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.throttleLatest
import com.t8rin.imagetoolbox.core.domain.utils.withProgress
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.decodeEscaped
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import kotlin.math.roundToInt

internal class AndroidDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keepAliveService: KeepAliveService,
    private val client: HttpClient,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder
) : DownloadManager,
    ResourceManager by resourceManager,
    DispatchersHolder by dispatchersHolder {

    override suspend fun download(
        url: String,
        destinationPath: String,
        onStart: suspend () -> Unit,
        onProgress: suspend (DownloadProgress) -> Unit,
        onFinish: suspend (Throwable?) -> Unit
    ): Unit = withContext(defaultDispatcher) {
        keepAliveService.track(
            initial = {
                updateOrStart(
                    title = getString(R.string.downloading)
                )
            }
        ) {
            onStart()
            url.makeLog("Start Download")

            val progressChannel = Channel<DownloadProgress>(Channel.BUFFERED)

            launch {
                progressChannel.receiveAsFlow()
                    .throttleLatest(50).collect {
                        updateProgress(
                            title = getString(R.string.downloading),
                            done = (it.currentPercent * 100).roundToInt(),
                            total = 100
                        )
                    }
            }

            val tmp = File(
                File(context.cacheDir, "downloadCache").apply(File::mkdirs),
                "${url.substringAfterLast('/').substringBefore('?')}.tmp"
            )

            client.prepareGet(url).execute { response ->
                val total = response.contentLength() ?: -1L

                ensureActive()
                val channel = response.bodyAsChannel()
                var downloaded = 0L

                tmp.outputStream().use { fos ->
                    try {
                        while (!channel.isClosedForRead) {
                            ensureActive()
                            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                            while (!packet.exhausted()) {
                                ensureActive()
                                val bytes = packet.readByteArray()
                                downloaded += bytes.size
                                fos.write(bytes)
                                onProgress(
                                    DownloadProgress(
                                        currentPercent = if (total > 0) downloaded.toFloat() / total else 0f,
                                        currentTotalSize = total
                                    ).also(progressChannel::trySend)
                                )
                            }
                        }

                        withContext(ioDispatcher) {
                            tmp.renameTo(File(destinationPath))
                        }
                        tmp.delete()
                        onFinish(null)
                        progressChannel.close()
                    } catch (e: Throwable) {
                        tmp.delete()
                        onFinish(e)
                        progressChannel.close()
                    } finally {
                        tmp.delete()
                        progressChannel.close()
                    }
                }
            }
        }
    }

    override suspend fun downloadZip(
        url: String,
        destinationPath: String,
        onStart: suspend () -> Unit,
        onProgress: (DownloadProgress) -> Unit,
        downloadOnlyNewData: Boolean
    ) {
        keepAliveService.track(
            initial = {
                updateOrStart(
                    title = getString(R.string.downloading)
                )
            }
        ) {
            channelFlow {
                client.prepareGet(url).execute { response ->
                    val total = response.contentLength() ?: -1L

                    trySend(
                        DownloadProgress(
                            currentPercent = 0f,
                            currentTotalSize = total
                        ).also(onProgress)
                    )

                    ensureActive()
                    val source = response.bodyAsChannel().toInputStream().withProgress(
                        total = total,
                        onProgress = { percent ->
                            trySend(
                                DownloadProgress(
                                    currentPercent = percent,
                                    currentTotalSize = total
                                ).also(onProgress)
                            )
                        }
                    )

                    ZipInputStream(source).use { zipIn ->
                        var entry: ZipEntry?
                        while (zipIn.nextEntry.also { entry = it } != null) {
                            entry?.let { zipEntry ->
                                val filename = zipEntry.name

                                if (filename.isNullOrBlank() || filename.startsWith("__")) return@let

                                val outFile = File(
                                    destinationPath,
                                    filename.decodeEscaped()
                                ).apply {
                                    delete()
                                    parentFile?.mkdirs()
                                    createNewFile()
                                }

                                if (downloadOnlyNewData) {
                                    val file = File(destinationPath).listFiles()?.find {
                                        it.name == filename && it.length() > 0L
                                    }

                                    if (file != null) return@let
                                }

                                FileOutputStream(outFile).use { fos ->
                                    zipIn.copyTo(fos)
                                }
                                zipIn.closeEntry()
                            }
                        }
                    }
                }

                close()
            }.throttleLatest(50).collect {
                updateProgress(
                    title = getString(R.string.downloading),
                    done = (it.currentPercent * 100).roundToInt(),
                    total = 100
                )
            }
        }
    }

}