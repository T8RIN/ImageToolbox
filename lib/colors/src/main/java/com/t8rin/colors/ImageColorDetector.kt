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

package com.t8rin.colors

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import com.t8rin.gesture.observePointersCountWithOffset
import com.t8rin.gesture.pointerMotionEvents
import com.t8rin.image.ImageWithConstraints
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import kotlinx.coroutines.delay
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.ZoomableDefaults.defaultZoomOnDoubleTap
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable


@Composable
fun ImageColorDetector(
    modifier: Modifier = Modifier,
    color: Color,
    panEnabled: Boolean = false,
    imageBitmap: ImageBitmap,
    isMagnifierEnabled: Boolean,
    boxModifier: Modifier = Modifier,
    onColorChange: (Color) -> Unit,
    zoomState: ZoomState? = rememberZoomState(maxScale = 20f)
) {
    var pickerOffset by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val magnifierEnabled by remember(zoomState?.scale, isMagnifierEnabled, panEnabled) {
        derivedStateOf {
            (zoomState?.scale ?: 1f) <= 3f && !panEnabled && isMagnifierEnabled
        }
    }
    var globalTouchPosition by remember { mutableStateOf(Offset.Unspecified) }
    var globalTouchPointersCount by remember { mutableIntStateOf(0) }

    Box(
        modifier = boxModifier
            .observePointersCountWithOffset { size, offset ->
                globalTouchPointersCount = size
                globalTouchPosition = offset
            }
            .then(
                if (magnifierEnabled && globalTouchPointersCount == 1) {
                    Modifier.magnifier(
                        sourceCenter = {
                            if (pickerOffset.isSpecified) {
                                globalTouchPosition
                            } else Offset.Unspecified
                        },
                        magnifierCenter = {
                            globalTouchPosition - Offset(0f, 100.dp.toPx())
                        },
                        size = DpSize(height = 100.dp, width = 100.dp),
                        zoom = 2f / (zoomState?.scale ?: 1f),
                        cornerRadius = 50.dp,
                        elevation = 2.dp
                    )
                } else Modifier
            )
            .then(
                zoomState?.let {
                    Modifier.zoomable(
                        zoomState = zoomState,
                        zoomEnabled = (globalTouchPointersCount >= 2 || panEnabled),
                        enableOneFingerZoom = panEnabled,
                        onDoubleTap = { pos ->
                            if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
                        }
                    )
                } ?: Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        ImageWithConstraints(
            modifier = modifier,
            imageBitmap = imageBitmap
        ) {

            val density = LocalDensity.current.density

            val size = rememberUpdatedState(
                newValue = Size(
                    width = imageWidth.value * density,
                    height = imageHeight.value * density
                )
            )

            fun updateOffset(pointerInputChange: PointerInputChange): Offset {
                val offsetX = pointerInputChange.position.x
                    .coerceIn(0f, size.value.width)
                val offsetY = pointerInputChange.position.y
                    .coerceIn(0f, size.value.height)
                pointerInputChange.consume()
                return Offset(offsetX, offsetY)
            }

            if (!panEnabled) {
                var touchStartedWithOnePointer by remember {
                    mutableStateOf(false)
                }
                Box(
                    modifier = Modifier
                        .size(imageWidth, imageHeight)
                        .pointerMotionEvents(
                            onDown = {
                                touchStartedWithOnePointer = globalTouchPointersCount <= 1

                                if (touchStartedWithOnePointer) pickerOffset = updateOffset(it)
                            },
                            onMove = {
                                if (touchStartedWithOnePointer) pickerOffset = updateOffset(it)
                            },
                            onUp = {
                                if (touchStartedWithOnePointer) pickerOffset = updateOffset(it)
                            },
                            delayAfterDownInMillis = 20
                        )
                )
            }

            if (pickerOffset.isSpecified && pickerOffset.isFinite) {
                onColorChange(
                    calculateColorInPixel(
                        offsetX = pickerOffset.x,
                        offsetY = pickerOffset.y,
                        rect = rect,
                        width = imageWidth.value * density,
                        height = imageHeight.value * density,
                        bitmap = imageBitmap.asAndroidBitmap()
                    )
                )
            }

            ColorSelectionDrawing(
                modifier = Modifier
                    .size(imageWidth, imageHeight),
                selectedColor = color,
                zoom = zoomState?.scale ?: 1f,
                offset = pickerOffset
            )
        }
    }
}

@Composable
internal fun ColorSelectionDrawing(
    modifier: Modifier,
    selectedColor: Color = Color.Black,
    zoom: Float = 1f,
    offset: Offset,
) {
    val isDark = selectedColor.luminance() <= 0.3f

    val darkOuter = MaterialTheme.colorScheme.primaryFixedDim
    val darkInner = MaterialTheme.colorScheme.onPrimaryFixed

    val outerColor by animateColorAsState(
        targetValue = if (isDark) {
            darkOuter
        } else {
            darkInner
        },
        label = "outerColor"
    )

    val innerColor by animateColorAsState(
        targetValue = if (isDark) {
            darkInner
        } else {
            darkOuter
        },
        label = "innerColor"
    )


    val safeZoom = zoom.coerceAtLeast(0.01f)

    val density = LocalDensity.current
    val dash = with(density) { 7.8.dp.toPx() }
    val gap = with(density) { 8.dp.toPx() }
    val phase = (dash + gap) * 3

    val pathEffect = rememberAnimatedBorder(
        intervals = floatArrayOf(dash / safeZoom, gap / safeZoom),
        phase = phase / safeZoom,
        repeatDuration = 4000
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        if (!offset.isSpecified) return@Canvas

        val radius = 9.dp.toPx() / safeZoom

        fun drawStarOutline(
            starSize: Float,
            color: Color,
            style: DrawStyle = Fill,
        ) {
            val outline = MaterialStarShape.createOutline(
                size = Size(starSize, starSize),
                layoutDirection = layoutDirection,
                density = this
            )

            withTransform(
                transformBlock = {
                    translate(
                        left = offset.x - starSize / 2f,
                        top = offset.y - starSize / 2f
                    )
                }
            ) {
                drawOutline(
                    outline = outline,
                    color = color,
                    style = style
                )
            }
        }

        drawStarOutline(
            starSize = radius * 3.25f,
            color = outerColor,
            style = Stroke(
                width = radius * 0.4f
            )
        )

        drawStarOutline(
            starSize = radius * 3.25f,
            color = innerColor,
            style = Stroke(
                width = radius * 0.3f,
                pathEffect = pathEffect
            )
        )

        drawStarOutline(
            starSize = radius * 1f,
            color = outerColor
        )

        drawStarOutline(
            starSize = radius * 0.9f,
            color = innerColor
        )

        drawStarOutline(
            starSize = radius * 0.5f,
            color = outerColor
        )
    }
}

/**
 * Calculate color of a pixel in a [Bitmap] that is drawn to a Composable with
 * [width] and [height]. [startImageX]
 *
 * @param offsetX x coordinate in Composable from top left corner
 * @param offsetY y coordinate in Composable from top left corner
 * @param startImageX x coordinate of top left position of image
 * @param startImageY y coordinate of top left position of image
 * @param rect contains coordinates of original bitmap to be used as. Full bitmap has
 * rect with (0,0) top left and size of [bitmap]
 * @param width of the Composable that draws this [bitmap]
 * @param height of the Composable that draws this [bitmap]
 * @param bitmap of picture/image that to detect color of a specific pixel in
 */
private fun calculateColorInPixel(
    offsetX: Float,
    offsetY: Float,
    startImageX: Float = 0f,
    startImageY: Float = 0f,
    rect: IntRect,
    width: Float,
    height: Float,
    bitmap: Bitmap,
): Color {

    val bitmapWidth = bitmap.width
    val bitmapHeight = bitmap.height

    if (bitmapWidth == 0 || bitmapHeight == 0) return Color.Unspecified

    // End positions, this might be less than Image dimensions if bitmap doesn't fit Image
    val endImageX = width - startImageX
    val endImageY = height - startImageY

    val scaledX = scale(
        start1 = startImageX,
        end1 = endImageX,
        pos = offsetX,
        start2 = rect.left.toFloat(),
        end2 = rect.right.toFloat()
    ).toInt().coerceIn(0, bitmapWidth - 1)

    val scaledY = scale(
        start1 = startImageY,
        end1 = endImageY,
        pos = offsetY,
        start2 = rect.top.toFloat(),
        end2 = rect.bottom.toFloat()
    ).toInt().coerceIn(0, bitmapHeight - 1)

    val pixel: Int = bitmap[scaledX, scaledY]

    val red = android.graphics.Color.red(pixel)
    val green = android.graphics.Color.green(pixel)
    val blue = android.graphics.Color.blue(pixel)

    return (Color(red, green, blue))
}


/**
 * [Linear Interpolation](https://en.wikipedia.org/wiki/Linear_interpolation) function that moves
 * amount from it's current position to start and amount
 * @param start of interval
 * @param end of interval
 * @param amount e closed unit interval [0, 1]
 */
private fun lerp(start: Float, end: Float, amount: Float): Float {
    return (1 - amount) * start + amount * end
}

/**
 * Scale x1 from start1..end1 range to start2..end2 range

 */
private fun scale(start1: Float, end1: Float, pos: Float, start2: Float, end2: Float) =
    lerp(start2, end2, calculateFraction(start1, end1, pos))

/**
 * Calculate fraction for value between a range [end] and [start] coerced into 0f-1f range
 */
private fun calculateFraction(start: Float, end: Float, pos: Float) =
    (if (end - start == 0f) 0f else (pos - start) / (end - start)).coerceIn(0f, 1f)

@Suppress("SameParameterValue")
@Composable
private fun rememberAnimatedBorder(
    intervals: FloatArray = floatArrayOf(20f, 20f),
    phase: Float = 80f,
    repeatDuration: Int = 1000
): PathEffect = PathEffect.dashPathEffect(
    intervals = intervals,
    phase = rememberAnimatedBorderPhase(
        phase = phase,
        repeatDuration = repeatDuration
    )
)

@Composable
private fun rememberAnimatedBorderPhase(
    phase: Float = 80f,
    repeatDuration: Int = 1000
): Float {
    val transition = rememberInfiniteTransition()

    val animatedPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = phase,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = repeatDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    return animatedPhase
}

private val MaterialStarShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 865.0807f
        val baseHeight = 865.0807f

        val path = Path().apply {
            moveTo(403.3913f, 8.7356f)
            cubicTo(421.0787f, -2.9119f, 444.002f, -2.9119f, 461.6894f, 8.7356f)
            lineTo(518.743f, 46.3066f)
            cubicTo(528.2839f, 52.5895f, 539.5995f, 55.6215f, 551.0036f, 54.9508f)
            lineTo(619.1989f, 50.9402f)
            cubicTo(640.3404f, 49.6968f, 660.1926f, 61.1585f, 669.6865f, 80.0892f)
            lineTo(700.3109f, 141.1534f)
            cubicTo(705.4321f, 151.365f, 713.7157f, 159.6486f, 723.9273f, 164.7699f)
            lineTo(784.9915f, 195.3942f)
            cubicTo(803.9222f, 204.8881f, 815.3839f, 224.7403f, 814.1406f, 245.8818f)
            lineTo(810.1299f, 314.0771f)
            cubicTo(809.4593f, 325.4812f, 812.4913f, 336.7969f, 818.7742f, 346.3378f)
            lineTo(856.3451f, 403.3913f)
            cubicTo(867.9926f, 421.0787f, 867.9927f, 444.002f, 856.3452f, 461.6894f)
            lineTo(818.7742f, 518.743f)
            cubicTo(812.4913f, 528.2839f, 809.4593f, 539.5995f, 810.1299f, 551.0036f)
            lineTo(814.1406f, 619.1989f)
            cubicTo(815.3839f, 640.3404f, 803.9223f, 660.1926f, 784.9916f, 669.6865f)
            lineTo(723.9274f, 700.3109f)
            cubicTo(713.7158f, 705.4321f, 705.4321f, 713.7157f, 700.3109f, 723.9273f)
            lineTo(669.6866f, 784.9915f)
            cubicTo(660.1926f, 803.9222f, 640.3404f, 815.3839f, 619.1989f, 814.1406f)
            lineTo(551.0036f, 810.1299f)
            cubicTo(539.5995f, 809.4593f, 528.2839f, 812.4913f, 518.743f, 818.7742f)
            lineTo(461.6894f, 856.3451f)
            cubicTo(444.0021f, 867.9926f, 421.0787f, 867.9927f, 403.3914f, 856.3452f)
            lineTo(346.3378f, 818.7742f)
            cubicTo(336.7969f, 812.4913f, 325.4812f, 809.4593f, 314.0771f, 810.1299f)
            lineTo(245.8818f, 814.1406f)
            cubicTo(224.7404f, 815.3839f, 204.8882f, 803.9223f, 195.3942f, 784.9916f)
            lineTo(164.7699f, 723.9274f)
            cubicTo(159.6486f, 713.7158f, 151.365f, 705.4321f, 141.1534f, 700.3109f)
            lineTo(80.0892f, 669.6866f)
            cubicTo(61.1585f, 660.1926f, 49.6968f, 640.3404f, 50.9402f, 619.199f)
            lineTo(54.9508f, 551.0036f)
            cubicTo(55.6215f, 539.5995f, 52.5895f, 528.2839f, 46.3066f, 518.743f)
            lineTo(8.7356f, 461.6894f)
            cubicTo(-2.9119f, 444.0021f, -2.9119f, 421.0787f, 8.7356f, 403.3914f)
            lineTo(46.3066f, 346.3378f)
            cubicTo(52.5895f, 336.7969f, 55.6215f, 325.4813f, 54.9508f, 314.0771f)
            lineTo(50.9402f, 245.8818f)
            cubicTo(49.6968f, 224.7404f, 61.1585f, 204.8882f, 80.0892f, 195.3942f)
            lineTo(141.1534f, 164.7699f)
            cubicTo(151.365f, 159.6486f, 159.6486f, 151.365f, 164.7699f, 141.1534f)
            lineTo(195.3942f, 80.0892f)
            cubicTo(204.8882f, 61.1585f, 224.7403f, 49.6968f, 245.8818f, 50.9402f)
            lineTo(314.0771f, 54.9508f)
            cubicTo(325.4813f, 55.6215f, 336.7969f, 52.5895f, 346.3378f, 46.3066f)
            lineTo(403.3913f, 8.7356f)
            close()
        }

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(Matrix().apply {
                        setScale(size.width / baseWidth, size.height / baseHeight)
                    })
                }
                .asComposePath()
        )
    }
}

@Preview
@Composable
private fun Preview() = MaterialTheme {
    val selected = remember { Animatable(Color.Black) }

    LaunchedEffect(Unit) {
        while (true) {
            selected.animateTo(Color.Black)
            delay(3000)
            selected.animateTo(Color.White)
            delay(3000)
        }
    }
    BoxWithConstraints(modifier = Modifier.background(selected.value)) {
        ColorSelectionDrawing(
            modifier = Modifier
                .fillMaxWidth()
                .scale(10f),
            selectedColor = selected.value,
            offset = Offset(
                x = constraints.maxWidth / 2f,
                y = constraints.maxHeight / 2f
            )
        )
    }
}