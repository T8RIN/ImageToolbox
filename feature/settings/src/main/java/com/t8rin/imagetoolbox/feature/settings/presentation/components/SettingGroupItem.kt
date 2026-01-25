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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
fun SettingGroupItem(
    groupKey: Int,
    icon: ImageVector,
    text: String,
    initialState: Boolean = false,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(2.dp),
    content: @Composable ColumnScope.(Boolean) -> Unit
) {
    val essentials = rememberLocalEssentials()

    val settingsState = LocalSettingsState.current

    val initialState =
        settingsState.settingGroupsInitialVisibility[groupKey] ?: initialState

    val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current

    ExpandableItem(
        modifier = modifier,
        visibleContent = {
            TitleItem(
                modifier = Modifier.padding(start = 8.dp),
                icon = icon,
                text = text
            )
        },
        color = takeColorFromScheme {
            surfaceContainer.blend(
                surfaceContainerLowest, 0.4f
            )
        },
        onLongClick = {
            essentials.launch {
                simpleSettingsInteractor.toggleSettingsGroupVisibility(
                    key = groupKey,
                    value = !initialState
                )

                essentials.showToast(
                    message = essentials.getString(
                        if (initialState) {
                            R.string.settings_group_visibility_hidden
                        } else {
                            R.string.settings_group_visibility_visible
                        },
                        text
                    ),
                    icon = Icons.Outlined.Settings
                )
            }
        },
        expandableContent = content,
        initialState = initialState
    )
}