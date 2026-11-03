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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.TiledStreak.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GLITCH)
internal class TiledStreakFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.TiledStreak {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.tiled_streak, R.string.filter_help_tiled_streak)

        override val parameters = structuredParameters<Value> {
            field(Value::intensity, floatParam(R.string.texture_intensity, 1f, 0f..1f, roundTo = 3))
            field(
                Value::balance,
                floatParam(R.string.procedural_balance, 0.5f, 0f..1f, roundTo = 3)
            )
            field(
                Value::variability,
                floatParam(R.string.procedural_variability, 0.5f, 0f..1f, roundTo = 3)
            )
            field(Value::frequency, floatParam(R.string.frequency, 8f, 1f..32f, roundTo = 3))
            field(Value::seed, intParam(R.string.procedural_seed, 0, 0..1000))
            field(Value::offsetX, floatParam(R.string.offset_x, 0f, -1f..1f, roundTo = 3))
            field(Value::offsetY, floatParam(R.string.offset_y, 0f, -1f..1f, roundTo = 3))
            field(Value::scale, floatParam(R.string.scale, 1f, 0.05f..8f, roundTo = 3))
            field(Value::rotation, floatParam(R.string.rotation, 0f, -180f..180f, roundTo = 3))
        }

        private val SHADER = """

            vec2 cell = floor(p * frequency);
            vec2 local = fract(p * frequency) - 0.5;
            float angle = angleOf(local);
            float radius = length(local);
            vec2 center = (cell + 0.5) / frequency;
            vec2 mapped = center + vec2(radius, sin(angle) * (1.0 - balance) * 0.2) / frequency;
            mapped += (hash2(cell + seed) - 0.5) * variability / frequency;
            q = mix(p, mapped, intensity);
        """.trimIndent()
    }
}