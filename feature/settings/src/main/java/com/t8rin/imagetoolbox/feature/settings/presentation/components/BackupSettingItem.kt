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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Settings
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerShape
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults

@Composable
fun BackupSettingItem(
    onCreateBackupFilename: () -> String,
    onCreateBackup: (Uri, Boolean) -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var fullBackup by rememberSaveable { mutableStateOf(false) }
    val backupSavingLauncher = rememberFileCreator(
        onSuccess = { onCreateBackup(it, fullBackup) }
    )

    PreferenceItem(
        onClick = { showDialog = true },
        shape = shape,
        modifier = modifier,
        title = stringResource(R.string.backup),
        subtitle = stringResource(R.string.backup_sub),
        startIcon = Icons.Outlined.UploadFile
    )

    EnhancedAlertDialog(
        visible = showDialog,
        onDismissRequest = { showDialog = false },
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
                        onClick = {
                            fullBackup = false
                            showDialog = false
                            backupSavingLauncher.make(onCreateBackupFilename())
                        }
                    )
                    PreferenceItem(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.full_backup),
                        subtitle = stringResource(R.string.full_backup_sub),
                        startIcon = Icons.Outlined.UploadFile,
                        shape = ShapeDefaults.bottom,
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyle.copy(
                            textAlign = TextAlign.Start
                        ),
                        onClick = {
                            fullBackup = true
                            showDialog = false
                            backupSavingLauncher.make(onCreateBackupFilename())
                        }
                    )
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = { showDialog = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}