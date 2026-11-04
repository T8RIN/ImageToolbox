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

package com.t8rin.neural_tools.tryon

import java.nio.FloatBuffer

internal class MobileVtonScheduler(
    val steps: Int = 28
) {

    init {
        require(steps > 0) { "The number of denoising steps must be positive" }
    }

    private val sigmas = FloatArray(steps + 1).apply {
        // Diffusers 0.32.2 shifts the training endpoint before shifting the inference schedule.
        val minimumSigma = 3f * (1f / 1000f) / (1f + 2f * (1f / 1000f))
        val minimumTimestep = minimumSigma.toDouble() * 1000.0
        val spacing = if (steps == 1) 0.0 else (minimumTimestep - 1000.0) / (steps - 1)
        for (index in 0 until steps) {
            val timestep = if (steps > 1 && index == steps - 1) {
                minimumTimestep
            } else {
                1000.0 + index * spacing
            }
            val sigma = timestep / 1000.0
            this[index] = (3.0 * sigma / (1.0 + 2.0 * sigma)).toFloat()
        }
    }

    fun timestep(index: Int): Float {
        require(index in 0 until steps)
        return sigmas[index] * 1000f
    }

    fun step(
        index: Int,
        sample: FloatBuffer,
        conditional: FloatBuffer,
        unconditional: FloatBuffer? = null,
        guidanceScale: Float = 2f
    ) {
        require(index in 0 until steps)
        require(sample.remaining() == conditional.remaining())
        require(unconditional == null || sample.remaining() == unconditional.remaining())
        require(guidanceScale.isFinite())

        val delta = sigmas[index + 1] - sigmas[index]
        val sampleOffset = sample.position()
        val conditionalOffset = conditional.position()
        val unconditionalOffset = unconditional?.position() ?: 0
        for (offset in 0 until sample.remaining()) {
            val prediction = conditional.get(conditionalOffset + offset)
            val guided = if (unconditional != null) {
                val negative = unconditional.get(unconditionalOffset + offset)
                negative + guidanceScale * (prediction - negative)
            } else prediction
            val sampleIndex = sampleOffset + offset
            sample.put(sampleIndex, sample.get(sampleIndex) + delta * guided)
        }
    }
}