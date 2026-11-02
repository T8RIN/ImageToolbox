/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Database
import com.t8rin.imagetoolbox.core.resources.icons.Settings
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerShape
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload

@Composable
fun BackupSettingItem(
    onCreateBackupFilename: () -> String,
    onCreateBackup: (Uri, Boolean) -> Unit,
    fullBackupSize: Long?,
    onRequestBackupSize: () -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var fullBackup by rememberSaveable { mutableStateOf(false) }
    val backupSavingLauncher = if (LocalInspectionMode.current) {
        null
    } else {
        rememberFileCreator(
            onSuccess = { onCreateBackup(it, fullBackup) }
        )
    }

    LaunchedEffect(showDialog) {
        if (showDialog) onRequestBackupSize()
    }

    PreferenceItem(
        onClick = { showDialog = true },
        shape = shape,
        modifier = modifier,
        title = stringResource(R.string.backup),
        subtitle = stringResource(R.string.backup_sub),
        startIcon = Icons.Outlined.UploadFile
    )

    BackupTypeDialog(
        visible = showDialog,
        fullBackupSize = fullBackupSize,
        onDismiss = { showDialog = false },
        onSelect = {
            fullBackup = it
            showDialog = false
            backupSavingLauncher?.make(onCreateBackupFilename())
        }
    )
}

@Composable
internal fun BackupTypeDialog(
    visible: Boolean,
    fullBackupSize: Long?,
    onDismiss: () -> Unit,
    onSelect: (Boolean) -> Unit
) {
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.UploadFile,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.backup)) },
        text = {
            CompositionLocalProvider(LocalContainerShape provides null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PreferenceItem(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.settings),
                        subtitle = stringResource(R.string.backup_settings_sub),
                        startIcon = Icons.Outlined.Settings,
                        shape = ShapeDefaults.top,
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyle.copy(
                            textAlign = TextAlign.Start
                        ),
                        onClick = { onSelect(false) }
                    )
                    PreferenceItemOverload(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.full_backup),
                        subtitle = stringResource(R.string.full_backup_sub),
                        startIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Database,
                                contentDescription = null
                            )
                        },
                        badgeAlignment = Alignment.CenterVertically,
                        badge = {
                            EnhancedBadge(
                                modifier = Modifier.padding(start = 8.dp),
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ) {
                                Text(fullBackupSize?.let(::humanFileSize) ?: "…")
                            }
                        },
                        shape = ShapeDefaults.bottom,
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyle.copy(
                            textAlign = TextAlign.Start
                        ),
                        onClick = { onSelect(true) }
                    )
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupTypePreview() = BackupTypePreviewContent {
    BackupTypeDialog(
        visible = true,
        fullBackupSize = 4_500_000_000L,
        onDismiss = {},
        onSelect = {}
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupSizeLoadingPreview() = BackupTypePreviewContent {
    BackupTypeDialog(
        visible = true,
        fullBackupSize = null,
        onDismiss = {},
        onSelect = {}
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupLightPreview() = BackupTypePreviewContent(isDarkTheme = false) {
    BackupTypeDialog(
        visible = true,
        fullBackupSize = 4_500_000_000L,
        onDismiss = {},
        onSelect = {}
    )
}

@Composable
private fun BackupTypePreviewContent(
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) = ImageToolboxThemeForPreview(isDarkTheme = isDarkTheme) {
    Box(Modifier.fillMaxSize()) {
        content()
    }
}