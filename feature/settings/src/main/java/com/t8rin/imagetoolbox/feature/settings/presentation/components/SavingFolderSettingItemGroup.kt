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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFolderPicker
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.utils.uiPath

@Composable
fun SavingFolderSettingItemGroup(
    modifier: Modifier = Modifier,
    onValueChange: (Uri?) -> Unit
) {
    Column(modifier) {
        val settingsState = LocalSettingsState.current
        val currentFolderUri = settingsState.saveFolderUri
        val launcher = rememberFolderPicker(
            onSuccess = onValueChange
        )

        PreferenceItem(
            shape = ShapeDefaults.top,
            onClick = { onValueChange(null) },
            title = stringResource(R.string.def),
            subtitle = stringResource(R.string.default_folder),
            containerColor = takeColorFromScheme {
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
                    shape = ShapeDefaults.top
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        PreferenceItem(
            shape = ShapeDefaults.bottom,
            onClick = {
                launcher.pickFolder(currentFolderUri)
            },
            title = stringResource(R.string.custom),
            subtitle = currentFolderUri.uiPath(
                default = stringResource(R.string.unspecified)
            ),
            containerColor = takeColorFromScheme {
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
                    shape = ShapeDefaults.bottom
                )
        )
    }
}