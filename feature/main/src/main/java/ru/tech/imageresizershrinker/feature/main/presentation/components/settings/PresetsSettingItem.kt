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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalEditPresetsState
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem

@Composable
fun PresetsSettingItem(
    shape: Shape = ContainerShapeDefaults.defaultShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val editPresetsState = LocalEditPresetsState.current
    val settingsState = LocalSettingsState.current
    PreferenceItem(
        shape = shape,
        onClick = {
            editPresetsState.value = true
        },
        title = stringResource(R.string.values),
        subtitle = settingsState.presets.joinToString(", "),
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        startIcon = Icons.Outlined.Numbers,
        endIcon = Icons.Rounded.CreateAlt,
        modifier = modifier
    )
}