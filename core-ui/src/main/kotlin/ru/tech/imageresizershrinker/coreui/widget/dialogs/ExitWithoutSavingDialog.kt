package ru.tech.imageresizershrinker.coreui.widget.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.alertDialogBorder

@Composable
fun ExitWithoutSavingDialog(
    onExit: () -> Unit,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (visible) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            onDismissRequest = onDismiss,
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        onDismiss()
                        onExit()
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            confirmButton = {
                EnhancedButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.stay))
                }
            },
            title = { Text(stringResource(R.string.image_not_saved)) },
            text = {
                Text(
                    stringResource(R.string.image_not_saved_sub),
                    textAlign = TextAlign.Center
                )
            },
            icon = { Icon(Icons.Outlined.Save, null) }
        )
    }
}