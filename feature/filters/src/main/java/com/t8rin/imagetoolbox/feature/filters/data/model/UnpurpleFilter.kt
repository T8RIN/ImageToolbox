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
import com.t8rin.gmic.filters.Unpurple
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Unpurple.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class UnpurpleFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Unpurple {

    override fun createFilter(): GmicFilter = Unpurple(
        intensityOfPurpleFringe = value.settings[0].toFloat(),
        minimumBrightness = value.settings[1].toFloat(),
        minimumRedBlueRatioInTheFringe = value.settings[2].toFloat(),
        maximumRedBlueRatioInTheFringe = value.settings[3].toFloat(),
        blurStandardDeviation = value.settings[4].toInt(),
        gentleModeOverridesMinimumBrightnessAndMinimunRedBlueRatio = value.settings[5].toBoolean(),
        bitDepth = value.settings[6].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_unpurple, R.string.filter_help_gmic_filter_unpurple)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_intensity_of_purple_fringe, 1, 0f..1f),
                gmicNumber(R.string.gmic_param_minimum_brightness, 0, 0f..1f),
                gmicNumber(R.string.gmic_param_minimum_red_blue_ratio_in_the_fringe, 0, 0f..1f),
                gmicNumber(
                    R.string.gmic_param_maximum_red_blue_ratio_in_the_fringe,
                    0.33f,
                    0f..1f
                ),
                gmicInteger(R.string.gmic_param_blur_standard_deviation, 5, 1f..10f),
                gmicToggle(
                    R.string.gmic_param_gentle_mode_overrides_minimum_brightness_and_minimun_red_blue_ratio,
                    false
                ),
                gmicInteger(R.string.bit_depth, 0, 0f..32f)
            )
        }
    }
}