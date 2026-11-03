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

import android.graphics.Color
import android.opengl.GLES20
import kotlin.math.min

internal sealed interface ShaderUniform {
    val name: String

    data class Number(override val name: String, val value: Float) : ShaderUniform
    data class Color(override val name: String, val value: Int) : ShaderUniform
}

internal class ShaderEffectGpuFilter(
    shader: String,
    private val uniforms: List<ShaderUniform>
) : BitmapGpuImageFilter(shader.toFragmentShader(uniforms)) {

    private var aspectLocation = -1

    override fun onInit() {
        super.onInit()
        aspectLocation = GLES20.glGetUniformLocation(program, "imageAspect")
        uniforms.forEach { uniform ->
            val location = GLES20.glGetUniformLocation(program, uniform.name)
            when (uniform) {
                is ShaderUniform.Number -> setFloat(location, uniform.value)
                is ShaderUniform.Color -> {
                    val color = uniform.value
                    setFloatVec4(
                        location,
                        floatArrayOf(
                            Color.red(color) / 255f,
                            Color.green(color) / 255f,
                            Color.blue(color) / 255f,
                            Color.alpha(color) / 255f
                        )
                    )
                }
            }
        }
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        val shortSide = min(width, height).coerceAtLeast(1).toFloat()
        setFloatVec2(aspectLocation, floatArrayOf(width / shortSide, height / shortSide))
    }
}