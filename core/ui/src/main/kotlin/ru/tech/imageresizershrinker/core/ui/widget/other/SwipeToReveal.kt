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

@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DEPRECATION")

package ru.tech.imageresizershrinker.core.ui.widget.other

import androidx.compose.animation.core.Easing
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.utils.animation.AlphaEasing
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

typealias RevealState = SwipeableState<RevealValue>

@Composable
fun SwipeToReveal(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    alphaEasing: Easing = AlphaEasing,
    maxRevealDp: Dp = 75.dp,
    maxAmountOfOverflow: Dp = 250.dp,
    directions: Set<RevealDirection> = setOf(
        RevealDirection.StartToEnd,
    ),
    alphaTransformEnabled: Boolean = false,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    state: RevealState = rememberRevealState(),
    revealedContentEnd: @Composable BoxScope.() -> Unit = {},
    revealedContentStart: @Composable BoxScope.() -> Unit = {},
    swipeableContent: @Composable () -> Unit,
) {
    Box(modifier) {
        val maxRevealPx = with(LocalDensity.current) { maxRevealDp.toPx() }
        val draggedRatio =
            (state.offset.value.absoluteValue / maxRevealPx.absoluteValue).coerceIn(0f, 1f)

        val alpha = if (alphaTransformEnabled) alphaEasing.transform(draggedRatio) else 1f

        // non swipable with hidden content
        Box(
            modifier = Modifier.matchParentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart,
                    content = revealedContentStart
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd,
                    content = revealedContentEnd
                )
            }
        }

        Box(
            modifier = Modifier
                .then(
                    if (enableSwipe) {
                        Modifier
                            .offset {
                                IntOffset(
                                    state.offset.value.roundToInt(),
                                    0
                                )
                            }
                            .revealSwipeable(
                                state = state,
                                maxRevealPx = maxRevealPx,
                                maxAmountOfOverflow = maxAmountOfOverflow,
                                directions = directions,
                                interactionSource = interactionSource
                            )
                    } else Modifier
                )
        ) {
            swipeableContent()
        }
    }
}


fun Modifier.revealSwipeable(
    maxRevealPx: Float,
    maxAmountOfOverflow: Dp,
    directions: Set<RevealDirection>,
    state: RevealState,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource
) = this.composed {

    val maxAmountOfOverflowPx = with(LocalDensity.current) { maxAmountOfOverflow.toPx() }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val anchors = mutableMapOf(0f to RevealValue.Default)

    if (RevealDirection.StartToEnd in directions) anchors += maxRevealPx to RevealValue.FullyRevealedEnd
    if (RevealDirection.EndToStart in directions) anchors += -maxRevealPx to RevealValue.FullyRevealedStart

    val thresholds = { _: RevealValue, _: RevealValue ->
        FractionalThreshold(0.5f)
    }

    val minFactor =
        if (RevealDirection.EndToStart in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor
    val maxFactor =
        if (RevealDirection.StartToEnd in directions) SwipeableDefaults.StandardResistanceFactor else SwipeableDefaults.StiffResistanceFactor

    Modifier.swipeable(
        state = state,
        anchors = anchors,
        thresholds = thresholds,
        orientation = Orientation.Horizontal,
        enabled = enabled, // state.value == RevealValue.System,
        reverseDirection = isRtl,
        resistance = ResistanceConfig(
            basis = maxAmountOfOverflowPx,
            factorAtMin = minFactor,
            factorAtMax = maxFactor
        ),
        interactionSource = interactionSource
    )
}

enum class RevealDirection {
    /**
     * Can be dismissed by swiping in the reading direction.
     */
    StartToEnd,

    /**
     * Can be dismissed by swiping in the reverse of the reading direction.
     */
    EndToStart
}

/**
 * Possible values of [RevealState].
 */
enum class RevealValue {
    /**
     * Indicates the component has not been revealed yet.
     */
    Default,

    /**
     * Fully revealed to end
     */
    FullyRevealedEnd,

    /**
     * Fully revealed to start
     */
    FullyRevealedStart,
}

/**
 * Create and [remember] a [RevealState] with the default animation clock.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberRevealState(
    initialValue: RevealValue = RevealValue.Default,
    confirmStateChange: (RevealValue) -> Boolean = { true },
): RevealState {
    return rememberSwipeableState(
        initialValue = initialValue,
        confirmStateChange = confirmStateChange
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
suspend fun RevealState.reset() {
    animateTo(
        targetValue = RevealValue.Default,
    )
}