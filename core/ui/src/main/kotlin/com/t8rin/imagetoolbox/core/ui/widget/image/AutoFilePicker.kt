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

package com.t8rin.imagetoolbox.core.ui.widget.image

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastDuration

@Composable
fun AutoFilePicker(
    onAutoPick: () -> Unit,
    isPickedAlready: Boolean
) {
    val essentials = rememberLocalEssentials()
    val settingsState = LocalSettingsState.current

    var picked by rememberSaveable(isPickedAlready) {
        mutableStateOf(isPickedAlready)
    }
    LaunchedEffect(Unit) {
        if (settingsState.skipImagePicking && !picked) {
            runCatching {
                onAutoPick()
                picked = true
            }.onFailure {
                essentials.showToast(
                    message = essentials.getString(R.string.activate_files),
                    icon = Icons.Outlined.FolderOff,
                    duration = ToastDuration.Long
                )
            }
        }
    }
}