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

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShaderParams
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidationError
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValidator
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderGpuImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

@FilterInject
internal class ShaderFilter(
    override val value: ShaderParams = ShaderParams()
) : GPUFilterTransformation(), Filter.Shader {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GPUImageFilter {
        val preset = value.preset
        if (preset == null || !preset.isValidShaderPreset()) return GPUImageFilter()

        return ShaderGpuImageFilter(
            preset = preset,
            values = value.resolvedValues
        )
    }

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val preset = value.preset ?: return input
        if (!preset.isValidShaderPreset()) return input

        return runCatching { super.transform(input, size) }.getOrElse { input }
    }

    private fun ShaderPreset.isValidShaderPreset(): Boolean =
        ShaderValidator.validate(this).none {
            it !is ShaderValidationError.BlankName
        }

}
