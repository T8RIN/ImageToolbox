package ru.tech.imageresizershrinker.feature.crop.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ScreenRotationAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
fun CropRotationSelector(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedSliderItem(
        value = value,
        title = stringResource(R.string.rotation),
        modifier = modifier,
        icon = Icons.Rounded.ScreenRotationAlt,
        valueRange = -45f..45f,
        internalStateTransformation = { it.roundTo(1) },
        onValueChange = onValueChange
    )
}