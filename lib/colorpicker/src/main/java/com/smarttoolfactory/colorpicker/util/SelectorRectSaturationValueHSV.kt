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

package com.smarttoolfactory.colorpicker.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.gesture.detectMotionEvents

/**
 * Rectangle Saturation and Vale selector for
 * [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model
 * @param hue is in [0f..360f] of HSL color
 * @param value is in [0f..1f] of HSL color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [value]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorRectSaturationValueHSV(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float = 0.5f,
    value: Float = 0.5f,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    val valueGradient = valueGradient(hue)
    val hueSaturation = saturationHSVGradient(hue)

    SelectorRect(
        modifier = modifier,
        saturation = saturation,
        property = value,
        brushSrc = hueSaturation,
        brushDst = valueGradient,
        selectionRadius = selectionRadius,
        onChange = onChange
    )
}

@Composable
private fun SelectorRect(
    modifier: Modifier = Modifier,
    saturation: Float = 0.5f,
    property: Float = 0.5f,
    brushSrc: Brush,
    brushDst: Brush,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {

    BoxWithConstraints(modifier) {

        val density = LocalDensity.current.density
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        // Center position of color picker
        val center = Offset(width / 2, height / 2)

        /**
         * Circle selector radius for setting [saturation] and [property] by gesture
         */
        val selectorRadius =
            if (selectionRadius != Dp.Unspecified) selectionRadius.value * density
            else width.coerceAtMost(height) * .04f

        var currentPosition by remember {
            mutableStateOf(center)
        }

        val posX = saturation * width
        val posY = (1 - property) * height
        currentPosition = Offset(posX, posY)


        val canvasModifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        val saturationChange = (position.x / width).coerceIn(0f, 1f)
                        val valueChange = (1 - (position.y / height)).coerceIn(0f, 1f)
                        onChange(saturationChange, valueChange)
                        it.consume()

                    },
                    onMove = {
                        val position = it.position
                        val saturationChange = (position.x / width).coerceIn(0f, 1f)
                        val valueChange = (1 - (position.y / height)).coerceIn(0f, 1f)
                        onChange(saturationChange, valueChange)
                        it.consume()
                    },
                    delayAfterDownInMillis = 20
                )
            }

        SelectorRectImpl(
            modifier = canvasModifier,
            brushSrc = brushSrc,
            brushDst = brushDst,
            selectorPosition = currentPosition,
            selectorRadius = selectorRadius
        )
    }
}

@Composable
private fun SelectorRectImpl(
    modifier: Modifier,
    brushSrc: Brush,
    brushDst: Brush,
    selectorPosition: Offset,
    selectorRadius: Float
) {

    Canvas(modifier = modifier) {
        drawBlendingRectGradient(
            dst = brushDst,
            src = brushSrc,
            blendMode = BlendMode.Multiply
        )
        drawHueSelectionCircle(
            center = selectorPosition,
            radius = selectorRadius
        )
    }
}

private fun saturationHSVGradient(
    hue: Float,
    value: Float = 1f,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset(Float.POSITIVE_INFINITY, 0f)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsv(hue = hue, saturation = 0f, value = value, alpha = alpha),
            Color.hsv(hue = hue, saturation = 1f, value = value, alpha = alpha)
        ),
        start = start,
        end = end
    )
}

/**
 * Vertical gradient that goes from 1 value to 0 with 90 degree rotation by default.
 */
fun valueGradient(
    hue: Float,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset(0f, Float.POSITIVE_INFINITY)
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsv(hue = hue, saturation = 0f, value = 1f, alpha = alpha),
            Color.hsv(hue = hue, saturation = 0f, value = 0f, alpha = alpha)
        ),
        start = start,
        end = end
    )
}

private fun DrawScope.drawBlendingRectGradient(
    dst: Brush,
    dstTopLeft: Offset = Offset.Zero,
    dstSize: Size = this.size,
    src: Brush,
    srcTopLeft: Offset = Offset.Zero,
    srcSize: Size = this.size,
    blendMode: BlendMode = BlendMode.Multiply
) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        drawRect(dst, dstTopLeft, dstSize)
        drawRect(src, srcTopLeft, srcSize, blendMode = blendMode)
        restoreToCount(checkPoint)
    }
}

private fun DrawScope.drawHueSelectionCircle(
    center: Offset,
    radius: Float
) {
    drawCircle(
        Color.White,
        radius = radius,
        center = center,
        style = Stroke(width = radius / 4)
    )

    drawCircle(
        Color.Black,
        radius = radius + radius / 8,
        center = center,
        style = Stroke(width = radius / 8)
    )
}