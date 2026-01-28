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

@file:Suppress("NOTHING_TO_INLINE")

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.t8rin.imagetoolbox.core.domain.utils.autoCast
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.animation.lessSpringySpec
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object ShapeDefaults {

    @Composable
    fun byIndex(
        index: Int,
        size: Int,
        forceDefault: Boolean = false,
        vertical: Boolean = true
    ): Shape {
        val internalShape = when {
            index == -1 || size == 1 || forceDefault -> default
            index == 0 && size > 1 -> if (vertical) top else start
            index == size - 1 -> if (vertical) bottom else end
            else -> center
        }

        return AutoCornersShape(
            topStart = internalShape.topStart.animate(),
            topEnd = internalShape.topEnd.animate(),
            bottomStart = internalShape.bottomStart.animate(),
            bottomEnd = internalShape.bottomEnd.animate()
        )
    }

    val top
        @Composable get() = AutoCornersShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp
        )

    val center
        @Composable get() = AutoCornersShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp
        )

    val bottom
        @Composable get() = AutoCornersShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )

    val start
        @Composable get() = AutoCornersShape(
            topStart = 16.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 4.dp
        )

    val end
        @Composable get() = AutoCornersShape(
            topStart = 4.dp,
            topEnd = 16.dp,
            bottomStart = 4.dp,
            bottomEnd = 16.dp
        )

    val topEnd
        @Composable get() = AutoCornersShape(
            topEnd = 16.dp,
            topStart = 4.dp,
            bottomEnd = 4.dp,
            bottomStart = 4.dp
        )

    val topStart
        @Composable get() = AutoCornersShape(
            topEnd = 4.dp,
            topStart = 16.dp,
            bottomEnd = 4.dp,
            bottomStart = 4.dp
        )

    val bottomEnd
        @Composable get() = AutoCornersShape(
            topEnd = 4.dp,
            topStart = 4.dp,
            bottomEnd = 16.dp,
            bottomStart = 4.dp
        )

    val smallTop
        @Composable get() = AutoCornersShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp
        )

    val smallBottom
        @Composable get() = AutoCornersShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        )

    val smallStart
        @Composable get() = AutoCornersShape(
            topStart = 12.dp,
            topEnd = 4.dp,
            bottomStart = 12.dp,
            bottomEnd = 4.dp
        )

    val smallEnd
        @Composable get() = AutoCornersShape(
            topEnd = 12.dp,
            topStart = 4.dp,
            bottomEnd = 12.dp,
            bottomStart = 4.dp
        )

    val extremeSmall @Composable get() = AutoCornersShape(2.dp)

    val extraSmall @Composable get() = AutoCornersShape(4.dp)

    val pressed @Composable get() = AutoCornersShape(6.dp)

    val mini @Composable get() = AutoCornersShape(8.dp)

    val smallMini @Composable get() = AutoCornersShape(10.dp)

    val small @Composable get() = AutoCornersShape(12.dp)

    val default @Composable get() = AutoCornersShape(16.dp)

    val large @Composable get() = AutoCornersShape(20.dp)

    val extraLarge @Composable get() = AutoCornersShape(24.dp)

    val extremeLarge @Composable get() = AutoCornersShape(28.dp)

    val circle @Composable get() = AutoCircleShape()

    @Composable
    private inline fun CornerSize.animate(): Dp = animateDpAsState(
        targetValue = with(LocalDensity.current) {
            toPx(
                shapeSize = Size.Unspecified,
                density = this
            ).toDp()
        },
        animationSpec = lessSpringySpec()
    ).value

}

@JvmInline
value class CornerSides internal constructor(private val mask: Int) {
    companion object {
        val TopStart = CornerSides(1 shl 0)
        val TopEnd = CornerSides(1 shl 1)
        val BottomStart = CornerSides(1 shl 2)
        val BottomEnd = CornerSides(1 shl 3)

        val Top = TopStart + TopEnd
        val Bottom = BottomStart + BottomEnd
        val Start = TopStart + BottomStart
        val End = TopEnd + BottomEnd

        val All = Top + Bottom
        val None = CornerSides(0)
    }

    operator fun plus(other: CornerSides): CornerSides = CornerSides(mask or other.mask)

    operator fun contains(other: CornerSides): Boolean = (mask and other.mask) == other.mask

    fun has(other: CornerSides): Boolean = contains(other)
}


