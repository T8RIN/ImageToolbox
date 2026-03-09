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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.switches.ActualSwitchColors.Companion.forConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OneUISwitch(
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.colors(),
    onCheckedChange: ((Boolean) -> Unit)? = { },
    enabled: Boolean = true,
    checked: Boolean = false,
    interactionSource: MutableInteractionSource? = null
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    var isAnimating by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    val actualColors = colors.forConfig(enabled, checked)

    val spec = tween<Color>(animDuration)
    val thumbColor by animateColorAsState(
        targetValue = actualColors.thumb,
        label = "Switch thumb color",
        animationSpec = spec
    )
    val track by animateColorAsState(
        targetValue = actualColors.track,
        label = "Switch track color",
        animationSpec = spec
    )
    val stroke by animateColorAsState(
        targetValue = actualColors.stroke,
        label = "Switch stroke color",
        animationSpec = spec
    )
    val rippleAlphaFactor by animateFloatAsState(
        targetValue = if (isAnimating) 1F else 0F,
        label = "Switch ripple alpha",
        animationSpec = tween(animDuration)
    )
    val rippleRadius by animateDpAsState(
        targetValue = if (isAnimating) rippleRadius else 0.dp,
        label = "Switch ripple radius",
        animationSpec = tween(animDuration)
    )

    val ripple = MaterialTheme.colorScheme.outline.copy(0.1f)

    var dragProgress by remember { mutableStateOf<Float?>(null) }
    val progress by animateFloatAsState(
        targetValue = dragProgress ?: if (checked) 1f else 0f,
        label = "Switch progress",
        animationSpec = tween(animDuration)
    )

    val shape = ShapeDefaults.circle

    Canvas(
        modifier = modifier
            .width(trackSize.width + (thumbOvershoot * 2))
            .height(thumbSize.height)
            .hapticsClickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                onClick = {
                    onCheckedChange?.invoke(!checked)
                    isAnimating = true
                    scope.launch {
                        delay(animDuration.toLong())
                        isAnimating = false
                    }
                }
            )
            .pointerInput(enabled, onCheckedChange) {
                if (!enabled) return@pointerInput
                var interaction: DragInteraction.Start? = null
                detectDragGestures(
                    onDragStart = {
                        interaction = DragInteraction.Start().also(interactionSource::tryEmit)
                        isAnimating = true
                        dragProgress = progress
                    },
                    onDragEnd = {
                        val newChecked = (dragProgress ?: progress) > 0.5f
                        onCheckedChange?.invoke(newChecked)
                        scope.launch {
                            dragProgress = if (newChecked) 1f else 0f
                            delay(animDuration.toLong())
                            dragProgress = null
                            isAnimating = false
                        }
                        interaction?.let {
                            interactionSource.tryEmit(DragInteraction.Stop(it))
                        }
                    },
                    onDragCancel = {
                        val newChecked = (dragProgress ?: progress) > 0.5f
                        onCheckedChange?.invoke(newChecked)
                        scope.launch {
                            dragProgress = if (newChecked) 1f else 0f
                            delay(animDuration.toLong())
                            dragProgress = null
                        }
                        isAnimating = false
                        interaction?.let {
                            interactionSource.tryEmit(DragInteraction.Cancel(it))
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragProgress = ((dragProgress ?: progress) + dragAmount.x).coerceIn(0f, 1f)
                    }
                )
            }
    ) {
        drawTrack(
            color = track,
            dpSize = trackSize,
            spacingStart = thumbOvershoot,
            shape = shape
        )

        val thumbPos = thumbPosition(
            overshoot = thumbOvershoot,
            progress = progress,
            radius = thumbSize.width / 2,
            size = size,
            density = this
        )

        drawThumb(
            color = thumbColor,
            radius = thumbSize.width / 2,
            position = thumbPos,
            shape = shape
        )

        drawOutline(
            color = stroke,
            radius = (thumbSize.width + strokeWidth) / 2,
            position = thumbPos,
            strokeWidth = strokeWidth,
            shape = shape
        )

        drawRipple(
            color = ripple.copy(alpha = ripple.alpha * rippleAlphaFactor),
            radius = rippleRadius,
            position = thumbPos,
            shape = shape
        )
    }
}

private fun DrawScope.drawTrack(
    color: Color,
    dpSize: DpSize,
    spacingStart: Dp,
    shape: Shape
) {
    val dif = size.height - dpSize.height.toPx()
    val outline = shape.createOutline(
        size = dpSize.toSize(),
        layoutDirection = LayoutDirection.Ltr,
        density = this
    )
    translate(
        left = spacingStart.toPx(),
        top = dif / 2
    ) {
        drawOutline(outline = outline, color = color)
    }
}

