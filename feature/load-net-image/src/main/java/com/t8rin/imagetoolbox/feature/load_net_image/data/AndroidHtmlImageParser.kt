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

package com.t8rin.imagetoolbox.feature.load_net_image.data

import android.content.Context
import android.graphics.Bitmap
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
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
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
    ): List<String> = withContext(defaultDispatcher) {
        val trimmedUrl = url.trim()
        val realUrl = if (trimmedUrl.isMalformed()) {
            "https://$trimmedUrl"
        } else trimmedUrl

        val baseImage = loadImage(realUrl)

        val parsedImages = if (realUrl.isNotEmpty()) {
            runSuspendCatching {
                val parsed = Jsoup
                    .connect(realUrl)
                    .userAgent(USER_AGENT)
                    .execute()
                    .parse()

                val list = parsed.getElementsByTag("img")
                    .mapNotNull { element ->
                        element.absUrl("src").takeIf { it.isNotEmpty() }?.substringBefore("?")
                    }

                val content = parsed.getElementsByTag("meta")
                    .mapNotNull { element ->
                        when (element.attr("property")) {
                            "og:image" -> element.attr("content")
                            else -> null
                        }
                    }

                val favIcon = loadImage(
                    parsed.head()
                        .select("link[href~=.*\\.ico]")
                        .firstOrNull()
                        ?.attr("href") ?: ""
                ).ifEmpty {
                    loadImage(realUrl.removeSuffix("/") + "/favicon.ico")
                }

                content + list + favIcon
            }.onFailure {
                if (it is UnknownHostException) onFailure(context.getString(R.string.unknown_host))
            }.getOrNull() ?: emptyList()
        } else {
            emptyList()
        }

        baseImage + parsedImages
    }

    private suspend fun loadImage(
        url: String
    ): List<String> = imageGetter.getImage(data = url)?.let {
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