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

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.DiffusionTensors
import com.t8rin.gmic.filters.TensorColorMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DiffusionTensors.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class DiffusionTensorsFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.DiffusionTensors {

    override fun createFilter(): GmicFilter = DiffusionTensors(
        resolution = value.settings[0].toFloat(),
        size = value.settings[1].toFloat(),
        colorMode = TensorColorMode.valueOf(value.settings[2]),
        outline = value.settings[3].toInt(),
        sharpness = value.settings[4].toFloat(),
        anisotropy = value.settings[5].toFloat(),
        gradientSmoothness = value.settings[6].toFloat(),
        tensorSmoothness = value.settings[7].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_diffusion_tensors,
            R.string.filter_help_gmic_filter_diffusion_tensors
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.resolution, 10, 0f..20f),
                gmicNumber(R.string.just_size, 5, 0f..16f),
                gmicSelection(
                    R.string.gmic_param_color_mode,
                    "Color",
                    listOf("Monochrome", "Grayscale", "Orientation", "Color")
                ),
                gmicInteger(R.string.gmic_param_outline, 1, 0f..16f),
                gmicNumber(R.string.tag_sharpness, 0.15f, 0f..1f),
                gmicNumber(R.string.texture_anisotropy, 1, 0f..1f),
                gmicNumber(R.string.gmic_param_gradient_smoothness, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_tensor_smoothness, 3, 0f..10f)
            )
        }
    }
}