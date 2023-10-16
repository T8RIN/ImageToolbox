package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSliderItem

@Composable
fun DrawAlphaSelector(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    EnhancedSliderItem(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        value = value,
        icon = Icons.Rounded.Opacity,
        title = stringResource(R.string.paint_alpha),
        sliderModifier = Modifier
            .padding(top = 14.dp, start = 12.dp, end = 12.dp, bottom = 10.dp),
        valueRange = 0.01f..1f,
        onValueChange = {
            onValueChange(it.roundToTwoDigits())
        },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    )
}