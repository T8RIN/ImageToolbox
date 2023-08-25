package com.smarttoolfactory.colorpicker.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smarttoolfactory.colorpicker.model.ColorHSL
import com.smarttoolfactory.colorpicker.model.ColorHSV
import com.smarttoolfactory.colorpicker.model.ColorModel
import com.smarttoolfactory.colorpicker.model.ColorRGB
import com.smarttoolfactory.colorpicker.model.CompositeColor
import com.smarttoolfactory.extendedcolors.util.HSLUtil
import com.smarttoolfactory.extendedcolors.util.HSVUtil
import com.smarttoolfactory.extendedcolors.util.RGBUtil

/**
 *
 * Slider display panel that allows user to pick color between HSV, HSL or RGB color models using other
 * **CompositeSliderPanel**s.
 *
 * * Input color model determines in which color model this Composable
 * will use to calculate which depends on current Sliders of preference, if RGB color model is
 * selected then [inputColorModel] will also be [ColorModel.RGB]. This is for matching sliders
 * with color picked using them.
 *
 * * Output color model determines in which color model output will be. If this picker is using
 * **HSL** color model for saturation pickers and hue picker then output model should be
 * [ColorModel.HSL].
 *
 * @param compositeColor Composite color that can be HSV, HSL or RGB color model
 * @param onColorChange callback that gets triggered when any of the sliders contained is dragged
 * @param inputColorModel color model of [compositeColor]
 * @param outputColorModel color model of color as result of [onColorChange]
 * @param showAlphaSlider when set to true Sliders that display alpha are shown
 */
@Composable
fun CompositeSliderPanel(
    modifier: Modifier = Modifier,
    compositeColor: CompositeColor,
    onColorChange: (CompositeColor) -> Unit,
    inputColorModel: ColorModel,
    outputColorModel: ColorModel,
    showAlphaSlider: Boolean = true
) {

    val inputColor = convertColor(inputColorModel, compositeColor)

    when (inputColorModel) {
        ColorModel.HSL -> {
            CompositeSliderPanelHSL(
                modifier = modifier,
                compositeColor = inputColor as ColorHSL,
                onColorChange = {
                    onColorChange(convertColor(colorModel = outputColorModel, it))
                },
                showAlphaSlider = showAlphaSlider
            )
        }

        ColorModel.HSV -> {
            CompositeSliderPanelHSV(
                modifier = modifier,
                compositeColor = inputColor as ColorHSV,
                onColorChange = {
                    onColorChange(convertColor(colorModel = outputColorModel, it))
                },
                showAlphaSlider = showAlphaSlider
            )
        }

        ColorModel.RGB -> {
            CompositeSliderPanelRGB(
                modifier = modifier,
                compositeColor = inputColor as ColorRGB,
                onColorChange = {
                    onColorChange(convertColor(colorModel = outputColorModel, it))
                },
                showAlphaSlider = showAlphaSlider
            )
        }
    }
}

fun convertColor(colorModel: ColorModel, compositeColor: CompositeColor): CompositeColor {

    return when (colorModel) {
        ColorModel.HSL -> {

            when (compositeColor) {

                is ColorHSL -> {
                    compositeColor
                }

                is ColorHSV -> {
                    val hue = compositeColor.hue
                    val saturation = compositeColor.saturation
                    val value = compositeColor.value
                    val alpha = compositeColor.alpha
                    val hslArray = HSVUtil.hsvToHSL(hue, saturation = saturation, value = value)
                    ColorHSL(
                        hslArray[0],
                        hslArray[1],
                        hslArray[2],
                        alpha
                    )
                }

                is ColorRGB -> {
                    val red = compositeColor.red
                    val green = compositeColor.green
                    val blue = compositeColor.blue
                    val alpha = compositeColor.alpha

                    val rgbArray = RGBUtil.rgbFloatToHSL(red, green, blue)
                    ColorHSL(
                        rgbArray[0],
                        rgbArray[1],
                        rgbArray[2],
                        alpha
                    )
                }

                else -> ColorHSL.Unspecified
            }
        }

        ColorModel.HSV -> {

            when (compositeColor) {

                is ColorHSL -> {

                    val hue = compositeColor.hue
                    val saturation = compositeColor.saturation
                    val lightness = compositeColor.lightness
                    val alpha = compositeColor.alpha
                    val hsvArray =
                        HSLUtil.hslToHSV(hue = hue, saturation = saturation, lightness = lightness)
                    ColorHSV(
                        hsvArray[0],
                        hsvArray[1],
                        hsvArray[2],
                        alpha
                    )


                }

                is ColorHSV -> {
                    compositeColor
                }

                is ColorRGB -> {
                    val red = compositeColor.red
                    val green = compositeColor.green
                    val blue = compositeColor.blue
                    val alpha = compositeColor.alpha

                    val rgbArray = RGBUtil.rgbFloatToHSV(red, green, blue)
                    ColorHSV(
                        rgbArray[0],
                        rgbArray[1],
                        rgbArray[2],
                        alpha
                    )
                }

                else -> ColorHSV.Unspecified
            }

        }

        ColorModel.RGB -> {

            when (compositeColor) {

                is ColorHSL -> {

                    val hue = compositeColor.hue
                    val saturation = compositeColor.saturation
                    val lightness = compositeColor.lightness
                    val alpha = compositeColor.alpha

                    val rgbArray =
                        HSLUtil.hslToRGBFloat(hue = hue, saturation = saturation, lightness)
                    ColorRGB(
                        rgbArray[0],
                        rgbArray[1],
                        rgbArray[2],
                        alpha
                    )
                }

                is ColorHSV -> {

                    val hue = compositeColor.hue
                    val saturation = compositeColor.saturation
                    val value = compositeColor.value
                    val alpha = compositeColor.alpha

                    val rgbArray = HSVUtil.hsvToRGBFloat(hue, saturation, value)
                    ColorRGB(
                        rgbArray[0],
                        rgbArray[1],
                        rgbArray[2],
                        alpha
                    )

                }

                is ColorRGB -> {
                    compositeColor
                }

                else -> ColorRGB.Unspecified
            }
        }

    }
}

