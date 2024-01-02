package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem

@Composable
fun BackupSettingItem(
    createBackupFilename: () -> String,
    createBackup: (Uri) -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val backupSavingLauncher = rememberLauncherForActivityResult(
        contract = object : ActivityResultContracts.CreateDocument("*/*") {
            override fun createIntent(context: Context, input: String): Intent {
                return super.createIntent(
                    context = context,
                    input = input.split("#")[0]
                ).putExtra(Intent.EXTRA_TITLE, input.split("#")[1])
            }
        },
        onResult = {
            it?.let { uri ->
                createBackup(uri)
            }
        }
    )
    PreferenceItem(
        onClick = {
            backupSavingLauncher.launch("*/*#${createBackupFilename()}")
        },
        shape = shape,
        modifier = modifier,
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        title = stringResource(R.string.backup),
        subtitle = stringResource(R.string.backup_sub),
        endIcon = Icons.Rounded.UploadFile
    )
}