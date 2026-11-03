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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BokehLights.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.LIGHT)
internal class BokehLightsFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.BokehLights {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.bokeh_lights, R.string.filter_help_bokeh_lights)

        override val parameters = structuredParameters<Value> {
            field(
                Value::lightColor,
                colorParam(R.string.color, ColorModel(-675749), allowAlpha = true)
            )
            field(
                Value::intensity,
                floatParam(R.string.texture_intensity, 0.4f, 0f..2f, roundTo = 3)
            )
            field(Value::count, intParam(R.string.procedural_count, 24, 1..64))
            field(Value::blades, intParam(R.string.procedural_blades, 6, 3..12))
            field(Value::radius, floatParam(R.string.radius, 0.07f, 0.005f..0.3f, roundTo = 3))
            field(
                Value::variability,
                floatParam(R.string.procedural_variability, 0.5f, 0f..1f, roundTo = 3)
            )
            field(
                Value::colorVariation,
                floatParam(R.string.procedural_color_variation, 0.3f, 0f..1f, roundTo = 3)
            )
            field(Value::seed, intParam(R.string.procedural_seed, 0, 0..1000))
            field(Value::offsetX, floatParam(R.string.offset_x, 0f, -1f..1f, roundTo = 3))
            field(Value::offsetY, floatParam(R.string.offset_y, 0f, -1f..1f, roundTo = 3))
            field(Value::scale, floatParam(R.string.scale, 1f, 0.05f..8f, roundTo = 3))
            field(Value::rotation, floatParam(R.string.rotation, 0f, -180f..180f, roundTo = 3))
        }

        private val SHADER = """

            vec3 light = vec3(0.0);
            for (int i = 0; i < 64; i++) {
                if (float(i) >= count) break;
                vec2 random = hash2(vec2(float(i), seed));
                vec2 center = (random - 0.5) * imageAspect * 1.5;
                vec2 delta = p - center;
                float r = radius * mix(1.0, 0.25 + random.x, variability);
                float sector = TAU / blades;
                float angle = mod(angleOf(delta) + sector * 0.5, sector) - sector * 0.5;
                float edge = r * cos(sector * 0.5) / max(cos(angle), 0.001);
                float disc = 1.0 - smoothstep(edge * 0.8, edge, length(delta));
                vec3 hsv = rgbToHsv(lightColor.rgb);
                hsv.x = fract(hsv.x + (random.y - 0.5) * colorVariation);
                light += hsvToRgb(hsv) * disc * lightColor.a;
            }
            vec4 source = sampleInput(textureCoordinate);
            gl_FragColor = vec4(min(vec3(source.a), source.rgb + light * intensity * source.a), source.a);
            return;
        """.trimIndent()
    }
}