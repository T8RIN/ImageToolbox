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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ConcentricCircleBreaks.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GLITCH)
internal class ConcentricCircleBreaksFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.ConcentricCircleBreaks {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.concentric_circle_breaks,
            R.string.filter_help_concentric_circle_breaks
        )

        override val parameters = structuredParameters {
            Value::intensity.float(R.string.texture_intensity, 0.5f, -1f..1f, roundTo = 3)
            Value::dampening.float(R.string.procedural_dampening, 2f, 0f..8f, roundTo = 3)
            Value::distortion.float(R.string.distortion, 0.2f, -1f..1f, roundTo = 3)
            Value::frequency.float(R.string.frequency, 16f, 1f..64f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """

            float r = length(p);
            float band = floor(r * frequency);
            vec2 offset = hash2(vec2(band, 17.0)) - 0.5;
            float strength = intensity * exp(-r * dampening);
            q = rotatePoint(p, offset.x * distortion * strength) + offset * strength / frequency;
        """.trimIndent()
    }
}