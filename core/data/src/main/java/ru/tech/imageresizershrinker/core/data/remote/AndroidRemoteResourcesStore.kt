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

package ru.tech.imageresizershrinker.core.data.remote

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import org.json.JSONArray
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResources
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResourcesDownloadProgress
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResourcesStore
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


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

        if (shouldDownload) onDownloadRequest(name)
        else {
            availableFiles?.let {
                RemoteResources(
                    name = name,
                    uris = availableFiles.mapNotNull {
                        it.toUri().toString()
                    }
                )
            }
        }
    }

    override suspend fun downloadResources(
        name: String,
        onProgress: (RemoteResourcesDownloadProgress) -> Unit,
        onFailure: (Throwable) -> Unit
    ): RemoteResources? = withContext(defaultDispatcher) {
        runCatching {
            val connection = URL(getResourcesLink(name)).openConnection() as HttpURLConnection

            connection.apply {
                doOutput = false
                requestMethod = "GET"
                setRequestProperty("Accept-Charset", Charsets.UTF_8.toString())
                connectTimeout = 15000
                connect()
            }

            val result = StringBuilder()
            BufferedInputStream(connection.inputStream).use { stream ->
                val reader = BufferedReader(InputStreamReader(stream))

                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    result.append(line)
                }
            }

            val items = JSONArray(result.toString())

            val downloadedUris = mutableListOf<String>()

            onProgress(
                RemoteResourcesDownloadProgress(
                    currentPercent = 0f,
                    currentTotalSize = 0,
                    itemsCount = items.length(),
                    itemsDownloaded = 0
                )
            )

            val savingDir = getSavingDir(name)

            for (i in 0..<items.length()) {
                val item = items.getJSONObject(i)
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
                    item.get("name") as String
                ).apply {
                    delete()
                    createNewFile()
                }

                val data = ByteArray(1024 * 8)
                var count: Int
                var downloaded = 0

                val isSuccess = runCatching {
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
                    downloadedUris.add(outFile.toUri().toString())
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

            RemoteResources(
                name = name,
                uris = downloadedUris
            )
        }.onFailure(onFailure).getOrNull()
    }

    private fun getResourcesLink(
        dirName: String
    ): String = BaseUrl.replace("*", dirName)


    private fun getSavingDir(dirName: String): File = File(rootDir, dirName).apply {
        mkdirs()
    }

    private val rootDir = File(context.filesDir, "remoteResources").apply {
        mkdirs()
    }

}

private const val BaseUrl =
    "https://api.github.com/repos/T8RIN/ImageToolboxRemoteResources/contents/*?ref=main"