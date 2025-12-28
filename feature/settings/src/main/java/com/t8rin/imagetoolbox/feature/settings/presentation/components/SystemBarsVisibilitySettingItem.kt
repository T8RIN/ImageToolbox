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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.SystemBarsVisibility
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.TelevisionAmbientLight
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun SystemBarsVisibilitySettingItem(
    onValueChange: (SystemBarsVisibility) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val items = remember {
        SystemBarsVisibility.entries
    }

    Column(
        modifier = modifier.container(
            shape = shape
        )
    ) {
        TitleItem(
            modifier = Modifier.padding(
                top = 12.dp,
                end = 12.dp,
                bottom = 16.dp,
                start = 12.dp
            ),
            iconEndPadding = 14.dp,
            text = stringResource(R.string.system_bars_visibility),
            icon = Icons.Outlined.TelevisionAmbientLight
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
        ) {
            val value = settingsState.systemBarsVisibility
            items.forEach { item ->
                EnhancedChip(
                    onClick = {
                        onValueChange(item)
                    },
                    selected = item::class.isInstance(value),
                    label = {
                        Text(text = item.title)
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val SystemBarsVisibility.title: String
    @Composable
    get() = stringResource(
        when (this) {
            SystemBarsVisibility.Auto -> R.string.auto
            SystemBarsVisibility.HideAll -> R.string.hide_all
            SystemBarsVisibility.HideNavigationBar -> R.string.hide_nav_bar
            SystemBarsVisibility.HideStatusBar -> R.string.hide_status_bar
            SystemBarsVisibility.ShowAll -> R.string.show_all
        }
    )