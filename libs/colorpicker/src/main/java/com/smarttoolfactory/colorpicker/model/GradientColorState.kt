package com.smarttoolfactory.colorpicker.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import com.smarttoolfactory.colorpicker.selector.gradient.GradientType
import com.smarttoolfactory.colorpicker.ui.GradientAngle
import com.smarttoolfactory.colorpicker.ui.GradientOffset
import com.smarttoolfactory.extendedcolors.util.ColorUtil

@Composable
fun rememberGradientColorState(
    color: Color = Color.Unspecified,
    size: DpSize = DpSize.Zero
): GradientColorState {

    val density = LocalDensity.current

    return remember {

        val sizePx = if (size == DpSize.Zero) {
            Size.Zero
        } else {
            with(density) {
                Size(
                    size.width.toPx(),
                    size.height.toPx()
                )
            }
        }
        GradientColorState(color, sizePx)
    }
}

/**
 * Gradient and color state for setting and getting gradient color
 * with [gradientType] such as Linear, Radial or Sweep, [tileMode]s, and [colorStops].
 * * Linear gradient uses [gradientOffset] to set offset or angle.
 * * Radial gradient uses [centerFriction] and [radiusFriction]
 */
class GradientColorState internal constructor(initialColor: Color, size: Size) {

    var size by mutableStateOf(size)
    var color: Color = initialColor

    val hexString: String
        get() {
            return ColorUtil.colorToHexAlpha(color)
        }

    val brush: Brush
        get() {

            val colorStops = if (colorStops.size == 1) {
                listOf(colorStops.first(), colorStops.first()).toTypedArray()
            } else {
                colorStops.toTypedArray()
            }

            val brush = when (gradientType) {
                GradientType.Linear -> Brush.linearGradient(
                    colorStops = colorStops,
                    start = gradientOffset.start,
                    end = gradientOffset.end,
                    tileMode = tileMode
                )

                GradientType.Radial -> Brush.radialGradient(
                    colorStops = colorStops,
                    center = Offset(
                        x = size.width * centerFriction.x,
                        y = size.height * centerFriction.y
                    ),
                    radius = ((size.width.coerceAtLeast(size.height)) / 2 * radiusFriction)
                        .coerceAtLeast(0.01f),
                    tileMode = tileMode
                )

                GradientType.Sweep -> Brush.sweepGradient(
                    colorStops = colorStops,
                    center = Offset(
                        x = size.width * centerFriction.x,
                        y = size.height * centerFriction.y
                    ),
                )
            }
            return brush
        }

    val brushColor: BrushColor
        get() {
            return BrushColor(color = color, brush = brush)
        }

    var gradientType: GradientType by mutableStateOf(GradientType.Linear)
    var colorStops = mutableStateListOf(
        0.0f to Color.Red,
        0.3f to Color.Green,
        1.0f to Color.Blue,
    )
    var tileMode by mutableStateOf(TileMode.Clamp)
    var gradientOffset by mutableStateOf(GradientOffset(GradientAngle.CW0))
    var centerFriction by mutableStateOf(Offset(.5f, .5f))
    var radiusFriction by mutableStateOf(.5f)
}
