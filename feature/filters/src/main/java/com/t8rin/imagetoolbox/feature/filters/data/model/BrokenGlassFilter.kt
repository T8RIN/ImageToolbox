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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BrokenGlass.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class BrokenGlassFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.BrokenGlass {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.broken_glass, R.string.filter_help_broken_glass)

        override val parameters = structuredParameters {
            Value::intensity.float(R.string.texture_intensity, 0.4f, -1f..1f, roundTo = 3)
            Value::frequency.float(R.string.frequency, 8f, 1f..40f, roundTo = 3)
            Value::seed.seed(R.string.procedural_seed, 0, 0..1000)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """

            vec2 grid = p * frequency;
            vec2 cell = floor(grid);
            float nearest = 100.0;
            vec2 local = vec2(0.0);
            vec2 chosen = cell;
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    vec2 candidate = cell + vec2(float(x), float(y));
                    vec2 delta = grid - candidate - 0.5 - (hash2(candidate + seed) - 0.5) * 0.8;
                    float distance = dot(delta, delta);
                    if (distance < nearest) {
                        nearest = distance;
                        local = delta;
                        chosen = candidate;
                    }
                }
            }
            vec2 random = hash2(chosen + seed + 19.3) - 0.5;
            q += (rotatePoint(local, random.x * intensity * 3.0) - local + random * intensity) / frequency;
        """.trimIndent()
    }
}