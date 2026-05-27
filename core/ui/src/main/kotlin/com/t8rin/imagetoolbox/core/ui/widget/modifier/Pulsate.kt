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

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun Modifier.pulsate(
    range: ClosedFloatingPointRange<Float> = 0.95f..1.05f,
    duration: Int = 1000,
    enabled: Boolean = true
): Modifier = this then PulsateElement(
    range = range,
    duration = duration,
    enabled = enabled
)

private data class PulsateElement(
    val range: ClosedFloatingPointRange<Float>,
    val duration: Int,
    val enabled: Boolean
) : ModifierNodeElement<PulsateNode>() {

    override fun create(): PulsateNode = PulsateNode(
        range = range,
        duration = duration,
        enabled = enabled
    )

    override fun update(node: PulsateNode) {
        node.update(
            range = range,
            duration = duration,
            enabled = enabled
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pulsate"
        properties["range"] = range
        properties["duration"] = duration
        properties["enabled"] = enabled
    }
}

private class PulsateNode(
    private var range: ClosedFloatingPointRange<Float>,
    private var duration: Int,
    private var enabled: Boolean
) : Modifier.Node(), DrawModifierNode {

    private var scale = range.start
    private var job: Job? = null

    override fun onAttach() {
        start()
    }

    override fun onDetach() {
        job?.cancel()
        job = null
    }

    fun update(
        range: ClosedFloatingPointRange<Float>,
        duration: Int,
        enabled: Boolean
    ) {
        val changed = this.range != range ||
                this.duration != duration ||
                this.enabled != enabled

        this.range = range
        this.duration = duration
        this.enabled = enabled

        if (changed && isAttached) {
            start()
        }
    }

    private fun start() {
        job?.cancel()

        if (!enabled) {
            scale = 1f
            invalidateDraw()
            return
        }

        job = coroutineScope.launch {
            val animationState = AnimationState(
                typeConverter = Float.VectorConverter,
                initialValue = scale.coerceIn(range.start, range.endInclusive)
            )

            while (true) {
                animationState.animateTo(
                    targetValue = range.endInclusive,
                    animationSpec = tween(durationMillis = duration)
                ) {
                    scale = value
                    invalidateDraw()
                }

                animationState.animateTo(
                    targetValue = range.start,
                    animationSpec = tween(durationMillis = duration)
                ) {
                    scale = value
                    invalidateDraw()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        if (enabled) {
            val contentDrawScope = this

            withTransform(
                transformBlock = {
                    scale(
                        scaleX = scale,
                        scaleY = scale,
                        pivot = center
                    )
                }
            ) {
                contentDrawScope.drawContent()
            }
        } else {
            drawContent()
        }
    }
}