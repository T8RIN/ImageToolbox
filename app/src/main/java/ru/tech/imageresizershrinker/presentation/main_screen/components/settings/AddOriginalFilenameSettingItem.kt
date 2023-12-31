package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Difference
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun AddOriginalFilenameSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val enabled = settingsState.imagePickerModeInt != 0
    PreferenceRowSwitch(
        shape = shape,
        enabled = !settingsState.randomizeFilename && !settingsState.overwriteFiles,
        applyHorPadding = false,
        modifier = modifier
            .alpha(
                animateFloatAsState(
                    if (enabled) 1f
                    else 0.5f
                ).value
            ),
        resultModifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        autoShadowElevation = if (enabled) 1.dp else 0.dp,
        startContent = {
            Icon(
                imageVector = Icons.Outlined.Difference,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        onClick = {
            if (enabled) onClick(it)
            else scope.launch {
                toastHostState.showToast(
                    message = context.getString(R.string.filename_not_work_with_photopicker),
                    icon = Icons.Outlined.ErrorOutline
                )
            }
        },
        title = stringResource(R.string.add_original_filename),
        subtitle = stringResource(R.string.add_original_filename_sub),
        checked = settingsState.addOriginalFilename && enabled
    )
}