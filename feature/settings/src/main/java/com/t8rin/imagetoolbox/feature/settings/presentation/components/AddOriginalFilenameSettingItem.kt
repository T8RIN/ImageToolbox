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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Difference
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.PicturePickerMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlinx.coroutines.launch

@Composable
fun AddOriginalFilenameSettingItem(
    onClick: () -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        enabled = !settingsState.randomizeFilename && !settingsState.overwriteFiles && settingsState.picturePickerMode != PicturePickerMode.PhotoPicker && settingsState.hashingTypeForFilename == null,
        modifier = modifier,
        startIcon = Icons.Outlined.Difference,
        onClick = {
            onClick()
        },
        onDisabledClick = {
            scope.launch {
                toastHostState.showToast(
                    message = context.getString(R.string.filename_not_work_with_photopicker),
                    icon = Icons.Outlined.ErrorOutline
                )
            }
        },
        title = stringResource(R.string.add_original_filename),
        subtitle = stringResource(R.string.add_original_filename_sub),
        checked = settingsState.addOriginalFilename
    )
}