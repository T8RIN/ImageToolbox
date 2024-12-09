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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.SettingsTimelapse
import ru.tech.imageresizershrinker.core.settings.domain.model.FastSettingsSide
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun FastSettingsSideSettingItem(
    onValueChange: (FastSettingsSide) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        shape = shape,
        modifier = modifier,
        onClick = {
            if (it) {
                onValueChange(FastSettingsSide.CenterEnd)
            } else {
                onValueChange(FastSettingsSide.None)
            }
        },
        title = stringResource(R.string.fast_settings_side),
        subtitle = stringResource(R.string.fast_settings_side_sub),
        checked = settingsState.fastSettingsSide != FastSettingsSide.None,
        startIcon = Icons.Outlined.SettingsTimelapse,
        resultModifier = Modifier.padding(
            top = 16.dp, end = 16.dp, start = 16.dp
        ),
        additionalContent = {
            AnimatedVisibility(
                visible = settingsState.fastSettingsSide != FastSettingsSide.None,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ToggleGroupButton(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemCount = 2,
                        itemContent = {
                            Text(
                                stringResource(
                                    when (it) {
                                        0 -> R.string.end_position
                                        else -> R.string.start_position
                                    }
                                )
                            )
                        },
                        onIndexChange = {
                            onValueChange(
                                FastSettingsSide.fromOrdinal(it + 1) ?: FastSettingsSide.CenterEnd
                            )
                        },
                        selectedIndex = settingsState.fastSettingsSide.ordinal - 1,
                        inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            }
        }
    )
}