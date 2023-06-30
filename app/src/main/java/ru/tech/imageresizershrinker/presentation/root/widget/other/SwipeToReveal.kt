package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SnapSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
typealias RevealState = SwipeableState<RevealValue>


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToReveal(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    alphaEasing: Easing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f),
    maxRevealDp: Dp = 75.dp,
    maxAmountOfOverflow: Dp = 250.dp,
    directions: Set<RevealDirection> = setOf(
        RevealDirection.StartToEnd,
    ),
    alphaTransformEnabled: Boolean = false,
    state: RevealState = rememberRevealState(),
    revealedContentEnd: @Composable BoxScope.() -> Unit = {},
    revealedContentStart: @Composable BoxScope.() -> Unit = {},
    swipeableContent: @Composable () -> Unit,
) {
    Box {
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
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart,
                    content = revealedContentStart
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterEnd,
                    content = revealedContentEnd
                )
            }
        }

        Box(
            modifier = modifier
                .then(
                    if (enableSwipe) Modifier
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
                            directions = directions
                        )
                    else Modifier
                )
        ) {
            swipeableContent()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun Modifier.revealSwipeable(
    maxRevealPx: Float,
    maxAmountOfOverflow: Dp,
    directions: Set<RevealDirection>,
    state: RevealState,
    enabled: Boolean = true
) = composed {

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
        enabled = enabled, // state.value == RevealValue.Default,
        reverseDirection = isRtl,
        resistance = ResistanceConfig(
            basis = maxAmountOfOverflowPx,
            factorAtMin = minFactor,
            factorAtMax = maxFactor
        )
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
@OptIn(ExperimentalMaterialApi::class)
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
@OptIn(ExperimentalMaterialApi::class)
suspend fun RevealState.reset() {
    animateTo(
        targetValue = RevealValue.Default,
    )
}

/**
 * Reset the component to the default position, with an animation.
 */
@OptIn(ExperimentalMaterialApi::class)
suspend fun RevealState.resetFast() {
    animateTo(
        targetValue = RevealValue.Default,
        anim = SnapSpec(1)
    )
}