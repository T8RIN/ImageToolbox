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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.buttons.MediaCheckBox
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsCombinedClickable

@Composable
fun MediaStickyHeader(
    modifier: Modifier = Modifier,
    date: String,
    isCheckVisible: MutableState<Boolean>,
    isChecked: Boolean,
    onChecked: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.hapticsCombinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onLongClick = onChecked,
                onClick = {
                    if (isCheckVisible.value) onChecked?.invoke()
                }
            )
        )
        if (onChecked != null) {
            AnimatedVisibility(
                visible = isCheckVisible.value,
                enter = enterAnimation,
                exit = exitAnimation
            ) {
                MediaCheckBox(
                    isChecked = isChecked,
                    onCheck = onChecked,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private val enterAnimation = fadeIn(tween(150))
private val exitAnimation = fadeOut(tween(150))