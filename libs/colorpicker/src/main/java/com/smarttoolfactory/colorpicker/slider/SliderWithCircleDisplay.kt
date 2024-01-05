package com.smarttoolfactory.colorpicker.slider

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.widget.drawChecker
import com.smarttoolfactory.slider.ColorfulSlider

/**
 * Slider with Circle on left and 2 sliders on right side that display [hue] and [alpha]
 * for HSV color model. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * @param modifier [Modifier] for whole Composable
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param alpha in [0..1f]
 * @param onHueChange callback that returns change in [hue] when Slider is dragged
 * @param onAlphaChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderCircleColorDisplayHueHSV(
    modifier: Modifier = Modifier,
    enableAlpha: Boolean,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onHueChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
) {
    Column(modifier) {
        SliderHueHSV(
            hue = hue,
            saturation = saturation,
            value = value,
            onValueChange = onHueChange
        )
        if (enableAlpha) {
            SliderAlphaHSL(hue = hue, alpha = alpha, onValueChange = onAlphaChange)
        }
    }
}

/**
 * Slider with Circle on left and 2 sliders on right side that display [saturation] and [alpha]
 * for HSV color model. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * @param modifier [Modifier] for whole Composable
 * @param circleModifier [Modifier] for Circle shaped box
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param alpha in [0..1f]
 * @param onSaturationChange callback that returns change in [saturation] when Slider is dragged
 * @param onAlphaChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderCircleColorDisplaySaturationHSV(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onSaturationChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
) {
    SliderWithCircleDisplay(
        modifier = modifier,
        circleModifier = circleModifier,
        color = Color.hsv(hue, saturation, value, alpha)
    ) {
        SliderSaturationHSV(
            hue = hue,
            saturation = saturation,
            value = 1f,
            onValueChange = onSaturationChange
        )
        SliderAlphaHSV(
            hue = hue,
            alpha = alpha,
            onValueChange = onAlphaChange
        )
    }
}

/**
 * Slider with Circle on left and 2 sliders on right side that display [value] and [alpha]
 * for HSV color model. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * @param modifier [Modifier] for whole Composable
 * @param circleModifier [Modifier] for Circle shaped box
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param value in [0..1f]
 * @param alpha in [0..1f]
 * @param onValueChange callback that returns change in [value] when Slider is dragged
 * @param onAlphaChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderCircleColorDisplayValueHSV(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) value: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onValueChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
) {

    SliderWithCircleDisplay(
        modifier = modifier,
        circleModifier = circleModifier,
        color = Color.hsv(hue, saturation, value, alpha)
    ) {
        SliderValueHSV(
            hue = hue,
            value = value,
            onValueChange = onValueChange
        )
        SliderAlphaHSV(
            hue = hue,
            alpha = alpha,
            onValueChange = onAlphaChange
        )
    }
}


/**
 * Slider with Circle on left and 2 sliders on right side that display [hue] and [alpha]
 * for HSL color model. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * @param modifier [Modifier] for whole Composable
 * @param circleModifier [Modifier] for Circle shaped box
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param alpha in [0..1f]
 * @param onHueChange callback that returns change in [hue] when Slider is dragged
 * @param onAlphaChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderCircleColorDisplayHueHSL(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onHueChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
) {
    SliderWithCircleDisplay(
        modifier = modifier,
        circleModifier = circleModifier,
        color = Color.hsl(hue, saturation, lightness, alpha)
    ) {

        SliderHueHSL(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            onValueChange = onHueChange
        )

        SliderAlphaHSL(hue = hue, alpha = alpha, onValueChange = onAlphaChange)
    }
}

/**
 * Slider with Circle on left and 2 sliders on right side that display [saturation] and [alpha]
 * for HSL color model. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * @param modifier [Modifier] for whole Composable
 * @param circleModifier [Modifier] for Circle shaped box
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param alpha in [0..1f]
 * @param onSaturationChange callback that returns change in [saturation] when Slider is dragged
 * @param onAlphaChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderCircleColorDisplaySaturationHSL(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
    onSaturationChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
) {
    SliderWithCircleDisplay(
        modifier = modifier,
        circleModifier = circleModifier,
        color = Color.hsl(hue, saturation, lightness, alpha)
    ) {
        SliderSaturationHSL(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            onValueChange = onSaturationChange
        )
        SliderAlphaHSL(hue = hue, alpha = alpha, onValueChange = onAlphaChange)
    }
}

/**
 * Slider with Circle on left and 2 sliders on right side that display [lightness] and [alpha]
 * for HSL color model. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * @param modifier [Modifier] for whole Composable
 * @param circleModifier [Modifier] for Circle shaped box
 * @param hue in [0..360f]
 * @param saturation in [0..1f]
 * @param lightness in [0..1f]
 * @param alpha in [0..1f]
 * @param onLightnessChange callback that returns change in [lightness] when Slider is dragged
 * @param onAlphaChange callback that returns change in [alpha] when Slider is dragged
 */
@Composable
fun SliderCircleColorDisplayLightnessHSL(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    alpha: Float,
    onLightnessChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
) {
    SliderWithCircleDisplay(
        modifier = modifier,
        circleModifier = circleModifier,
        color = Color.hsl(hue, saturation, lightness, alpha)
    ) {
        SliderLightnessHSL(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            onValueChange = onLightnessChange
        )
        SliderAlphaHSL(hue = hue, alpha = alpha, onValueChange = onAlphaChange)
    }
}

/**
 * Slider with Circle on left and 2 sliders on right side. This composable requires minimum
 * 100.dp height, giving a height lower than this might break its layout.
 *
 * * [ColorfulSlider] requires min height of 48.dp and with 2 sliders min height should be 96.dp
 * * [CircleDisplay] has min height and width of 80 dp.
 *
 * @param circleModifier modifier for [CircleDisplay]
 * @param color color of [CircleDisplay]
 * @param content 2 sliders that either show Saturation or lightness for HSV and alpha, or
 * Saturation or Value and alpha for HSV.
 */
@Composable
fun SliderWithCircleDisplay(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    color: Color,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.requiredHeightIn(min = 100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleDisplay(
            modifier = circleModifier
                .widthIn(min = 70.dp)
                .heightIn(min = 70.dp),
            color = color
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.SpaceAround
        ) {
            content()
        }
    }
}

/**
 * [Box] with [CircleShape] displays [color] as background, and when alpha is lower than 1f
 * checker pattern is displayed behind colored background.
 */
@Composable
fun CircleDisplay(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .drawChecker(CircleShape)
            .background(color)
    )
}
