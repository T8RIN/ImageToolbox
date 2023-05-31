package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.needToShowStoragePermissionRequest
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.widget.image.findActivity
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun PermissionDialog() {
    val context = LocalContext.current.findActivity()
    val settingsState = LocalSettingsState.current

    if (context?.needToShowStoragePermissionRequest() == true) {
        AlertDialog(
            modifier = Modifier.alertDialog(),
            onDismissRequest = { },
            icon = {
                Icon(Icons.Rounded.Storage, null)
            },
            title = { Text(stringResource(R.string.permission)) },
            text = {
                Text(stringResource(R.string.permission_sub))
            },
            confirmButton = {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                    ),
                    onClick = {
                        context.requestStoragePermission()
                    }
                ) {
                    Text(stringResource(id = R.string.grant))
                }
            }
        )
    }
}