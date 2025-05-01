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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ContainerShapeDefaults {

    @Composable
    fun shapeForIndex(
        index: Int,
        size: Int,
        forceDefault: Boolean = false,
    ): RoundedCornerShape {
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
        val topStart by animateDpAsState(
            internalShape.topStart.dp
        )
        val topEnd by animateDpAsState(
            internalShape.topEnd.dp
        )
        val bottomStart by animateDpAsState(
            internalShape.bottomStart.dp
        )
        val bottomEnd by animateDpAsState(
            internalShape.bottomEnd.dp
        )
        return RoundedCornerShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomStart = bottomStart,
            bottomEnd = bottomEnd
        )
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

    private val CornerSize.dp: Dp
        @Composable
        get() = with(LocalDensity.current) { toPx(Size.Unspecified, this).toDp() }
}

@Stable
internal class AnimatedShapeState(
    val shape: RoundedCornerShape,
    val density: Density,
    val spec: FiniteAnimationSpec<Float>,
) {
    private val size = Size(
        width = with(density) { 48.dp.toPx() },
        height = with(density) { 48.dp.toPx() }
    )

    private var topStart: Animatable<Float, AnimationVector1D> =
        Animatable(shape.topStart.toPx(size, density))

    private var topEnd: Animatable<Float, AnimationVector1D> =
        Animatable(shape.topEnd.toPx(size, density))

    private var bottomStart: Animatable<Float, AnimationVector1D> =
        Animatable(shape.bottomStart.toPx(size, density))

    private var bottomEnd: Animatable<Float, AnimationVector1D> =
        Animatable(shape.bottomEnd.toPx(size, density))

    fun topStart(): Float = topStart.value

    fun topEnd(): Float = topEnd.value

    fun bottomStart(): Float = bottomStart.value

    fun bottomEnd(): Float = bottomEnd.value

    suspend fun animateToShape(shape: CornerBasedShape) = coroutineScope {
        launch { topStart.animateTo(shape.topStart.toPx(size, density), spec) }
        launch { topEnd.animateTo(shape.topEnd.toPx(size, density), spec) }
        launch { bottomStart.animateTo(shape.bottomStart.toPx(size, density), spec) }
        launch { bottomEnd.animateTo(shape.bottomEnd.toPx(size, density), spec) }
    }
}

//TODO: Workaround for https://github.com/arkivanov/Decompose/issues/845
@Composable
private fun rememberAnimatedShapeImpl(
    state: AnimatedShapeState,
): Shape = remember(state) {
    object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val clampedRange = 0f..size.height / 2
            return RoundedCornerShape(
                topStart = state.topStart().coerceIn(clampedRange),
                topEnd = state.topEnd().coerceIn(clampedRange),
                bottomStart = state.bottomStart().coerceIn(clampedRange),
                bottomEnd = state.bottomEnd().coerceIn(clampedRange),
            ).createOutline(size, layoutDirection, density)
        }
    }
}

@Composable
internal fun rememberAnimatedShape(
    currentShape: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float>
): Shape {
    val density = LocalDensity.current

    val state = remember(animationSpec, density) {
        AnimatedShapeState(
            shape = currentShape,
            spec = animationSpec,
            density = density
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

    return rememberAnimatedShapeImpl(state)
}

@Composable
fun animateShape(
    targetValue: RoundedCornerShape,
    animationSpec: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    ),
): Shape = rememberAnimatedShape(
    currentShape = targetValue,
    animationSpec = animationSpec
)

@Composable
fun shapeByInteraction(
    shape: Shape,
    pressedShape: Shape,
    interactionSource: MutableInteractionSource?,
    animationSpec: FiniteAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    ),
    enabled: Boolean = true
): Shape {
    if (!enabled || interactionSource == null) return shape

    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val usePressedShape = pressed || focused

    val targetShapeState = remember {
        mutableStateOf(shape)
    }

    LaunchedEffect(usePressedShape) {
        if (usePressedShape) {
            targetShapeState.value = pressedShape
        } else {
            if (shape is RoundedCornerShape) delay(300)
            targetShapeState.value = shape
        }
    }

    val targetShape = targetShapeState.value

    if (targetShape is RoundedCornerShape) {
        return key(shape, pressedShape) {
            rememberAnimatedShape(
                currentShape = targetShape,
                animationSpec = animationSpec,
            )
        }
    }

    return targetShape
}