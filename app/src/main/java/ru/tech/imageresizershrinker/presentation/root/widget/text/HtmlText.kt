package ru.tech.imageresizershrinker.presentation.root.widget.text

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

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
    onHyperlinkClick: (uri: String) -> Unit = {}
) {
    val spanned = remember(html) {
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY, null, null)
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
                        addStringAnnotation(
                            tag = Tag.Hyperlink.name,
                            annotation = span.url,
                            start = startIndex,
                            end = endIndex
                        )
                    }
                }
            }
        }
    }

    ClickableText(
        annotatedText,
        modifier = modifier,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout
    ) { it ->
        annotatedText.getStringAnnotations(tag = Tag.Hyperlink.name, start = it, end = it)
            .firstOrNull()?.let {
                onHyperlinkClick(it.item)
            }
    }
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

private enum class Tag {
    Hyperlink
}