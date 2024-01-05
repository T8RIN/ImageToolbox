package ru.tech.imageresizershrinker.feature.image_stitch.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatLineSpacing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSliderItem
import kotlin.math.roundToInt

@Composable
fun SpacingSelector(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    EnhancedSliderItem(
        modifier = modifier,
        value = value,
        title = stringResource(R.string.spacing),
        valueRange = -256f..256f,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChange = {
            onValueChange(it.roundToInt())
        },
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Rounded.FormatLineSpacing,
        shape = RoundedCornerShape(24.dp)
    )
}