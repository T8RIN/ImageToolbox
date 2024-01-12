package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun UsePixelSwitchSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        modifier = modifier,
        enabled = settingsState.borderWidth <= 0.dp,
        shape = shape,
        title = stringResource(R.string.use_pixel_switch),
        subtitle = stringResource(R.string.use_pixel_switch_sub),
        checked = settingsState.usePixelSwitch,
        onClick = onClick,
        startIcon = Icons.Outlined.Android
    )
}