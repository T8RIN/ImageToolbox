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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
fun MaterialYouPaletteItem(
    color: Color,
    colorScheme: ColorScheme,
    name: String,
    onCopy: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(color)
    val contentColor = colorScheme.contentColorFor(containerColor)

    LocalContentColor.ProvidesValue(contentColor) {
        Column(
            modifier = modifier
                .container(
                    shape = RectangleShape,
                    color = containerColor,
                    resultPadding = 0.dp
                )
                .hapticsClickable {
                    onCopy(containerColor)
                }
                .padding(12.dp)
        ) {
            AutoSizeText(
                text = name,
                maxLines = if (name.length < 11) 1
                else 2,
                style = LocalTextStyle.current.copy(
                    lineHeight = 16.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(6.dp))
            SelectionContainer {
                AutoSizeText(
                    text = containerColor.toHex(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 2.dp),
                    style = LocalTextStyle.current.copy(
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        color = LocalContentColor.current.copy(0.5f)
                    )
                )
            }
        }
    }

}

@Stable
private fun ColorScheme.contentColorFor(
    color: Color
): Color = when (color) {
    primary -> onPrimary
    secondary -> onSecondary
    tertiary -> onTertiary
    background -> onBackground
    error -> onError
    primaryContainer -> onPrimaryContainer
    secondaryContainer -> onSecondaryContainer
    tertiaryContainer -> onTertiaryContainer
    errorContainer -> onErrorContainer
    inverseSurface -> inverseOnSurface
    surface -> onSurface
    inversePrimary -> primary
    surfaceVariant -> onSurfaceVariant
    surfaceBright -> onSurface
    surfaceContainer -> onSurface
    surfaceContainerHigh -> onSurface
    surfaceContainerHighest -> onSurface
    surfaceContainerLow -> onSurface
    surfaceContainerLowest -> onSurface
    onPrimary -> primary
    onSecondary -> secondary
    onTertiary -> tertiary
    onBackground -> background
    onError -> error
    onPrimaryContainer -> primaryContainer
    onSecondaryContainer -> secondaryContainer
    onTertiaryContainer -> tertiaryContainer
    onErrorContainer -> errorContainer
    inverseOnSurface -> inverseSurface
    onSurface -> surface
    outline -> surfaceContainerLow
    outlineVariant -> onSurfaceVariant
    onSurfaceVariant -> surfaceVariant
    else -> Color.Unspecified
}