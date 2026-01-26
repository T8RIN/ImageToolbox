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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.animation.FastInvokeEasing
import com.t8rin.imagetoolbox.core.ui.utils.animation.PointToPointEasing
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberRipple
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun FluentSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    val realInteractionSource = interactionSource ?: remember {
        MutableInteractionSource()
    }
    val pressed by realInteractionSource.collectIsPressedAsState()
    val dragged by realInteractionSource.collectIsDraggedAsState()

    val height by animateDpAsState(
        when {
            pressed || dragged -> 14.dp
            else -> 12.dp
        },
        tween(Duration, easing = FastInvokeEasing)
    )

    val width by animateDpAsState(
        when {
            pressed || dragged -> 17.dp
            else -> 12.dp
        },
        tween(Duration, easing = FastInvokeEasing)
    )

    val maxValue = 32.dp - (width / 2)
    val minValue = 2.dp

    val density = LocalDensity.current
    var offsetAnimated by remember(checked) {
        mutableFloatStateOf(
            with(density) {
                if (checked) {
                    maxValue
                } else {
                    minValue
                }.toPx()
            }
        )
    }

    val state = rememberDraggableState {
        offsetAnimated = with(density) {
            (offsetAnimated + it).coerceIn(minValue.toPx(), maxValue.toPx())
        }
    }

    val offset by animateFloatAsState(
        targetValue = offsetAnimated,
        animationSpec = tween(Duration, easing = PointToPointEasing)
    )

    Row(
        modifier = modifier
            .toggleable(
                value = checked,
                indication = null,
                interactionSource = realInteractionSource,
                role = Role.Switch,
                onValueChange = {
                    onCheckedChange?.invoke(it)
                },
                enabled = enabled
            )
            .draggable(
                state = state,
                orientation = Orientation.Horizontal,
                interactionSource = realInteractionSource,
                enabled = enabled,
                onDragStopped = {
                    with(density) {
                        if (it < (maxValue.toPx() - minValue.toPx()) / 2f) {
                            offsetAnimated = minValue.toPx()
                            if (checked) onCheckedChange?.invoke(false)
                        } else {
                            offsetAnimated = maxValue.toPx()
                            if (!checked) onCheckedChange?.invoke(true)
                        }
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Box(
            modifier = Modifier
                .size(48.dp, 24.dp)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = ShapeDefaults.circle
                )
                .clip(ShapeDefaults.circle)
                .background(trackColor)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(width, height)
                    .graphicsLayer {
                        translationX = offset
                        transformOrigin = TransformOrigin.Center
                    }
                    .indication(
                        interactionSource = realInteractionSource,
                        indication = rememberRipple(
                            bounded = false,
                            radius = 16.dp
                        )
                    )
                    .clip(ShapeDefaults.circle)
                    .background(thumbColor)
            )
        }
    }
}

private const val Duration = 600

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