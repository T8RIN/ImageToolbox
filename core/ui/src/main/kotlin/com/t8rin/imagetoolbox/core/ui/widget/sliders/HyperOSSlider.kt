/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.sliders

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousCapsule
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.animation.animateFloatingRangeAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import androidx.compose.material3.SliderColors as M3SliderColors
import androidx.compose.material3.SliderDefaults as M3Defaults

@Composable
fun HyperOSSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: M3SliderColors = M3Defaults.colors(),
    steps: Int = 0,
    decimalPlaces: Int = 2,
    drawContainer: Boolean = true
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val minValue = valueRange.start
    val maxValue = valueRange.endInclusive
    val sliderColors = SliderDefaults.sliderColors(colors)
    val factor = remember(decimalPlaces) { 10f.pow(decimalPlaces) }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val stepSize = remember(steps, minValue, maxValue) {
        if (steps > 0) (maxValue - minValue) / (steps + 1) else 0f
    }

    val shape = ContinuousCapsule
    val calculateProgress = remember(minValue, maxValue, factor, stepSize) {
        { offset: Float, width: Int ->
            var newValue = (offset / width) * (maxValue - minValue) + minValue
            if (steps > 0) {
                val snapped = ((newValue - minValue) / stepSize).roundToInt() * stepSize + minValue
                newValue = snapped
            }
            (round(newValue * factor) / factor).coerceIn(minValue, maxValue)
        }
    }

    val settingsState = LocalSettingsState.current

    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val calculatedValue = calculateProgress(offset.x, size.width)
                                onValueChange(calculatedValue)
                                onValueChangeFinished?.invoke()
                            }
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    isDragging = true
                                    dragOffset = offset.x
                                    val calculatedValue = calculateProgress(dragOffset, size.width)
                                    onValueChange(calculatedValue)
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    dragOffset =
                                        (dragOffset + dragAmount).coerceIn(0f, size.width.toFloat())
                                    val calculatedValue = calculateProgress(dragOffset, size.width)
                                    onValueChange(calculatedValue)
                                },
                                onDragEnd = {
                                    isDragging = false
                                    onValueChangeFinished?.invoke()
                                }
                            )
                        }
                        .indication(interactionSource, LocalIndication.current)
                } else Modifier
            )
            .then(
                if (drawContainer) {
                    Modifier.container(
                        shape = shape,
                        autoShadowElevation = animateDpAsState(
                            if (settingsState.drawSliderShadows) 1.dp else 0.dp
                        ).value,
                        resultPadding = 0.dp,
                        borderColor = MaterialTheme.colorScheme.outlineVariant(
                            luminance = 0.1f,
                            onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                        ).copy(0.3f),
                        color = SafeLocalContainerColor
                            .copy(0.3f)
                            .compositeOver(
                                takeColorFromScheme {
                                    if (it) tertiaryContainer
                                        .blend(secondaryContainer, 0.5f)
                                        .copy(0.1f)
                                    else secondaryContainer
                                        .blend(tertiaryContainer, 0.3f)
                                        .copy(0.2f)
                                }
                            )
                            .copy(sliderColors.foregroundColor(true).alpha),
                        composeColorOnTopOfBackground = false
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        SliderTrack(
            modifier = Modifier
                .fillMaxWidth()
                .height(SliderDefaults.MinHeight),
            shape = shape,
            backgroundColor = sliderColors.backgroundColor(),
            foregroundColor = sliderColors.foregroundColor(enabled),
            progress = value,
            minValue = minValue,
            maxValue = maxValue,
            isDragging = isDragging,
        )

        if (steps > 0) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SliderDefaults.MinHeight)
                    .padding(horizontal = 2.dp)
            ) {
                val stepSpacing = size.width / (steps + 1)

                val currentStep = if (stepSize > 0f) {
                    ((value - minValue) / stepSize).toInt().coerceIn(0, steps)
                } else 0

                repeat(steps + 2) { i ->
                    if (i == 0 || i == steps + 1) return@repeat

                    val color = when {
                        i - 1 < currentStep -> colors.activeTickColor
                        else -> colors.inactiveTickColor.copy(alpha = 0.3f)
                    }

                    drawCircle(
                        color = color,
                        radius = 1.dp.toPx(),
                        center = Offset(i * stepSpacing, center.y)
                    )
                }
            }
        }
    }
}

