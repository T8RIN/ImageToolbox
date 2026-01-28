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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.animation.PointToPointEasing
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCircleShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import androidx.compose.material3.SwitchColors as M3SwitchColors


@Composable
fun HyperOSSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: M3SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true
) {
    val interactionSource = interactionSource ?: remember {
        MutableInteractionSource()
    }

    val colors = com.t8rin.imagetoolbox.core.ui.widget.switches.SwitchDefaults.switchColors(colors)

    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val springSpec = remember {
        spring<Dp>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) {
            if (!enabled) 26.dp else if (isPressed || isDragged || isHovered) 24.dp else 26.dp
        } else {
            if (!enabled) 4.dp else if (isPressed || isDragged || isHovered) 3.dp else 4.dp
        } + dragOffset.dp,
        animationSpec = tween(600, easing = PointToPointEasing)
    )

    val thumbSize by animateDpAsState(
        targetValue = if (!enabled) 20.dp else if (isPressed || isDragged || isHovered) 23.dp else 20.dp,
        animationSpec = springSpec
    )

    val thumbColor by animateColorAsState(
        if (checked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled)
    )

    val backgroundColor by animateColorAsState(
        if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = tween(durationMillis = 200)
    )

    val toggleableModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = interactionSource,
            indication = null
        )
    } else {
        Modifier
    }

    val shape = AutoCircleShape()
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(50.dp, 28.5.dp)
            .requiredSize(50.dp, 28.5.dp)
            .clip(shape)
            .drawBehind {
                drawRect(backgroundColor)
            }
            .hoverable(
                interactionSource = interactionSource,
                enabled = enabled
            )
            .then(toggleableModifier)
    ) {
        val scope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(thumbSize)
                .drawBehind {
                    drawOutline(
                        outline = shape.createOutline(
                            size = size,
                            layoutDirection = layoutDirection,
                            density = density
                        ),
                        color = thumbColor
                    )
                }
                .pointerInput(checked, enabled) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val pressInteraction: PressInteraction.Press
                        val down = awaitFirstDown().also {
                            pressInteraction = PressInteraction.Press(it.position)
                            interactionSource.tryEmit(pressInteraction)
                        }
                        waitForUpOrCancellation().also {
                            interactionSource.tryEmit(PressInteraction.Cancel(pressInteraction))
                        }
                        awaitVerticalTouchSlopOrCancellation(down.id) { _, _ ->
                            interactionSource.tryEmit(PressInteraction.Cancel(pressInteraction))
                        }
                    }
                }
                .pointerInput(checked, enabled, onCheckedChange) {
                    if (!enabled) return@pointerInput
                    val dragInteraction: DragInteraction.Start = DragInteraction.Start()
                    detectHorizontalDragGestures(
                        onDragStart = {
                            interactionSource.tryEmit(dragInteraction)
                        },
                        onDragEnd = {
                            if (dragOffset.absoluteValue > 21f / 2) onCheckedChange?.invoke(!checked)
                            interactionSource.tryEmit(DragInteraction.Stop(dragInteraction))
                            scope.launch {
                                delay(50)
                                dragOffset = 0f
                            }
                        },
                        onDragCancel = {
                            interactionSource.tryEmit(DragInteraction.Cancel(dragInteraction))
                            dragOffset = 0f
                        }
                    ) { _, dragAmount ->
                        dragOffset = (dragOffset + dragAmount / 2).let {
                            if (checked) it.coerceIn(-21f, 0f) else it.coerceIn(0f, 21f)
                        }
                    }
                }
        )
    }
}

private object SwitchDefaults {

    @Composable
    fun switchColors(
        switchColors: M3SwitchColors
    ): SwitchColors = SwitchColors(
        checkedThumbColor = switchColors.checkedThumbColor,
        uncheckedThumbColor = switchColors.uncheckedThumbColor,
        disabledCheckedThumbColor = switchColors.disabledCheckedThumbColor,
        disabledUncheckedThumbColor = switchColors.disabledUncheckedThumbColor,
        checkedTrackColor = switchColors.checkedTrackColor,
        uncheckedTrackColor = switchColors.uncheckedTrackColor,
        disabledCheckedTrackColor = switchColors.disabledCheckedTrackColor,
        disabledUncheckedTrackColor = switchColors.disabledUncheckedTrackColor
    )
}

@Immutable
private class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color
) {
    @Stable
    fun checkedThumbColor(enabled: Boolean): Color =
        if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    fun uncheckedThumbColor(enabled: Boolean): Color =
        if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    fun checkedTrackColor(enabled: Boolean): Color =
        if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    fun uncheckedTrackColor(enabled: Boolean): Color =
        if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}