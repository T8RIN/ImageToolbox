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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

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
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current

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
            scope.launch {
                simpleSettingsInteractor.toggleSettingsGroupVisibility(
                    key = groupKey,
                    value = !initialState
                )

                toastHostState.showToast(
                    message = context.getString(
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