@Composable
fun HyperOSRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: M3SliderColors = M3Defaults.colors(),
    steps: Int = 0,
    drawContainer: Boolean = true,
) {
    val sliderColors = SliderDefaults.sliderColors(colors)
    val minValue = valueRange.start
    val maxValue = valueRange.endInclusive
    val shape = ContinuousCapsule
    val stepSize = if (steps > 0) (maxValue - minValue) / (steps + 1) else 0f

    var startRaw by remember { mutableFloatStateOf(value.start) }
    var endRaw by remember { mutableFloatStateOf(value.endInclusive) }

    val animatedRange by animateFloatingRangeAsState(startRaw..endRaw)
    val start = animatedRange.start
    val end = animatedRange.endInclusive

    var activeThumb by remember { mutableStateOf<Thumb?>(null) }

    val settingsState = LocalSettingsState.current

    fun snapToStep(v: Float): Float {
        if (steps == 0) return v
        val snapped = ((v - minValue) / stepSize).roundToInt() * stepSize + minValue
        return snapped.coerceIn(minValue, maxValue)
    }

    Box(
        modifier = modifier.then(
            if (drawContainer) {
                Modifier.container(
                    shape = shape,
                    autoShadowElevation = animateDpAsState(
                        if (settingsState.drawSliderShadows) 1.dp else 0.dp
                    ).value,
                    resultPadding = 0.dp,
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f,
                        onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                    ).copy(0.3f),
                    color = SafeLocalContainerColor
                        .copy(0.3f)
                        .compositeOver(
                            takeColorFromScheme {
                                if (it) tertiaryContainer
                                    .blend(secondaryContainer, 0.5f)
                                    .copy(0.1f)
                                else secondaryContainer
                                    .blend(tertiaryContainer, 0.3f)
                                    .copy(0.2f)
                            }
                        )
                        .copy(sliderColors.foregroundColor(true).alpha),
                    composeColorOnTopOfBackground = false
                )
            } else Modifier
        )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(SliderDefaults.MinHeight)
                .then(
                    if (enabled) {
                        Modifier
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val width = size.width
                                    val tappedValue =
                                        ((offset.x / width) * (maxValue - minValue) + minValue).coerceIn(
                                            minValue,
                                            maxValue
                                        )

                                    activeThumb =
                                        if (abs(tappedValue - startRaw) < abs(tappedValue - endRaw)) {
                                            Thumb.Start
                                        } else Thumb.End

                                    when (activeThumb) {
                                        Thumb.Start -> {
                                            startRaw =
                                                snapToStep(tappedValue.coerceIn(minValue, endRaw))
                                        }

                                        else -> {
                                            endRaw =
                                                snapToStep(tappedValue.coerceIn(startRaw, maxValue))
                                        }
                                    }
                                    onValueChange(startRaw..endRaw)
                                    onValueChangeFinished?.invoke()
                                }
                            }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragStart = { offset ->
                                        val width = size.width
                                        val startX =
                                            ((startRaw - minValue) / (maxValue - minValue)) * width
                                        val endX =
                                            ((endRaw - minValue) / (maxValue - minValue)) * width
                                        activeThumb =
                                            if (abs(offset.x - startX) < abs(offset.x - endX)) Thumb.Start else Thumb.End

                                        if (activeThumb == Thumb.Start) {
                                            startInteractionSource.tryEmit(
                                                PressInteraction.Press(
                                                    offset
                                                )
                                            )
                                        } else {
                                            endInteractionSource.tryEmit(
                                                PressInteraction.Press(
                                                    offset
                                                )
                                            )
                                        }
                                    },
                                    onHorizontalDrag = { _, dragAmount ->
                                        val width = size.width
                                        val deltaValue =
                                            (dragAmount / width) * (maxValue - minValue)
                                        when (activeThumb) {
                                            Thumb.Start -> {
                                                startRaw = snapToStep(
                                                    (startRaw + deltaValue).coerceIn(
                                                        minValue,
                                                        endRaw
                                                    )
                                                )
                                                onValueChange(startRaw..endRaw)
                                            }

                                            Thumb.End -> {
                                                endRaw = snapToStep(
                                                    (endRaw + deltaValue).coerceIn(
                                                        startRaw,
                                                        maxValue
                                                    )
                                                )
                                                onValueChange(startRaw..endRaw)
                                            }

                                            null -> {}
                                        }
                                    },
                                    onDragEnd = {
                                        onValueChangeFinished?.invoke()
                                        activeThumb?.let { thumb ->
                                            val interactionSource =
                                                if (thumb == Thumb.Start) startInteractionSource else endInteractionSource
                                            interactionSource.tryEmit(
                                                PressInteraction.Release(
                                                    PressInteraction.Press(Offset.Zero)
                                                )
                                            )
                                        }
                                        activeThumb = null
                                    }
                                )
                            }
                            .indication(
                                interactionSource = if (activeThumb == Thumb.End) endInteractionSource else startInteractionSource,
                                indication = LocalIndication.current
                            )
                    } else Modifier
                )
        ) {
            val barWidth = size.width
            val barHeight = size.height
            val startX = (start - minValue) / (maxValue - minValue) * barWidth
            val endX = (end - minValue) / (maxValue - minValue) * barWidth

            drawRect(
                color = sliderColors.backgroundColor(),
                size = Size(barWidth, barHeight)
            )

            drawRect(
                color = sliderColors.foregroundColor(enabled),
                topLeft = Offset(startX, center.y - barHeight / 2),
                size = Size(endX - startX, barHeight)
            )

            if (steps > 0) {
                val stepSpacing = barWidth / (steps + 1)
                repeat(steps + 2) { i ->
                    if (i == 0 || i == steps + 1) return@repeat

                    val stepValue = minValue + (i - 1) * stepSize
                    val color = when {
                        stepValue < start -> colors.activeTickColor
                        stepValue > end -> colors.inactiveTickColor.copy(alpha = 0.3f)
                        else -> sliderColors.backgroundColor()
                            .copy(alpha = 0.3f)
                    }

                    drawCircle(
                        color = color,
                        radius = barHeight / 6,
                        center = Offset(i * stepSpacing, center.y)
                    )
                }
            }
        }
    }
}