private fun DrawScope.drawThumb(
    color: Color,
    radius: Dp,
    position: Offset,
    shape: Shape
) {
    val size = Size(radius.toPx() * 2, radius.toPx() * 2)
    val outline = shape.createOutline(
        size = size,
        layoutDirection = LayoutDirection.Ltr,
        density = this
    )
    translate(
        left = position.x - radius.toPx(),
        top = position.y - radius.toPx()
    ) {
        drawOutline(outline = outline, color = color)
    }
}

private fun DrawScope.drawOutline(
    color: Color,
    radius: Dp,
    position: Offset,
    strokeWidth: Dp,
    shape: Shape
) {
    val size = Size(radius.toPx() * 2, radius.toPx() * 2)
    val outline = shape.createOutline(
        size = size,
        layoutDirection = LayoutDirection.Ltr,
        density = this
    )
    translate(
        left = position.x - radius.toPx(),
        top = position.y - radius.toPx()
    ) {
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

private fun DrawScope.drawRipple(
    color: Color,
    radius: Dp,
    position: Offset,
    shape: Shape
) {
    val size = Size(radius.toPx() * 2, radius.toPx() * 2)
    val outline = shape.createOutline(
        size = size,
        layoutDirection = LayoutDirection.Ltr,
        density = this
    )
    translate(
        left = position.x - radius.toPx(),
        top = position.y - radius.toPx()
    ) {
        drawOutline(outline = outline, color = color)
    }
}

private fun thumbPosition(
    overshoot: Dp,
    progress: Float,
    radius: Dp,
    size: Size,
    density: Density
): Offset {
    val yCenter = size.height / 2
    val width = size.width
    val start =
        with(density) { thumbOvershoot.toPx() + (radius.toPx() - overshoot.toPx()) }
    val end = width - start
    val pos = mapRange(
        value = progress,
        targetStart = start,
        targetEnd = end
    )
    return Offset(
        x = pos,
        y = yCenter
    )
}

private fun mapRange(
    value: Float,
    origStart: Float = 0f,
    origEnd: Float = 1f,
    targetStart: Float,
    targetEnd: Float
): Float = (value - origStart) / (origEnd - origStart) * (targetEnd - targetStart) + targetStart

private data class ActualSwitchColors(
    val thumb: Color,
    val track: Color,
    val stroke: Color
) {
    companion object {
        @Suppress("KotlinConstantConditions")
        fun SwitchColors.forConfig(
            enabled: Boolean,
            checked: Boolean
        ): ActualSwitchColors = if (
            enabled && checked
        ) ActualSwitchColors(
            thumb = checkedThumbColor,
            track = checkedTrackColor,
            stroke = checkedTrackColor
        ) else if (
            !enabled && checked
        ) ActualSwitchColors(
            thumb = disabledCheckedThumbColor,
            track = disabledCheckedTrackColor,
            stroke = disabledCheckedTrackColor
        ) else if (
            enabled && !checked
        ) ActualSwitchColors(
            thumb = uncheckedTrackColor.copy(1f),
            track = uncheckedThumbColor.copy(1f),
            stroke = uncheckedThumbColor.copy(1f),
        ) else ActualSwitchColors(
            thumb = disabledUncheckedTrackColor,
            track = disabledUncheckedThumbColor,
            stroke = disabledUncheckedThumbColor
        )
    }
}

private const val animDuration = 250
private val strokeWidth = 2.dp
private val thumbSize = DpSize(
    width = 22.dp,
    height = 22.dp
)
private val thumbOvershoot = 2.dp
private val trackSize = DpSize(
    width = 35.dp,
    height = 18.5.dp
)
private val rippleRadius = 20.dp

@Composable
@EnPreview
private fun Preview() = ImageToolboxThemeForPreview(true, keyColor = Color.Green) {
    val colors = SwitchDefaults.colors(
        disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = 0.12f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    ).copy(
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
    Surface {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var checked by remember {
                mutableStateOf(true)
            }
            OneUISwitch(
                checked = checked,
                colors = colors,
                onCheckedChange = { checked = it }
            )

            OneUISwitch(
                checked = false,
                colors = colors
            )

            OneUISwitch(
                checked = true,
                enabled = false,
                colors = colors
            )

            OneUISwitch(
                checked = false,
                enabled = false,
                colors = colors
            )
        }
    }
}