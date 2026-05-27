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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.material3.MaterialTheme.LocalMaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import com.gigamole.composeshadowsplus.common.ShadowsPlusDefaults
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import kotlinx.coroutines.launch

fun Modifier.drawHorizontalStroke(
    top: Boolean = false,
    height: Dp = Dp.Unspecified,
    autoElevation: Dp = 6.dp,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this

    return this.then(
        DrawHorizontalStrokeElement(
            top = top,
            height = height,
            autoElevation = autoElevation
        )
    )
}

private data class DrawHorizontalStrokeElement(
    val top: Boolean,
    val height: Dp,
    val autoElevation: Dp
) : ModifierNodeElement<DrawHorizontalStrokeNode>() {

    override fun create(): DrawHorizontalStrokeNode {
        return DrawHorizontalStrokeNode(
            top = top,
            height = height,
            autoElevation = autoElevation
        )
    }

    override fun update(node: DrawHorizontalStrokeNode) {
        node.update(
            top = top,
            height = height,
            autoElevation = autoElevation
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "drawHorizontalStroke"
        properties["top"] = top
        properties["height"] = height
        properties["autoElevation"] = autoElevation
    }
}

private class DrawHorizontalStrokeNode(
    private var top: Boolean,
    private var height: Dp,
    private var autoElevation: Dp
) : Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode {

    private val shadowRadius = Animatable(0.dp, Dp.VectorConverter)

    private var strokeHeight: Dp? = null
    private var strokeColor: Color = Color.Unspecified

    fun update(
        top: Boolean,
        height: Dp,
        autoElevation: Dp
    ) {
        this.top = top
        this.height = height
        this.autoElevation = autoElevation

        updateState()
        invalidateDraw()
    }

    override fun onAttach() {
        updateState()
    }

    override fun onObservedReadsChanged() {
        updateState()
    }

    private fun updateState() {
        observeReads {
            val settingsState = currentValueOf(LocalSettingsState)
            val borderWidth = settingsState.borderWidth

            val newStrokeHeight = if (height.isUnspecified) {
                borderWidth.takeIf { it > 0.dp }
            } else {
                height
            }

            val colorScheme = currentValueOf(LocalMaterialTheme).colorScheme

            val newStrokeColor = colorScheme.outlineVariant(
                luminance = 0.1f,
                onTopOf = colorScheme.surfaceContainer
            )

            val targetRadius = if (
                newStrokeHeight == null &&
                settingsState.drawAppBarShadows
            ) {
                autoElevation
            } else {
                0.dp
            }

            val shouldInvalidate =
                strokeHeight != newStrokeHeight ||
                        strokeColor != newStrokeColor

            strokeHeight = newStrokeHeight
            strokeColor = newStrokeColor

            if (shouldInvalidate) {
                invalidateDraw()
            }

            animateShadowTo(targetRadius)
        }
    }

    private fun animateShadowTo(target: Dp) {
        if (shadowRadius.targetValue == target) return

        coroutineScope.launch {
            shadowRadius.animateTo(target)
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        drawSoftLayerShadow(
            radius = shadowRadius.value,
            color = ShadowsPlusDefaults.ShadowColor,
            shape = RectangleShape,
            spread = (-2).dp,
            offset = DpOffset(
                x = 0.dp,
                y = if (top) (-1).dp else 2.dp
            ),
            isAlphaContentClip = false
        )

        drawContent()

        val h = strokeHeight ?: return
        val heightPx = h.toPx()

        drawRect(
            color = strokeColor,
            topLeft = if (top) {
                Offset.Zero
            } else {
                Offset(
                    x = 0f,
                    y = size.height - heightPx
                )
            },
            size = Size(
                width = size.width,
                height = heightPx
            )
        )
    }

    private fun ContentDrawScope.drawSoftLayerShadow(
        radius: Dp,
        color: Color,
        shape: Shape,
        spread: Dp,
        offset: DpOffset,
        isAlphaContentClip: Boolean
    ) {
        val radiusPx = radius.toPx()
        val isRadiusValid = radiusPx > 0f

        val outline = shape.createOutline(
            size = size,
            layoutDirection = LayoutDirection.Rtl,
            density = this
        )

        val path = Path().apply {
            addOutline(outline)
        }

        val paint = Paint().apply {
            this.color = if (isRadiusValid) {
                Color.Transparent
            } else {
                color
            }

            nativePaint.apply {
                isDither = true
                isAntiAlias = true

                if (isRadiusValid) {
                    setShadowLayer(
                        radiusPx,
                        offset.x.toPx(),
                        offset.y.toPx(),
                        color.toArgb()
                    )
                } else {
                    clearShadowLayer()
                }
            }
        }

        val block: DrawScope.() -> Unit = {
            drawIntoCanvas { canvas ->
                canvas.save()

                if (!isRadiusValid) {
                    canvas.translate(
                        dx = offset.x.toPx(),
                        dy = offset.y.toPx()
                    )
                }

                if (spread.value != 0f) {
                    val spreadPx = spread.toPx()

                    canvas.scale(
                        sx = spreadScale(
                            spread = spreadPx,
                            size = size.width
                        ),
                        sy = spreadScale(
                            spread = spreadPx,
                            size = size.height
                        ),
                        pivotX = center.x,
                        pivotY = center.y
                    )
                }

                canvas.drawOutline(
                    outline = outline,
                    paint = paint
                )

                canvas.restore()
            }
        }

        if (isAlphaContentClip) {
            clipPath(
                path = path,
                block = block
            )
        } else {
            block()
        }
    }
}

private fun spreadScale(
    spread: Float,
    size: Float
): Float {
    if (size == 0f) return 1f
    return (size + spread * 2f) / size
}