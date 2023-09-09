package com.smarttoolfactory.colorpicker.slider

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.ui.brush.sliderAlphaHSLGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderAlphaHSVGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderAlphaRGBGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderBlueGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderGreenGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderHueHSLGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderHueHSVGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderLightnessGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderRedGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderSaturationHSLGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderSaturationHSVGradient
import com.smarttoolfactory.colorpicker.ui.brush.sliderValueGradient
import com.smarttoolfactory.colorpicker.widget.drawChecker
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

/*
    HSV Sliders
 */

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [hue] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param onValueChange callback that returns change in [hue] when Slider is dragged
 */
@Composable
fun SliderHueHSV(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    trackHeight: Dp = 12.dp,
    onValueChange: (Float) -> Unit
) {
    val sliderHueSelectionHSLGradient = sliderHueHSVGradient(
        saturation = saturation,
        value = value,
    )

    CheckeredColorfulSlider(
        modifier = modifier,
        trackHeight = trackHeight,
        value = animateFloatAsState(targetValue = hue).value,
        valueRange = 0f..360f,
        onValueChange = onValueChange,
        brush = sliderHueSelectionHSLGradient,
        drawChecker = true
    )
}

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [saturation] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param onValueChange callback that returns change in [saturation] when Slider is dragged
 */
@Composable
fun SliderSaturationHSV(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderHueSelectionHSLGradient = sliderSaturationHSVGradient(
        hue, value
    )

    CheckeredColorfulSlider(
        modifier = modifier,
        value = saturation,
        onValueChange = onValueChange,
        brush = sliderHueSelectionHSLGradient,
        drawChecker = true
    )
}

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [value] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param value in [0..1f]
 * @param onValueChange callback that returns change in [value] when Slider is dragged
 */
@Composable
fun SliderValueHSV(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    onValueChange: (Float) -> Unit
) {

    val sliderValueGradient = sliderValueGradient(hue = hue)

    CheckeredColorfulSlider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        brush = sliderValueGradient,
        drawChecker = true
    )
}

/**
 * [CheckeredColorfulSlider] that displays [alpha] change in
 * [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param alpha in [0..1f]
 */
@Composable
fun SliderAlphaHSV(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderAlphaHSLGradient = sliderAlphaHSVGradient(hue = hue)
    CheckeredColorfulSlider(
        modifier = modifier,
        value = alpha,
        onValueChange = onValueChange,
        brush = sliderAlphaHSLGradient,
        drawChecker = true
    )
}

/*
    HSL Sliders
 */

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [hue] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param onValueChange callback that returns change in [hue] when Slider is dragged
 */
@Composable
fun SliderHueHSL(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {

    val sliderHueSelectionHSLGradient = sliderHueHSLGradient(
        saturation = saturation,
        lightness = lightness,
    )

    CheckeredColorfulSlider(
        modifier = modifier,
        value = hue,
        valueRange = 0f..360f,
        onValueChange = onValueChange,
        brush = sliderHueSelectionHSLGradient,
        drawChecker = true
    )
}

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [saturation] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param onValueChange callback that returns change in [saturation] when Slider is dragged
 */
@Composable
fun SliderSaturationHSL(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {

    val sliderHueSelectionHSLGradient = sliderSaturationHSLGradient(
        hue, lightness
    )

    CheckeredColorfulSlider(
        modifier = modifier,
        value = saturation,
        onValueChange = onValueChange,
        brush = sliderHueSelectionHSLGradient,
        drawChecker = true
    )
}

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [lightness] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param lightness in [0..1f]
 * @param saturation in [0..1f]
 * white-black in hsl, others transition from hue with lightness.
 * @param onValueChange callback that returns change in [lightness] when Slider is dragged
 */
@Composable
fun SliderLightnessHSL(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderLightnessGradient = sliderLightnessGradient(hue, saturation)

    CheckeredColorfulSlider(
        modifier = modifier,
        value = lightness,
        onValueChange = onValueChange,
        brush = sliderLightnessGradient,
        drawChecker = true
    )
}

/**
 * [CheckeredColorfulSlider] is a slider to select
 * [alpha] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param alpha in [0..1f]
 * @param onValueChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderAlphaHSL(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    trackHeight: Dp = 12.dp,
    onValueChange: (Float) -> Unit,
) {
    val sliderAlphaHSLGradient = sliderAlphaHSLGradient(hue = hue)
    CheckeredColorfulSlider(
        modifier = modifier,
        trackHeight = trackHeight,
        value = animateFloatAsState(targetValue = alpha).value,
        onValueChange = onValueChange,
        brush = sliderAlphaHSLGradient,
        drawChecker = true
    )
}

/*
    RGB Sliders
 */

/**
 * [CheckeredColorfulSlider] that displays [red] change in
 * [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param red in [0..1f]
 * @param onValueChange callback that returns change in [red] when Slider is dragged
 */
@Composable
fun SliderRedRGB(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) red: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderRedGradient = sliderRedGradient()
    CheckeredColorfulSlider(
        modifier = modifier,
        value = red,
        onValueChange = onValueChange,
        brush = sliderRedGradient
    )
}

/**
 * [CheckeredColorfulSlider] that displays [green] change in
 * [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param green in [0..1f]
 * @param onValueChange callback that returns change in [green] when Slider is dragged
 */
@Composable
fun SliderGreenRGB(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) green: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderGreenGradient = sliderGreenGradient()
    CheckeredColorfulSlider(
        modifier = modifier,
        value = green,
        onValueChange = onValueChange,
        brush = sliderGreenGradient
    )
}

