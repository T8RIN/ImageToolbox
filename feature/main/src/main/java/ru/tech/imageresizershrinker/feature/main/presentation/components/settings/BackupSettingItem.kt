/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun BackupSettingItem(
    createBackupFilename: () -> String,
    createBackup: (Uri) -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current

    val backupSavingLauncher = rememberLauncherForActivityResult(
        contract = object : ActivityResultContracts.CreateDocument("*/*") {
            override fun createIntent(
                context: Context,
                input: String
            ): Intent {
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
            runCatching {
                backupSavingLauncher.launch("*/*#${createBackupFilename()}")
            }.onFailure {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.activate_files),
                        icon = Icons.Outlined.FolderOff,
                        duration = ToastDuration.Long
                    )
                }
            }
        },
        shape = shape,
        modifier = modifier,
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        title = stringResource(R.string.backup),
        subtitle = stringResource(R.string.backup_sub),
        startIcon = Icons.Rounded.UploadFile
    )
}