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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EmojiMultiple
import com.t8rin.imagetoolbox.core.resources.icons.Robot
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun EmojisCountSettingItem(
    onValueChange: (Int) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp)
) {
    val essentials = rememberLocalEssentials()
    val settingsState = LocalSettingsState.current

    EnhancedSliderItem(
        modifier = modifier.then(
            if (settingsState.selectedEmoji == null) {
                Modifier
                    .clip(ShapeDefaults.extraSmall)
                    .hapticsClickable {
                        essentials.showToast(
                            message = essentials.getString(R.string.random_emojis_error),
                            icon = Icons.Rounded.Robot
                        )
                    }
            } else Modifier
        ),
        shape = shape,
        value = settingsState.emojisCount.coerceAtLeast(1),
        title = stringResource(R.string.emojis_count),
        icon = Icons.Outlined.EmojiMultiple,
        valueRange = 1f..5f,
        steps = 3,
        enabled = settingsState.selectedEmoji != null,
        onValueChange = {},
        internalStateTransformation = {
            it.toInt()
        },
        onValueChangeFinished = {
            onValueChange(it.toInt())
        }
    )
}