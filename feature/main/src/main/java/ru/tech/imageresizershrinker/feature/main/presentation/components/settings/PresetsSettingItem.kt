package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun PresetsSettingItem(
    shape: Shape = ContainerShapeDefaults.defaultShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val editPresetsState = LocalEditPresetsState.current
    val settingsState = LocalSettingsState.current
    PreferenceItem(
        shape = shape,
        onClick = {
            editPresetsState.value = true
        },
        title = stringResource(R.string.values),
        subtitle = settingsState.presets.joinToString(", "),
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        icon = Icons.Outlined.Numbers,
        endIcon = Icons.Rounded.CreateAlt,
        modifier = modifier
    )
}