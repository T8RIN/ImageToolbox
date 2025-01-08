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

package ru.tech.imageresizershrinker.core.ui.widget.sliders.custom_slider

import androidx.annotation.IntRange
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * <a href="https://m3.material.io/components/sliders/overview" class="external" target="_blank">Material Design Range slider</a>.
 *
 * Range Sliders expand upon [CustomSlider] using the same concepts but allow the user to select 2 values.
 *
 * The two values are still bounded by the value range but they also cannot cross each other.
 *
 * Use continuous Range Sliders to allow users to make meaningful selections that don’t
 * require a specific values:
 *
 *
 * You can allow the user to choose only between predefined set of values by specifying the amount
 * of steps between min and max values:
 *
 *
 * @param value current values of the RangeSlider. If either value is outside of [valueRange]
 * provided, it will be coerced to this range.
 * @param onValueChange lambda in which values should be updated
 * @param modifier modifiers for the Range Slider layout
 * @param enabled whether or not component is enabled and can we interacted with or not
 * @param valueRange range of values that Range Slider values can take. Passed [value] will be
 * coerced to this range
 * @param steps if greater than 0, specifies the amounts of discrete values, evenly distributed
 * between across the whole value range. If 0, range slider will behave as a continuous slider and
 * allow to choose any value from the range specified. Must not be negative.
 * @param onValueChangeFinished lambda to be invoked when value change has ended. This callback
 * shouldn't be used to update the range slider values (use [onValueChange] for that), but rather to
 * know when the user has completed selecting a new value by ending a drag or a click.
 * @param colors [CustomSliderColors] that will be used to determine the color of the Range Slider
 * parts in different state. See [CustomSliderDefaults.colors] to customize.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0)
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: CustomSliderColors = CustomSliderDefaults.colors()
) {
    val startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    CustomRangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = {
            CustomSliderDefaults.Thumb(
                interactionSource = startInteractionSource,
                colors = colors,
                enabled = enabled
            )
        },
        endThumb = {
            CustomSliderDefaults.Thumb(
                interactionSource = endInteractionSource,
                colors = colors,
                enabled = enabled
            )
        },
        track = { rangeSliderState ->
            CustomSliderDefaults.Track(
                colors = colors,
                enabled = enabled,
                rangeSliderState = rangeSliderState
            )
        }
    )
}

/**
 * <a href="https://m3.material.io/components/sliders/overview" class="external" target="_blank">Material Design Range slider</a>.
 *
 * Range Sliders expand upon [CustomSlider] using the same concepts but allow the user to select 2 values.
 *
 * The two values are still bounded by the value range but they also cannot cross each other.
 *
 * It uses the provided startThumb for the slider's start thumb and endThumb for the
 * slider's end thumb. It also uses the provided track for the slider's track. If nothing is
 * passed for these parameters, it will use [CustomSliderDefaults.Thumb] and [CustomSliderDefaults.Track]
 * for the thumbs and track.
 *
 * Use continuous Range Sliders to allow users to make meaningful selections that don’t
 * require a specific values:
 *
 *
 * You can allow the user to choose only between predefined set of values by specifying the amount
 * of steps between min and max values:
 *
 * @param value current values of the RangeSlider. If either value is outside of [valueRange]
 * provided, it will be coerced to this range.
 * @param onValueChange lambda in which values should be updated
 * @param modifier modifiers for the Range Slider layout
 * @param enabled whether or not component is enabled and can we interacted with or not
 * @param onValueChangeFinished lambda to be invoked when value change has ended. This callback
 * shouldn't be used to update the range slider values (use [onValueChange] for that), but rather to
 * know when the user has completed selecting a new value by ending a drag or a click.
 * @param colors [CustomSliderColors] that will be used to determine the color of the Range Slider
 * parts in different state. See [CustomSliderDefaults.colors] to customize.
 * @param startInteractionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for the start thumb. You can create and pass in your own
 * `remember`ed instance to observe.
 * @param endInteractionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for the end thumb. You can create and pass in your own
 * `remember`ed instance to observe.
 * @param steps if greater than 0, specifies the amounts of discrete values, evenly distributed
 * between across the whole value range. If 0, range slider will behave as a continuous slider and
 * allow to choose any value from the range specified. Must not be negative.
 * @param startThumb the start thumb to be displayed on the Range Slider. The lambda receives a
 * [CustomRangeSliderState] which is used to obtain the current active track.
 * @param endThumb the end thumb to be displayed on the Range Slider. The lambda receives a
 * [CustomRangeSliderState] which is used to obtain the current active track.
 * @param track the track to be displayed on the range slider, it is placed underneath the thumb.
 * The lambda receives a [CustomRangeSliderState] which is used to obtain the current active track.
 * @param valueRange range of values that Range Slider values can take. Passed [value] will be
 * coerced to this range.
 */
