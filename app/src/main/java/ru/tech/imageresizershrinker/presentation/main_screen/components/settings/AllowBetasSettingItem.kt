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
import ru.tech.imageresizershrinker.presentation.root.icons.material.Beta
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun AllowBetasSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = SettingsShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        modifier = modifier,
        applyHorPadding = false,
        resultModifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        shape = shape,
        title = stringResource(R.string.allow_betas),
        subtitle = stringResource(R.string.allow_betas_sub),
        checked = settingsState.allowBetas,
        onClick = onClick,
        startContent = {
            Icon(
                Icons.Rounded.Beta,
                null,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}