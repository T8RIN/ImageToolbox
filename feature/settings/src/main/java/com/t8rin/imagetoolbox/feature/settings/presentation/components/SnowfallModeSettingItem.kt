/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Snowflake
import com.t8rin.imagetoolbox.core.settings.domain.model.SnowfallMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.state.derivedValueOf
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun SnowfallModeSettingItem(
    onValueChange: (SnowfallMode) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val value = settingsState.snowfallMode
    val entries = SnowfallMode.entries

    Column(
        modifier = modifier.container(shape = shape)
    ) {
        TitleItem(
            text = stringResource(R.string.snowfall_mode),
            icon = Icons.Outlined.Snowflake,
            iconEndPadding = 14.dp,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp)
        )
        EnhancedButtonGroup(
            enabled = true,
            itemCount = entries.size,
            title = {},
            modifier = Modifier.fillMaxWidth(),
            isScrollable = false,
            selectedIndex = derivedValueOf(value) {
                entries.indexOfFirst { it::class.isInstance(value) }
            },
            activeButtonColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
            itemContent = {
                Text(stringResource(entries[it].getTitle()))
            },
            onIndexChange = {
                onValueChange(entries[it])
            }
        )
    }
}

private fun SnowfallMode.getTitle(): Int = when (this) {
    SnowfallMode.Auto -> R.string.auto
    SnowfallMode.Enabled -> R.string.enabled
    SnowfallMode.Disabled -> R.string.disabled
}