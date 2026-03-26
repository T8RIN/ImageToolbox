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
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import com.t8rin.gesture.observePointersCountWithOffset
import com.t8rin.gesture.pointerMotionEvents
import com.t8rin.image.ImageWithConstraints
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
    onColorChange: (Color) -> Unit
) {
    var pickerOffset by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val zoomState = rememberZoomState(maxScale = 20f)

    val magnifierEnabled by remember(zoomState.scale, isMagnifierEnabled, panEnabled) {
        derivedStateOf {
            zoomState.scale <= 3f && !panEnabled && isMagnifierEnabled
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
                        zoom = 2f / zoomState.scale,
                        cornerRadius = 50.dp,
                        elevation = 2.dp
                    )
                } else Modifier
            )
            .zoomable(
                zoomState = zoomState,
                zoomEnabled = (globalTouchPointersCount >= 2 || panEnabled),
                enableOneFingerZoom = panEnabled,
                onDoubleTap = { pos ->
                    if (panEnabled) zoomState.defaultZoomOnDoubleTap(pos)
                }
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
                zoom = zoomState.scale,
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
    val color = animateColorAsState(
        if (selectedColor.luminance() > 0.3f) Color.Black else Color.White
    ).value

    Canvas(modifier = modifier.fillMaxSize()) {

        if (offset.isSpecified) {
            val radius: Float = 8.dp.toPx() / zoom

            // Draw touch position circle
            drawCircle(
                color,
                radius = radius * 0.3f,
                center = offset
            )
            drawCircle(
                color,
                radius = radius * 1.6f,
                center = offset,
                style = Stroke(radius * 0.6f)
            )
        }
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
