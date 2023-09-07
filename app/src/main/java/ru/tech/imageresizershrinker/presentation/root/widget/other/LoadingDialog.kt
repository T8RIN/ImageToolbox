package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(onCancelLoading: () -> Unit) {
    var showWantDismissDialog by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = { showWantDismissDialog = true }) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        showWantDismissDialog = !showWantDismissDialog
                    }
                }
        ) { Loading() }
    }
    if (showWantDismissDialog) {
        AlertDialog(
            onDismissRequest = {
                showWantDismissDialog = false
            },
            confirmButton = {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = BorderStroke(
                        LocalSettingsState.current.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                    ),
                    onClick = {
                        showWantDismissDialog = false
                    }
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
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    border = BorderStroke(
                        LocalSettingsState.current.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primaryContainer)
                    ),
                    onClick = onCancelLoading
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp
                )
            },
        )
    }
    KeepScreenOn()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(done: Int, left: Int, onCancelLoading: () -> Unit) {
    var showWantDismissDialog by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = { showWantDismissDialog = true }) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        showWantDismissDialog = !showWantDismissDialog
                    }
                }
        ) { Loading(done, left) }
    }
    if (showWantDismissDialog) {
        AlertDialog(
            onDismissRequest = {
                showWantDismissDialog = false
            },
            confirmButton = {
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = BorderStroke(
                        LocalSettingsState.current.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                    ),
                    onClick = {
                        showWantDismissDialog = false
                    }
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
                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    border = BorderStroke(
                        LocalSettingsState.current.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primaryContainer)
                    ),
                    onClick = onCancelLoading
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            icon = {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp
                )
            },
        )
    }
    KeepScreenOn()
}