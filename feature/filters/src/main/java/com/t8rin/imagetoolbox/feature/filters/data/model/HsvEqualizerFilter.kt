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
import com.t8rin.gmic.filters.HsvEqualizer
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HsvEqualizer.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class HsvEqualizerFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.HsvEqualizer {

    override fun createFilter(): GmicFilter = HsvEqualizer(
        firstHueBand = value.settings[0].toFloat(),
        firstBandWidth = value.settings[1].toFloat(),
        firstHueShift = value.settings[2].toFloat(),
        firstSaturationCorrection = value.settings[3].toFloat(),
        firstValueCorrection = value.settings[4].toFloat(),
        secondHueBand = value.settings[5].toFloat(),
        secondBandWidth = value.settings[6].toFloat(),
        secondHueShift = value.settings[7].toFloat(),
        secondSaturationCorrection = value.settings[8].toFloat(),
        secondValueCorrection = value.settings[9].toFloat(),
        thirdHueBand = value.settings[10].toFloat(),
        thirdBandWidth = value.settings[11].toFloat(),
        thirdHueShift = value.settings[12].toFloat(),
        thirdSaturationCorrection = value.settings[13].toFloat(),
        thirdValueCorrection = value.settings[14].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_hsv_equalizer,
            R.string.filter_help_gmic_filter_hsv_equalizer
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_first_hue_band, 25, 0f..360f),
                gmicNumber(R.string.gmic_param_first_band_width, 55, 1f..360f),
                gmicNumber(R.string.gmic_param_first_hue_shift, 8, -180f..180f),
                gmicNumber(
                    R.string.gmic_param_first_saturation_correction,
                    0.2f,
                    -0.99f..0.99f
                ),
                gmicNumber(R.string.gmic_param_first_value_correction, 0.04f, -0.99f..0.99f),
                gmicNumber(R.string.gmic_param_second_hue_band, 200, 0f..360f),
                gmicNumber(R.string.gmic_param_second_band_width, 70, 1f..360f),
                gmicNumber(R.string.gmic_param_second_hue_shift, -6, -180f..180f),
                gmicNumber(
                    R.string.gmic_param_second_saturation_correction,
                    0.14f,
                    -0.99f..0.99f
                ),
                gmicNumber(R.string.gmic_param_second_value_correction, -0.03f, -0.99f..0.99f),
                gmicNumber(R.string.gmic_param_third_hue_band, 330, 0f..360f),
                gmicNumber(R.string.gmic_param_third_band_width, 45, 1f..360f),
                gmicNumber(R.string.gmic_param_third_hue_shift, 4, -180f..180f),
                gmicNumber(
                    R.string.gmic_param_third_saturation_correction,
                    0.1f,
                    -0.99f..0.99f
                ),
                gmicNumber(R.string.gmic_param_third_value_correction, 0.06f, -0.99f..0.99f)
            )
        }
    }
}