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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodeLanguage
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewTheme
import dev.hossain.highlight.ui.rememberHighlightedCode

@Composable
internal fun rememberCodeHighlight(
    code: String,
    language: CodeLanguage,
    theme: CodePreviewTheme
): AnnotatedString {
    val highlightTheme = remember(theme) { theme.highlightTheme() }
    val highlightedCode by rememberHighlightedCode(
        code = code,
        theme = highlightTheme,
        language = language.highlightKey
    )
    var cached by remember(theme, language) {
        mutableStateOf<AnnotatedString?>(null)
    }

    LaunchedEffect(highlightedCode, code) {
        if (highlightedCode?.text == code) cached = highlightedCode
    }

    return remember(code, cached) {
        cached
            ?.takeIf { it.text == code }
            ?: cached?.applySpanStylesTo(code)
            ?: AnnotatedString(code)
    }
}

internal class CodeHighlightVisualTransformation(
    private val highlightedCode: AnnotatedString
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText = TransformedText(
        text = highlightedCode.takeIf { it.text == text.text } ?: text,
        offsetMapping = OffsetMapping.Identity
    )
}

private fun AnnotatedString.applySpanStylesTo(text: String): AnnotatedString {
    if (text.isEmpty()) return AnnotatedString(text)

    return AnnotatedString.Builder(text).apply {
        spanStyles.forEach { range ->
            val start = range.start.coerceAtMost(text.length)
            val end = range.end.coerceAtMost(text.length)
            if (start < end) addStyle(range.item, start, end)
        }
    }.toAnnotatedString()
}
