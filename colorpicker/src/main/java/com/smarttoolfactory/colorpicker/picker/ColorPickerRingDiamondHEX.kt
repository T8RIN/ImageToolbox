package com.smarttoolfactory.colorpicker.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.model.ColorHSL
import com.smarttoolfactory.colorpicker.model.ColorModel
import com.smarttoolfactory.colorpicker.selector.SelectorDiamondSaturationLightnessHSL
import com.smarttoolfactory.colorpicker.selector.SelectorRingHue
import com.smarttoolfactory.colorpicker.slider.CompositeSliderPanel
import com.smarttoolfactory.colorpicker.ui.Grey400
import com.smarttoolfactory.colorpicker.ui.Grey600
import com.smarttoolfactory.colorpicker.widget.ColorDisplayRoundedRect
import com.smarttoolfactory.colorpicker.widget.ColorModelChangeTabRow
import com.smarttoolfactory.colorpicker.widget.HexTextField
import com.smarttoolfactory.extendedcolors.util.ColorUtil

/**
 * ColorPicker with [SelectorRingHue] hue selector and [SelectorDiamondSaturationLightnessHSL]
 * saturation lightness Selector uses [HSL](https://en.wikipedia.org/wiki/HSL_and_HSV)
 * color model as base.
 *
 * This color picker has tabs section that can be changed between
 * HSL, HSV and RGB color models and color can be set using [CompositeSliderPanel] which contains
 * sliders for each color models.
 *
 * @param initialColor color that is passed to this picker initially.
 * @param ringOuterRadiusFraction outer radius of [SelectorRingHue].
 * @param ringInnerRadiusFraction inner radius of [SelectorRingHue].
 * @param ringBackgroundColor background from center to inner radius of [SelectorRingHue].
 * @param ringBorderStrokeColor stroke color for drawing borders around inner or outer radius.
 * @param ringBorderStrokeWidth stroke width of borders.
 * @param selectionRadius radius of white and black circle selector.
 * @param onColorChange callback that is triggered when [Color] is changed using [SelectorRingHue],
 * [SelectorDiamondSaturationLightnessHSL] or [CompositeSliderPanel]
 */
@Composable
fun ColorPickerRingDiamondHEX(
    modifier: Modifier = Modifier,
    initialColor: Color,
    ringOuterRadiusFraction: Float = .9f,
    ringInnerRadiusFraction: Float = .6f,
    ringBackgroundColor: Color = Color.Black,
    ringBorderStrokeColor: Color = Color.Black,
    ringBorderStrokeWidth: Dp = 4.dp,
    selectionRadius: Dp = 8.dp,
    onColorChange: (Color, String) -> Unit
) {

    var inputColorModel by remember { mutableStateOf(ColorModel.HSL) }

    val hslArray = ColorUtil.colorToHSL(initialColor)

    var hue by remember { mutableStateOf(hslArray[0]) }
    var saturation by remember { mutableStateOf(hslArray[1]) }
    var lightness by remember { mutableStateOf(hslArray[2]) }
    var alpha by remember { mutableStateOf(initialColor.alpha) }

    val currentColor =
        Color.hsl(hue = hue, saturation = saturation, lightness = lightness, alpha = alpha)

    var hexString by remember {
        mutableStateOf(
            ColorUtil.colorToHexAlpha(
                currentColor
            )
        )
    }

    onColorChange(currentColor, ColorUtil.colorToHexAlpha(currentColor))

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        // Initial and Current Colors
        ColorDisplayRoundedRect(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 10.dp),
            initialColor = initialColor,
            currentColor = currentColor
        )

        Box(contentAlignment = Alignment.Center) {

            // Ring Shaped Hue Selector
            SelectorRingHue(
                modifier = Modifier.fillMaxWidth(1f),
                hue = hue,
                outerRadiusFraction = ringOuterRadiusFraction,
                innerRadiusFraction = ringInnerRadiusFraction,
                backgroundColor = ringBackgroundColor,
                borderStrokeColor = ringBorderStrokeColor,
                borderStrokeWidth = ringBorderStrokeWidth,
                selectionRadius = selectionRadius
            ) { hueChange ->
                hue = hueChange
                hexString = ColorUtil.colorToHexAlpha(
                    Color.hsl(
                        hue = hue,
                        saturation = saturation,
                        lightness = lightness,
                        alpha = alpha
                    )
                )
            }

            // Diamond Shaped Saturation and Lightness Selector
            SelectorDiamondSaturationLightnessHSL(
                modifier = Modifier.fillMaxWidth(ringInnerRadiusFraction * .9f),
                hue = hue,
                saturation = saturation,
                lightness = lightness,
                selectionRadius = selectionRadius
            ) { s, l ->
                saturation = s
                lightness = l
                hexString = ColorUtil.colorToHexAlpha(
                    Color.hsl(
                        hue = hue,
                        saturation = saturation,
                        lightness = lightness,
                        alpha = alpha
                    )
                )
            }
        }

        // HSL-HSV-RGB Color Model Change Tabs
        ColorModelChangeTabRow(
            modifier = Modifier.width(350.dp),
            colorModel = inputColorModel,
            onColorModelChange = {
                inputColorModel = it
            }
        )
        // HSL-HSV-RGB Sliders
        CompositeSliderPanel(
            modifier = Modifier.padding(start = 10.dp, end = 7.dp),
            compositeColor = ColorHSL(
                hue = hue,
                saturation = saturation,
                lightness = lightness,
                alpha = alpha
            ),
            onColorChange = {
                (it as? ColorHSL)?.let { color ->
                    hue = color.hue
                    saturation = color.saturation
                    lightness = color.lightness
                    alpha = color.alpha

                    hexString = ColorUtil.colorToHexAlpha(
                        Color.hsl(
                            hue = hue,
                            saturation = saturation,
                            lightness = lightness,
                            alpha = alpha
                        )
                    )
                }
            },
            showAlphaSlider = true,
            inputColorModel = inputColorModel,
            outputColorModel = ColorModel.HSL
        )

        HexTextField(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            hexString = hexString,
            useAlpha = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Grey400,
                placeholderColor = Grey600,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onTextChange = {
                hexString = it
            },
            onColorChange = {
                val hslArrayNew = ColorUtil.colorToHSL(it)
                hue = hslArrayNew[0]
                saturation = hslArrayNew[1]
                lightness = hslArrayNew[2]
                alpha = it.alpha
            }
        )
    }
}
