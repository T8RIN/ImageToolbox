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
import com.t8rin.gmic.filters.Linify
import com.t8rin.gmic.filters.LinifyColorMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Linify.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class LinifyFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Linify {

    override fun createFilter(): GmicFilter = Linify(
        density = value.settings[0].toFloat(),
        spreading = value.settings[1].toFloat(),
        resolution = value.settings[2].toFloat(),
        lineOpacity = value.settings[3].toFloat(),
        linePrecision = value.settings[4].toInt(),
        colorMode = LinifyColorMode.valueOf(value.settings[5])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_linify, R.string.filter_help_gmic_filter_linify)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.density, 40, 0f..100f),
                gmicNumber(R.string.texture_spread, 2, 0f..10f),
                gmicNumber(R.string.resolution, 40, 0f..100f),
                gmicNumber(R.string.opacity, 10, 0f..30f),
                gmicInteger(R.string.gmic_param_line_precision, 24, 1f..128f),
                gmicSelection(
                    R.string.gmic_param_color_mode,
                    "Subtractive",
                    listOf("Subtractive", "Additive")
                )
            )
        }
    }
}