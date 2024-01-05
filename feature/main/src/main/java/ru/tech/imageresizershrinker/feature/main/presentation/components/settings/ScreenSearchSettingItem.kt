package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun ScreenSearchSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        applyHorPadding = false,
        resultModifier = Modifier.padding(
            end = 16.dp,
            top = 8.dp,
            bottom = 8.dp
        ),
        title = stringResource(R.string.search_option),
        startContent = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        subtitle = stringResource(R.string.search_option_sub),
        checked = settingsState.screensSearchEnabled,
        onClick = onClick
    )
}