inline fun <reified S : CornerBasedShape> S.only(
    sides: CornerSides
): S = autoCast {
    copy(
        topStart = if (sides.has(CornerSides.TopStart)) topStart else ZeroCornerSize,
        topEnd = if (sides.has(CornerSides.TopEnd)) topEnd else ZeroCornerSize,
        bottomEnd = if (sides.has(CornerSides.BottomEnd)) bottomEnd else ZeroCornerSize,
        bottomStart = if (sides.has(CornerSides.BottomStart)) bottomStart else ZeroCornerSize,
    )
}

val ZeroCornerSize: CornerSize = CornerSize(0f)

@Stable
internal class AnimatedShape(
    initialShape: CornerBasedShape,
    val shapesType: ShapeType,
    private val density: Density,
    private val animationSpec: FiniteAnimationSpec<Float>,
) : Shape {

    private var size: Size = Size(
        width = with(density) { 48.dp.toPx() },
        height = with(density) { 48.dp.toPx() }
    )

    private val halfHeight: Float get() = size.height / 2f

    private val topStart = Animatable(initialShape.topStart.toPx())
    private val topEnd = Animatable(initialShape.topEnd.toPx())
    private val bottomStart = Animatable(initialShape.bottomStart.toPx())
    private val bottomEnd = Animatable(initialShape.bottomEnd.toPx())

    private inline fun CornerSize.toPx() = toPx(size, density)

    private suspend inline fun ShapeAnimatable.animateTo(
        cornerSize: CornerSize
    ) = animateTo(
        targetValue = cornerSize.toPx(),
        animationSpec = animationSpec
    )

    private inline fun ShapeAnimatable.boundedValue() = value.fastCoerceIn(0f, halfHeight)

    suspend fun animateTo(targetShape: CornerBasedShape) = coroutineScope {
        launch { topStart.animateTo(targetShape.topStart) }
        launch { topEnd.animateTo(targetShape.topEnd) }
        launch { bottomStart.animateTo(targetShape.bottomStart) }
        launch { bottomEnd.animateTo(targetShape.bottomEnd) }
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = createOutline(
        size = size,
        layoutDirection = layoutDirection,
        density = density,
        shapesType = shapesType
    )

    fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
        shapesType: ShapeType
    ): Outline {
        if (size.width > 1f && size.height > 1f) {
            this.size = size
        }

        return AutoCornersShape(
            topStart = CornerSize(topStart.boundedValue()),
            topEnd = CornerSize(topEnd.boundedValue()),
            bottomStart = CornerSize(bottomStart.boundedValue()),
            bottomEnd = CornerSize(bottomEnd.boundedValue()),
            shapesType = shapesType
        ).createOutline(
            size = size,
            layoutDirection = layoutDirection,
            density = density
        )
    }

}

internal typealias ShapeAnimatable = Animatable<Float, AnimationVector1D>

@Composable
internal fun rememberAnimatedShape(
    currentShape: CornerBasedShape,
    animationSpec: FiniteAnimationSpec<Float> = lessSpringySpec(),
): AnimatedShape {
    val density = LocalDensity.current
    val shapesType = LocalSettingsState.current.shapesType

    val state = remember(animationSpec, density, shapesType) {
        AnimatedShape(
            shapesType = shapesType,
            initialShape = currentShape,
            animationSpec = animationSpec,
            density = density
        )
    }

    val channel = remember { Channel<CornerBasedShape>(Channel.CONFLATED) }

    SideEffect { channel.trySend(currentShape) }

    LifecycleResumeEffect(Unit) {
        channel.trySend(currentShape)

        onPauseOrDispose {
            channel.trySend(currentShape)
        }
    }

    LaunchedEffect(state, channel) {
        for (target in channel) {
            val newTarget = channel.tryReceive().getOrNull() ?: target
            launch { state.animateTo(newTarget) }
        }
    }

    return state
}

@Composable
fun animateShape(
    targetValue: CornerBasedShape,
    animationSpec: FiniteAnimationSpec<Float> = lessSpringySpec(),
): Shape = rememberAnimatedShape(
    currentShape = targetValue,
    animationSpec = animationSpec
)

@Composable
fun shapeByInteraction(
    shape: Shape,
    pressedShape: Shape,
    interactionSource: InteractionSource?,
    animationSpec: FiniteAnimationSpec<Float> = lessSpringySpec(),
    enabled: Boolean = true
): Shape {
    if (!enabled || interactionSource == null) return shape

    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val usePressedShape = pressed || focused

    val targetShape = if (usePressedShape) pressedShape else shape

    if (targetShape is CornerBasedShape) {
        return animateShape(
            targetValue = targetShape,
            animationSpec = animationSpec,
        )
    }

    return targetShape
}