@Composable
fun CompositeSliderPanelHSL(
    modifier: Modifier,
    compositeColor: ColorHSL,
    onColorChange: (ColorHSL) -> Unit,
    showAlphaSlider: Boolean
) {

    val hue = compositeColor.hue
    val saturation = compositeColor.saturation
    val lightness = compositeColor.lightness
    val alpha = compositeColor.alpha

    val alphaLambda: ((Float) -> Unit)? = if (showAlphaSlider) { alphaChange ->
        onColorChange(
            ColorHSL(
                hue = hue,
                saturation = saturation,
                lightness = lightness,
                alpha = alphaChange,
            )
        )

    } else null

    SliderDisplayPanelHSL(
        modifier = modifier,
        hue = hue,
        saturation = saturation,
        lightness = lightness,
        alpha = alpha,
        onHueChange = { hueChange ->
            onColorChange(
                ColorHSL(
                    hue = hueChange,
                    saturation = saturation,
                    lightness = lightness,
                    alpha = alpha,
                )
            )
        },
        onSaturationChange = { saturationChange ->
            onColorChange(
                ColorHSL(
                    hue = hue,
                    saturation = saturationChange,
                    lightness = lightness,
                    alpha = alpha,
                )
            )
        },
        onLightnessChange = { lightnessChange ->
            onColorChange(
                ColorHSL(
                    hue = hue,
                    saturation = saturation,
                    lightness = lightnessChange,
                    alpha = alpha,
                )
            )
        },
        onAlphaChange = alphaLambda,
    )
}

@Composable
fun CompositeSliderPanelHSV(
    modifier: Modifier,
    compositeColor: ColorHSV,
    onColorChange: (ColorHSV) -> Unit,
    showAlphaSlider: Boolean
) {

    val hue = compositeColor.hue
    val saturation = compositeColor.saturation
    val value = compositeColor.value
    val alpha = compositeColor.alpha

    val alphaLambda: ((Float) -> Unit)? = if (showAlphaSlider) { alphaChange ->
        onColorChange(
            ColorHSV(
                hue = hue,
                saturation = saturation,
                value = value,
                alpha = alphaChange,
            )
        )

    } else null

    SliderDisplayPanelHSV(
        modifier = modifier,
        hue = hue,
        saturation = saturation,
        value = value,
        alpha = alpha,
        onHueChange = { hueChange ->
            onColorChange(
                ColorHSV(
                    hue = hueChange,
                    saturation = saturation,
                    value = value,
                    alpha = alpha,
                )
            )
        },
        onSaturationChange = { saturationChange ->
            onColorChange(
                ColorHSV(
                    hue = hue,
                    saturation = saturationChange,
                    value = value,
                    alpha = alpha,
                )
            )
        },
        onValueChange = { onValueChange ->
            onColorChange(
                ColorHSV(
                    hue = hue,
                    saturation = saturation,
                    value = onValueChange,
                    alpha = alpha,
                )
            )
        },
        onAlphaChange = alphaLambda,
    )
}

@Composable
fun CompositeSliderPanelRGB(
    modifier: Modifier,
    compositeColor: ColorRGB,
    onColorChange: (ColorRGB) -> Unit,
    showAlphaSlider: Boolean
) {

    val red = compositeColor.red
    val green = compositeColor.green
    val blue = compositeColor.blue
    val alpha = compositeColor.alpha

    val alphaLambda: ((Float) -> Unit)? = if (showAlphaSlider) { alphaChange ->
        onColorChange(
            ColorRGB(
                red = red,
                green = green,
                blue = blue,
                alpha = alphaChange,
            )
        )

    } else null

    SliderDisplayPanelRGBA(
        modifier = modifier,
        red = red,
        green = green,
        blue = blue,
        alpha = alpha,
        onRedChange = { redChange ->
            onColorChange(
                ColorRGB(
                    red = redChange,
                    green = green,
                    blue = blue,
                    alpha = alpha,
                )
            )
        },
        onGreenChange = { greenChange ->
            onColorChange(
                ColorRGB(
                    red = red,
                    green = greenChange,
                    blue = blue,
                    alpha = alpha,
                )
            )
        },
        onBlueChange = { blueChange ->
            onColorChange(
                ColorRGB(
                    red = red,
                    green = green,
                    blue = blueChange,
                    alpha = alpha,
                )
            )
        },
        onAlphaChange = alphaLambda,
    )
}
