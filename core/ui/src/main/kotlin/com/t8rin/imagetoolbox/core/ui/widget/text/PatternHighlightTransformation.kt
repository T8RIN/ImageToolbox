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

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme

data class PatternHighlightTransformation(
    private val mapping: Map<Regex, Color>
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val annotated = buildAnnotatedString {
            append(text)

            mapping.forEach { (re, color) ->
                re.findAll(text).forEach { match ->
                    addStyle(
                        style = SpanStyle(
                            color = color,
                            fontWeight = FontWeight.SemiBold,
                            background = color.copy(0.15f)
                        ),
                        start = match.range.first,
                        end = match.range.last + 1
                    )
                }
            }
        }

        return TransformedText(
            text = annotated,
            offsetMapping = OffsetMapping.Identity
        )
    }

    companion object {
        @Composable
        fun default(): PatternHighlightTransformation {
            val color = takeColorFromScheme {
                primary.blend(primaryContainer, 0.1f)
            }
            val colorUpper = takeColorFromScheme {
                tertiary.blend(tertiaryContainer, 0.1f)
            }

            return remember(color, colorUpper) {
                PatternHighlightTransformation(
                    mapOf(
                        PATTERN_TOKENS to color,
                        UPPER_PATTERN_TOKENS to colorUpper
                    )
                )
            }
        }
    }

}

private val PATTERN_TOKENS = Regex(
    """\\[pwdhrcoimse](\{[^}]*\})?"""
)

private val UPPER_PATTERN_TOKENS = Regex(
    """\\[PDOIMSE](\{[^}]*\})?"""
)

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(
    isDarkTheme = true,
    keyColor = Color.Blue
) {
    TextField(
        value = FilenamePattern.Default + "\\E",
        onValueChange = {},
        visualTransformation = PatternHighlightTransformation.default()
    )
}

@Preview
@Composable
private fun Preview1() = ImageToolboxThemeForPreview(
    isDarkTheme = false,
    keyColor = Color.Blue
) {
    TextField(
        value = FilenamePattern.Default + "\\E",
        onValueChange = {},
        visualTransformation = PatternHighlightTransformation.default()
    )
}