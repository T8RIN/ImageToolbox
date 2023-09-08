package ru.tech.imageresizershrinker.presentation.main_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.Jxl
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun JxlWarning() {
    var showJxlWarning by rememberSaveable { mutableStateOf(BuildConfig.FLAVOR == "jxl") }
    if (showJxlWarning) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            onDismissRequest = { showJxlWarning = false },
            icon = {
                Icon(Icons.Filled.Jxl, null)
            },
            title = { },
            text = {
                Text(stringResource(R.string.jxl_sub))
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
                        showJxlWarning = false
                    }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }
}