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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Difference
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun AddOriginalFilenameSettingItem(
    onClick: (Boolean) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val enabled = settingsState.imagePickerModeInt != 0
    PreferenceRowSwitch(
        shape = shape,
        enabled = !settingsState.randomizeFilename && !settingsState.overwriteFiles,
        modifier = modifier
            .alpha(
                animateFloatAsState(
                    if (enabled) 1f
                    else 0.5f
                ).value
            ),
        autoShadowElevation = if (enabled) 1.dp else 0.dp,
        startIcon = Icons.Outlined.Difference,
        onClick = {
            if (enabled) onClick(it)
            else scope.launch {
                toastHostState.showToast(
                    message = context.getString(R.string.filename_not_work_with_photopicker),
                    icon = Icons.Outlined.ErrorOutline
                )
            }
        },
        title = stringResource(R.string.add_original_filename),
        subtitle = stringResource(R.string.add_original_filename_sub),
        checked = settingsState.addOriginalFilename && enabled
    )
}