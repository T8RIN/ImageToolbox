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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RandomKaleidoscopeGrid.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class RandomKaleidoscopeGridFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.RandomKaleidoscopeGrid {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.random_kaleidoscope_grid,
            R.string.filter_help_random_kaleidoscope_grid
        )

        override val parameters = structuredParameters {
            Value::count.int(R.string.procedural_count, 6, 1..32)
            Value::frequency.float(R.string.frequency, 3f, 1f..16f, roundTo = 3)
            Value::seed.seed(R.string.procedural_seed, 0, 0..1000)
            Value::variability.float(R.string.procedural_variability, 0.5f, 0f..1f, roundTo = 3)
            Value::blend.float(R.string.raw_highlight_recovery_blend, 0.25f, 0f..1f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """
            vec2 grid = p * frequency;
            vec2 cell = floor(grid);
            vec2 local = fract(grid) - 0.5;
            vec2 random = hash2(cell + seed);
            float spikes = max(1.0, floor(count + (random.x - 0.5) * variability * count));
            float sector = TAU / spikes;
            float angle = abs(mod(angleOf(local) + random.y * TAU, sector) - sector * 0.5);
            vec2 mapped = vec2(cos(angle), sin(angle)) * length(local);
            vec4 kaleidoscope = sampleField(mapped);
            vec4 original = sampleField(local);
            float edge = max(abs(local.x), abs(local.y)) * 2.0;
            float weight = smoothstep(1.0 - max(blend, 0.001), 1.0, edge);
            gl_FragColor = mix(kaleidoscope, original, weight);
            return;
        """.trimIndent()
    }
}