package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoDelete
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
fun AutoCacheClearSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        resultModifier = Modifier.padding(
            top = 8.dp,
            bottom = 8.dp,
            end = 16.dp
        ),
        applyHorPadding = false,
        title = stringResource(R.string.auto_cache_clearing),
        subtitle = stringResource(R.string.auto_cache_clearing_sub),
        checked = settingsState.clearCacheOnLaunch,
        startContent = {
            Icon(
                imageVector = Icons.Outlined.AutoDelete,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        onClick = onClick
    )
}