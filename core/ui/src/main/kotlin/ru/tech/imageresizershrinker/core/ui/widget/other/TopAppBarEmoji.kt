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

package ru.tech.imageresizershrinker.core.ui.widget.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap

@Composable
fun TopAppBarEmoji() {
    val settingsState = LocalSettingsState.current
    val confettiHostState = LocalConfettiHostState.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(end = 12.dp)
            .scaleOnTap(
                onRelease = {
                    scope.launch {
                        confettiHostState.showConfetti()
                    }
                }
            )
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(5) {
                AnimatedVisibility(
                    visible = settingsState.emojisCount > it,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    EmojiItem(
                        fontScale = LocalSettingsState.current.fontScale
                            ?: LocalDensity.current.fontScale,
                        emoji = settingsState.selectedEmoji?.toString(),
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )
                }
            }
        }
    }
}