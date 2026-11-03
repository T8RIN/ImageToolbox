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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.InversionFractal.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class InversionFractalFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.InversionFractal {

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.inversion_fractal, R.string.filter_help_inversion_fractal)

        override val parameters = structuredParameters {
            Value::mode.int(R.string.gmic_param_mode, 0, 0..2)
            Value::iterations.int(R.string.iterations, 6, 1..16)
            Value::intensity.float(R.string.texture_intensity, 0.8f, 0.1f..2f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """
            for (int i = 0; i < 16; i++) {
                if (float(i) >= iterations) break;
                if (mode < 0.5) q = abs(q) - vec2(0.4);
                else if (mode < 1.5) q = abs(q.yx) - vec2(0.35, 0.5);
                else q = mod(q + 0.5, 1.0) - 0.5;
                q *= intensity / max(dot(q, q), 0.01);
                q = clamp(q, vec2(-100.0), vec2(100.0));
            }
        """.trimIndent()
    }
}