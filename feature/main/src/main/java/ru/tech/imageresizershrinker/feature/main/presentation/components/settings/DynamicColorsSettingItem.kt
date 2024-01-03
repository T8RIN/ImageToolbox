package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun DynamicColorsSettingItem(
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp),
    shape: Shape = ContainerShapeDefaults.centerShape
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        modifier = modifier,
        shape = shape,
        applyHorPadding = false,
        startContent = {
            Icon(
                imageVector = Icons.Outlined.FormatColorFill,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        resultModifier = Modifier.padding(
            end = 16.dp,
            top = 8.dp,
            bottom = 8.dp
        ),
        title = stringResource(R.string.dynamic_colors),
        subtitle = stringResource(R.string.dynamic_colors_sub),
        checked = settingsState.isDynamicColors,
        onClick = onClick
    )
}