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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.presentation.model.PicturePickerMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun ImagePickerModeSettingItemGroup(
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsState = LocalSettingsState.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val data = remember {
            PicturePickerMode.entries
        }

        data.forEachIndexed { index, mode ->
            val selected = settingsState.picturePickerMode.ordinal == mode.ordinal

            val shape = ShapeDefaults.byIndex(
                index = index,
                size = data.size
            )
            PreferenceItem(
                shape = shape,
                onClick = { onValueChange(mode.ordinal) },
                title = stringResource(mode.title),
                startIcon = mode.icon,
                subtitle = stringResource(mode.subtitle),
                containerColor = takeColorFromScheme {
                    if (selected) secondaryContainer.copy(0.7f)
                    else SafeLocalContainerColor
                },
                contentColor = takeColorFromScheme {
                    if (selected) onSecondaryContainer
                    else MaterialTheme.colorScheme.onBackground
                },
                endIcon = if (selected) {
                    Icons.Rounded.RadioButtonChecked
                } else {
                    Icons.Rounded.RadioButtonUnchecked
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (selected) {
                                MaterialTheme.colorScheme.onSecondaryContainer.copy(0.5f)
                            } else Color.Transparent
                        ).value,
                        shape = shape
                    )
            )
        }
    }
}