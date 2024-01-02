package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.model.NightMode
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun NightModeSettingItemGroup(
    value: NightMode,
    onValueChange: (NightMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val settingsState = LocalSettingsState.current
        listOf(
            Triple(
                stringResource(R.string.dark),
                Icons.Outlined.DarkMode,
                NightMode.Dark
            ),
            Triple(
                stringResource(R.string.light),
                Icons.Outlined.LightMode,
                NightMode.Light
            ),
            Triple(
                stringResource(R.string.system),
                Icons.Outlined.SettingsSuggest,
                NightMode.System
            ),
        ).forEachIndexed { index, (title, icon, nightMode) ->
            val selected = nightMode == value
            val shape = ContainerShapeDefaults.shapeForIndex(index, 3)
            PreferenceItem(
                onClick = { onValueChange(nightMode) },
                title = title,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = animateFloatAsState(
                        if (selected) 0.7f
                        else 0.2f
                    ).value
                ),
                shape = shape,
                icon = icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (selected) MaterialTheme.colorScheme
                                .onSecondaryContainer
                                .copy(alpha = 0.5f)
                            else Color.Transparent
                        ).value,
                        shape = shape
                    ),
                endIcon = if (selected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked
            )
        }
    }
}