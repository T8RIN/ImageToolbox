package com.smarttoolfactory.colorpicker.picker

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
import com.smarttoolfactory.colorpicker.model.ColorModel
import com.smarttoolfactory.colorpicker.selector.SelectorRectHueValueHSV
import com.smarttoolfactory.colorpicker.slider.SliderCircleColorDisplaySaturationHSV
import com.smarttoolfactory.colorpicker.widget.ColorDisplayExposedSelectionMenu
import com.smarttoolfactory.extendedcolors.util.ColorUtil

@Composable
fun ColorPickerRectHueValueHSV(
    modifier: Modifier = Modifier,
    selectionRadius: Dp = 8.dp,
    initialColor: Color,
    onColorChange: (Color, String) -> Unit
) {

    val hsvArray = ColorUtil.colorToHSV(initialColor)

    var hue by remember { mutableStateOf(hsvArray[0]) }
    var saturation by remember { mutableStateOf(hsvArray[1]) }
    var value by remember { mutableStateOf(hsvArray[2]) }
    var alpha by remember { mutableStateOf(initialColor.alpha) }

    val currentColor =
        Color.hsv(hue = hue, saturation = saturation, value = value, alpha = alpha)

    var colorModel by remember { mutableStateOf(ColorModel.HSV) }

    onColorChange(currentColor, ColorUtil.colorToHexAlpha(currentColor))

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectorRectHueValueHSV(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 3f),
            hue = hue,
            value = value,
            selectionRadius = selectionRadius
        ) { h, v ->
            hue = h
            value = v
        }

        Column(modifier = Modifier.padding(8.dp)) {
            SliderCircleColorDisplaySaturationHSV(
                hue = hue,
                saturation = saturation,
                value = value,
                alpha = alpha,
                onSaturationChange = {
                    saturation = it
                },
                onAlphaChange = {
                    alpha = it
                }
            )

            ColorDisplayExposedSelectionMenu(
                color = currentColor,
                colorModel = colorModel,
                onColorModelChange = {
                    colorModel = it
                }
            )
        }
    }
}