package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.Numeric
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ReplaceSequenceNumberSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = SettingsShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        applyHorPadding = false,
        onClick = onClick,
        title = stringResource(R.string.replace_sequence_number),
        subtitle = stringResource(R.string.replace_sequence_number_sub),
        checked = settingsState.addSequenceNumber,
        resultModifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        startContent = {
            Icon(
                imageVector = Icons.Filled.Numeric,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}