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

package com.t8rin.imagetoolbox.core.ui.widget.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString

fun AnnotatedString.linkify(
    linkStyles: TextLinkStyles
): AnnotatedString = buildAnnotatedString {
    append(this@linkify)

    val text = this@linkify.text
    val matches = URL_PATTERN.findAll(text).toList()

    for (match in matches) {
        val url = match.value

        if (url.contains("@") && !url.startsWith("mailto:")) {
            continue
        }

        val start = match.range.first
        val end = match.range.last + 1

        val hasLinkAnnotation = this@linkify.getStringAnnotations(
            tag = url,
            start = start,
            end = end
        ).any { it.item == url }

        if (!hasLinkAnnotation) {
            addLink(
                url = LinkAnnotation.Url(
                    url = url,
                    styles = linkStyles
                ),
                start = start,
                end = end
            )
        }
    }
}

private val URL_PATTERN =
    Regex("(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.\\S{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.\\S{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.\\S{2,}|www\\.[a-zA-Z0-9]+\\.\\S{2,})")
