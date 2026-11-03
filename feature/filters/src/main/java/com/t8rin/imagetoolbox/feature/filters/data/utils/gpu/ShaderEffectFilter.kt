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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.FilterField
import com.t8rin.imagetoolbox.feature.filters.data.model.StructuredParameters
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

internal abstract class ShaderEffectFilter<T : Any>(
    private val uniforms: List<ShaderUniform>,
    private val shader: String
) : GPUFilterTransformation(), Filter<T> {

    override fun createFilter(): GPUImageFilter = ShaderEffectGpuFilter(shader, uniforms)
}

internal fun <T : Filter.ParameterValue> StructuredParameters<T>.uniforms(value: T): List<ShaderUniform> =
    fields.map { (property, field) ->
        val raw = property.get(value)
        when (raw) {
            is Float -> {
                val number = (field as? FilterField.Number)?.constrain(raw) ?: raw
                require(number.isFinite()) { "Shader uniform ${property.name} must be finite" }
                ShaderUniform.Number(property.name, number)
            }

            is Int -> ShaderUniform.Number(
                property.name,
                ((field as? FilterField.Integer)?.constrain(raw) ?: raw).toFloat()
            )

            is ColorModel -> ShaderUniform.Color(property.name, raw.colorInt)
            else -> error("Unsupported shader uniform ${property.name}")
        }
    }
