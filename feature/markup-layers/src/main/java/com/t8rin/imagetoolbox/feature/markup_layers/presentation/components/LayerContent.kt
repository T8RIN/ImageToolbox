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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import coil3.request.ImageRequest
import com.t8rin.imagetoolbox.core.data.image.utils.static
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.text.OutlineParams
import com.t8rin.imagetoolbox.core.ui.widget.text.OutlinedText
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.calculateTextLayerMetrics
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DomainTextDecoration
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType

@Composable
internal fun LayerContent(
    modifier: Modifier = Modifier,
    type: LayerType,
    textFullSize: Int,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    when (type) {
        is LayerType.Picture -> {
            Picture(
                model = remember(type.imageData) {
                    ImageRequest.Builder(appContext)
                        .data(type.imageData)
                        .static()
                        .size(1600)
                        .build()
                },
                contentScale = ContentScale.Fit,
                modifier = modifier,
                showTransparencyChecker = false
            )
        }

        is LayerType.Text -> {
            val context = LocalContext.current
            val density = LocalDensity.current
            val style = LocalTextStyle.current
            val fontFamily = type.font.toUiFont().fontFamily
            val textMetrics = remember(type, textFullSize, density) {
                context.calculateTextLayerMetrics(
                    type = type,
                    textFullSize = textFullSize
                )
            }
            val mergedStyle = remember(
                style,
                type,
                fontFamily,
                textMetrics,
                density
            ) {
                style.copy(
                    color = type.color.toColor(),
                    fontSize = with(density) { textMetrics.fontSizePx.toSp() },
                    lineHeight = with(density) { textMetrics.lineHeightPx.toSp() },
                    fontFamily = fontFamily,
                    textDecoration = TextDecoration.combine(
                        type.decorations.mapNotNull {
                            when (it) {
                                DomainTextDecoration.LineThrough -> TextDecoration.LineThrough
                                DomainTextDecoration.Underline -> TextDecoration.Underline
                                else -> null
                            }
                        }
                    ),
                    fontWeight = if (type.decorations.any { it == DomainTextDecoration.Bold }) {
                        FontWeight.Bold
                    } else {
                        style.fontWeight
                    },
                    fontStyle = if (type.decorations.any { it == DomainTextDecoration.Italic }) {
                        FontStyle.Italic
                    } else {
                        FontStyle.Normal
                    },
                    textAlign = when (type.alignment) {
                        LayerType.Text.Alignment.Start -> TextAlign.Start
                        LayerType.Text.Alignment.Center -> TextAlign.Center
                        LayerType.Text.Alignment.End -> TextAlign.End
                    }
                )
            }
            val outlineParams = remember(type) {
                type.outline?.let {
                    OutlineParams(
                        color = Color(it.color),
                        stroke = Stroke(
                            width = it.width,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            OutlinedText(
                text = type.text,
                style = mergedStyle,
                outlineParams = outlineParams,
                onTextLayout = onTextLayout,
                modifier = Modifier
                    .background(
                        color = type.backgroundColor.toColor()
                    )
                    .padding(
                        horizontal = with(density) { textMetrics.horizontalPaddingPx.toDp() },
                        vertical = with(density) { textMetrics.verticalPaddingPx.toDp() }
                    )
            )
        }
    }
}