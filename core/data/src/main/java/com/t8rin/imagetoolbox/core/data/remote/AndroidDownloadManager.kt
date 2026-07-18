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

import android.system.Os
import com.t8rin.imagetoolbox.core.data.utils.getWithProgress
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FailureNotifier
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.throttleLatest
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.decodeEscaped
import com.t8rin.imagetoolbox.core.utils.isNetworkAvailable
import com.t8rin.imagetoolbox.core.utils.makeLog
import io.ktor.client.HttpClient
import io.ktor.utils.io.jvm.javaio.copyTo
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.UnknownHostException
import java.util.zip.ZipInputStream
import javax.inject.Inject
import kotlin.math.roundToInt

internal class AndroidDownloadManager @Inject constructor(
    private val keepAliveService: KeepAliveService,
    private val client: HttpClient,
    private val failureNotifier: FailureNotifier,
    resourceManager: ResourceManager,
    dispatchersHolder: DispatchersHolder,
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
        var temporaryFile: File? = null

        if (!appContext.isNetworkAvailable()) {
            failureNotifier.sendNoConnection()
            onFinish(UnknownHostException())
            return@withContext
        }

        keepAliveService.track(
            initial = {
                updateOrStart(
                    title = getString(R.string.downloading)
                )
            },
            onCancel = {
                temporaryFile?.delete()
            },
            onFailure = onFinish
        ) {
            channelFlow {
                onStart()
                url.makeLog("Start Download")

                val destination = File(destinationPath)
                val tmp = destination.createTemporarySibling().also {
                    temporaryFile = it
                }

                try {
                    client.getWithProgress(
                        url = url,
                        onProgress = { bytesSentTotal, contentLength ->
                            val progress = contentLength
                                ?.takeIf { it > 0L }
                                ?.let { bytesSentTotal.toFloat() / it.toFloat() }
                                ?.coerceIn(0f, 1f)
                                ?: 0f

                            onProgress(
                                DownloadProgress(
                                    currentPercent = progress,
                                    currentTotalSize = contentLength ?: -1
                                ).also(::trySend)
                            )
                        },
                        onOpen = { response ->
                            tmp.outputStream().use { output ->
                                response.copyTo(output)
                            }
                        }
                    )

                    tmp.replaceAtomically(destination)
                    onFinish(null)
                } finally {
                    tmp.delete()
                    temporaryFile = null
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

    override suspend fun downloadZip(
        url: String,
        destinationPath: String,
        onStart: suspend () -> Unit,
        onProgress: (DownloadProgress) -> Unit,
        onFailure: (Throwable) -> Unit,
        downloadOnlyNewData: Boolean
    ): Unit = withContext(defaultDispatcher) {
        if (!appContext.isNetworkAvailable()) {
            failureNotifier.sendNoConnection()
            onFailure(UnknownHostException())
            return@withContext
        }

        keepAliveService.track(
            initial = {
                updateOrStart(
                    title = getString(R.string.downloading)
                )
            },
            onFailure = onFailure
        ) {
            channelFlow {
                onStart()

                client.getWithProgress(
                    url = url,
                    onProgress = { bytesSentTotal, contentLength ->
                        val progress = contentLength
                            ?.takeIf { it > 0L }
                            ?.let { bytesSentTotal.toFloat() / it.toFloat() }
                            ?.coerceIn(0f, 1f)
                            ?: 0f

                        onProgress(
                            DownloadProgress(
                                currentPercent = progress,
                                currentTotalSize = contentLength ?: -1
                            ).also(::trySend)
                        )
                    },
                    onOpen = { response ->
                        val destination = File(destinationPath).apply {
                            check(mkdirs() || isDirectory)
                        }.canonicalFile

                        ZipInputStream(response.toInputStream()).use { zipIn ->
                            while (true) {
                                val entry = zipIn.nextEntry ?: break

                                try {
                                    val filename = entry.name?.decodeEscaped()
                                    if (filename.isNullOrBlank() || filename.startsWith("__")) {
                                        continue
                                    }

                                    val output = destination.resolveZipEntry(filename)
                                    if (entry.isDirectory) {
                                        check(output.mkdirs() || output.isDirectory)
                                        continue
                                    }
                                    if (downloadOnlyNewData && output.length() > 0L) continue

                                    val tmp = output.createTemporarySibling()
                                    try {
                                        FileOutputStream(tmp).use { stream ->
                                            zipIn.copyTo(stream)
                                        }
                                        tmp.replaceAtomically(output)
                                    } finally {
                                        tmp.delete()
                                    }
                                } finally {
                                    zipIn.closeEntry()
                                }
                            }
                        }
                    }
                )

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

private fun File.createTemporarySibling(): File {
    val directory = requireNotNull(parentFile)
    check(directory.mkdirs() || directory.isDirectory)

    return File.createTempFile(".download-$name.", ".tmp", directory)
}

private fun File.replaceAtomically(destination: File) {
    Os.rename(absolutePath, destination.absolutePath)
}

private fun File.resolveZipEntry(filename: String): File {
    val output = File(this, filename).canonicalFile
    val destinationPrefix = canonicalPath + File.separator

    require(output.path.startsWith(destinationPrefix))

    return output
}