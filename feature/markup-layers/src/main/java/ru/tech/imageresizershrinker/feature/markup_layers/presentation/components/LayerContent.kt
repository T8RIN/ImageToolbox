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

package ru.tech.imageresizershrinker.feature.markup_layers.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.request.ImageRequest
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiFontFamily
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerType

@Composable
internal fun LayerContent(
    modifier: Modifier = Modifier,
    type: LayerType,
    textFullSize: Int
) {
    when (type) {
        is LayerType.Image -> {
            Picture(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(type.imageData)
                    .size(1600)
                    .build(),
                contentScale = ContentScale.Fit,
                modifier = modifier,
                showTransparencyChecker = false
            )
        }

        is LayerType.Text -> {
            val style = LocalTextStyle.current
            val mergedStyle by remember(style, type) {
                derivedStateOf {
                    style.copy(
                        color = type.color.toColor(),
                        fontSize = (textFullSize * type.size / 5).sp,
                        lineHeight = (textFullSize * type.size / 5).sp,
                        fontFamily = UiFontFamily.entries.firstOrNull {
                            (it.fontRes ?: 0) == type.font
                        }?.fontFamily,
                        drawStyle = when (type.style) {
                            0 -> Fill
                            1 -> Stroke()
                            else -> null
                        }
                    )
                }
            }
            AutoSizeText(
                text = type.text,
                style = mergedStyle,
                modifier = Modifier
                    .background(
                        color = type.backgroundColor.toColor(),
                        shape = RoundedCornerShape(3.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}