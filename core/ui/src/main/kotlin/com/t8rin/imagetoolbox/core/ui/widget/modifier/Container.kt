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

@file:Suppress("AnimateAsStateLabel")

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.takeOrElse
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerBrush
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerShape
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor

@Composable
fun Modifier.container(
    shape: Shape? = null,
    color: Color = Color.Unspecified,
    resultPadding: Dp = 4.dp,
    borderWidth: Dp = Dp.Unspecified,
    borderColor: Color? = null,
    autoShadowElevation: Dp = if (color != Color.Transparent) 1.dp else 0.dp,
    clip: Boolean = true,
    composeColorOnTopOfBackground: Boolean = true,
    isShadowClip: Boolean = if (color.isUnspecified) true else color.alpha < 1f,
    isStandaloneContainer: Boolean = true,
    shadowColor: Color = Color.Black
): Modifier {
    val resultShape = LocalContainerShape.current ?: shape ?: ShapeDefaults.default
    val settingsState = LocalSettingsState.current

    val targetBorderWidth = borderWidth.takeOrElse {
        settingsState.borderWidth
    }

    val colorScheme = MaterialTheme.colorScheme

    val containerColor = if (color.isUnspecified) {
        SafeLocalContainerColor
    } else if (composeColorOnTopOfBackground) {
        color.compositeOverSafe(colorScheme.background)
    } else color

    val enableShadow = if (isStandaloneContainer) settingsState.drawContainerShadows else true

    val outlineColor = remember(borderColor, colorScheme.onSecondaryContainer, containerColor) {
        borderColor ?: colorScheme.outlineVariant(
            luminance = 0.1f,
            onTopOf = containerColor
        )
    }

    val brush = LocalContainerBrush.current

    return this
        .materialShadow(
            shape = resultShape,
            elevation = animateDpAsState(
                if (targetBorderWidth > 0.dp || !enableShadow) 0.dp else autoShadowElevation.coerceAtLeast(
                    0.dp
                )
            ),
            isClipped = isShadowClip,
            color = shadowColor
        )
        .then(
            remember(
                resultShape,
                clip,
                resultPadding,
                brush,
                containerColor,
                targetBorderWidth,
                outlineColor
            ) {
                Modifier
                    .then(
                        if (resultShape is CornerBasedShape) {
                            Modifier
                                .then(
                                    brush?.let {
                                        Modifier.background(
                                            brush = brush,
                                            shape = resultShape
                                        )
                                    } ?: Modifier.background(
                                        color = containerColor,
                                        shape = resultShape
                                    )
                                )
                                .then(
                                    if (targetBorderWidth > 0.dp) {
                                        Modifier.border(
                                            width = targetBorderWidth,
                                            color = outlineColor,
                                            shape = resultShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                        } else {
                            Modifier.drawWithCache {
                                val outline = resultShape.createOutline(
                                    size = size,
                                    layoutDirection = layoutDirection,
                                    density = this
                                )
                                val stroke = if (targetBorderWidth > 0.dp) {
                                    Stroke(targetBorderWidth.toPx())
                                } else {
                                    null
                                }

                                onDrawWithContent {
                                    brush?.let {
                                        drawOutline(
                                            outline = outline,
                                            brush = brush
                                        )
                                    } ?: drawOutline(
                                        outline = outline,
                                        color = containerColor
                                    )

                                    stroke?.let {
                                        drawOutline(
                                            outline = outline,
                                            color = outlineColor,
                                            style = stroke
                                        )
                                    }
                                    drawContent()
                                }
                            }
                        }
                    )
                    .then(if (clip) Modifier.clip(resultShape) else Modifier)
                    .then(if (resultPadding > 0.dp) Modifier.padding(resultPadding) else Modifier)
            }
        )
}