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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Fireworks.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderEffectFilter
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms

@FilterDefinition(group = FilterGroups.LIGHT)
internal class FireworksFilter(
    override val value: Value
) : ShaderEffectFilter<Value>(parameters.uniforms(value), SHADER), Filter.Fireworks {

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.fireworks, R.string.filter_help_fireworks)

        override val parameters = structuredParameters {
            Value::seed.seed(R.string.procedural_seed, 0, 0..1000)
            Value::explosions.int(R.string.procedural_explosions, 4, 1..12)
            Value::particles.int(R.string.procedural_particles, 48, 8..128)
            Value::mode.int(R.string.gmic_param_mode, 0, 0..2)
            Value::intensity.float(R.string.texture_intensity, 0.6f, 0f..2f, roundTo = 3)
            Value::time.float(R.string.time, 0.5f, 0.05f..1f, roundTo = 3)
            Value::offsetX.float(R.string.offset_x, 0f, -1f..1f, roundTo = 3)
            Value::offsetY.float(R.string.offset_y, 0f, -1f..1f, roundTo = 3)
            Value::scale.float(R.string.scale, 1f, 0.05f..8f, roundTo = 3)
            Value::rotation.float(R.string.rotation, 0f, -180f..180f, roundTo = 3)
        }

        private val SHADER = """

            vec3 light = vec3(0.0);
            for (int i = 0; i < 12; i++) {
                if (float(i) >= explosions) break;
                vec2 random = hash2(vec2(float(i), seed));
                vec2 delta = p - (random - 0.5) * imageAspect;
                float radius = length(delta);
                float angle = angleOf(delta) + random.x * TAU;
                float spoke = pow(max(0.0, cos(angle * particles)), 24.0);
                float distance = abs(radius - time * (0.1 + random.y * 0.4));
                float trail = exp(-distance * (mode < 0.5 ? 80.0 : 30.0));
                if (mode > 1.5) trail *= 0.5 + 0.5 * sin(radius * 150.0);
                light += hsvToRgb(vec3(random.x, 0.8, 1.0)) * spoke * trail;
            }
            vec4 source = sampleInput(textureCoordinate);
            gl_FragColor = vec4(min(vec3(source.a), source.rgb + light * intensity * source.a), source.a);
            return;
        """.trimIndent()
    }
}