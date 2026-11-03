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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.StreakCircles.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GLITCH)
internal class StreakCirclesFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.StreakCircles {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.streak_circles, R.string.filter_help_streak_circles)

        override val parameters = structuredParameters {
            Value::intensity.float(R.string.texture_intensity, 1f, 0f..1f, roundTo = 3)
            Value::count.int(R.string.procedural_count, 20, 1..64)
            Value::radius.float(R.string.radius, 0.15f, 0.01f..0.5f, roundTo = 3)
            Value::layers.int(R.string.texture_layers, 8, 1..32)
            Value::seed.seed(R.string.procedural_seed, 0, 0..1000)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """

            vec4 result = sampleInput(textureCoordinate);
            for (int i = 0; i < 64; i++) {
                if (float(i) >= count) break;
                vec2 center = (hash2(vec2(float(i), seed)) - 0.5) * imageAspect;
                float r = length(p - center);
                float limit = radius * (0.3 + hash(vec2(float(i), seed + 7.0)));
                if (r < limit) {
                    float ring = floor(r / limit * layers) / layers;
                    vec4 color = sampleField(center + vec2(ring * limit, 0.0));
                    result = mix(result, color, intensity);
                }
            }
            gl_FragColor = result;
            return;
        """.trimIndent()
    }
}