/**
 * [CheckeredColorfulSlider] that displays [blue] change in
 * [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param blue in [0..1f]
 * @param onValueChange callback that returns change in [blue] when Slider is dragged
 */
@Composable
fun SliderBlueRGB(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) blue: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderBlueGradient = sliderBlueGradient()
    CheckeredColorfulSlider(
        modifier = modifier,
        value = blue,
        onValueChange = onValueChange,
        brush = sliderBlueGradient
    )
}

/**
 * [CheckeredColorfulSlider] that displays [alpha] change in
 * [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param red in [0..1f]
 * @param green in [0..1f]
 * @param blue in [0..1f]
 * @param alpha in [0..1f]
 * @param onValueChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderAlphaRGB(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) red: Float,
    @FloatRange(from = 0.0, to = 1.0) green: Float,
    @FloatRange(from = 0.0, to = 1.0) blue: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderAlphaRGBGradient = sliderAlphaRGBGradient(red, green, blue)
    CheckeredColorfulSlider(
        modifier = modifier,
        value = alpha,
        onValueChange = onValueChange,
        brush = sliderAlphaRGBGradient,
        drawChecker = true
    )
}

/**
 * Slider implementation that uses [ColorfulSlider] to have create **Slider** with gradient
 * colors with custom thumb radius and track height, and draws checker pattern behind the
 * track of [ColorfulSlider] if [drawChecker] is set to true
 * @param value that is read by [ColorfulSlider]
 * @param valueRange is the values are selected from
 * @param onValueChange is triggered when slider is dragged returns float between [valueRange]
 * @param brush is used for drawing gradient for track of [ColorfulSlider]
 * @param drawChecker when set to true draws checker behind track of [ColorfulSlider] to
 * display alpha
 */
@Composable
fun CheckeredColorfulSlider(
    modifier: Modifier = Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
    brush: Brush,
    drawChecker: Boolean = false,
    trackHeight: Dp = 12.dp
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {

        if (drawChecker) {
            Box(
                modifier = Modifier
                    .width(maxWidth)
                    .height(trackHeight - 1.dp)
                    .drawChecker(shape = CircleShape)
            )
        }

        ColorfulSlider(
            value = value,
            modifier = Modifier,
            thumbRadius = 12.dp,
            trackHeight = trackHeight,
            onValueChange = { value ->
                onValueChange(value.roundToTwoDigits())
            },
            valueRange = valueRange,
            coerceThumbInTrack = true,
            colors = MaterialSliderDefaults.materialColors(
                activeTrackColor = SliderBrushColor(brush = brush),
                inactiveTrackColor = SliderBrushColor(color = Color.Transparent),
                thumbColor = SliderBrushColor(MaterialTheme.colorScheme.inverseSurface)
            ),
            drawInactiveTrack = false
        )
    }
}
