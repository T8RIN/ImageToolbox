package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.coreresources.BuildConfig
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.icons.material.Jxl
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun JxlWarning() {
    val settingsState = LocalSettingsState.current
    var showJxlWarning by rememberSaveable { mutableStateOf(BuildConfig.FLAVOR == "jxl") }
    if (showJxlWarning && settingsState.appOpenCount <= 1) {
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
                EnhancedButton(
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