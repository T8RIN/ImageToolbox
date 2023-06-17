package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
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
import androidx.compose.ui.text.style.TextAlign
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.helper.findActivity
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@Composable
fun AppExitDialog(
    onDismiss: () -> Unit,
    visible: Boolean
) {
    val activity = LocalContext.current.findActivity()
    val settingsState = LocalSettingsState.current

    if (visible) {
        AlertDialog(
            modifier = Modifier.alertDialog(),
            onDismissRequest = onDismiss,
            dismissButton = {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                    ),
                    onClick = {
                        activity?.finishAffinity()
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
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
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.stay))
                }
            },
            title = { Text(stringResource(R.string.app_closing)) },
            text = {
                Text(
                    stringResource(R.string.app_closing_sub),
                    textAlign = TextAlign.Center
                )
            },
            icon = { Icon(Icons.Outlined.DoorBack, null) }
        )
    }
}