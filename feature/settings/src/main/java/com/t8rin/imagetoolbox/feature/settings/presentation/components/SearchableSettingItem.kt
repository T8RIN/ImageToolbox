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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.settings.presentation.model.IconShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent

@Composable
internal fun SearchableSettingItem(
    modifier: Modifier = Modifier,
    group: SettingsGroup,
    setting: Setting,
    shape: Shape,
    component: SettingsComponent,
    onNavigateToEasterEgg: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLibrariesInfo: () -> Unit,
    isUpdateAvailable: Boolean
) {
    Column(
        modifier = modifier.container(
            resultPadding = 0.dp,
            shape = shape
        )
    ) {
        val settingState = LocalSettingsState.current
        val iconShape = remember(settingState.iconShape) {
            derivedStateOf {
                settingState.iconShape?.takeOrElseFrom(IconShape.entries)
            }
        }.value

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconShapeContainer(
                enabled = true,
                iconShape = iconShape?.copy(
                    iconSize = 16.dp
                )
            ) {
                Icon(
                    imageVector = group.icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(id = group.titleId),
                fontSize = 12.sp
            )
        }
        val itemShape = when (setting) {
            is Setting.ImagePickerMode -> null
            is Setting.NightMode -> null
            else -> ShapeDefaults.small
        }
        ProvideContainerDefaults(itemShape) {
            SettingItem(
                setting = setting,
                component = component,
                isUpdateAvailable = isUpdateAvailable,
                onNavigateToEasterEgg = onNavigateToEasterEgg,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToLibrariesInfo = onNavigateToLibrariesInfo
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}