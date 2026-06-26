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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.zedalpha.shadowgadgets.compose.clippedShadow

@Composable
fun Modifier.materialShadow(
    shape: Shape,
    elevation: Dp,
    enabled: Boolean = true,
    isClipped: Boolean = true,
    color: Color = Color.Black
): Modifier = materialShadow(
    shape = shape,
    elevation = animateDpAsState(if (enabled) elevation else 0.dp),
    isClipped = isClipped,
    color = color
)

@Composable
fun Modifier.materialShadow(
    shape: Shape,
    elevation: State<Dp>,
    isClipped: Boolean = true,
    color: Color = Color.Black
): Modifier {
    return this then if (elevation.value > 0.dp) {
        val shape = remember(shape) {
            if ((shape is AnimatedShape && shape.shapesType is ShapeType.Smooth)) {
                @Stable
                object : Shape by shape {
                    override fun createOutline(
                        size: Size,
                        layoutDirection: LayoutDirection,
                        density: Density
                    ): Outline = shape.createOutline(
                        size = size,
                        layoutDirection = layoutDirection,
                        density = density,
                        shapesType = ShapeType.Rounded()
                    )
                }
            } else shape
        }
        val isWavy =
            shape is WavyShape || (shape is AnimatedShape && shape.shapesType is ShapeType.Wavy)

        fun api21Shadow() = Modifier.rsBlurShadow(
            shape = shape,
            radius = elevation.value,
            isAlphaContentClip = isClipped,
            color = color
        )

        fun api29Shadow() = if (isClipped) {
            Modifier.clippedShadow(
                shape = shape,
                elevation = elevation.value,
                ambientColor = color,
                spotColor = color
            )
        } else {
            Modifier.shadow(
                shape = shape,
                elevation = elevation.value,
                ambientColor = color,
                spotColor = color
            )
        }

        when {
            isWavy -> {
                if (elevation.value <= 0.dp) {
                    Modifier
                } else {
                    Modifier.drawWithCache {
                        val shadowColor = Color.Black.copy(alpha = 0.18f).toArgb()
                        val transparentColor = Color.Transparent.toArgb()
                        val outline = shape.createOutline(
                            size = size,
                            layoutDirection = layoutDirection,
                            density = this
                        )
                        val path = when (outline) {
                            is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
                            is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
                            is Outline.Generic -> outline.path
                        }
                        val radius = elevation.value.toPx()
                        val paint = Paint().apply {
                            nativePaint.color = transparentColor
                            nativePaint.setShadowLayer(
                                radius * 1f,
                                0f,
                                radius * 0.9f,
                                shadowColor
                            )
                        }

                        onDrawBehind {
                            drawIntoCanvas {
                                it.drawPath(path, paint)
                            }
                        }
                    }
                }
            }

            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                val isConcavePath = remember(shape) {
                    shape.createOutline(
                        size = Size(1f, 1f),
                        layoutDirection = LayoutDirection.Ltr,
                        density = Density(1f)
                    ).let {
                        it is Outline.Generic && !it.path.isConvex
                    }
                }

                if (isConcavePath) api21Shadow() else api29Shadow()
            }

            else -> api29Shadow()
        }
    } else Modifier
}