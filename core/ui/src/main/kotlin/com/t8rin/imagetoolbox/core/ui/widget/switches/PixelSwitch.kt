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

package com.t8rin.imagetoolbox.core.ui.widget.switches

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.animation.PointToPointEasing
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberRipple
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlin.math.roundToInt

@Composable
fun PixelSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val realInteractionSource = interactionSource ?: remember {
        MutableInteractionSource()
    }
    val trackColor by animateColorAsState(
        targetValue = trackColor(enabled, checked, colors),
        animationSpec = tween(Duration, easing = PointToPointEasing)
    )
    val thumbColor by animateColorAsState(
        targetValue = thumbColor(enabled, checked, colors),
        animationSpec = tween(Duration, easing = PointToPointEasing)
    )
    val borderColor by animateColorAsState(
        targetValue = borderColor(enabled, checked, colors),
        animationSpec = tween(Duration, easing = PointToPointEasing)
    )
    val thumbSize by animateDpAsState(
        targetValue = if (checked) SelectedHandleSize else UnselectedHandleSize,
        animationSpec = tween(Duration, easing = PointToPointEasing)
    )

    val density = LocalDensity.current
    var offsetAnimated by remember(checked) {
        mutableFloatStateOf(
            with(density) {
                if (checked) {
                    ThumbPaddingEnd
                } else {
                    ThumbPaddingStart
                }.toPx()
            }
        )
    }

    val state = rememberDraggableState {
        offsetAnimated = with(density) {
            (offsetAnimated + it).coerceIn(ThumbPaddingStart.toPx(), ThumbPaddingEnd.toPx())
        }
    }

    val thumbOffset by animateFloatAsState(
        targetValue = offsetAnimated,
        animationSpec = tween(Duration, easing = PointToPointEasing)
    )

    Box(
        modifier = modifier
            .clickable(
                interactionSource = realInteractionSource,
                indication = null,
                enabled = enabled,
                onClickLabel = null,
                role = Role.Switch,
                onClick = { onCheckedChange?.invoke(!checked) }
            )
            .draggable(
                state = state,
                orientation = Orientation.Horizontal,
                interactionSource = realInteractionSource,
                enabled = enabled,
                onDragStopped = {
                    with(density) {
                        if (it < (ThumbPaddingEnd.toPx() - ThumbPaddingStart.toPx()) / 2f) {
                            offsetAnimated = ThumbPaddingStart.toPx()
                            if (checked) onCheckedChange?.invoke(false)
                        } else {
                            offsetAnimated = ThumbPaddingEnd.toPx()
                            if (!checked) onCheckedChange?.invoke(true)
                        }
                    }
                }
            )
            .background(trackColor, ShapeDefaults.circle)
            .size(TrackWidth, TrackHeight)
            .border(
                width = TrackOutlineWidth,
                color = borderColor,
                shape = ShapeDefaults.circle
            )
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(x = thumbOffset.roundToInt(), y = 0) }
                .indication(
                    interactionSource = realInteractionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = 16.dp
                    )
                )
                .align(Alignment.CenterStart)
                .background(thumbColor, ShapeDefaults.circle)
                .size(thumbSize)
        )
    }
}

private const val Duration = 500

@Stable
private fun trackColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color = if (enabled) {
    if (checked) colors.checkedTrackColor else colors.uncheckedTrackColor
} else {
    if (checked) colors.disabledCheckedTrackColor else colors.disabledUncheckedTrackColor
}

@Stable
private fun thumbColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color = if (enabled) {
    if (checked) colors.checkedThumbColor else colors.uncheckedThumbColor
} else {
    if (checked) colors.disabledCheckedThumbColor else colors.disabledUncheckedThumbColor
}

@Stable
private fun borderColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color = if (enabled) {
    if (checked) colors.checkedBorderColor else colors.uncheckedBorderColor
} else {
    if (checked) colors.disabledCheckedBorderColor else colors.disabledUncheckedBorderColor
}

private val TrackWidth = 56.0.dp
private val TrackHeight = 28.0.dp
private val TrackOutlineWidth = 1.8.dp
private val SelectedHandleSize = 20.0.dp
private val UnselectedHandleSize = 20.0.dp

private val ThumbPaddingStart = (TrackHeight - UnselectedHandleSize) / 2
private val ThumbPaddingEnd = TrackWidth - SelectedHandleSize / 2 - TrackHeight / 2