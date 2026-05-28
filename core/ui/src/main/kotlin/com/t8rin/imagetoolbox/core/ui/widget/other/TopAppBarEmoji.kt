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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.emoji.Emoji
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap

@Composable
fun TopAppBarEmoji(
    allowMultiple: Boolean = true,
    containerColor: Color? = null,
    shape: Shape? = null,
    contentPadding: Dp? = null
) {
    val settingsState = LocalSettingsState.current

    Box(
        modifier = Modifier
            .padding(end = 12.dp)
            .scaleOnTap {
                AppToastHost.showConfetti()
            }
    ) {
        Row {
            val count = if (allowMultiple) 5 else 1

            val emoji = remember(settingsState.selectedEmoji) {
                settingsState.selectedEmoji?.toString()
            }
            val animatedEmoji =
                remember(settingsState.selectedEmoji, settingsState.useAnimatedEmojis) {
                    settingsState.selectedEmoji
                        ?.takeIf { settingsState.useAnimatedEmojis }
                        ?.let(Emoji::animatedIconFor)
                        ?.toString()
                }

            repeat(count) { index ->
                AnimatedVisibility(
                    visible = settingsState.emojisCount > index,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    EmojiItem(
                        emoji = emoji,
                        animatedEmoji = animatedEmoji,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        modifier = if (index != count - 1) {
                            Modifier.padding(end = 2.dp)
                        } else {
                            Modifier
                        },
                        containerColor = containerColor,
                        shape = shape,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}
