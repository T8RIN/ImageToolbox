package com.smarttoolfactory.colorpicker.selector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.smarttoolfactory.colorpicker.ui.gradientColorScaleHSVReversed
import com.smarttoolfactory.colorpicker.util.calculateAngleFomLocalCoordinates
import com.smarttoolfactory.colorpicker.util.calculateDistanceFromCenter
import com.smarttoolfactory.colorpicker.util.calculatePositionFromAngleAndDistance
import com.smarttoolfactory.gesture.detectMotionEvents

/**
 * Circle Hue and Saturation picker for
 * [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model. Angle is between touch point
 * and center of this selector is returned as [hue], distance from center as [saturation].
 * @param hue is in [0f..360f] of HSV color
 * @param saturation is in [0f..1f] of HSV color
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] and [saturation]
 *  when position of touch in this selector has changed.
 */
@Composable
fun SelectorCircleHueSaturationHSV(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float, Float) -> Unit
) {
    BoxWithConstraints(modifier) {

        val density = LocalDensity.current.density

        // Check if user touches between the valid area of circle
        var isTouched by remember { mutableStateOf(false) }

        val width = constraints.maxWidth.toFloat()
        val radius = width / 2

        // Center position of color picker
        val center = Offset(radius, radius)

        var currentPosition by remember {
            mutableStateOf(center)
        }

        currentPosition = calculatePositionFromAngleAndDistance(
            distance = saturation * radius,
            angle = hue,
            center = center
        )

        val selectorRadius =
            if (selectionRadius != Dp.Unspecified) selectionRadius.value * density
            else width * .04f


        val canvasModifier = Modifier
            .pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position

                        // Distance from center to touch point
                        val distance =
                            calculateDistanceFromCenter(center, position).coerceAtMost(radius)

                        // if distance is between inner and outer radius then we touched valid area
                        isTouched = (distance < radius)

                        if (isTouched) {
                            val hueChange = calculateAngleFomLocalCoordinates(center, position)
                            val saturationChange = (distance / radius).coerceIn(0f, 1f)
                            onChange(hueChange, saturationChange)
                            it.consume()
                        }

                    },
                    onMove = {
                        if (isTouched) {

                            val position = it.position
                            val hueChange = calculateAngleFomLocalCoordinates(center, position)
                            val distance =
                                calculateDistanceFromCenter(center, position).coerceAtMost(radius)

                            val saturationChange = (distance / radius).coerceIn(0f, 1f)
                            onChange(hueChange, saturationChange)
                            it.consume()
                        }

                    },
                    onUp = {
                        if (isTouched) {
                            it.consume()
                        }
                        isTouched = false

                    },
                    delayAfterDownInMillis = 20
                )
            }

        SelectorCircleImpl(
            modifier = canvasModifier,
            selectorPosition = currentPosition,
            selectorRadius = selectorRadius
        )
    }
}

@Composable
private fun SelectorCircleImpl(
    modifier: Modifier,
    selectorPosition: Offset,
    selectorRadius: Float
) {
    val colorScaleHSVSweep = remember { Brush.sweepGradient(gradientColorScaleHSVReversed) }
    val whiteToTransparentRadial = remember {
        Brush.radialGradient(
            colors = listOf(Color.White, Color(0x00FFFFFF))
        )
    }
    Canvas(
        modifier = modifier.aspectRatio(1f)

    ) {
        // Draw hue selection circle with sweep gradient
        drawCircle(colorScaleHSVSweep)
        drawCircle(whiteToTransparentRadial)
        drawHueSelectionCircle(center = selectorPosition, radius = selectorRadius)
    }
}
