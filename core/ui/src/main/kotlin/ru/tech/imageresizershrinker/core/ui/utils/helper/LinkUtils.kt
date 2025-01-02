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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import ru.tech.imageresizershrinker.core.domain.USER_AGENT

object LinkUtils {
    fun parseLinks(text: String): Set<String> {
        val regex = Regex("""\b(?:https?://|www\.|http?://)\S+\b""")
        val matches = regex.findAll(text)
        return matches.map { it.value }.toSet()
    }
}

@ConsistentCopyVisibility
data class LinkPreview internal constructor(
    val title: String?,
    val description: String?,
    val image: String?,
    val url: String?,
    val link: String?
)

suspend fun LinkPreview(
    link: String
): LinkPreview = withContext(Dispatchers.Default) {
    var image: String? = null
    var title: String? = null
    var description: String? = null
    var url: String? = null

    runCatching {
        Jsoup
            .connect(link)
            .userAgent(USER_AGENT)
            .execute()
            .parse()
            .getElementsByTag("meta")
            .forEach { element ->
                when (element.attr("property")) {
                    "og:image" -> image = element.attr("content")
                    "og:title" -> title = element.attr("content")
                    "og:description" -> description = element.attr("content")
                    "og:url" -> url = element.attr("content")
                }
            }
    }

    LinkPreview(
        link = link,
        image = image,
        title = title,
        description = description,
        url = url
    )
}