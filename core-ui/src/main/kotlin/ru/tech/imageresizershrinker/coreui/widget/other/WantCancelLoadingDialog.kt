package ru.tech.imageresizershrinker.coreui.widget.other

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.alertDialogBorder

@Composable
fun WantCancelLoadingDialog(
    visible: Boolean,
    onCancelLoading: () -> Unit,
    onDismissDialog: () -> Unit
) {
    if (visible) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            onDismissRequest = onDismissDialog,
            confirmButton = {
                EnhancedButton(
                    onClick = onDismissDialog
                ) {
                    Text(stringResource(id = R.string.wait))
                }
            },
            title = {
                Text(stringResource(id = R.string.loading))
            },
            text = {
                Text(stringResource(R.string.saving_almost_complete))
            },
            dismissButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onCancelLoading
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeCap = StrokeCap.Round,
                    trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                    strokeWidth = 3.dp
                )
            },
        )
    }
}