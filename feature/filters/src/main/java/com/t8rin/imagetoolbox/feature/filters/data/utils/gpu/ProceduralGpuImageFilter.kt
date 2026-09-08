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
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.ProceduralEffect
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import kotlin.math.min

internal class ProceduralGpuImageFilter(
    private val effect: ProceduralEffect,
    value: ProceduralParams
) : BitmapGpuImageFilter(effect.fragmentShader()) {

    private val values = effect.resolve(value)
    private val colors = effect.resolveColors(value)
    private var aspectLocation = -1
    override fun onInit() {
        super.onInit()
        aspectLocation = GLES20.glGetUniformLocation(program, "imageAspect")
        values.forEach { (name, value) ->
            setFloat(GLES20.glGetUniformLocation(program, name), value)
        }
        colors.forEach { (name, color) ->
            setFloatVec4(
                GLES20.glGetUniformLocation(program, name),
                floatArrayOf(
                    Color.red(color) / 255f, Color.green(color) / 255f,
                    Color.blue(color) / 255f, Color.alpha(color) / 255f
                )
            )
        }
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        val shortSide = min(width, height).coerceAtLeast(1).toFloat()
        setFloatVec2(aspectLocation, floatArrayOf(width / shortSide, height / shortSide))
    }
}