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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.ui.utils.animation.OverslideEasing

@Composable
fun Modifier.trackOverslide(
    value: Float,
    onNewOverslideAmount: (Float) -> Unit,
): Modifier {

    val valueState = rememberUpdatedState(value)
    val scope = rememberCoroutineScope()
    val overslideAmountAnimatable = remember { Animatable(0f, .0001f) }
    var length by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        snapshotFlow { overslideAmountAnimatable.value }.collect {
            onNewOverslideAmount(OverslideEasing.transform(it / length))
        }
    }

    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr

    return onSizeChanged { length = it.width.toFloat() }
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                // User has touched the screen

                awaitHorizontalTouchSlopOrCancellation(down.id) { _, _ -> }
                // User has moved the minimum horizontal amount to recognize a drag

                var overslideAmount = 0f

                // Start tracking horizontal drag amount
                horizontalDrag(down.id) {
                    // Negate the change in X when Rtl language is used
                    val deltaX = it.positionChange().x * if (isLtr) 1f else -1f

                    // Clamp overslide amount
                    overslideAmount = when (valueState.value) {
                        0f -> (overslideAmount + deltaX).coerceAtMost(0f)
                        1f -> (overslideAmount + deltaX).coerceAtLeast(1f)
                        else -> 0f
                    }

                    // Animate to new overslide amount
                    scope.launch {
                        overslideAmountAnimatable.animateTo(overslideAmount)
                    }
                }
                // User has lifted finger off the screen
                // Drag has stopped

                // Animate overslide to 0, with a bounce
                scope.launch {
                    overslideAmountAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = .45f,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }
        }
}