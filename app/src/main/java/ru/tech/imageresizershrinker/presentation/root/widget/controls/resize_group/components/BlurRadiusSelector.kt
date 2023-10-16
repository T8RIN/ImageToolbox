package ru.tech.imageresizershrinker.presentation.root.widget.controls.resize_group.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSliderItem
import kotlin.math.roundToInt

@Composable
fun BlurRadiusSelector(
    modifier: Modifier,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    EnhancedSliderItem(
        modifier = modifier,
        value = value,
        title = stringResource(R.string.blur_radius),
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Rounded.BlurCircular,
        valueRange = 5f..100f,
        onValueChange = {
            onValueChange(it.roundToInt())
        },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    )
}