package ru.tech.imageresizershrinker.presentation.root.widget.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ResetDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    title: String = stringResource(R.string.reset_image),
    text: String = stringResource(R.string.reset_image_sub)
) {
    val settingsState = LocalSettingsState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toastHostState = LocalToastHost.current

    if (visible) {
        AlertDialog(
            modifier = Modifier.alertDialog(),
            icon = { Icon(Icons.Rounded.RestartAlt, null) },
            title = { Text(title) },
            text = { Text(text) },
            onDismissRequest = onDismiss,
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
                    Text(stringResource(R.string.close))
                }
            },
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
                        onReset()
                        onDismiss()
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(R.string.values_reset),
                                Icons.Rounded.DoneOutline
                            )
                        }
                    }
                ) {
                    Text(stringResource(R.string.reset))
                }
            }
        )
    }
}