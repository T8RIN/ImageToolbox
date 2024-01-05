package com.smarttoolfactory.colorpicker.slider

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smarttoolfactory.slider.ColorfulSlider

/*
    Slider Display Panels that contain Title, Slider and Property as %, degree or Int
 */

/**
 * Composable that contains variant number of Slider displays with Title on left, [ColorfulSlider],
 * and current value from slider depending on **onChange** callbacks are null or not.
 *
 * Based on availability of callbacks order of sliders are placed in order from top to bottom
 * ```
 * Hue Slider
 * Saturation Slider
 * Value Slider
 * Alpha Slider
 * ```
 * @param hue Hue of HSV in [0..360f]
 * @param saturation Hue of HSV in [0..1f]
 * @param value Hue of HSV in [0..1f]
 * @param alpha Hue of HSV in [0..1f]
 * @param onHueChange lambda in which [hue] should be updated. when this lambda is not nul
 * [SliderDisplayHueHSV] is added to Column of sliders.
 * @param onSaturationChange lambda in which [saturation] should be updated. when this lambda is not nul
 * [[SliderDisplaySaturationHSV] is added to Column of sliders.
 * @param onValueChange lambda in which [value] should be updated. when this lambda is not nul
 * [[SliderDisplayValueHSV] is added to Column of sliders.
 * @param onAlphaChange lambda in which [alpha] should be updated. when this lambda is not nul
 * [[SliderDisplayAlphaHSV] is added to Column of slider displays.
 */
@Composable
fun SliderDisplayPanelHSV(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float = .5f,
    @FloatRange(from = 0.0, to = 1.0) value: Float = .5f,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f,
    onHueChange: ((Float) -> Unit)? = null,
    onSaturationChange: ((Float) -> Unit)? = null,
    onValueChange: ((Float) -> Unit)? = null,
    onAlphaChange: ((Float) -> Unit)? = null,
) {
    Column(modifier) {

        onHueChange?.let { onHueChanged ->
            SliderDisplayHueHSV(
                modifier = Modifier,
                hue = hue,
                saturation = saturation,
                value = value,
                onValueChange = onHueChanged
            )
        }

        onSaturationChange?.let { onSaturationChanged ->
            SliderDisplaySaturationHSV(
                modifier = Modifier,
                hue = hue,
                saturation = saturation,
                value = value,
                onValueChange = onSaturationChanged
            )
        }

        onValueChange?.let { onValueChanged ->
            SliderDisplayValueHSV(
                modifier = Modifier,
                hue = hue,
                value = value,
                onValueChange = onValueChanged
            )
        }

        onAlphaChange?.let { onAlphaChanged ->
            SliderDisplayAlphaHSV(
                modifier = Modifier,
                hue = hue,
                alpha = alpha,
                onValueChange = onAlphaChanged
            )
        }
    }
}

/**
 * Composable that contains variant number of Slider displays with Title on left, [ColorfulSlider],
 * and current value from slider depending on **onChange** callbacks are null or not.
 *
 * Based on availability of callbacks order of sliders are placed in order from top to bottom
 * ```
 * Hue Slider Display
 * Saturation Slider Display
 * Lightness Slider Display
 * Alpha Slider Display
 * ```
 * @param hue Hue of HSL hue in [0..360f]
 * @param saturation Hue of HSL in [0..1f]
 * @param lightness Hue of HSL in [0..1f]
 * @param alpha Hue of HSL in [0..1f]
 * @param onHueChange lambda in which [hue] should be updated. when this lambda is not nul
 * [SliderDisplayHueHSL] is added to Column of sliders.
 * @param onSaturationChange lambda in which [saturation] should be updated. when this lambda is not nul
 * [[SliderDisplaySaturationHSV] is added to Column of sliders.
 * @param onLightnessChange lambda in which [lightness] should be updated. when this lambda is not nul
 * [[SliderDisplayLightnessHSL] is added to Column of sliders.
 * @param onAlphaChange lambda in which [alpha] should be updated. when this lambda is not nul
 * [SliderDisplayAlphaHSL] is added to Column of sliders.
 */
