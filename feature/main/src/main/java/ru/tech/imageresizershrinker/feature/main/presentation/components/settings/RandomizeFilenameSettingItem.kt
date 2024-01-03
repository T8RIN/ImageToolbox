package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.icons.material.Symbol
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun RandomizeFilenameSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        applyHorPadding = false,
        enabled = !settingsState.overwriteFiles,
        resultModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
        title = stringResource(R.string.randomize_filename),
        subtitle = stringResource(R.string.randomize_filename_sub),
        checked = settingsState.randomizeFilename,
        startContent = {
            Icon(
                Icons.Rounded.Symbol,
                null,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}