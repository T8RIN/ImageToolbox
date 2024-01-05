package com.smarttoolfactory.colorpicker.picker.gradient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.smarttoolfactory.colorpicker.model.BrushColor
import com.smarttoolfactory.colorpicker.model.ColorHSL
import com.smarttoolfactory.colorpicker.model.ColorMode
import com.smarttoolfactory.colorpicker.model.ColorModel
import com.smarttoolfactory.colorpicker.model.GradientColorState
import com.smarttoolfactory.colorpicker.model.rememberGradientColorState
import com.smarttoolfactory.colorpicker.selector.SelectorDiamondSaturationLightnessHSL
import com.smarttoolfactory.colorpicker.selector.SelectorRingHue
import com.smarttoolfactory.colorpicker.selector.gradient.BrushDisplay
import com.smarttoolfactory.colorpicker.selector.gradient.GradientSelector
import com.smarttoolfactory.colorpicker.slider.CompositeSliderPanel
import com.smarttoolfactory.colorpicker.widget.ColorDisplayRoundedRect
import com.smarttoolfactory.colorpicker.widget.ColorGradientModeChangeTabRow
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
 * @param initialBrushColor [BrushColor] that is passed to this picker initially.
 * @param ringOuterRadiusFraction outer radius of [SelectorRingHue].
 * @param ringInnerRadiusFraction inner radius of [SelectorRingHue].
 * @param ringBackgroundColor background from center to inner radius of [SelectorRingHue].
 * @param ringBorderStrokeColor stroke color for drawing borders around inner or outer radius.
 * @param ringBorderStrokeWidth stroke width of borders.
 * @param selectionRadius radius of white and black circle selector.
 * @param onBrushColorChange callback that is triggered when [Color] is changed using [SelectorRingHue],
 * [SelectorDiamondSaturationLightnessHSL] or [CompositeSliderPanel]
 */
@Composable
fun ColorPickerGradientRingDiamondHSL(
    modifier: Modifier = Modifier,
    initialBrushColor: BrushColor,
    gradientColorState: GradientColorState = rememberGradientColorState(),
    ringOuterRadiusFraction: Float = .9f,
    ringInnerRadiusFraction: Float = .6f,
    ringBackgroundColor: Color = Color.Black,
    ringBorderStrokeColor: Color = Color.Black,
    ringBorderStrokeWidth: Dp = 4.dp,
    selectionRadius: Dp = 8.dp,
    onBrushColorChange: (BrushColor) -> Unit
) {

    var inputColorModel by remember { mutableStateOf(ColorModel.HSL) }

    var colorMode by remember {
        mutableStateOf(
            if (initialBrushColor.brush != null) ColorMode.Gradient else ColorMode.HSL
        )
    }

    // Hue, Saturation, Lightness and Alpha properties
    val hslArray = remember { ColorUtil.colorToHSL(gradientColorState.color) }
    var hue by remember { mutableStateOf(hslArray[0]) }
    var saturation by remember { mutableStateOf(hslArray[1]) }
    var lightness by remember { mutableStateOf(hslArray[2]) }
    var alpha by remember { mutableStateOf(gradientColorState.color.alpha) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        // Initial-Current Colors/Gradient Color
        Box(modifier = Modifier.height(80.dp), contentAlignment = Alignment.Center) {
            when (colorMode) {
                ColorMode.Gradient -> BrushDisplay(gradientColorState = gradientColorState)
                else -> ColorDisplayRoundedRect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp, vertical = 10.dp),
                    initialColor = initialBrushColor.color,
                    currentColor = gradientColorState.color
                )
            }
        }

        Box(contentAlignment = Alignment.Center) {

            // Ring Shaped Hue Selector
            SelectorRingHue(
                modifier = Modifier.fillMaxWidth(.9f),
                hue = hue,
                outerRadiusFraction = ringOuterRadiusFraction,
                innerRadiusFraction = ringInnerRadiusFraction,
                backgroundColor = ringBackgroundColor,
                borderStrokeColor = ringBorderStrokeColor,
                borderStrokeWidth = ringBorderStrokeWidth,
                selectionRadius = selectionRadius
            ) { hueChange ->
                hue = hueChange

                setGradientColor(gradientColorState, hue, saturation, lightness, alpha)
                onBrushColorChange(BrushColor(color = gradientColorState.color))
            }

            // Diamond Shaped Saturation and Lightness Selector
            SelectorDiamondSaturationLightnessHSL(
                modifier = Modifier.fillMaxWidth(ringInnerRadiusFraction * .8f),
                hue = hue,
                saturation = saturation,
                lightness = lightness,
                selectionRadius = selectionRadius
            ) { s, l ->
                saturation = s
                lightness = l

                setGradientColor(gradientColorState, hue, saturation, lightness, alpha)
                onBrushColorChange(BrushColor(color = gradientColorState.color))
            }
        }

        // HSL-HSV-RGB-Gradient Color Model Change Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorGradientModeChangeTabRow(
                colorMode = colorMode,
                onColorModeChange = {
                    colorMode = it
                    when (colorMode) {
                        ColorMode.HSL -> {
                            inputColorModel = ColorModel.HSL
                        }

                        ColorMode.HSV -> {
                            inputColorModel = ColorModel.HSV
                        }

                        ColorMode.RGB -> {
                            inputColorModel = ColorModel.RGB
                        }

                        else -> Unit
                    }
                }
            )
        }

        // HSL-HSV-RGB Sliders
        when (colorMode) {
            ColorMode.Gradient -> {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    GradientSelector(
                        color = gradientColorState.color,
                        gradientColorState = gradientColorState
                    ) {
                        onBrushColorChange(BrushColor(brush = it))
                    }
                }
            }

            else -> {
                CompositeSliderPanel(
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

                            setGradientColor(gradientColorState, hue, saturation, lightness, alpha)
                            onBrushColorChange(BrushColor(color = gradientColorState.color))
                        }
                    },
                    showAlphaSlider = true,
                    inputColorModel = inputColorModel,
                    outputColorModel = ColorModel.HSL
                )
            }
        }
    }
}

private fun setGradientColor(
    gradientColorState: GradientColorState,
    hue: Float,
    saturation: Float,
    lightness: Float,
    alpha: Float
) {
    gradientColorState.color =
        Color.hsl(
            hue = hue,
            saturation = saturation,
            lightness = lightness,
            alpha = alpha
        )
}

