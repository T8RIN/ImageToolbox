package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BurstMode
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun ImagePickerModeSettingItemGroup(
    updateImagePickerMode: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsState = LocalSettingsState.current
    Column(modifier) {
        PreferenceItem(
            shape = ContainerShapeDefaults.topShape,
            onClick = { updateImagePickerMode(0) },
            title = stringResource(R.string.photo_picker),
            icon = Icons.Outlined.BurstMode,
            subtitle = stringResource(R.string.photo_picker_sub),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = animateFloatAsState(
                    if (settingsState.imagePickerModeInt == 0) 0.7f
                    else 0.2f
                ).value
            ),
            endIcon = if (settingsState.imagePickerModeInt == 0) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (settingsState.imagePickerModeInt == 0) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.5f
                        )
                        else Color.Transparent
                    ).value,
                    shape = ContainerShapeDefaults.topShape
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        PreferenceItem(
            shape = ContainerShapeDefaults.centerShape,
            onClick = { updateImagePickerMode(1) },
            title = stringResource(R.string.gallery_picker),
            icon = Icons.Outlined.Image,
            subtitle = stringResource(R.string.gallery_picker_sub),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = animateFloatAsState(
                    if (settingsState.imagePickerModeInt == 1) 0.7f
                    else 0.2f
                ).value
            ),
            endIcon = if (settingsState.imagePickerModeInt == 1) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (settingsState.imagePickerModeInt == 1) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.5f
                        )
                        else Color.Transparent
                    ).value,
                    shape = ContainerShapeDefaults.centerShape
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        PreferenceItem(
            shape = ContainerShapeDefaults.bottomShape,
            onClick = { updateImagePickerMode(2) },
            title = stringResource(R.string.file_explorer_picker),
            subtitle = stringResource(R.string.file_explorer_picker_sub),
            icon = Icons.Rounded.FolderOpen,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                alpha = animateFloatAsState(
                    if (settingsState.imagePickerModeInt == 2) 0.7f
                    else 0.2f
                ).value
            ),
            endIcon = if (settingsState.imagePickerModeInt == 2) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (settingsState.imagePickerModeInt == 2) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.5f
                        )
                        else Color.Transparent
                    ).value,
                    shape = ContainerShapeDefaults.bottomShape
                )
        )
    }
}