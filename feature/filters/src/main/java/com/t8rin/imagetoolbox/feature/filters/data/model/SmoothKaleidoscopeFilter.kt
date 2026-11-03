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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SmoothKaleidoscope.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class SmoothKaleidoscopeFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.SmoothKaleidoscope {

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.smooth_kaleidoscope, R.string.filter_help_smooth_kaleidoscope)

        override val parameters = structuredParameters {
            Value::blend.float(R.string.raw_highlight_recovery_blend, 0.25f, 0f..1f, roundTo = 3)
            Value::offset.float(R.string.offset, 0f, -1f..1f, roundTo = 3)
            Value::frequency.float(R.string.frequency, 2f, 1f..16f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """
            vec2 grid = p * frequency;
            vec2 cell = floor(grid);
            vec2 f = fract(grid);
            vec4 sum = vec4(0.0);
            float weightSum = 0.0;
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) {
                    vec2 corner = vec2(float(x), float(y));
                    vec2 local = f - corner;
                    float angle = mod(cell.x + corner.x + cell.y + corner.y, 4.0) * TAU * 0.25;
                    vec2 uv = rotatePoint(local, angle) * 0.5 + 0.5 + offset;
                    vec2 weight = max(vec2(0.0), vec2(1.0) - abs(local));
                    float w = pow(weight.x * weight.y, mix(16.0, 1.0, blend));
                    sum += sampleInput(1.0 - abs(mod(uv, 2.0) - 1.0)) * w;
                    weightSum += w;
                }
            }
            gl_FragColor = sum / max(weightSum, 0.00000000000000000001);
            return;
        """.trimIndent()
    }
}