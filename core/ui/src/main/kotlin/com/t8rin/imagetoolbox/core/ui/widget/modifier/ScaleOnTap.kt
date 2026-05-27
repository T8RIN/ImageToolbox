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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalHapticFeedback
import com.t8rin.imagetoolbox.core.ui.utils.animation.springySpec
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.scaleOnTap(
    initial: Float = 1f,
    min: Float = 0.8f,
    max: Float = 1.3f,
    interactionSource: MutableInteractionSource? = null,
    onHold: () -> Unit = {},
    onRelease: (time: Long) -> Unit
): Modifier = this then ScaleOnTapElement(
    initial = initial,
    min = min,
    max = max,
    interactionSource = interactionSource,
    onHold = onHold,
    onRelease = onRelease
)

private data class ScaleOnTapElement(
    val initial: Float,
    val min: Float,
    val max: Float,
    val interactionSource: MutableInteractionSource?,
    val onHold: () -> Unit,
    val onRelease: (time: Long) -> Unit
) : ModifierNodeElement<ScaleOnTapNode>() {

    override fun create(): ScaleOnTapNode = ScaleOnTapNode(
        initial = initial,
        min = min,
        max = max,
        interactionSource = interactionSource,
        onHold = onHold,
        onRelease = onRelease
    )

    override fun update(node: ScaleOnTapNode) {
        node.update(
            initial = initial,
            min = min,
            max = max,
            interactionSource = interactionSource,
            onHold = onHold,
            onRelease = onRelease
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "scaleOnTap"
        properties["initial"] = initial
        properties["min"] = min
        properties["max"] = max
        properties["interactionSource"] = interactionSource
        properties["onHold"] = onHold
        properties["onRelease"] = onRelease
    }
}

private class ScaleOnTapNode(
    private var initial: Float,
    private var min: Float,
    private var max: Float,
    private var interactionSource: MutableInteractionSource?,
    private var onHold: () -> Unit,
    private var onRelease: (time: Long) -> Unit
) : DelegatingNode(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {

    private var scale = initial
    private val animatable = Animatable(initial)

    private var animationJob: Job? = null

    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures(
                onPress = { onPress(it) }
            )
        }
    )

    fun update(
        initial: Float,
        min: Float,
        max: Float,
        interactionSource: MutableInteractionSource?,
        onHold: () -> Unit,
        onRelease: (time: Long) -> Unit
    ) {
        val initialChanged = this.initial != initial

        val shouldResetPointerInput =
            this.initial != initial ||
                    this.min != min ||
                    this.max != max

        this.initial = initial
        this.min = min
        this.max = max
        this.interactionSource = interactionSource
        this.onHold = onHold
        this.onRelease = onRelease

        if (initialChanged) {
            setTarget(initial)
        }

        if (shouldResetPointerInput) {
            pointerInputNode.resetPointerInputHandler()
        }
    }

    override fun onDetach() {
        animationJob?.cancel()
        animationJob = null
    }

    private suspend fun PressGestureScope.onPress(offset: Offset) {
        val time = System.currentTimeMillis()

        setTarget(max)

        currentValueOf(LocalHapticFeedback).longPress()

        val press = PressInteraction.Press(offset)

        interactionSource?.emit(press)

        onHold()

        delay(200)

        tryAwaitRelease()

        onRelease(System.currentTimeMillis() - time)

        currentValueOf(LocalHapticFeedback).longPress()

        interactionSource?.emit(PressInteraction.Release(press))

        setTarget(min)

        delay(200)

        setTarget(initial)
    }

    private fun setTarget(target: Float) {
        animationJob?.cancel()

        animationJob = coroutineScope.launch {
            animatable.animateTo(
                targetValue = target,
                animationSpec = springySpec()
            ) {
                scale = value
                invalidateDraw()
            }

            scale = animatable.value
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        val scope = this

        withTransform(
            transformBlock = {
                scale(
                    scaleX = scale,
                    scaleY = scale,
                    pivot = center
                )
            }
        ) {
            scope.drawContent()
        }
    }
}