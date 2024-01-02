package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WaterDrop
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
fun AllowImageMonetSettingItem(
    shape: Shape = ContainerShapeDefaults.topShape,
    onClick: (Boolean) -> Unit,
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
        title = stringResource(R.string.allow_image_monet),
        subtitle = stringResource(R.string.allow_image_monet_sub),
        checked = settingsState.allowChangeColorByImage,
        onClick = onClick,
        startContent = {
            Icon(
                Icons.Outlined.WaterDrop,
                null,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}