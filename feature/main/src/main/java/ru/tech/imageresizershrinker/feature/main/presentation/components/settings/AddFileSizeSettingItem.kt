package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hexagon
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
fun AddFileSizeSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        resultModifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        enabled = !settingsState.randomizeFilename && !settingsState.overwriteFiles,
        startContent = {
            Icon(
                imageVector = Icons.Outlined.Hexagon,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        applyHorPadding = false,
        onClick = onClick,
        title = stringResource(R.string.add_file_size),
        subtitle = stringResource(R.string.add_file_size_sub),
        checked = settingsState.addSizeInFilename
    )
}