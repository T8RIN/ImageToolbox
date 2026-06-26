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

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberRipple
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
@Suppress("ComposableLambdaParameterNaming", "ComposableLambdaParameterPosition")
fun ComposeSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val realInteractionSource = interactionSource ?: remember {
        MutableInteractionSource()
    }
    val pressed by realInteractionSource.collectIsPressedAsState()
    val circleShape = ShapeDefaults.circle

    val toggleableModifier = if (onCheckedChange != null) {
        Modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = realInteractionSource,
                indication = null
            )
    } else {
        Modifier
    }

    val trackColor by animateColorAsState(
        targetValue = trackColor(enabled, checked, colors)
    )
    val thumbColor by animateColorAsState(
        targetValue = thumbColor(enabled, checked, colors)
    )
    val borderColor by animateColorAsState(
        targetValue = borderColor(enabled, checked, colors)
    )
    val iconColor by animateColorAsState(
        targetValue = iconColor(enabled, checked, colors)
    )

    val thumbSize by animateDpAsState(
        targetValue = when {
            pressed -> PressedThumbSize
            thumbContent != null || checked -> ThumbSize
            else -> UncheckedThumbSize
        }
    )
    val thumbOffset by animateDpAsState(
        targetValue = when {
            pressed && checked -> CheckedThumbOffset - TrackOutlineWidth
            pressed -> TrackOutlineWidth
            checked -> CheckedThumbOffset
            else -> (SwitchHeight - thumbSize) / 2f
        },
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    Box(
        modifier = modifier
            .then(toggleableModifier)
            .wrapContentSize(Alignment.Center)
            .requiredSize(SwitchWidth, SwitchHeight)
            .border(
                width = TrackOutlineWidth,
                color = borderColor,
                shape = circleShape
            )
            .background(trackColor, circleShape)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .indication(
                    interactionSource = realInteractionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = StateLayerSize / 2f
                    )
                )
                .background(thumbColor, circleShape)
                .size(thumbSize),
            contentAlignment = Alignment.Center
        ) {
            if (thumbContent != null) {
                CompositionLocalProvider(
                    LocalContentColor provides iconColor,
                    content = thumbContent
                )
            }
        }
    }
}

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

@Stable
private fun iconColor(
    enabled: Boolean,
    checked: Boolean,
    colors: SwitchColors
): Color = if (enabled) {
    if (checked) colors.checkedIconColor else colors.uncheckedIconColor
} else {
    if (checked) colors.disabledCheckedIconColor else colors.disabledUncheckedIconColor
}

private val SwitchWidth = 52.dp
private val SwitchHeight = 32.dp
private val TrackOutlineWidth = 2.dp
private val StateLayerSize = 40.dp
private val ThumbSize = 24.dp
private val UncheckedThumbSize = 16.dp
private val PressedThumbSize = 28.dp
private val ThumbPadding = (SwitchHeight - ThumbSize) / 2f
private val CheckedThumbOffset = SwitchWidth - ThumbSize - ThumbPadding