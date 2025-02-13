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
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.utils.cast

object ContainerShapeDefaults {

    @Composable
    fun shapeForIndex(
        index: Int,
        size: Int,
        forceDefault: Boolean = false,
    ): Shape {
        val internalShape by remember(index, size, forceDefault) {
            derivedStateOf {
                when {
                    index == -1 || size == 1 || forceDefault -> defaultShape
                    index == 0 && size > 1 -> topShape
                    index == size - 1 -> bottomShape
                    else -> centerShape
                }
            }
        }
        return animateShape(internalShape)
    }

    val topShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp
    )
    val centerShape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
        bottomStart = 6.dp,
        bottomEnd = 6.dp
    )
    val bottomShape = RoundedCornerShape(
        topStart = 6.dp,
        topEnd = 6.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    val defaultShape = RoundedCornerShape(16.dp)
}

@Stable
internal class AnimatedShapeState(
    val shape: RoundedCornerShape,
    val spec: FiniteAnimationSpec<Float>,
) {
    var size: Size = Size.Zero
    var density: Density = Density(0f, 0f)

    private var topStart: Animatable<Float, AnimationVector1D>? = null

    private var topEnd: Animatable<Float, AnimationVector1D>? = null

    private var bottomStart: Animatable<Float, AnimationVector1D>? = null

    private var bottomEnd: Animatable<Float, AnimationVector1D>? = null

    fun topStart(
        size: Size = this.size,
        density: Density = this.density
    ): Float {
        return (topStart ?: Animatable(shape.topStart.toPx(size, density)).also { topStart = it })
            .value
    }

    fun topEnd(
        size: Size = this.size,
        density: Density = this.density
    ): Float {
        return (topEnd ?: Animatable(shape.topEnd.toPx(size, density)).also { topEnd = it }).value
    }

    fun bottomStart(
        size: Size = this.size,
        density: Density = this.density
    ): Float {
        return (bottomStart
            ?: Animatable(shape.bottomStart.toPx(size, density)).also { bottomStart = it })
            .value
    }

    fun bottomEnd(
        size: Size = this.size,
        density: Density = this.density
    ): Float {
        return (bottomEnd
            ?: Animatable(shape.bottomEnd.toPx(size, density)).also { bottomEnd = it })
            .value
    }

    suspend fun animateToShape(shape: CornerBasedShape) = coroutineScope {
        launch { topStart?.animateTo(shape.topStart.toPx(size, density), spec) }
        launch { topEnd?.animateTo(shape.topEnd.toPx(size, density), spec) }
        launch { bottomStart?.animateTo(shape.bottomStart.toPx(size, density), spec) }
        launch { bottomEnd?.animateTo(shape.bottomEnd.toPx(size, density), spec) }
    }
}

@Composable
private fun rememberAnimatedShape(
    state: AnimatedShapeState,
): Shape {
    val density = LocalDensity.current
    state.density = density

    return remember(density, state) {
        object : ShapeWithHorizontalCenterOptically {
            var clampedRange by mutableStateOf(0f..1f)

            override fun offset(): Float {
                val topStart = state.topStart().coerceIn(clampedRange)
                val topEnd = state.topEnd().coerceIn(clampedRange)
                val bottomStart = state.bottomStart().coerceIn(clampedRange)
                val bottomEnd = state.bottomEnd().coerceIn(clampedRange)
                val avgStart = (topStart + bottomStart) / 2
                val avgEnd = (topEnd + bottomEnd) / 2
                return CenterOpticallyCoefficient * (avgStart - avgEnd)
            }

            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline {
                state.size = size

                clampedRange = 0f..size.height / 2
                return RoundedCornerShape(
                    topStart = state.topStart().coerceIn(clampedRange),
                    topEnd = state.topEnd().coerceIn(clampedRange),
                    bottomStart = state.bottomStart().coerceIn(clampedRange),
                    bottomEnd = state.bottomEnd().coerceIn(clampedRange),
                )
                    .createOutline(size, layoutDirection, density)
            }
        }
    }
}

internal const val CenterOpticallyCoefficient = 0.11f

internal interface ShapeWithHorizontalCenterOptically : Shape {
    fun offset(): Float
}

@Composable
internal fun rememberAnimatedShape(
    currentShape: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.defaultEffectsSpec(),
): Shape {
    val state =
        remember(animationSpec) {
            AnimatedShapeState(
                shape = currentShape,
                spec = animationSpec,
            )
        }

    val channel = remember { Channel<RoundedCornerShape>(Channel.CONFLATED) }

    SideEffect { channel.trySend(currentShape) }
    LaunchedEffect(state, channel) {
        for (target in channel) {
            val newTarget = channel.tryReceive().getOrNull() ?: target
            launch { state.animateToShape(newTarget) }
        }
    }

    return rememberAnimatedShape(state)
}

@Composable
fun animateShape(
    targetValue: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.defaultEffectsSpec(),
): Shape = rememberAnimatedShape(targetValue, animationSpec)

@Composable
fun shapeByInteraction(
    shape: Shape,
    pressedShape: Shape,
    interactionSource: MutableInteractionSource,
    animationSpec: FiniteAnimationSpec<Float> = MaterialTheme.motionScheme.defaultEffectsSpec()
): Shape {
    val pressed by interactionSource.collectIsPressedAsState()

    val resultShape = if (pressed) {
        pressedShape
    } else {
        shape
    }

    if (shape is RoundedCornerShape && pressedShape is RoundedCornerShape) {
        return key(shape, pressedShape) {
            rememberAnimatedShape(
                resultShape.cast(),
                animationSpec,
            )
        }
    }

    return resultShape
}