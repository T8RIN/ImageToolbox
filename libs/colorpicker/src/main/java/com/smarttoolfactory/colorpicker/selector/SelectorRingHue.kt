package com.smarttoolfactory.colorpicker.selector

import androidx.annotation.FloatRange
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.ui.gradientColorScaleHSVReversed
import com.smarttoolfactory.colorpicker.util.calculateAngleFomLocalCoordinates
import com.smarttoolfactory.colorpicker.util.calculateDistanceFromCenter
import com.smarttoolfactory.gesture.detectMotionEvents

/**
 * Hue selector Ring that selects hue based on rotation of circle selector.
 *
 * @param hue is in [0f..360f] of HSL color. Touching any point in ring returns angle between
 * touch point and center
 * @param outerRadiusFraction radius ratio of outer radius of ring in percent
 * @param innerRadiusFraction radius ratio of inner radius of ring in percent
 * @param borderStrokeColor color for drawing border outer and inner positions of ring
 * @param borderStrokeWidth width of stroke for drawing border inner and outer positions of ring
 * @param backgroundColor is the color of circle drawn from center to inner radius
 * @param selectionRadius radius of selection circle that moves based on touch position
 * @param onChange callback that returns [hue] on user touch
 */
@Composable
fun SelectorRingHue(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.1, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) outerRadiusFraction: Float = .9f,
    @FloatRange(from = 0.0, to = 1.0) innerRadiusFraction: Float = .6f,
    borderStrokeColor: Color = Color.Black,
    borderStrokeWidth: Dp = 4.dp,
    backgroundColor: Color = Color.Black,
    selectionRadius: Dp = Dp.Unspecified,
    onChange: (Float) -> Unit
) {

    BoxWithConstraints(modifier) {

        // Angle from center is required to get Hue which is between 0-360
        // only changes with touch, hue is used for drawing, this is for recomposition after touch
        var angle by remember { mutableStateOf(hue) }

        val density = LocalDensity.current.density

        // Check if user touches between the valid area of circle
        var isTouched by remember { mutableStateOf(false) }

        val width = constraints.maxWidth.toFloat()
        val radius = width / 2

        // Center Offset of selector
        val center = Offset(radius, radius)

        val radiusInner: Float = (radius * innerRadiusFraction).coerceAtLeast(0f)
        val radiusOuter: Float = (radius * outerRadiusFraction).coerceAtLeast(radiusInner)

        // Border stroke width for inner and outer radius positions
        val borderStrokeWidthPx =
            (borderStrokeWidth.value * density).coerceAtMost(radiusInner * .2f)

        val coerced = hue.coerceIn(0f, 360f)

        /**
         * Circle selector radius for setting **angle** which sets hue
         */
        val selectorRadius =
            if (selectionRadius == Dp.Unspecified)
                (radiusInner * 2 * .04f).coerceAtMost(radiusOuter - radiusInner)
            else selectionRadius.value * density

        val canvasModifier = Modifier
            .pointerInput(Unit) {
                detectMotionEvents(
                    onDown = {
                        val position = it.position
                        // Distance from center to touch point
                        val distance = calculateDistanceFromCenter(center, position)

                        // if distance is between inner and outer radius then we touched valid area
                        isTouched = (distance in radiusInner..radiusOuter)
                        if (isTouched) {
                            angle = calculateAngleFomLocalCoordinates(center, position)
                            onChange(angle)
                            it.consume()
                        }

                    },
                    onMove = {
                        if (isTouched) {
                            val position = it.position
                            angle = calculateAngleFomLocalCoordinates(center, position)
                            onChange(angle)
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

        HueSelectorRingImpl(
            modifier = canvasModifier,
            radiusOuter = radiusOuter,
            radiusInner = radiusInner,
            backgroundColor = backgroundColor,
            borderStrokeColor = borderStrokeColor,
            borderStrokeWidth = borderStrokeWidthPx,
            selectorRadius = selectorRadius,
            degrees = coerced
        )
    }
}

@Composable
private fun HueSelectorRingImpl(
    modifier: Modifier,
    radiusOuter: Float,
    radiusInner: Float,
    backgroundColor: Color,
    borderStrokeColor: Color,
    borderStrokeWidth: Float,
    selectorRadius: Float,
    degrees: Float
) {
    Canvas(
        modifier = modifier.aspectRatio(1f)
    ) {

        val strokeWidth = (radiusOuter - radiusInner)

        // Draw hue selection circle with sweep gradient
        drawCircle(
            brush = Brush.sweepGradient(colors = gradientColorScaleHSVReversed, center = center),
            radius = radiusInner + strokeWidth / 2,
            style = Stroke(
                width = strokeWidth
            )
        )

        // draw background with background color from center to inner radius
        drawCircle(color = backgroundColor, radius = radiusInner)

        // Stroke draws half in and half out of the current radius.
        // with 200 radius 20 stroke width starts from 190 and ends at 210
        drawCircle(
            color = borderStrokeColor,
            radius = radiusInner - borderStrokeWidth / 2,
            style = Stroke(width = borderStrokeWidth)
        )
        drawCircle(
            color = borderStrokeColor,
            radius = radiusOuter + borderStrokeWidth / 2,
            style = Stroke(width = borderStrokeWidth)
        )

        // rotate selection circle based on hue value
        withTransform(
            {
                rotate(degrees = -degrees)
            }
        ) {
            // draw hue selection circle
            drawHueSelectionCircle(
                center = Offset(center.x + radiusInner + strokeWidth / 2f, center.y),
                radius = selectorRadius
            )
        }
    }
}