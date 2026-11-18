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

package com.t8rin.imagetoolbox.core.games.presentation.components

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.games.presentation.components.model.ArcadePhase
import com.t8rin.imagetoolbox.core.games.presentation.components.model.ArcadeRun
import com.t8rin.imagetoolbox.core.games.presentation.components.utils.ArcadeDefaults.LevelTransitionDuration
import com.t8rin.imagetoolbox.core.games.presentation.components.utils.longPress

@Composable
internal fun ArcadeField(
    run: ArcadeRun,
    worldSize: Size = Size(width = 360f, height = 220f),
    transition: (() -> Float)? = null,
    onPress: ((Offset) -> Unit)? = null,
    onSecondaryTap: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
    onLongPress: ((Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onRelease: (() -> Unit)? = null,
    onSwipe: ((Offset) -> Unit)? = null,
    swipeOnDrag: Boolean = false,
    lightFeedback: Boolean = false,
    draw: DrawScope.() -> Unit
) {
    val levelEnding by rememberUpdatedState(transition)
    val press by rememberUpdatedState(onPress)
    val secondaryTap by rememberUpdatedState(onSecondaryTap)
    val tap by rememberUpdatedState(onTap)
    val hold by rememberUpdatedState(onLongPress)
    val drag by rememberUpdatedState(onDrag)
    val release by rememberUpdatedState(onRelease)
    val swipe by rememberUpdatedState(onSwipe)
    val haptics = LocalHapticFeedback.current
    val feedback by rememberUpdatedState(
        if (lightFeedback) HapticFeedbackType.TextHandleMove else HapticFeedbackType.LongPress
    )
    val topInset = with(LocalDensity.current) { 44.dp.toPx() }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                run.frame
                val remaining = levelEnding?.invoke() ?: 0f
                val outgoing = if (remaining > 0f) {
                    (remaining / LevelTransitionDuration).coerceAtMost(1f)
                } else 1f
                val incoming = 1f - run.levelTransition / LevelTransitionDuration
                alpha = minOf(outgoing, incoming)
            }
            .onSizeChanged {
                val scale = arcadeScale(
                    width = it.width.toFloat(),
                    height = it.height.toFloat(),
                    worldSize = worldSize
                )
                if (scale > 0f) run.resize(
                    width = it.width / scale,
                    height = it.height / scale,
                    topInset = topInset / scale
                )
            }
            .pointerInput(run, haptics, swipeOnDrag, run.phase, worldSize) {
                fun acceptsInput() = run.phase == ArcadePhase.Running &&
                        run.levelTransition == 0f && (levelEnding?.invoke() ?: 0f) <= 0f

                if (run.phase != ArcadePhase.Running) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    if (!acceptsInput()) return@awaitEachGesture
                    val scale = arcadeScale(
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        worldSize = worldSize
                    )
                    if (scale <= 0f) return@awaitEachGesture
                    down.consume()
                    press?.let {
                        it(down.position / scale)
                        haptics.performHapticFeedback(feedback)
                    }
                    var swiped = false
                    var dragged = false
                    var held = false
                    var released = false
                    val gestureSize = size
                    val secondaryTouches = mutableMapOf<PointerId, Pair<Offset, Long>>()
                    try {
                        while (run.phase == ArcadePhase.Running) {
                            val event = if (hold != null && !held && !dragged) {
                                val remaining = viewConfiguration.longPressTimeoutMillis -
                                        (SystemClock.uptimeMillis() - down.uptimeMillis)
                                if (remaining <= 0L) null else withTimeoutOrNull(remaining) {
                                    awaitPointerEvent()
                                }
                            } else awaitPointerEvent()
                            if (!acceptsInput() || size != gestureSize) break
                            if (event == null) {
                                held = true
                                hold?.invoke(down.position / scale)
                                haptics.longPress()
                                continue
                            }
                            secondaryTap?.let { fire ->
                                event.changes.filter { it.id != down.id }.forEach { change ->
                                    if (change.isConsumed) {
                                        secondaryTouches.remove(change.id)
                                    } else if (change.pressed && !change.previousPressed) {
                                        secondaryTouches[change.id] =
                                            change.position to change.uptimeMillis
                                    } else {
                                        secondaryTouches[change.id]?.let { (position, time) ->
                                            if ((change.position - position).getDistance() > viewConfiguration.touchSlop) {
                                                secondaryTouches.remove(change.id)
                                            } else if (!change.pressed) {
                                                if (change.uptimeMillis - time < viewConfiguration.longPressTimeoutMillis) {
                                                    fire(change.position / scale)
                                                    haptics.performHapticFeedback(feedback)
                                                }
                                                secondaryTouches.remove(change.id)
                                            }
                                        }
                                    }
                                    change.consume()
                                }
                            }
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.isConsumed) break
                            val delta = change.position - down.position
                            if (delta.getDistance() > viewConfiguration.touchSlop) dragged = true
                            if (!swiped && (swipeOnDrag || !change.pressed)) {
                                if (delta.getDistance() > viewConfiguration.touchSlop) {
                                    swipe?.let {
                                        it(delta / scale)
                                        haptics.performHapticFeedback(feedback)
                                        swiped = true
                                    }
                                }
                            }
                            if (!change.pressed) {
                                released = true
                                if (!held && !swiped && !dragged && change.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis) {
                                    tap?.let {
                                        it(change.position / scale)
                                        if (press == null) haptics.performHapticFeedback(feedback)
                                    }
                                }
                                change.consume()
                                break
                            }
                            if (dragged) drag?.invoke(change.position / scale)
                            change.consume()
                        }
                    } finally {
                        if (released && acceptsInput()) release?.invoke()
                        else run.stopInput()
                    }
                }
            }
    ) {
        run.frame
        val scale = arcadeScale(
            width = size.width,
            height = size.height,
            worldSize = worldSize
        )
        if (scale > 0f) scale(scale, Offset.Zero) { draw() }
    }
}

private fun arcadeScale(
    width: Float,
    height: Float,
    worldSize: Size
): Float = minOf(width / worldSize.width, height / worldSize.height)