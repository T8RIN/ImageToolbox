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
import com.t8rin.gmic.filters.Retinex
import com.t8rin.gmic.filters.RetinexColorSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Retinex.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class RetinexFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Retinex {

    override fun createFilter(): GmicFilter = Retinex(
        strength = value.settings[0].toFloat(),
        valueOffset = value.settings[1].toFloat(),
        colorSpace = RetinexColorSpace.valueOf(value.settings[2]),
        minCut = value.settings[3].toFloat(),
        maxCut = value.settings[4].toFloat(),
        regularization = value.settings[5].toFloat(),
        lowScale = value.settings[6].toFloat(),
        middleScale = value.settings[7].toFloat(),
        highScale = value.settings[8].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_retinex, R.string.filter_help_gmic_filter_retinex)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.strength, 75, 0f..100f),
                gmicNumber(R.string.offset, 16, 1f..256f),
                gmicSelection(
                    R.string.tag_color_space,
                    "Hsv",
                    listOf("Hsi", "Hsv", "Lab", "LinearRgb", "Rgb", "YCbCr")
                ),
                gmicNumber(R.string.gmic_param_min_cut, 1, 0f..100f),
                gmicNumber(R.string.gmic_param_max_cut, 1, 0f..100f),
                gmicNumber(R.string.gmic_param_regularization, 5, 0f..32f),
                gmicNumber(R.string.gmic_param_low_scale, 15, 1f..512f),
                gmicNumber(R.string.gmic_param_middle_scale, 80, 1f..512f),
                gmicNumber(R.string.gmic_param_high_scale, 250, 1f..512f)
            )
        }
    }
}