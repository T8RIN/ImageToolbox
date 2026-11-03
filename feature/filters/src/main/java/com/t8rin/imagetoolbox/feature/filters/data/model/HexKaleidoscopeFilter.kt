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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HexKaleidoscope.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class HexKaleidoscopeFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.HexKaleidoscope {

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.hex_kaleidoscope, R.string.filter_help_hex_kaleidoscope)

        override val parameters = structuredParameters {
            Value::count.int(R.string.procedural_count, 6, 1..32)
            Value::frequency.float(R.string.frequency, 4f, 1f..16f, roundTo = 3)
            Value::offset.float(R.string.offset, 0f, -1f..1f, roundTo = 3)
            Value::mode.int(R.string.gmic_param_mode, 0, 0..2)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """
            vec2 grid = p * frequency;
            vec2 stepSize = vec2(1.7320508, 3.0);
            vec2 a = mod(grid, stepSize) - stepSize * 0.5;
            vec2 b = mod(grid - stepSize * 0.5, stepSize) - stepSize * 0.5;
            vec2 local = dot(a, a) < dot(b, b) ? a : b;
            float sector = TAU / count;
            float angle = mod(angleOf(local) + sector * 0.5, sector);
            if (mode < 0.5) angle = abs(angle - sector * 0.5);
            else if (mode < 1.5) angle -= sector * 0.5;
            else angle = sector * 0.5 - abs(angle - sector * 0.5);
            q = vec2(cos(angle), sin(angle)) * length(local) * 0.5 + vec2(offset, 0.0);
        """.trimIndent()
    }
}