package com.smarttoolfactory.colorpicker.selector.gradient

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.ui.Grey400
import com.smarttoolfactory.extendedcolors.util.fractionToIntPercent
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor
import com.smarttoolfactory.slider.ui.InactiveTrackColor

@Composable
internal fun SliderWithPercent(
    modifier: Modifier = Modifier,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.width(40.dp),
            text = title,
            color = Grey400,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(10.dp))
        ColorfulSlider(
            modifier = Modifier.weight(1f),
            value = value,
            valueRange = valueRange,
            onValueChange = onValueChange,
            thumbRadius = 10.dp,
            trackHeight = 8.dp,
            colors = MaterialSliderDefaults.materialColors(
                inactiveTrackColor = SliderBrushColor(InactiveTrackColor)
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "${value.fractionToIntPercent()}%",
            fontSize = 12.sp,
            color = Grey400,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp)
        )
    }
}