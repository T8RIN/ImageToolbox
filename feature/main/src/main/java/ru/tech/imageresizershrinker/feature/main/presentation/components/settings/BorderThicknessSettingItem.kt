package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.round
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun BorderThicknessSettingItem(
    updateBorderWidth: (Float) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    EnhancedSliderItem(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        valueSuffix = " Dp",
        value = settingsState.borderWidth.value.coerceAtLeast(0f),
        title = stringResource(R.string.border_thickness),
        icon = Icons.Outlined.BorderStyle,
        onValueChange = {},
        internalStateTransformation = {
            (it * 10).round() / 10f
        },
        onValueChangeFinished = {
            updateBorderWidth((it * 10).round() / 10f)
        },
        valueRange = 0f..1.5f,
        steps = 14
    )
}