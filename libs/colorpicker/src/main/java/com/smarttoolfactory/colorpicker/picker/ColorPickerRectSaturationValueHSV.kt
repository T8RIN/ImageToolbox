package com.smarttoolfactory.colorpicker.picker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.selector.SelectorRectSaturationValueHSV
import com.smarttoolfactory.colorpicker.slider.SliderCircleColorDisplayHueHSV
import com.smarttoolfactory.extendedcolors.util.ColorUtil

@Composable
fun ColorPickerRectSaturationValueHSV(
    modifier: Modifier = Modifier,
    selectionRadius: Dp = 8.dp,
    initialColor: Color,
    enableAlpha: Boolean,
    onColorChange: (Color, String) -> Unit,
) {

    val hsvArray = ColorUtil.colorToHSV(initialColor)

    var hue by remember { mutableFloatStateOf(hsvArray[0]) }
    var saturation by remember { mutableFloatStateOf(hsvArray[1]) }
    var value by remember { mutableFloatStateOf(hsvArray[2]) }
    var alpha by remember { mutableFloatStateOf(initialColor.alpha) }

    val currentColor =
        Color.hsv(hue = hue, saturation = saturation, value = value, alpha = alpha)

    onColorChange(currentColor, ColorUtil.colorToHexAlpha(currentColor))

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectorRectSaturationValueHSV(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            hue = hue,
            saturation = saturation,
            value = value,
            selectionRadius = selectionRadius
        ) { s, v ->
            saturation = s
            value = v
        }

        Column(modifier = Modifier.padding(8.dp)) {
            SliderCircleColorDisplayHueHSV(
                hue = hue,
                saturation = saturation,
                value = value,
                alpha = alpha,
                enableAlpha = enableAlpha,
                onHueChange = {
                    hue = it
                },
                onAlphaChange = {
                    alpha = it
                }
            )
        }
    }
}