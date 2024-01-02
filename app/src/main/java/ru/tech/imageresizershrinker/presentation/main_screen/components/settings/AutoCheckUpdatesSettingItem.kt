package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun AutoCheckUpdatesSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = if (!LocalContext.current.isInstalledFromPlayStore()) {
        ContainerShapeDefaults.topShape
    } else ContainerShapeDefaults.defaultShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        applyHorPadding = false,
        resultModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        title = stringResource(R.string.check_updates),
        subtitle = stringResource(R.string.check_updates_sub),
        checked = settingsState.showDialogOnStartup,
        onClick = onClick,
        startContent = {
            Icon(
                Icons.Outlined.NewReleases,
                null,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}