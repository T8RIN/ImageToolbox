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

package com.t8rin.imagetoolbox.feature.load_net_image.data

import android.content.Context
import android.graphics.Bitmap
import android.webkit.MimeTypeMap
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.USER_AGENT
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.load_net_image.domain.HtmlImageParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL
import java.net.UnknownHostException
import javax.inject.Inject

internal class AndroidHtmlImageParser @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shareProvider: ImageShareProvider<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : HtmlImageParser, DispatchersHolder by dispatchersHolder {

    override suspend fun parseImagesSrc(
        url: String,
        onFailure: (message: String) -> Unit
    ): List<String> = withContext(ioDispatcher) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isEmpty()) return@withContext emptyList()
        val realUrl = if (trimmedUrl.isMalformed()) "https://$trimmedUrl" else trimmedUrl

        runSuspendCatching {
            val connection = Jsoup.connect(realUrl)
                .userAgent(USER_AGENT)
                .ignoreContentType(true)
                .maxBodySize(0)
            val response = runInterruptible { connection.execute() }
            val contentType = response.contentType()?.substringBefore(';').orEmpty()
            if (contentType.startsWith("image/")) {
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType)
                    ?: "img"
                val cached = response.bodyStream().use { stream ->
                    shareProvider.cacheDataOrThrow(filename = "net_image.$extension") { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        val destination = output.outputStream()
                        while (true) {
                            ensureActive()
                            val count = stream.read(buffer)
                            if (count < 0) break
                            destination.write(buffer, 0, count)
                        }
                    }
                }
                return@runSuspendCatching listOf(cached)
            }
            if (contentType == "text/html" || contentType == "application/xhtml+xml") {
                connection.maxBodySize(2 * 1024 * 1024)
                response.readFully()
            } else {
                loadImage(response.bodyAsBytes()).takeIf { it.isNotEmpty() }?.let {
                    return@runSuspendCatching it
                }
            }
            val parsed = response.parse()
            val images = parsed.getElementsByTag("img").mapNotNull {
                it.absUrl("src").takeIf(String::isNotEmpty)
            }
            val content = parsed.select("meta[property=og:image]").mapNotNull {
                it.absUrl("content").takeIf(String::isNotEmpty)
            }
            val iconUrl = parsed.head()
                .select("link[href~=.*\\.ico]")
                .firstOrNull()
                ?.absUrl("href")
                ?.takeIf(String::isNotEmpty)
            val favIcon = iconUrl?.let { loadImage(it) }.orEmpty().ifEmpty {
                loadImage(URL(response.url(), "/favicon.ico").toString())
            }
            content + images + favIcon
        }.onFailure {
            if (it is UnknownHostException) onFailure(context.getString(R.string.unknown_host))
        }.getOrElse { loadImage(realUrl) }
    }

    private suspend fun loadImage(
        data: Any
    ): List<String> = imageGetter.getImage(data = data)?.let {
        shareProvider.cacheImage(
            image = it,
            imageInfo = ImageInfo(
                width = it.width,
                height = it.height,
                imageFormat = ImageFormat.Png.Lossless
            )
        )
    }.let(::listOfNotNull)

    private fun String.isMalformed(): Boolean = !(startsWith("https://") || startsWith("http://"))

}