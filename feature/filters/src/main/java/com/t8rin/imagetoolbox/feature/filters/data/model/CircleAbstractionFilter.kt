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
import com.t8rin.gmic.filters.CircleAbstraction
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CircleAbstraction.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class CircleAbstractionFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.CircleAbstraction {

    override fun createFilter(): GmicFilter = CircleAbstraction(
        numberOfColors = value.settings[0].toInt(),
        density = value.settings[1].toInt(),
        opacity = value.settings[2].toFloat(),
        smoothness = value.settings[3].toFloat(),
        filledCircles = value.settings[4].toBoolean(),
        fillTransparentHoles = value.settings[5].toBoolean(),
        normalizeColors = value.settings[6].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_circle_abstraction,
            R.string.filter_help_gmic_filter_circle_abstraction
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_number_of_colors, 8, 2f..16f),
                gmicInteger(R.string.density, 5, 1f..100f),
                gmicNumber(R.string.opacity, 0.8f, 0f..1f),
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..4f),
                gmicToggle(R.string.gmic_param_filled_circles, true),
                gmicToggle(R.string.gmic_param_fill_transparent_holes, true),
                gmicToggle(R.string.gmic_param_normalize_colors, true)
            )
        }
    }
}