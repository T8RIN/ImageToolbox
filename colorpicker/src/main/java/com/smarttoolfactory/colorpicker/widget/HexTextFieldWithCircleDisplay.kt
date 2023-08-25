package com.smarttoolfactory.colorpicker.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colorpicker.slider.CircleDisplay
import com.smarttoolfactory.extendedcolors.util.ColorUtil


@Composable
fun HexTextFieldWithCircleDisplay(
    modifier: Modifier = Modifier,
    circleModifier: Modifier = Modifier,
    color: Color,
    useAlpha: Boolean = false,
    onColorChange: (Color) -> Unit
) {

    var hexString by remember(color) {
        mutableStateOf(
            if (useAlpha) {
                ColorUtil.colorToHexAlpha(color)
            } else {
                ColorUtil.colorToHex(color)
            }
        )
    }

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
        HexTextField(
            modifier = Modifier.weight(1f),
            useAlpha = useAlpha,
            hexString = hexString,
            onTextChange = {
                hexString = it
            },
            onColorChange = onColorChange
        )
    }
}