private enum class Thumb { Start, End }

/** * Internal slider track renderer */
@Composable
private fun SliderTrack(
    modifier: Modifier,
    shape: Shape,
    backgroundColor: Color,
    foregroundColor: Color,
    progress: Float,
    minValue: Float,
    maxValue: Float,
    isDragging: Boolean,
) {
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.044f else 0f,
        animationSpec = tween(150)
    )
    Canvas(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .drawBehind { drawRect(Color.Black.copy(alpha = backgroundAlpha)) }
    ) {
        val barHeight = size.height
        val barWidth = size.width
        val progressWidth = barWidth * ((progress - minValue) / (maxValue - minValue))
        val cornerRadius = CornerRadius.Zero

        drawRoundRect(
            color = foregroundColor,
            size = Size(progressWidth, barHeight),
            topLeft = Offset(0f, center.y - barHeight / 2),
            cornerRadius = cornerRadius
        )
    }
}

private object SliderDefaults {
    val MinHeight = 30.dp

    @Composable
    fun sliderColors(
        sliderColors: M3SliderColors
    ): SliderColors = SliderColors(
        foregroundColor = sliderColors.activeTrackColor,
        disabledForegroundColor = sliderColors.disabledActiveTrackColor,
        backgroundColor = sliderColors.inactiveTrackColor
    )
}

@Immutable
private class SliderColors(
    private val foregroundColor: Color,
    private val disabledForegroundColor: Color,
    private val backgroundColor: Color
) {
    @Stable
    fun foregroundColor(enabled: Boolean): Color =
        if (enabled) foregroundColor else disabledForegroundColor

    @Stable
    fun backgroundColor(): Color = backgroundColor
}