@Composable
fun SliderDisplayPanelHSL(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float = .5f,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float = .5f,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f,
    onHueChange: ((Float) -> Unit)? = null,
    onSaturationChange: ((Float) -> Unit)? = null,
    onLightnessChange: ((Float) -> Unit)? = null,
    onAlphaChange: ((Float) -> Unit)? = null,
) {
    Column(modifier) {

        onHueChange?.let { onHueChanged ->
            SliderDisplayHueHSL(
                modifier = Modifier,
                hue = hue,
                saturation = saturation,
                lightness = lightness,
                onValueChange = onHueChanged
            )
        }

        onSaturationChange?.let { onSaturationChanged ->
            SliderDisplaySaturationHSL(
                modifier = Modifier,
                hue = hue,
                saturation = saturation,
                lightness = lightness,
                onValueChange = onSaturationChanged
            )
        }

        onLightnessChange?.let { onValueChanged ->
            SliderDisplayLightnessHSL(
                modifier = Modifier,
                lightness = lightness,
                onValueChange = onValueChanged
            )
        }

        onAlphaChange?.let { onAlphaChanged ->
            SliderDisplayAlphaHSL(
                modifier = Modifier,
                hue = hue,
                alpha = alpha,
                onValueChange = onAlphaChanged
            )
        }
    }
}

/**
 * Composable that contains variant number of Slider displays with Title on left, [ColorfulSlider],
 * and current value from slider depending on **onChange** callbacks are null or not.
 *
 * Based on availability of callbacks order of sliders are placed in order from top to bottom
 * ```
 * Red Slider Display
 * Green Slider Display
 * Blue Slider Display
 * Alpha Slider Display
 * ```
 * @param red Red of RGBA  in [0..1f]
 * @param green Hue of RGBA  in [0..1f]
 * @param blue Hue of RGBA  in [0..1f]
 * @param alpha Hue of RGBA  in [0..1f]
 * @param onRedChange lambda in which [red] should be updated. when this lambda is not nul
 * [SliderDisplayHueHSL] is added to Column of sliders.
 * @param onGreenChange lambda in which [green] should be updated. when this lambda is not nul
 * [[SliderDisplaySaturationHSV] is added to Column of sliders.
 * @param onBlueChange lambda in which [blue] should be updated. when this lambda is not nul
 * [[SliderDisplayLightnessHSL] is added to Column of sliders.
 * @param onAlphaChange lambda in which [alpha] should be updated. when this lambda is not nul
 * [SliderDisplayAlphaHSL] is added to Column of sliders.
 */
@Composable
fun SliderDisplayPanelRGBA(
    modifier: Modifier,
    @FloatRange(from = 0.0, to = 1.0) red: Float = 1f,
    @FloatRange(from = 0.0, to = 1.0) green: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) blue: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f,
    onRedChange: ((Float) -> Unit)? = null,
    onGreenChange: ((Float) -> Unit)? = null,
    onBlueChange: ((Float) -> Unit)? = null,
    onAlphaChange: ((Float) -> Unit)? = null,
) {
    Column(modifier) {

        onRedChange?.let { onRedChanged ->
            SliderDisplayRedRGB(
                modifier = Modifier,
                red = red,
                onValueChange = onRedChanged
            )
        }

        onGreenChange?.let { onGreenChanged ->
            SliderDisplayGreenRGB(
                modifier = Modifier,
                green = green,
                onValueChange = onGreenChanged
            )
        }

        onBlueChange?.let { onBlueChanged ->
            SliderDisplayBlueRGB(
                modifier = Modifier,
                blue = blue,
                onValueChange = onBlueChanged
            )
        }

        onAlphaChange?.let { onAlphaChanged ->
            SliderDisplayAlphaRGB(
                modifier = Modifier,
                red = red,
                green = green,
                blue = blue,
                alpha = alpha,
                onValueChange = onAlphaChanged
            )
        }
    }
}
