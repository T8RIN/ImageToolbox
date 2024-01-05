package com.smarttoolfactory.colorpicker.slider

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.ui.Grey400
import com.smarttoolfactory.extendedcolors.util.fractionToPercent
import com.smarttoolfactory.extendedcolors.util.fractionToRGBString

/*
    HSV Slider Displays
 */
/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [hue] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param onValueChange callback that returns change in [hue] when Slider is dragged
 */
@Composable
fun SliderDisplayHueHSV(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    onValueChange: (Float) -> Unit
) {

    TitledSliderDisplay(
        modifier = modifier,
        title = "Hue",
        description = "${hue.toInt()}°"
    ) {
        SliderHueHSL(
            hue = hue,
            saturation = saturation,
            lightness = value,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [saturation] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param onValueChange callback that returns change in [saturation] when Slider is dragged
 */
@Composable
fun SliderDisplaySaturationHSV(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Saturation",
        description = "${saturation.fractionToPercent()}"
    ) {
        SliderSaturationHSV(
            hue = hue,
            saturation = saturation,
            value = value,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [value] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param value in [0..1f]
 * @param onValueChange callback that returns change in [value] when Slider is dragged
 */
@Composable
fun SliderDisplayValueHSV(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Value",
        description = "${value.fractionToPercent()}"
    ) {
        SliderValueHSV(
            hue = hue,
            value = value,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [alpha] in [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param alpha in [0..1f]
 * @param onValueChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderDisplayAlphaHSV(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Alpha",
        description = "${alpha.fractionToPercent()}"
    ) {
        SliderAlphaHSV(
            hue = hue,
            alpha = alpha,
            onValueChange = onValueChange
        )
    }
}

/*
    HSL Slider Displays
 */
/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [hue] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param onValueChange callback that returns change in [hue] when Slider is dragged
 */
@Composable
fun SliderDisplayHueHSL(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {

    TitledSliderDisplay(
        modifier = modifier,
        title = "Hue",
        description = "${hue.toInt()}°"

    ) {
        SliderHueHSL(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [saturation] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param onValueChange callback that returns change in [saturation] when Slider is dragged
 */
@Composable
fun SliderDisplaySaturationHSL(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Saturation",
        description = "${saturation.fractionToPercent()}"
    ) {
        SliderSaturationHSL(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            onValueChange = { value ->
                onValueChange(value)
            }
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [lightness] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param lightness in [0..1f]
 * @param onValueChange callback that returns change in [lightness] when Slider is dragged
 */
@Composable
fun SliderDisplayLightnessHSL(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Lightness",
        description = "${lightness.fractionToPercent()}"
    ) {
        SliderLightnessHSL(
            lightness = lightness,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [alpha] in [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV) color model.
 * @param hue in [0..360f]
 * @param onValueChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderDisplayAlphaHSL(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Alpha",
        description = "${alpha.fractionToPercent()}"
    ) {
        SliderAlphaHSV(
            hue = hue,
            alpha = alpha,
            onValueChange = onValueChange
        )
    }
}

/*
    RGB Slider Displays
 */

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [red] in [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param red in [0..1f]
 * @param onValueChange callback that returns change in [red] when Slider is dragged
 */
@Composable
fun SliderDisplayRedRGB(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) red: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        titleColor = Color.Red,
        title = "Red",
        description = red.fractionToRGBString()
    ) {
        SliderRedRGB(
            red = red,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [green] in [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param green in [0..1f]
 * @param onValueChange callback that returns change in [green] when Slider is dragged
 */
@Composable
fun SliderDisplayGreenRGB(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) green: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        titleColor = Color.Green,
        title = "Green",
        description = green.fractionToRGBString()
    ) {
        SliderGreenRGB(
            green = green,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [blue] in [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param blue in [0..1f]
 * @param onValueChange callback that returns change in [blue] when Slider is dragged
 */
@Composable
fun SliderDisplayBlueRGB(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) blue: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        titleColor = Color.Blue,
        title = "Blue",
        description = blue.fractionToRGBString()
    ) {
        SliderBlueRGB(
            blue = blue,
            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to select
 * [alpha] in [RGB](https://en.wikipedia.org/wiki/RGB_color_model) color model.
 * @param red in [0..1f]
 * @param green in [0..1f]
 * @param blue in [0..1f]
 * @param alpha in [0..1f]
 * @param onValueChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderDisplayAlphaRGB(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) red: Float,
    @FloatRange(from = 0.0, to = 1.0) green: Float,
    @FloatRange(from = 0.0, to = 1.0) blue: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onValueChange: (Float) -> Unit
) {
    TitledSliderDisplay(
        modifier = modifier,
        title = "Alpha",
        description = "${alpha.fractionToPercent()}"
    ) {
        SliderAlphaRGB(
            red = red,
            green = green,
            blue = blue,
            alpha = alpha,

            onValueChange = onValueChange
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to pick color.
 * @param title Title is positioned left side of the slider and presented with only initial letter
 * @param titleColor color of the [title] string
 * @param description shows the value retrieved from [slider] value change.
 * @param slider is Composable that uses [CheckeredColorfulSlider]
 */
@Composable
fun TitledSliderDisplay(
    modifier: Modifier,
    title: String,
    titleColor: Color = Grey400,
    description: String,
    slider: @Composable () -> Unit
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title.substring(0, 1),
            color = titleColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            slider()
        }
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = description,
            fontSize = 12.sp,
            color = Grey400,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(30.dp)
        )
    }
}