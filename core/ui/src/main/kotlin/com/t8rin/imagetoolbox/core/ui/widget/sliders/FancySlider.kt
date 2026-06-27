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

package com.t8rin.imagetoolbox.core.ui.widget.sliders

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.resources.utils.compositeOverSafe
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.animation.animateFloatingRangeAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberRipple
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCircleShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.materialShadow
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomRangeSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomRangeSliderState
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSlider
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSliderColors
import com.t8rin.imagetoolbox.core.ui.widget.sliders.custom_slider.CustomSliderState

@Composable
fun FancySlider(
    value: Float,
    enabled: Boolean,
    colors: SliderColors,
    interactionSource: MutableInteractionSource,
    thumbShape: Shape,
    modifier: Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    drawContainer: Boolean = true
) {
    val thumbColor by animateColorAsState(
        if (enabled) colors.thumbColor else colors.disabledThumbColor
    )
    val settingsState = LocalSettingsState.current

    val thumb: @Composable (CustomSliderState) -> Unit = { sliderState ->
        val sliderFraction by remember(value, sliderState) {
            derivedStateOf {
                (value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
            }
        }
        Spacer(
            Modifier
                .zIndex(100f)
                .rotate(1080f * sliderFraction)
                .size(26.dp)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = false,
                        radius = 24.dp
                    )
                )
                .hoverable(interactionSource = interactionSource)
                .materialShadow(
                    shape = thumbShape,
                    elevation = 1.dp,
                    enabled = settingsState.drawSliderShadows
                )
                .background(thumbColor, thumbShape)
        )
    }

    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        CustomSlider(
            interactionSource = interactionSource,
            thumb = thumb,
            enabled = enabled,
            modifier = modifier
                .then(
                    if (drawContainer) {
                        Modifier
                            .container(
                                shape = ShapeDefaults.circle,
                                autoShadowElevation = animateDpAsState(
                                    if (settingsState.drawSliderShadows) {
                                        1.dp
                                    } else 0.dp
                                ).value,
                                resultPadding = 0.dp,
                                borderColor = MaterialTheme.colorScheme
                                    .outlineVariant(
                                        luminance = 0.1f,
                                        onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                                    )
                                    .copy(0.3f),
                                color = SafeLocalContainerColor
                                    .copy(0.5f)
                                    .compositeOverSafe(MaterialTheme.colorScheme.surface)
                                    .copy(colors.activeTrackColor.alpha),
                                composeColorOnTopOfBackground = false
                            )
                            .padding(horizontal = 6.dp)
                    } else Modifier
                ),
            colors = colors.toCustom(),
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            track = { sliderState ->
                FancyTrack(
                    sliderState = sliderState,
                    colors = colors.toCustom(),
                    trackHeight = 38.dp,
                    enabled = enabled
                )
            }
        )
    }
}

@Composable
fun FancyRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    colors: SliderColors,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    thumbShape: Shape,
    modifier: Modifier,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    drawContainer: Boolean = true
) {
    val thumbColor by animateColorAsState(
        if (enabled) colors.thumbColor else colors.disabledThumbColor
    )


    val settingsState = LocalSettingsState.current
    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        CustomRangeSlider(
            startInteractionSource = startInteractionSource,
            endInteractionSource = endInteractionSource,
            enabled = enabled,
            modifier = modifier
                .then(
                    if (drawContainer) {
                        Modifier
                            .container(
                                shape = ShapeDefaults.circle,
                                autoShadowElevation = animateDpAsState(
                                    if (settingsState.drawSliderShadows) {
                                        1.dp
                                    } else 0.dp
                                ).value,
                                resultPadding = 0.dp,
                                borderColor = MaterialTheme.colorScheme
                                    .outlineVariant(
                                        luminance = 0.1f,
                                        onTopOf = SwitchDefaults.colors().disabledCheckedTrackColor
                                    )
                                    .copy(0.3f),
                                color = SafeLocalContainerColor
                                    .copy(0.5f)
                                    .compositeOverSafe(MaterialTheme.colorScheme.surface)
                                    .copy(colors.activeTrackColor.alpha),
                                composeColorOnTopOfBackground = false
                            )
                            .padding(horizontal = 6.dp)
                    } else Modifier
                ),
            colors = colors.toCustom(),
            value = animateFloatingRangeAsState(value).value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            startThumb = {
                Spacer(
                    Modifier
                        .zIndex(100f)
                        .size(26.dp)
                        .indication(
                            interactionSource = startInteractionSource,
                            indication = rememberRipple(
                                bounded = false,
                                radius = 24.dp
                            )
                        )
                        .hoverable(interactionSource = startInteractionSource)
                        .materialShadow(
                            shape = thumbShape,
                            elevation = 1.dp,
                            enabled = LocalSettingsState.current.drawSliderShadows
                        )
                        .background(thumbColor, thumbShape)
                )
            },
            endThumb = {
                Spacer(
                    Modifier
                        .zIndex(100f)
                        .size(26.dp)
                        .indication(
                            interactionSource = endInteractionSource,
                            indication = rememberRipple(
                                bounded = false,
                                radius = 24.dp
                            )
                        )
                        .hoverable(interactionSource = endInteractionSource)
                        .materialShadow(
                            shape = thumbShape,
                            elevation = 1.dp,
                            enabled = LocalSettingsState.current.drawSliderShadows
                        )
                        .background(thumbColor, thumbShape)
                )
            },
            steps = steps,
            track = { sliderState ->
                FancyTrack(
                    rangeSliderState = sliderState,
                    colors = colors.toCustom(),
                    trackHeight = 38.dp,
                    enabled = enabled
                )
            }
        )
    }
}

