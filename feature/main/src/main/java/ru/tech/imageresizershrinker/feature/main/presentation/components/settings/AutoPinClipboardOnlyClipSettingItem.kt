package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SaveAs
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState


@Composable
fun AutoPinClipboardOnlyClipSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        modifier = modifier,
        enabled = settingsState.copyToClipboardMode is CopyToClipboardMode.Enabled,
        shape = shape,
        title = stringResource(R.string.only_clip),
        subtitle = stringResource(R.string.only_clip_sub),
        checked = settingsState.copyToClipboardMode is CopyToClipboardMode.Enabled.WithoutSaving,
        onClick = onClick,
        startIcon = Icons.Outlined.SaveAs
    )
}