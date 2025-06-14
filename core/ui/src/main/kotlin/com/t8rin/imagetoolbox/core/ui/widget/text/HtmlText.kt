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

package com.t8rin.imagetoolbox.core.ui.widget.text

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.text.util.Linkify
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.text.toSpannable
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Left)
        .copy(color = MaterialTheme.colorScheme.onSurface),
    hyperlinkStyle: TextStyle = style.copy(
        color = if (LocalSettingsState.current.isNightMode) Color.Cyan.blend(MaterialTheme.colorScheme.primary)
        else Color.Blue.blend(MaterialTheme.colorScheme.primary, 0.5f)
    ),
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onHyperlinkClick: (uri: String) -> Unit = LocalUriHandler.current::openUri
) {
    val spanned = remember(html) {
        html.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)
            .toSpannable()
            .also {
                Linkify.addLinks(it, Linkify.WEB_URLS)
            }
    }

    val annotatedText = remember(spanned, hyperlinkStyle) {
        buildAnnotatedString {
            append(spanned.toString())

            spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
                val startIndex = spanned.getSpanStart(span)
                val endIndex = spanned.getSpanEnd(span)

                when (span) {
                    is StyleSpan -> {
                        span.toSpanStyle()?.let {
                            addStyle(style = it, start = startIndex, end = endIndex)
                        }
                    }

                    is UnderlineSpan -> {
                        addStyle(
                            SpanStyle(textDecoration = TextDecoration.Underline),
                            start = startIndex,
                            end = endIndex
                        )
                    }

                    is URLSpan -> {
                        addStyle(
                            style = hyperlinkStyle.toSpanStyle()
                                .copy(textDecoration = TextDecoration.Underline),
                            start = startIndex,
                            end = endIndex
                        )
                        addLink(
                            url = LinkAnnotation.Url(
                                url = span.url,
                                linkInteractionListener = { onHyperlinkClick(span.url) }
                            ),
                            start = startIndex,
                            end = endIndex
                        )
                    }
                }
            }
        }
    }

    BasicText(
        text = annotatedText,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    )
}

private fun StyleSpan.toSpanStyle(): SpanStyle? {
    return when (style) {
        Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
        Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
        Typeface.BOLD_ITALIC -> SpanStyle(
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )

        else -> null
    }
}