@Stable
internal fun SliderColors.toCustom(): CustomSliderColors = CustomSliderColors(
    thumbColor = thumbColor,
    activeTrackColor = activeTrackColor,
    activeTickColor = activeTickColor,
    inactiveTrackColor = inactiveTrackColor,
    inactiveTickColor = inactiveTickColor,
    disabledThumbColor = disabledThumbColor,
    disabledActiveTrackColor = disabledActiveTrackColor,
    disabledActiveTickColor = disabledActiveTickColor,
    disabledInactiveTrackColor = disabledInactiveTrackColor,
    disabledInactiveTickColor = disabledInactiveTickColor
)

@Composable
internal fun fancyThumbShape(): Shape {
    val shapesType = LocalSettingsState.current.shapesType

    return if (shapesType is ShapeType.Cut || shapesType is ShapeType.Scoop || shapesType is ShapeType.Notch) {
        remember(shapesType) {
            AutoCircleShape(
                shapesType = shapesType.copy(
                    strength = shapesType.strength.coerceAtLeast(0.5f)
                )
            )
        }
    } else {
        MaterialStarShape
    }
}

@Composable
private fun FancyTrack(
    sliderState: CustomSliderState,
    colors: CustomSliderColors,
    enabled: Boolean,
    trackHeight: Dp
) {
    FancyTrack(
        tickFractions = sliderState.tickFractions,
        activeRangeStart = 0f,
        activeRangeEnd = sliderState.coercedValueAsFraction,
        colors = colors,
        enabled = enabled,
        trackHeight = trackHeight
    )
}

@Composable
private fun FancyTrack(
    rangeSliderState: CustomRangeSliderState,
    colors: CustomSliderColors,
    enabled: Boolean,
    trackHeight: Dp
) {
    FancyTrack(
        tickFractions = rangeSliderState.tickFractions,
        activeRangeStart = rangeSliderState.coercedActiveRangeStartAsFraction,
        activeRangeEnd = rangeSliderState.coercedActiveRangeEndAsFraction,
        colors = colors,
        enabled = enabled,
        trackHeight = trackHeight
    )
}

@Composable
private fun FancyTrack(
    tickFractions: FloatArray,
    activeRangeStart: Float,
    activeRangeEnd: Float,
    colors: CustomSliderColors,
    enabled: Boolean,
    trackHeight: Dp,
    shape: Shape = ShapeDefaults.circle
) {
    val inactiveTrackColor = colors.trackColor(enabled, active = false)
    val activeTrackColor = colors.trackColor(enabled, active = true)
    val inactiveTickColor = colors.tickColor(enabled, active = false)
    val activeTickColor = colors.tickColor(enabled, active = true)

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(trackHeight)
    ) {
        drawFancyTrack(
            tickFractions = tickFractions,
            activeRangeStart = activeRangeStart,
            activeRangeEnd = activeRangeEnd,
            inactiveTrackColor = inactiveTrackColor,
            activeTrackColor = activeTrackColor,
            inactiveTickColor = inactiveTickColor,
            activeTickColor = activeTickColor,
            shape = shape
        )
    }
}

private fun DrawScope.drawFancyTrack(
    tickFractions: FloatArray,
    activeRangeStart: Float,
    activeRangeEnd: Float,
    inactiveTrackColor: Color,
    activeTrackColor: Color,
    inactiveTickColor: Color,
    activeTickColor: Color,
    shape: Shape
) {
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val activeStart = if (isRtl) 1f - activeRangeEnd else activeRangeStart
    val activeEnd = if (isRtl) 1f - activeRangeStart else activeRangeEnd
    val tickSize = TickSize.toPx()

    drawFancyTrackSegment(
        startFraction = 0f,
        endFraction = 1f,
        color = inactiveTrackColor,
        shape = shape
    )
    drawFancyTrackSegment(
        startFraction = activeStart,
        endFraction = activeEnd,
        color = activeTrackColor,
        shape = shape
    )

    for (tick in tickFractions) {
        val outsideFraction = tick !in activeRangeStart..activeRangeEnd
        val visualTick = if (isRtl) 1f - tick else tick
        drawCircle(
            color = if (outsideFraction) inactiveTickColor else activeTickColor,
            center = Offset(size.width * visualTick, center.y),
            radius = tickSize / 2f
        )
    }
}

private fun DrawScope.drawFancyTrackSegment(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    shape: Shape
) {
    val capRadius = size.height / 2f
    val left = size.width * startFraction.coerceIn(0f, 1f) - capRadius
    val right = size.width * endFraction.coerceIn(0f, 1f) + capRadius
    val width = right - left

    if (width <= 0f) return

    val outline = shape.createOutline(
        size = Size(
            width = width,
            height = size.height
        ),
        layoutDirection = layoutDirection,
        density = this
    )

    translate(left = left) {
        drawFancyTrackOutline(outline, color)
    }
}

private fun DrawScope.drawFancyTrackOutline(
    outline: Outline,
    color: Color
) {
    when (outline) {
        is Outline.Rectangle -> drawRect(
            color = color,
            topLeft = outline.rect.topLeft,
            size = outline.rect.size
        )

        is Outline.Rounded -> drawPath(
            path = Path().apply { addRoundRect(outline.roundRect) },
            color = color
        )

        is Outline.Generic -> drawPath(
            path = outline.path,
            color = color
        )
    }
}

private val TickSize = 2.dp