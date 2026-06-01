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

package com.t8rin.imagetoolbox.feature.filters.data.utils.gpu

import android.opengl.GLES20
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParam
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

internal class ShaderGpuImageFilter(
    private val preset: ShaderPreset,
    values: Map<String, ShaderValue>
) : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER,
    preset.shader
) {
    private val resolvedValues: Map<String, ShaderValue> = preset.params.associate { param ->
        param.name to param.resolveValue(values[param.name])
    }
    private val uniformLocations: MutableMap<String, Int> = mutableMapOf()

    override fun onInit() {
        super.onInit()
        preset.params.forEach { param ->
            uniformLocations[param.name] = GLES20.glGetUniformLocation(program, param.name)
        }
    }

    override fun onDrawArraysPre() {
        super.onDrawArraysPre()
        applyUniformValues()
    }

    private fun applyUniformValues() {
        preset.params.forEach { param ->
            val location = uniformLocations[param.name] ?: return@forEach
            if (location < 0) return@forEach

            when (val value = resolvedValues[param.name] ?: param.defaultValue) {
                is ShaderValue.FloatValue -> GLES20.glUniform1f(location, value.value)
                is ShaderValue.IntValue -> GLES20.glUniform1i(location, value.value)
                is ShaderValue.BoolValue -> GLES20.glUniform1i(
                    location,
                    if (value.value) TRUE_INT else FALSE_INT
                )

                is ShaderValue.ColorValue -> GLES20.glUniform4f(
                    location,
                    value.red.normalizedChannel(),
                    value.green.normalizedChannel(),
                    value.blue.normalizedChannel(),
                    value.alpha.normalizedChannel()
                )

                is ShaderValue.Vec2Value -> GLES20.glUniform2f(location, value.x, value.y)
            }
        }
    }

    private fun ShaderParam.resolveValue(value: ShaderValue?): ShaderValue =
        value?.takeIf { it.type == type } ?: defaultValue

    private fun Int.normalizedChannel(): Float =
        coerceIn(MIN_COLOR_CHANNEL, MAX_COLOR_CHANNEL) / MAX_COLOR_CHANNEL.toFloat()

    private companion object {
        const val TRUE_INT = 1
        const val FALSE_INT = 0
        const val MIN_COLOR_CHANNEL = 0
        const val MAX_COLOR_CHANNEL = 255
    }
}
