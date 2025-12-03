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
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes


internal class AndroidRemoteResourcesStore @Inject constructor(
    @ApplicationContext private val context: Context,
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
            withTimeout(5.minutes) {
                val connection = URL(getResourcesLink(name)).openConnection() as HttpURLConnection

                connection.apply {
                    doOutput = false
                    requestMethod = "GET"
                    setRequestProperty("Accept-Charset", Charsets.UTF_8.toString())
                    connectTimeout = 15000
                    connect()
                }

                name to connection.url makeLog "downloadResources"

                val result = StringBuilder()

                connection.inputStream.bufferedReader().use { reader ->
                    var line: String?
                    while ((reader.readLine().also { line = it }) != null) {
                        result.append(line)
                    }
                }

                result.makeLog("RemoteResources")

                var items = JSONArray(result.toString())

                val savingDir = getSavingDir(name)

                if (downloadOnlyNewData) {
                    val newItems = JSONArray()
                    for (i in 0..<items.length()) {
                        val item = items.getJSONObject(i)

                        val file = savingDir.listFiles()?.find {
                            it.name == item.get("name") && it.length() > 0L
                        }

                        if (file == null) {
                            newItems.put(item)
                        }
                    }

                    items = newItems
                }

                val downloadedUris = mutableListOf<RemoteResource>()

                onProgress(
                    RemoteResourcesDownloadProgress(
                        currentPercent = 0f,
                        currentTotalSize = 0,
                        itemsCount = items.length(),
                        itemsDownloaded = 0
                    )
                )

                for (i in 0..<items.length()) {
                    val item = items.getJSONObject(i)
                    val fileName = item.get("name") as String

                    val conn = URL(
                        item.get("download_url") as String
                    ).openStream()

                    val totalContentSize = (item.get("size") as Int).toLong()
                    onProgress(
                        RemoteResourcesDownloadProgress(
                            currentPercent = 0f,
                            currentTotalSize = totalContentSize,
                            itemsCount = items.length(),
                            itemsDownloaded = downloadedUris.size
                        )
                    )

                    val outFile = File(
                        savingDir,
                        fileName.decodeEscaped()
                    ).apply {
                        delete()
                        createNewFile()
                    }

                    val data = ByteArray(1024 * 8)
                    var count: Int
                    var downloaded = 0

                    val isSuccess = runSuspendCatching {
                        BufferedInputStream(conn).use { input ->
                            FileOutputStream(outFile).use { output ->
                                withContext(ioDispatcher) {
                                    while (input.read(data).also { count = it } != -1) {
                                        output.write(data, 0, count)
                                        downloaded += count
                                        val percentage = downloaded * 100f / totalContentSize
                                        onProgress(
                                            RemoteResourcesDownloadProgress(
                                                currentPercent = percentage,
                                                currentTotalSize = totalContentSize,
                                                itemsCount = items.length(),
                                                itemsDownloaded = downloadedUris.size
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }.isSuccess

                    if (isSuccess) {
                        downloadedUris.add(
                            RemoteResource(
                                uri = outFile.toUri().toString(),
                                name = fileName.decodeEscaped()
                            )
                        )
                        onProgress(
                            RemoteResourcesDownloadProgress(
                                currentPercent = 100f,
                                currentTotalSize = totalContentSize,
                                itemsCount = items.length(),
                                itemsDownloaded = downloadedUris.size
                            )
                        )
                    }
                }

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
            }
        }.onFailure {
            it.printStackTrace()
            onFailure(it)
        }.getOrNull()
    }

    override suspend fun getResourceLinks(
        name: String
    ): RemoteResources? = withContext(defaultDispatcher) {
        runSuspendCatching {
            val connection = URL(getResourcesLink(name)).openConnection() as HttpURLConnection

            connection.apply {
                doOutput = false
                requestMethod = "GET"
                setRequestProperty("Accept-Charset", Charsets.UTF_8.toString())
                connectTimeout = 15000
                connect()
            }

            val result = StringBuilder()

            connection.inputStream.bufferedReader().use { reader ->
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    result.append(line)
                }
            }

            val items = JSONArray(result.toString())

            val resources = mutableSetOf<RemoteResource>()

            for (i in 0..<items.length()) {
                val item = items.getJSONObject(i)
                val fileName = item.get("name") as String
                val url = item.get("download_url") as String

                resources.add(
                    RemoteResource(
                        uri = url,
                        name = fileName.decodeEscaped()
                    )
                )
            }

            RemoteResources(
                name = name,
                list = resources.toList()
            )
        }.getOrNull()
    }

    private fun getResourcesLink(
        dirName: String
    ): String = BaseUrl.replace("*", dirName)


    private fun getSavingDir(
        dirName: String
    ): File = File(rootDir, dirName).apply(File::mkdirs)

    private val rootDir = File(context.filesDir, "remoteResources").apply(File::mkdirs)

}

private const val BaseUrl =
    "https://api.github.com/repos/T8RIN/ImageToolboxRemoteResources/contents/*?ref=main"