package ru.tech.imageresizershrinker.coreui.widget.controls.draw

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.icons.material.Dots
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSliderItem

@Composable
fun BrushSoftnessSelector(
    modifier: Modifier,
    value: Float,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    onValueChange: (Float) -> Unit
) {
    EnhancedSliderItem(
        modifier = modifier,
        value = value,
        title = stringResource(R.string.brush_softness),
        valueRange = 0f..100f,
        onValueChange = {
            onValueChange(it.roundToTwoDigits())
        },
        valueSuffix = " Pt",
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Rounded.Dots,
        shape = RoundedCornerShape(24.dp),
        color = color
    )
}