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
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.NightMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun NightModeSettingItemGroup(
    value: NightMode,
    onValueChange: (NightMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val settingsState = LocalSettingsState.current
        listOf(
            Triple(
                stringResource(R.string.dark),
                Icons.Outlined.DarkMode,
                NightMode.Dark
            ),
            Triple(
                stringResource(R.string.light),
                Icons.Outlined.LightMode,
                NightMode.Light
            ),
            Triple(
                stringResource(R.string.system),
                Icons.Outlined.SettingsSuggest,
                NightMode.System
            ),
        ).forEachIndexed { index, (title, icon, nightMode) ->
            val selected = nightMode == value
            val shape = ShapeDefaults.byIndex(index, 3)
            PreferenceItem(
                onClick = { onValueChange(nightMode) },
                title = title,
                color = takeColorFromScheme {
                    if (selected) secondaryContainer.copy(0.7f)
                    else SafeLocalContainerColor
                },
                shape = shape,
                startIcon = icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = settingsState.borderWidth,
                        color = animateColorAsState(
                            if (selected) MaterialTheme.colorScheme
                                .onSecondaryContainer
                                .copy(alpha = 0.5f)
                            else Color.Transparent
                        ).value,
                        shape = shape
                    ),
                endIcon = if (selected) {
                    Icons.Rounded.RadioButtonChecked
                } else Icons.Rounded.RadioButtonUnchecked
            )
        }
    }
}