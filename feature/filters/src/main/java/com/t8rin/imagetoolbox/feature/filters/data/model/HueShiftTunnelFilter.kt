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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HueShiftTunnel.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.COLOR)
internal class HueShiftTunnelFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.HueShiftTunnel {

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.hue_shift_tunnel, R.string.filter_help_hue_shift_tunnel)

        override val parameters = structuredParameters {
            Value::intensity.float(R.string.texture_intensity, 2f, -10f..10f, roundTo = 3)
            Value::saturation.float(R.string.saturation, 1f, 0f..2f, roundTo = 3)
            Value::hueOffset.float(R.string.hue, 0f, -1f..1f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """
            vec4 source = sampleInput(textureCoordinate);
            vec3 hsv = rgbToHsv(source.rgb / max(source.a, 0.00001));
            hsv.x = fract(hsv.x + length(p) * intensity + hueOffset);
            hsv.y = clamp(hsv.y * saturation, 0.0, 1.0);
            gl_FragColor = vec4(hsvToRgb(hsv) * source.a, source.a);
            return;
        """.trimIndent()
    }
}