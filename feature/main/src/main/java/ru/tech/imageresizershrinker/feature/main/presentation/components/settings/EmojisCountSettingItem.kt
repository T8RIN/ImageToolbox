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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.Robot
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState

@Composable
fun EmojisCountSettingItem(
    updateEmojisCount: (Int) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp)
) {
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    EnhancedSliderItem(
        modifier = modifier.then(
            if (settingsState.selectedEmoji == null) {
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        scope.launch {
                            toastHost.showToast(
                                message = context.getString(R.string.random_emojis_error),
                                icon = Icons.Rounded.Robot
                            )
                        }
                    }
            } else Modifier
        ),
        shape = shape,
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        value = settingsState.emojisCount.coerceAtLeast(1),
        title = stringResource(R.string.emojis_count),
        icon = Icons.Outlined.EmojiEmotions,
        valueRange = 1f..5f,
        steps = 3,
        enabled = settingsState.selectedEmoji != null,
        onValueChange = {},
        internalStateTransformation = {
            it.toInt()
        },
        onValueChangeFinished = {
            updateEmojisCount(it.toInt())
        }
    )
}