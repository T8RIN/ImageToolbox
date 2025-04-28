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

package ru.tech.imageresizershrinker.feature.settings.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFolderOpener
import ru.tech.imageresizershrinker.core.ui.utils.helper.toUiPath
import ru.tech.imageresizershrinker.core.ui.utils.provider.SafeLocalContainerColor
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun SavingFolderSettingItemGroup(
    modifier: Modifier = Modifier,
    onValueChange: (Uri?) -> Unit
) {
    Column(modifier) {
        val context = LocalContext.current

        val settingsState = LocalSettingsState.current
        val currentFolderUri = settingsState.saveFolderUri
        val launcher = rememberFolderOpener(
            onSuccess = { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                onValueChange(uri)
            }
        )

        PreferenceItem(
            shape = ContainerShapeDefaults.topShape,
            onClick = { onValueChange(null) },
            title = stringResource(R.string.def),
            subtitle = stringResource(R.string.default_folder),
            color = takeColorFromScheme {
                if (currentFolderUri == null) secondaryContainer.copy(0.7f)
                else SafeLocalContainerColor
            },
            startIcon = Icons.Outlined.FolderSpecial,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (currentFolderUri == null) {
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(0.5f)
                        } else Color.Transparent
                    ).value,
                    shape = ContainerShapeDefaults.topShape
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        PreferenceItem(
            shape = ContainerShapeDefaults.bottomShape,
            onClick = {
                launcher.open(currentFolderUri)
            },
            title = stringResource(R.string.custom),
            subtitle = currentFolderUri.toUiPath(
                context = context,
                default = stringResource(R.string.unspecified)
            ),
            color = takeColorFromScheme {
                if (currentFolderUri != null) secondaryContainer.copy(0.7f)
                else Color.Unspecified
            },
            startIcon = Icons.Outlined.FolderShared,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = animateColorAsState(
                        if (currentFolderUri != null) {
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(0.5f)
                        } else Color.Transparent
                    ).value,
                    shape = ContainerShapeDefaults.bottomShape
                )
        )
    }
}