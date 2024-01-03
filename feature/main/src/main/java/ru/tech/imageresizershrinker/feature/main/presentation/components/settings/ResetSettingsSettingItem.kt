package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem

@Composable
fun ResetSettingsSettingItem(
    onReset: () -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    var showResetDialog by remember { mutableStateOf(false) }
    PreferenceItem(
        onClick = {
            showResetDialog = true
        },
        shape = shape,
        modifier = modifier,
        color = MaterialTheme.colorScheme
            .errorContainer
            .copy(alpha = 0.8f),
        title = stringResource(R.string.reset),
        subtitle = stringResource(R.string.reset_settings_sub),
        endIcon = Icons.Rounded.RestartAlt
    )
    ResetDialog(
        visible = showResetDialog,
        onDismiss = {
            showResetDialog = false
        },
        onReset = {
            showResetDialog = false
            onReset()
        },
        title = stringResource(R.string.reset),
        text = stringResource(R.string.reset_settings_sub)
    )
}