@Composable
@ExperimentalMaterial3Api
fun CustomRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: CustomSliderColors = CustomSliderDefaults.colors(),
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    startThumb: @Composable (CustomRangeSliderState) -> Unit = {
        CustomSliderDefaults.Thumb(
            interactionSource = startInteractionSource,
            colors = colors,
            enabled = enabled
        )
    },
    endThumb: @Composable (CustomRangeSliderState) -> Unit = {
        CustomSliderDefaults.Thumb(
            interactionSource = endInteractionSource,
            colors = colors,
            enabled = enabled
        )
    },
    track: @Composable (CustomRangeSliderState) -> Unit = { rangeSliderState ->
        CustomSliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            rangeSliderState = rangeSliderState
        )
    },
    @IntRange(from = 0)
    steps: Int = 0
) {
    val state = remember(
        steps,
        valueRange,
        onValueChangeFinished
    ) {
        CustomRangeSliderState(
            value.start,
            value.endInclusive,
            steps,
            onValueChangeFinished,
            valueRange
        )
    }

    state.onValueChange = { onValueChange(it.start..it.endInclusive) }
    state.activeRangeStart = value.start
    state.activeRangeEnd = value.endInclusive

    CustomRangeSlider(
        modifier = modifier,
        state = state,
        enabled = enabled,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = startThumb,
        endThumb = endThumb,
        track = track
    )
}

/**
 * <a href="https://m3.material.io/components/sliders/overview" class="external" target="_blank">Material Design Range slider</a>.
 *
 * Range Sliders expand upon [CustomSlider] using the same concepts but allow the user to select 2 values.
 *
 * The two values are still bounded by the value range but they also cannot cross each other.
 *
 * It uses the provided startThumb for the slider's start thumb and endThumb for the
 * slider's end thumb. It also uses the provided track for the slider's track. If nothing is
 * passed for these parameters, it will use [CustomSliderDefaults.Thumb] and [CustomSliderDefaults.Track]
 * for the thumbs and track.
 *
 * Use continuous Range Sliders to allow users to make meaningful selections that don’t
 * require a specific values:
 *
 * You can allow the user to choose only between predefined set of values by specifying the amount
 * of steps between min and max values:
 *
 * A custom start/end thumb and track can be provided:
 *
 * @param state [CustomRangeSliderState] which contains the current values of the RangeSlider.
 * @param modifier modifiers for the Range Slider layout
 * @param enabled whether or not component is enabled and can we interacted with or not
 * @param colors [CustomSliderColors] that will be used to determine the color of the Range Slider
 * parts in different state. See [CustomSliderDefaults.colors] to customize.
 * @param startInteractionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for the start thumb. You can create and pass in your own
 * `remember`ed instance to observe.
 * @param endInteractionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for the end thumb. You can create and pass in your own
 * `remember`ed instance to observe.
 * @param startThumb the start thumb to be displayed on the Range Slider. The lambda receives a
 * [CustomRangeSliderState] which is used to obtain the current active track.
 * @param endThumb the end thumb to be displayed on the Range Slider. The lambda receives a
 * [CustomRangeSliderState] which is used to obtain the current active track.
 * @param track the track to be displayed on the range slider, it is placed underneath the thumb.
 * The lambda receives a [CustomRangeSliderState] which is used to obtain the current active track.
 */
