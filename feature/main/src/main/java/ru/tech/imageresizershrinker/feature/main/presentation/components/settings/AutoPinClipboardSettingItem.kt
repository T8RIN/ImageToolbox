package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
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
fun AutoPinClipboardSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.defaultShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        modifier = modifier,
        enabled = settingsState.borderWidth <= 0.dp,
        shape = shape,
        title = stringResource(R.string.auto_pin),
        subtitle = stringResource(R.string.auto_pin_sub),
        checked = settingsState.autoCopyToClipBoard,
        onClick = onClick,
        startIcon = Icons.Outlined.PushPin
    )
}