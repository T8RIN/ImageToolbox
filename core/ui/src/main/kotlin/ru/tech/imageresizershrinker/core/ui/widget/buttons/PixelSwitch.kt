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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberRipple

@Composable
fun PixelSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val trackColor by animateColorAsState(targetValue = trackColor(enabled, checked, colors))
    val thumbColor by animateColorAsState(targetValue = thumbColor(enabled, checked, colors))
    val borderColor by animateColorAsState(targetValue = borderColor(enabled, checked, colors))
    val thumbSize by animateDpAsState(targetValue = if (checked) SelectedHandleSize else UnselectedHandleSize)
    val thumbOffset by animateDpAsState(targetValue = if (checked) ThumbPaddingEnd else ThumbPaddingStart)

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                onClickLabel = null,
                role = Role.Switch,
                onClick = { onCheckedChange?.invoke(!checked) }
            )
            .background(trackColor, CircleShape)
            .size(TrackWidth, TrackHeight)
            .border(
                width = TrackOutlineWidth,
                color = borderColor,
                shape = CircleShape
            )
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(x = thumbOffset.roundToPx(), y = 0) }
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = 16.dp
                    )
                )
                .align(Alignment.CenterStart)
                .background(thumbColor, CircleShape)
                .size(thumbSize)
        )
    }
}

@Stable
private fun trackColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color =
    if (enabled) {
        if (checked) colors.checkedTrackColor else colors.uncheckedTrackColor
    } else {
        if (checked) colors.disabledCheckedTrackColor else colors.disabledUncheckedTrackColor
    }

@Stable
private fun thumbColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color =
    if (enabled) {
        if (checked) colors.checkedThumbColor else colors.uncheckedThumbColor
    } else {
        if (checked) colors.disabledCheckedThumbColor else colors.disabledUncheckedThumbColor
    }

@Stable
private fun borderColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color =
    if (enabled) {
        if (checked) colors.checkedBorderColor else colors.uncheckedBorderColor
    } else {
        if (checked) colors.disabledCheckedBorderColor else colors.disabledUncheckedBorderColor
    }

internal val TrackWidth = 56.0.dp
internal val TrackHeight = 28.0.dp
internal val TrackOutlineWidth = 1.8.dp
internal val SelectedHandleSize = 20.0.dp
internal val UnselectedHandleSize = 20.0.dp

internal val ThumbPaddingStart = (TrackHeight - UnselectedHandleSize) / 2
internal val ThumbPaddingEnd = TrackWidth - SelectedHandleSize / 2 - TrackHeight / 2