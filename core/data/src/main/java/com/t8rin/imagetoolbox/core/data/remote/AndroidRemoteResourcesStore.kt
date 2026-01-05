/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.utils.decodeEscaped
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResource
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResources
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesStore
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.withProgress
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject


internal class AndroidRemoteResourcesStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient,
    dispatchersHolder: DispatchersHolder
) : RemoteResourcesStore, DispatchersHolder by dispatchersHolder {

    override suspend fun getResources(
        name: String,
        forceUpdate: Boolean,
        onDownloadRequest: suspend (name: String) -> RemoteResources?
    ): RemoteResources? = withContext(ioDispatcher) {
        val availableFiles = getSavingDir(name).listFiles()
        val shouldDownload = forceUpdate || availableFiles.isNullOrEmpty()

        val availableResources = if (!availableFiles.isNullOrEmpty()) {
            RemoteResources(
                name = name,
                list = availableFiles.mapNotNull {
                    it.toUri().toString()
                }.map { uri ->
                    RemoteResource(
                        uri = uri,
                        name = uri.takeLastWhile { it != '/' }.decodeEscaped()
                    )
                }.sortedBy { it.name }
            )
        } else null

        if (shouldDownload) onDownloadRequest(name) ?: availableResources
        else availableResources
    }

    override suspend fun downloadResources(
        name: String,
        onProgress: (RemoteResourcesDownloadProgress) -> Unit,
        onFailure: (Throwable) -> Unit,
        downloadOnlyNewData: Boolean
    ): RemoteResources? = withContext(defaultDispatcher) {
        runSuspendCatching {
            val url = getResourcesLink(name)
            val savingDir = getSavingDir(name)

            val downloadedUris = mutableListOf<RemoteResource>()

            client.prepareGet(url).execute { response ->
                val total = response.contentLength() ?: -1L

                val source = response.bodyAsChannel().toInputStream().withProgress(
                    total = total,
                    onProgress = { percent ->
                        onProgress(
                            RemoteResourcesDownloadProgress(
                                currentPercent = percent,
                                currentTotalSize = total
                            )
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
                                savingDir,
                                filename.decodeEscaped()
                            ).apply {
                                delete()
                                parentFile?.mkdirs()
                                createNewFile()
                            }

                            if (downloadOnlyNewData) {
                                val file = savingDir.listFiles()?.find {
                                    it.name == filename && it.length() > 0L
                                }

                                if (file != null) return@let
                            }

                            FileOutputStream(outFile).use { fos ->
                                zipIn.copyTo(fos)
                            }
                            zipIn.closeEntry()

                            downloadedUris.add(
                                RemoteResource(
                                    uri = outFile.toUri().toString(),
                                    name = filename.decodeEscaped()
                                )
                            )
                        }
                    }
                }
            }

            name to url makeLog "downloadResources"

            val savedAlready = savingDir.listFiles()?.mapNotNull {
                it.toUri().toString()
            }?.map { uri ->
                RemoteResource(
                    uri = uri,
                    name = uri.takeLastWhile { it != '/' }.decodeEscaped()
                )
            } ?: emptyList()

            if (downloadedUris.isNotEmpty()) {
                RemoteResources(
                    name = name,
                    list = (savedAlready + downloadedUris).distinct().sortedBy { it.name }
                )
            } else {
                RemoteResources(
                    name = name,
                    list = savedAlready.sortedBy { it.name }
                )
            }
        }.onFailure {
            it.makeLog()
            onFailure(it)
        }.getOrNull()
    }

    private fun getResourcesLink(
        dirName: String
    ): String = BaseUrl.replace("*", dirName)


    private fun getSavingDir(
        dirName: String
    ): File = File(rootDir, dirName.substringBefore("/")).apply(File::mkdirs)

    private val rootDir = File(context.filesDir, "remoteResources").apply(File::mkdirs)

}

private const val BaseUrl =
    "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/*"