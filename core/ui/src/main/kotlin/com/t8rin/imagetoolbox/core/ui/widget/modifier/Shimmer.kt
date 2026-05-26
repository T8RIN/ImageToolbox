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

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme.LocalMaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.t8rin.imagetoolbox.core.ui.theme.blend
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.max

fun Modifier.shimmer(
    visible: Boolean,
    color: Color = Color.Unspecified,
): Modifier = this then ShimmerElement(
    visible = visible,
    color = color,
)

private data class ShimmerElement(
    private val visible: Boolean,
    private val color: Color,
) : ModifierNodeElement<ShimmerNode>() {

    override fun create(): ShimmerNode = ShimmerNode(
        visible = visible,
        color = color,
    )

    override fun update(node: ShimmerNode) {
        node.update(
            visible = visible,
            color = color,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "shimmer"
        properties["visible"] = visible
        properties["color"] = color
    }
}

private class ShimmerNode(
    private var visible: Boolean,
    private var color: Color,
) : Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode {

    private val placeholderAlpha = Animatable(if (visible) 1f else 0f)
    private val contentAlpha = Animatable(if (visible) 0f else 1f)
    private val highlightProgress = Animatable(0f)

    private val paint = Paint()

    private var crossfadeJob: Job? = null
    private var shimmerJob: Job? = null

    private var lastSize: Size? = null
    private var lastLayoutDirection: LayoutDirection? = null

    private val resolvedColor: Color
        get() {
            val colorScheme = currentValueOf(LocalMaterialTheme).colorScheme

            return color.takeOrElse {
                colorScheme.surfaceColorAtElevation(16.dp)
            }
        }

    private val resolvedHighlightColor: Color
        get() {
            val colorScheme = currentValueOf(LocalMaterialTheme).colorScheme

            return Color.Unspecified.blend(
                color = colorScheme.primary,
                fraction = 0.5f,
            )
        }

    override fun onAttach() {
        startCrossfade()
        startOrStopShimmer()
    }

    override fun onDetach() {
        crossfadeJob?.cancel()
        shimmerJob?.cancel()
    }

    fun update(
        visible: Boolean,
        color: Color,
    ) {
        val visibleChanged = this.visible != visible
        val colorChanged = this.color != color

        this.visible = visible
        this.color = color

        if (visibleChanged) {
            startCrossfade()
            startOrStopShimmer()
        }

        if (colorChanged) {
            invalidateDraw()
        }
    }

    override fun onObservedReadsChanged() {
        invalidateDraw()
    }

    private fun startCrossfade() {
        crossfadeJob?.cancel()
        crossfadeJob = coroutineScope.launch {
            launch {
                placeholderAlpha.animateTo(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = spring(),
                ) {
                    invalidateDraw()
                    startOrStopShimmer()
                }
            }

            launch {
                contentAlpha.animateTo(
                    targetValue = if (visible) 0f else 1f,
                    animationSpec = spring(),
                ) {
                    invalidateDraw()
                }
            }
        }
    }

    private fun startOrStopShimmer() {
        val shouldRun = visible || placeholderAlpha.value >= 0.01f

        if (!shouldRun) {
            shimmerJob?.cancel()
            shimmerJob = null
            return
        }

        if (shimmerJob?.isActive == true) return

        shimmerJob = coroutineScope.launch {
            highlightProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1700, delayMillis = 200),
                    repeatMode = RepeatMode.Restart,
                ),
            ) {
                invalidateDraw()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        observeReads {
            drawShimmer()
        }
    }

    private fun ContentDrawScope.drawShimmer() {
        val contentAlpha = contentAlpha.value
        val placeholderAlpha = placeholderAlpha.value

        if (contentAlpha in 0.01f..0.99f) {
            paint.alpha = contentAlpha
            withLayer(paint) {
                drawContent()
            }
        } else if (contentAlpha >= 0.99f) {
            drawContent()
        }

        if (placeholderAlpha < 0.01f) return

        val placeholderColor = resolvedColor
        val highlight = PlaceholderHighlight.shimmer(
            highlightColor = resolvedHighlightColor,
        )

        if (placeholderAlpha in 0.01f..0.99f) {
            paint.alpha = placeholderAlpha
            withLayer(paint) {
                drawPlaceholder(
                    color = placeholderColor,
                    highlight = highlight,
                )
            }
        } else {
            drawPlaceholder(
                color = placeholderColor,
                highlight = highlight,
            )
        }

        lastSize = size
        lastLayoutDirection = layoutDirection
    }

    private fun DrawScope.drawPlaceholder(
        color: Color,
        highlight: PlaceholderHighlight,
    ) {
        val progress = highlightProgress.value

        drawRect(color = color)
        drawRect(
            brush = highlight.brush(progress, size),
            alpha = highlight.alpha(progress),
        )
    }
}

private inline fun ContentDrawScope.withLayer(
    paint: Paint,
    drawBlock: ContentDrawScope.() -> Unit,
) = drawIntoCanvas { canvas ->
    canvas.saveLayer(size.toRect(), paint)
    drawBlock()
    canvas.restore()
}

@Stable
private interface PlaceholderHighlight {

    fun brush(
        @FloatRange(from = 0.0, to = 1.0) progress: Float,
        size: Size,
    ): Brush

    @FloatRange(from = 0.0, to = 1.0)
    fun alpha(
        @FloatRange(from = 0.0, to = 1.0) progress: Float,
    ): Float

    companion object
}

private fun PlaceholderHighlight.Companion.shimmer(
    highlightColor: Color,
    @FloatRange(from = 0.0, to = 1.0) progressForMaxAlpha: Float = 0.6f,
): PlaceholderHighlight = ShimmerHighlight(
    highlightColor = highlightColor,
    progressForMaxAlpha = progressForMaxAlpha,
)

private data class ShimmerHighlight(
    private val highlightColor: Color,
    private val progressForMaxAlpha: Float,
) : PlaceholderHighlight {

    override fun brush(
        progress: Float,
        size: Size,
    ): Brush = Brush.radialGradient(
        colors = listOf(
            highlightColor.copy(alpha = 0f),
            highlightColor,
            highlightColor.copy(alpha = 0f),
        ),
        center = Offset.Zero,
        radius = (max(size.width, size.height) * progress * 2f).coerceAtLeast(0.01f),
    )

    override fun alpha(progress: Float): Float = when {
        progress <= progressForMaxAlpha -> lerp(
            start = 0f,
            stop = 1f,
            fraction = progress / progressForMaxAlpha,
        )

        else -> lerp(
            start = 1f,
            stop = 0f,
            fraction = (progress - progressForMaxAlpha) / (1f - progressForMaxAlpha),
        )
    }
}