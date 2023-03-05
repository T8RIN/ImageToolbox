package ru.tech.imageresizershrinker.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive
import kotlin.math.absoluteValue
import kotlin.math.ceil

/**
 * Marquee is an implementation of marquee effect from Android XML for JetpackCompose
 *
 * @param modifier Adjust the drawing layout or drawing decoration of the content.
 * @param params Params which specify appearance of marquee effect
 * @param content Composable content that will be applied marquee effect.
 */
@Composable
fun Marquee(
    modifier: Modifier = Modifier,
    edgeColor: Color = MaterialTheme.colorScheme.background,
    params: MarqueeParams = defaultMarqueeParams(edgeColor),
    content: @Composable (Modifier) -> Unit
) {
    val density = LocalDensity.current
    val gradientEdgeWidth = with(density) { params.gradientEdgeWidth.toPx().toInt() }

    var xOffset by remember { mutableStateOf(gradientEdgeWidth) }
    val layoutInfoState = remember { mutableStateOf<MarqueeLayoutInfo?>(null) }

    LaunchedEffect(layoutInfoState.value) {
        val ltr = params.direction == LayoutDirection.Ltr

        val layoutInfo = layoutInfoState.value

        if (layoutInfo == null) {
            xOffset = 0
            return@LaunchedEffect
        }

        if (layoutInfo.width <= layoutInfo.containerWidth) return@LaunchedEffect

        val duration =
            ceil(layoutInfo.containerWidth / with(density) { params.velocity.toPx().absoluteValue / 1000f }).toInt()

        val animation = TargetBasedAnimation(
            animationSpec = infiniteRepeatable(
                initialStartOffset = StartOffset(-params.delayMillis + params.initialDelayMillis),
                animation = tween(
                    durationMillis = duration,
                    easing = params.easing,
                    delayMillis = params.delayMillis
                ),
                repeatMode = RepeatMode.Restart
            ),
            typeConverter = Int.VectorConverter,
            initialValue = if (ltr) 0 else -layoutInfo.width,
            targetValue = if (ltr) -layoutInfo.width else 0
        )

        val startTime = withFrameNanos { it }

        do {
            val playTime = withFrameNanos { it } - startTime
            xOffset = animation.getValueFromNanos(playTime) + gradientEdgeWidth
        } while (isActive)
    }

    SubcomposeLayout(
        modifier = modifier.clipToBounds()
    ) { constraints ->
        val infiniteWidthConstraints = constraints.copy(maxWidth = Int.MAX_VALUE)

        var main = subcompose(MarqueeLayers.Main) {
            content(Modifier)
        }.first().measure(infiniteWidthConstraints)

        var gradient: Placeable? = null
        var secondPlaceableWithOffset: Pair<Placeable, Int>? = null

        if (main.width <= constraints.maxWidth) {
            main = subcompose(MarqueeLayers.Secondary) {
                content(Modifier.fillMaxWidth())
            }.first().measure(constraints)
            layoutInfoState.value = null
            xOffset = 0
        } else {
            val spacing = constraints.maxWidth / 3
            layoutInfoState.value = MarqueeLayoutInfo(
                width = main.width + spacing, containerWidth = constraints.maxWidth
            )
            val secondTextOffset = main.width + xOffset + spacing
            val secondTextSpace = constraints.maxWidth - secondTextOffset

            if (secondTextSpace > 0) {
                secondPlaceableWithOffset = subcompose(MarqueeLayers.Secondary) {
                    content(Modifier)
                }.first().measure(infiniteWidthConstraints) to secondTextOffset
            }
            gradient = if (params.gradientEnabled) subcompose(MarqueeLayers.EdgesGradient) {
                Row {
                    GradientEdge(
                        width = params.gradientEdgeWidth,
                        startColor = params.gradientEdgeColor,
                        endColor = Color.Transparent
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    GradientEdge(
                        width = params.gradientEdgeWidth,
                        startColor = Color.Transparent,
                        endColor = params.gradientEdgeColor
                    )
                }
            }.first().measure(constraints = constraints.copy(maxHeight = main.height)) else null
        }

        layout(
            width = constraints.maxWidth, height = main.height
        ) {
            main.place(xOffset, 0)
            secondPlaceableWithOffset?.let {
                it.first.place(it.second, 0)
            }
            gradient?.place(0, 0)
        }
    }
}

/**
 * Data class which represents Marquee layout params
 *
 * @param velocity Velocity of marquee effect
 * @param gradientEnabled If gradient edges will be shown or not
 * @param gradientEdgeColor Color of gradient edges
 * @param direction Direction of marquee effect reproducing
 * @param easing Easing of marquee effect
 * @param initialDelayMillis Initial delay
 * @param delayMillis Repeating delay
 * @param gradientEdgeWidth Width of gradient edge in Dp
 */
data class MarqueeParams(
    val velocity: Dp,
    val gradientEnabled: Boolean,
    val gradientEdgeColor: Color,
    val direction: LayoutDirection,
    val easing: Easing,
    val initialDelayMillis: Int,
    val delayMillis: Int,
    val gradientEdgeWidth: Dp
)

/**
 * Function which represents default Marquee layout params
 */
@Composable
fun defaultMarqueeParams(
    edgeColor: Color = MaterialTheme.colorScheme.background
): MarqueeParams = MarqueeParams(
    velocity = 30.dp,
    gradientEnabled = true,
    gradientEdgeColor = edgeColor,
    direction = LocalLayoutDirection.current,
    easing = LinearEasing,
    delayMillis = 1_500,
    initialDelayMillis = 1_500,
    gradientEdgeWidth = 10.dp
)

@Composable
private fun GradientEdge(
    width: Dp,
    startColor: Color,
    endColor: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    0f to startColor, 1f to endColor
                )
            )
    )
}

private enum class MarqueeLayers {
    Main,
    Secondary,
    EdgesGradient
}

private data class MarqueeLayoutInfo(
    val width: Int,
    val containerWidth: Int
)