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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrain
import kotlinx.coroutines.launch

/**
 * This modifier animates its own size when its child modifier (or the child composable if it
 * is already at the tail of the chain) changes size. This allows the parent modifier to observe
 * a smooth size change, resulting in an overall continuous visual change.
 *
 * A [FiniteAnimationSpec] can be optionally specified for the size change animation. By default,
 * [spring] will be used.
 *
 * An optional [finishedListener] can be supplied to get notified when the size change animation is
 * finished. Since the content size change can be dynamic in many cases, both initial value and
 * target value (i.e. final size) will be passed to the [finishedListener]. __Note:__ if the
 * animation is interrupted, the initial value will be the size at the point of interruption. This
 * is intended to help determine the direction of the size change (i.e. expand or collapse in x and
 * y dimensions).
 *
 * @sample androidx.compose.animation.samples.AnimateContent
 *
 * @param animationSpec a finite animation that will be used to animate size change, [spring] by
 *                      default
 * @param finishedListener an optional listener to be called when the content change animation is
 *                         completed.
 */
fun Modifier.animateContentSizeNoClip(
    animationSpec: FiniteAnimationSpec<IntSize> = spring(
        stiffness = Spring.StiffnessMediumLow
    ),
    alignment: Alignment = Alignment.TopCenter,
    isClipped: Boolean = false,
    finishedListener: ((initialValue: IntSize, targetValue: IntSize) -> Unit)? = null
): Modifier = this
    .then(
        if (isClipped) Modifier.clipToBounds()
        else Modifier
    )
    .then(
        SizeAnimationModifierElement(
            animationSpec = animationSpec,
            alignment = alignment,
            finishedListener = finishedListener
        )
    )

fun Modifier.animateContentSizeNoClip(
    animationSpec: FiniteAnimationSpec<IntSize> = spring(
        stiffness = Spring.StiffnessMediumLow
    ),
    alignment: Alignment = Alignment.TopCenter,
    finishedListener: ((initialValue: IntSize, targetValue: IntSize) -> Unit)? = null
) = this
    .clipToBounds()
    .then(
        SizeAnimationModifierElement(
            animationSpec = animationSpec,
            alignment = alignment,
            finishedListener = finishedListener
        )
    )

private data class SizeAnimationModifierElement(
    val animationSpec: FiniteAnimationSpec<IntSize>,
    val alignment: Alignment,
    val finishedListener: ((initialValue: IntSize, targetValue: IntSize) -> Unit)?
) : ModifierNodeElement<SizeAnimationModifierNode>() {
    override fun create(): SizeAnimationModifierNode = SizeAnimationModifierNode(
        animationSpec = animationSpec,
        alignment = alignment,
        listener = finishedListener
    )

    override fun update(node: SizeAnimationModifierNode) {
        node.animationSpec = animationSpec
        node.listener = finishedListener
        node.alignment = alignment
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "animateContentSizeNoClip"
        properties["animationSpec"] = animationSpec
        properties["alignment"] = alignment
        properties["finishedListener"] = finishedListener
    }
}

internal val InvalidSize = IntSize(Int.MIN_VALUE, Int.MIN_VALUE)
internal val IntSize.isValid: Boolean
    get() = this != InvalidSize

/**
 * This class creates a [LayoutModifier] that measures children, and responds to children's size
 * change by animating to that size. The size reported to parents will be the animated size.
 */
private class SizeAnimationModifierNode(
    var animationSpec: AnimationSpec<IntSize>,
    var alignment: Alignment = Alignment.TopStart,
    var listener: ((startSize: IntSize, endSize: IntSize) -> Unit)? = null
) : LayoutModifierNodeWithPassThroughIntrinsics() {
    private var lookaheadSize: IntSize = InvalidSize
    private var lookaheadConstraints: Constraints = Constraints()
        set(value) {
            field = value
            lookaheadConstraintsAvailable = true
        }

    private var lookaheadConstraintsAvailable: Boolean = false

    private fun targetConstraints(default: Constraints) =
        if (lookaheadConstraintsAvailable) {
            lookaheadConstraints
        } else {
            default
        }

    data class AnimData(val anim: Animatable<IntSize, AnimationVector2D>, var startSize: IntSize)

    var animData: AnimData? by mutableStateOf(null)

    override fun onReset() {
        super.onReset()
        // Reset is an indication that the node may be re-used, in such case, animData becomes stale
        animData = null
    }

    override fun onAttach() {
        super.onAttach()
        // When re-attached, we may be attached to a tree without lookahead scope.
        lookaheadSize = InvalidSize
        lookaheadConstraintsAvailable = false
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable =
            if (isLookingAhead) {
                lookaheadConstraints = constraints
                measurable.measure(constraints)
            } else {
                // Measure with lookahead constraints when available, to avoid unnecessary relayout
                // in child during the lookahead animation.
                measurable.measure(targetConstraints(constraints))
            }
        val measuredSize = IntSize(placeable.width, placeable.height)
        val (width, height) =
            if (isLookingAhead) {
                lookaheadSize = measuredSize
                measuredSize
            } else {
                animateTo(if (lookaheadSize.isValid) lookaheadSize else measuredSize).let {
                    // Constrain the measure result to incoming constraints, so that parent doesn't
                    // force center this layout.
                    constraints.constrain(it)
                }
            }
        return layout(width, height) {
            val offset =
                alignment.align(
                    size = measuredSize,
                    space = IntSize(width, height),
                    layoutDirection = this@measure.layoutDirection
                )
            placeable.place(offset)
        }
    }

    fun animateTo(targetSize: IntSize): IntSize {
        val data =
            animData?.apply {
                // TODO(b/322878517): Figure out a way to seamlessly continue the animation after
                //  re-attach. Note that in some cases restarting the animation is the correct
                // behavior.
                val wasInterrupted = (targetSize != anim.value && !anim.isRunning)

                if (targetSize != anim.targetValue || wasInterrupted) {
                    startSize = anim.value
                    coroutineScope.launch {
                        val result = anim.animateTo(targetSize, animationSpec)
                        if (result.endReason == AnimationEndReason.Finished) {
                            listener?.invoke(startSize, result.endState.value)
                        }
                    }
                }
            }
                ?: AnimData(
                    Animatable(targetSize, IntSize.VectorConverter, IntSize(1, 1)),
                    targetSize
                )

        animData = data
        return data.anim.value
    }
}

internal abstract class LayoutModifierNodeWithPassThroughIntrinsics :
    LayoutModifierNode, Modifier.Node() {
    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width)

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width)
}