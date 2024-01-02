package ru.tech.imageresizershrinker.coreui.widget.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost

@Composable
fun ResetDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    title: String = stringResource(R.string.reset_image),
    text: String = stringResource(R.string.reset_image_sub)
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toastHostState = LocalToastHost.current

    if (visible) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            icon = { Icon(Icons.Rounded.RestartAlt, null) },
            title = { Text(title) },
            text = { Text(text) },
            onDismissRequest = onDismiss,
            confirmButton = {
                EnhancedButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
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