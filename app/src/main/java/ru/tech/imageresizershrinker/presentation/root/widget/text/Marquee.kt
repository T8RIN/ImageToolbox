package ru.tech.imageresizershrinker.presentation.root.widget.text

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
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
    content: @Composable () -> Unit
) {
    Marquee(modifier, edgeColor, params, keys = arrayOf(null), content)
}

@Composable
fun Marquee(
    modifier: Modifier = Modifier,
    edgeColor: Color = MaterialTheme.colorScheme.background,
    params: MarqueeParams = defaultMarqueeParams(edgeColor),
    vararg keys: Any? = arrayOf(Unit),
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val gradientEdgeWidth = with(density) { params.gradientEdgeWidth.toPx().toInt() }

    var xOffset by remember(keys = keys) { mutableIntStateOf(gradientEdgeWidth) }
    val layoutInfoState = remember(keys = keys) { mutableStateOf<MarqueeLayoutInfo?>(null) }

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
            content()
        }.first().measure(infiniteWidthConstraints)

        var secondPlaceableWithOffset: Pair<Placeable, Int>? = null

        if (main.width <= constraints.maxWidth) {
            main = subcompose(MarqueeLayers.Secondary) {
                content()
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
                    content()
                }.first().measure(infiniteWidthConstraints) to secondTextOffset
            }
        }
        val gradient = subcompose(MarqueeLayers.EdgesGradient) {
            Box(Modifier.fillMaxWidth()) {
                GradientEdge(
                    modifier = Modifier.align(Alignment.CenterStart),
                    width = params.gradientEdgeWidth,
                    startColor = params.gradientEdgeColor,
                    endColor = Color.Transparent
                )
                GradientEdge(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    width = params.gradientEdgeWidth,
                    startColor = Color.Transparent,
                    endColor = params.gradientEdgeColor
                )
            }
        }.first().measure(constraints = constraints.copy(maxHeight = main.height))

        layout(
            width = constraints.maxWidth, height = main.height
        ) {
            main.place(xOffset, 0)
            secondPlaceableWithOffset?.let {
                it.first.place(it.second, 0)
            }
            if (params.gradientEnabled && layoutInfoState.value?.let { it.width > it.containerWidth } == true) {
                gradient.place(0, 0)
            }
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
    modifier: Modifier,
    width: Dp,
    startColor: Color,
    endColor: Color
) {
    Box(
        modifier = modifier
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