@Composable
@ExperimentalMaterial3Api
fun CustomRangeSlider(
    state: CustomRangeSliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CustomSliderColors = CustomSliderDefaults.colors(),
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    startThumb: @Composable (CustomRangeSliderState) -> Unit = {
        CustomSliderDefaults.Thumb(
            interactionSource = startInteractionSource,
            colors = colors,
            enabled = enabled
        )
    },
    endThumb: @Composable (CustomRangeSliderState) -> Unit = {
        CustomSliderDefaults.Thumb(
            interactionSource = endInteractionSource,
            colors = colors,
            enabled = enabled
        )
    },
    track: @Composable (CustomRangeSliderState) -> Unit = { rangeSliderState ->
        CustomSliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            rangeSliderState = rangeSliderState
        )
    }
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    CustomRangeSliderImpl(
        modifier = modifier,
        state = state,
        enabled = enabled,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = startThumb,
        endThumb = endThumb,
        track = track
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomRangeSliderImpl(
    modifier: Modifier,
    state: CustomRangeSliderState,
    enabled: Boolean,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    startThumb: @Composable ((CustomRangeSliderState) -> Unit),
    endThumb: @Composable ((CustomRangeSliderState) -> Unit),
    track: @Composable ((CustomRangeSliderState) -> Unit)
) {
    state.isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val pressDrag = Modifier.rangeSliderPressDragModifier(
        state,
        startInteractionSource,
        endInteractionSource,
        enabled
    )

    val startThumbSemantics = Modifier.rangeSliderStartThumbSemantics(state, enabled)
    val endThumbSemantics = Modifier.rangeSliderEndThumbSemantics(state, enabled)

    Layout(
        {
            Box(
                modifier = Modifier
                    .layoutId(RangeSliderComponents.START_THUMB)
                    .focusable(enabled, startInteractionSource)
                    .then(startThumbSemantics)
            ) { startThumb(state) }
            Box(
                modifier = Modifier
                    .layoutId(RangeSliderComponents.END_THUMB)
                    .focusable(enabled, endInteractionSource)
                    .then(endThumbSemantics)
            ) { endThumb(state) }
            Box(modifier = Modifier.layoutId(RangeSliderComponents.TRACK)) {
                track(state)
            }
        },
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredSizeIn(
                minWidth = 20.dp,
                minHeight = 20.dp
            )
            .then(pressDrag)
    ) { measurables, constraints ->
        val startThumbPlaceable = measurables.fastFirst {
            it.layoutId == RangeSliderComponents.START_THUMB
        }.measure(
            constraints
        )

        val endThumbPlaceable = measurables.fastFirst {
            it.layoutId == RangeSliderComponents.END_THUMB
        }.measure(
            constraints
        )

        val trackPlaceable = measurables.fastFirst {
            it.layoutId == RangeSliderComponents.TRACK
        }.measure(
            constraints.offset(
                horizontal = -(startThumbPlaceable.width + endThumbPlaceable.width) / 2
            ).copy(minHeight = 0)
        )

        val sliderWidth = trackPlaceable.width +
                (startThumbPlaceable.width + endThumbPlaceable.width) / 2
        val sliderHeight = maxOf(
            trackPlaceable.height,
            startThumbPlaceable.height,
            endThumbPlaceable.height
        )

        state.startThumbWidth = startThumbPlaceable.width.toFloat()
        state.endThumbWidth = endThumbPlaceable.width.toFloat()
        state.totalWidth = sliderWidth

        state.updateMinMaxPx()

        val trackOffsetX = startThumbPlaceable.width / 2
        val startThumbOffsetX = (trackPlaceable.width * state.coercedActiveRangeStartAsFraction)
            .roundToInt()
        // When start thumb and end thumb have different widths,
        // we need to add a correction for the centering of the slider.
        val endCorrection = (startThumbPlaceable.width - endThumbPlaceable.width) / 2
        val endThumbOffsetX =
            (trackPlaceable.width * state.coercedActiveRangeEndAsFraction + endCorrection)
                .roundToInt()
        val trackOffsetY = (sliderHeight - trackPlaceable.height) / 2
        val startThumbOffsetY = (sliderHeight - startThumbPlaceable.height) / 2
        val endThumbOffsetY = (sliderHeight - endThumbPlaceable.height) / 2

        layout(
            sliderWidth,
            sliderHeight
        ) {
            trackPlaceable.placeRelative(
                trackOffsetX,
                trackOffsetY
            )
            startThumbPlaceable.placeRelative(
                startThumbOffsetX,
                startThumbOffsetY
            )
            endThumbPlaceable.placeRelative(
                endThumbOffsetX,
                endThumbOffsetY
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
private fun Modifier.rangeSliderPressDragModifier(
    state: CustomRangeSliderState,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    enabled: Boolean
): Modifier = if (enabled) {
    pointerInput(startInteractionSource, endInteractionSource, state) {
        val rangeSliderLogic = RangeSliderLogic(
            state,
            startInteractionSource,
            endInteractionSource
        )
        coroutineScope {
            awaitEachGesture {
                val event = awaitFirstDown(requireUnconsumed = false)
                val interaction = DragInteraction.Start()
                var posX = if (state.isRtl)
                    state.totalWidth - event.position.x else event.position.x
                val compare = rangeSliderLogic.compareOffsets(posX)
                var draggingStart = if (compare != 0) {
                    compare < 0
                } else {
                    state.rawOffsetStart > posX
                }

                awaitSlop(event.id, event.type)?.let {
                    val slop = viewConfiguration.pointerSlop(event.type)
                    val shouldUpdateCapturedThumb = abs(state.rawOffsetEnd - posX) < slop &&
                            abs(state.rawOffsetStart - posX) < slop
                    if (shouldUpdateCapturedThumb) {
                        val dir = it.second
                        draggingStart = if (state.isRtl) dir >= 0f else dir < 0f
                        posX += it.first.positionChange().x
                    }
                }

                rangeSliderLogic.captureThumb(
                    draggingStart,
                    posX,
                    interaction,
                    this@coroutineScope
                )

                val finishInteraction = try {
                    val success = horizontalDrag(pointerId = event.id) {
                        val deltaX = it.positionChange().x
                        state.onDrag(draggingStart, if (state.isRtl) -deltaX else deltaX)
                    }
                    if (success) {
                        DragInteraction.Stop(interaction)
                    } else {
                        DragInteraction.Cancel(interaction)
                    }
                } catch (_: CancellationException) {
                    DragInteraction.Cancel(interaction)
                }

                state.gestureEndAction(draggingStart)
                launch {
                    rangeSliderLogic
                        .activeInteraction(draggingStart)
                        .emit(finishInteraction)
                }
            }
        }
    }
} else {
    this
}

private suspend fun AwaitPointerEventScope.awaitSlop(
    id: PointerId,
    type: PointerType
): Pair<PointerInputChange, Float>? {
    var initialDelta = 0f
    val postPointerSlop = { pointerInput: PointerInputChange, offset: Float ->
        pointerInput.consume()
        initialDelta = offset
    }
    val afterSlopResult = awaitHorizontalPointerSlopOrCancellation(id, type, postPointerSlop)
    return if (afterSlopResult != null) afterSlopResult to initialDelta else null
}

@OptIn(ExperimentalMaterial3Api::class)
private class RangeSliderLogic(
    val state: CustomRangeSliderState,
    val startInteractionSource: MutableInteractionSource,
    val endInteractionSource: MutableInteractionSource
) {
    fun activeInteraction(draggingStart: Boolean): MutableInteractionSource =
        if (draggingStart) startInteractionSource else endInteractionSource

    fun compareOffsets(eventX: Float): Int {
        val diffStart = abs(state.rawOffsetStart - eventX)
        val diffEnd = abs(state.rawOffsetEnd - eventX)
        return diffStart.compareTo(diffEnd)
    }

    fun captureThumb(
        draggingStart: Boolean,
        posX: Float,
        interaction: Interaction,
        scope: CoroutineScope
    ) {
        state.onDrag(
            draggingStart,
            posX - if (draggingStart) state.rawOffsetStart else state.rawOffsetEnd
        )
        scope.launch {
            activeInteraction(draggingStart).emit(interaction)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun Modifier.rangeSliderStartThumbSemantics(
    state: CustomRangeSliderState,
    enabled: Boolean
): Modifier {
    val valueRange = state.valueRange.start..state.activeRangeEnd

    return semantics {

        if (!enabled) disabled()
        setProgress(
            action = { targetValue ->
                var newValue = targetValue.coerceIn(
                    valueRange.start,
                    valueRange.endInclusive
                )
                val originalVal = newValue
                val resolvedValue = if (state.startSteps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.startSteps + 1) {
                        val stepValue = lerp(
                            valueRange.start,
                            valueRange.endInclusive,
                            i.toFloat() / (state.startSteps + 1)
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.activeRangeStart) {
                    false
                } else {
                    val resolvedRange = CustomSliderRange(resolvedValue, state.activeRangeEnd)
                    val activeRange =
                        CustomSliderRange(state.activeRangeStart, state.activeRangeEnd)
                    if (resolvedRange != activeRange) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.let { it(resolvedRange) }
                        } else {
                            state.activeRangeStart = resolvedRange.start
                            state.activeRangeEnd = resolvedRange.endInclusive
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            }
        )
    }.progressSemantics(
        state.activeRangeStart,
        valueRange,
        state.startSteps
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun Modifier.rangeSliderEndThumbSemantics(
    state: CustomRangeSliderState,
    enabled: Boolean
): Modifier {
    val valueRange = state.activeRangeStart..state.valueRange.endInclusive

    return semantics {
        if (!enabled) disabled()

        setProgress(
            action = { targetValue ->
                var newValue = targetValue.coerceIn(valueRange.start, valueRange.endInclusive)
                val originalVal = newValue
                val resolvedValue = if (state.endSteps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.endSteps + 1) {
                        val stepValue = lerp(
                            valueRange.start,
                            valueRange.endInclusive,
                            i.toFloat() / (state.endSteps + 1)
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.activeRangeEnd) {
                    false
                } else {
                    val resolvedRange = CustomSliderRange(state.activeRangeStart, resolvedValue)
                    val activeRange =
                        CustomSliderRange(state.activeRangeStart, state.activeRangeEnd)
                    if (resolvedRange != activeRange) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.let { it(resolvedRange) }
                        } else {
                            state.activeRangeStart = resolvedRange.start
                            state.activeRangeEnd = resolvedRange.endInclusive
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            }
        )
    }.progressSemantics(
        state.activeRangeEnd,
        valueRange,
        state.endSteps
    )
}

private enum class RangeSliderComponents {
    END_THUMB,
    START_THUMB,
    TRACK
}