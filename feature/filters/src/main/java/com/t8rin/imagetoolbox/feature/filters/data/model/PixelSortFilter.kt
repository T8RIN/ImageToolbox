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
import com.t8rin.gmic.filters.PixelSort
import com.t8rin.gmic.filters.PixelSortAxis
import com.t8rin.gmic.filters.PixelSortCriterion
import com.t8rin.gmic.filters.PixelSortMask
import com.t8rin.gmic.filters.PixelSortOrder
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PixelSort.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class PixelSortFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PixelSort {

    override fun createFilter(): GmicFilter = PixelSort(
        order = PixelSortOrder.valueOf(value.settings[0]),
        axis = PixelSortAxis.valueOf(value.settings[1]),
        criterion = PixelSortCriterion.valueOf(value.settings[2]),
        mask = PixelSortMask.valueOf(value.settings[3]),
        lowerThreshold = value.settings[4].toFloat(),
        upperThreshold = value.settings[5].toFloat(),
        maskSmoothness = value.settings[6].toFloat(),
        invertMask = value.settings[7].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.pixel_sort, R.string.filter_help_pixel_sort)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(R.string.order, "Increasing", listOf("Decreasing", "Increasing")),
                gmicSelection(
                    R.string.gmic_param_axis,
                    "X",
                    listOf("X", "Y", "XThenY", "YThenX")
                ),
                gmicSelection(
                    R.string.gmic_param_criterion,
                    "Red",
                    listOf(
                        "Red",
                        "Green",
                        "Blue",
                        "Intensity",
                        "Luminance",
                        "Lightness",
                        "Hue",
                        "Saturation",
                        "Minimum",
                        "Maximum",
                        "Random"
                    )
                ),
                gmicSelection(
                    R.string.mask,
                    "Criterion",
                    listOf("Criterion", "Contours", "Random")
                ),
                gmicNumber(R.string.gmic_param_lower_threshold, 0, 0f..100f),
                gmicNumber(R.string.gmic_param_upper_threshold, 100, 0f..100f),
                gmicNumber(R.string.softness, 0, 0f..5f),
                gmicToggle(R.string.gmic_param_invert_mask, false)
            )
        }
    }
}