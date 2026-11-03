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
import com.t8rin.gmic.filters.Flower
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Flower.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class FlowerFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Flower {

    override fun createFilter(): GmicFilter = Flower(
        centerX = value.settings[0].toFloat(),
        centerY = value.settings[1].toFloat(),
        amplitudePointX = value.settings[2].toFloat(),
        amplitudePointY = value.settings[3].toFloat(),
        petals = value.settings[4].toInt(),
        offset = value.settings[5].toFloat(),
        boundary = GmicBoundary.valueOf(value.settings[6])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_flower, R.string.filter_help_gmic_filter_flower)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_amplitude_point_x, 75, 0f..100f),
                gmicNumber(R.string.gmic_param_amplitude_point_y, 50, 0f..100f),
                gmicInteger(R.string.texture_petals, 6, 2f..20f),
                gmicNumber(R.string.offset, 0, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Mirror",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
                )
            )
        }
    }
}