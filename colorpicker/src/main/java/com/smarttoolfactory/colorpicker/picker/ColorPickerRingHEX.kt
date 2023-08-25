package com.smarttoolfactory.colorpicker.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.smarttoolfactory.colorpicker.selector.SelectorRectSaturationValueHSV
import com.smarttoolfactory.colorpicker.selector.SelectorRingHue
import com.smarttoolfactory.colorpicker.slider.CompositeSliderPanel
import com.smarttoolfactory.colorpicker.widget.HexTextFieldWithCircleDisplay
import com.smarttoolfactory.extendedcolors.util.ColorUtil


/**
 * ColorPicker with [SelectorRingHue] hue selector and [SelectorRectSaturationValueHSV]
 * saturation lightness Selector that uses [HSV](https://en.wikipedia.org/wiki/HSL_and_HSV)
 * color model as base.
 * This color picker has tabs section that can be changed between
 * HSL, HSV and RGB color models and color can be set using [CompositeSliderPanel] which contains
 * sliders for each color models.
 *
 * @param initialColor color that is passed to this picker initially.
 * @param selectionRadius radius of white and black circle selector.
 * @param onColorChange callback that is triggered when [Color] is changed using [SelectorRingHue],
 * [SelectorRectSaturationValueHSV] or [CompositeSliderPanel]
 */
@Composable
fun ColorPickerRingRectHex(
    modifier: Modifier = Modifier,
    selectionRadius: Dp = 8.dp,
    initialColor: Color,
    onColorChange: (Color, String) -> Unit
) {
    val hsvArray = ColorUtil.colorToHSV(initialColor)

    var hue by remember { mutableStateOf(hsvArray[0]) }
    var saturation by remember { mutableStateOf(hsvArray[1]) }
    var value by remember { mutableStateOf(hsvArray[2]) }

    val currentColor = Color.hsv(
        hue = hue,
        saturation = saturation,
        value = value
    )

    onColorChange(currentColor, ColorUtil.colorToHexAlpha(currentColor))

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(contentAlignment = Alignment.Center) {

            // Ring Shaped Hue Selector
            SelectorRingHue(
                modifier = Modifier.fillMaxWidth(1f),
                hue = hue,
                outerRadiusFraction = .95f,
                innerRadiusFraction = .75f,
                backgroundColor = Color.Transparent,
                borderStrokeColor = Color.Transparent,
                borderStrokeWidth = 0.dp,
                selectionRadius = selectionRadius,
            ) { hueChange ->
                hue = hueChange
            }

            // Rect Shaped Saturation and Lightness Selector
            SelectorRectSaturationValueHSV(
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .aspectRatio(1f),
                hue = hue,
                saturation = saturation,
                value = value,
                selectionRadius = selectionRadius
            ) { s, v ->
                saturation = s
                value = v
            }
        }

        HexTextFieldWithCircleDisplay(
            modifier = Modifier.padding(8.dp),
            color = currentColor,
            onColorChange = {
                val hsvArrayNew = ColorUtil.colorToHSV(it)
                hue = hsvArrayNew[0]
                saturation = hsvArrayNew[1]
                value = hsvArrayNew[2]
            }
        )
    }
}
