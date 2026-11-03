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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Rubidium.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class RubidiumFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.Rubidium {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.rubidium, R.string.filter_help_rubidium)

        override val parameters = structuredParameters {
            Value::displacement.float(R.string.displacement, 0.1f, -0.5f..0.5f, roundTo = 3)
            Value::intensity.float(R.string.texture_intensity, 0.2f, -1f..1f, roundTo = 3)
            Value::balance.float(R.string.procedural_balance, 0.5f, 0f..1f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """
            vec2 gradient = luminanceGradient(p, 0.008);
            vec2 tangent = vec2(-gradient.y, gradient.x);
            q += mix(gradient, tangent, balance) / (1.0 + length(gradient)) * displacement;
            vec3 color = sampleField(p).rgb;
            q += vec2(color.r - color.g, color.b - color.g) * intensity * 0.2;
        """.trimIndent()
    }
}