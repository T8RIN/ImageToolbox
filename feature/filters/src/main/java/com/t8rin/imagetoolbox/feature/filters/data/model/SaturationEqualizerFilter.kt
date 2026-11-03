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
import com.t8rin.gmic.filters.SaturationEqualizer
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SaturationEqualizer.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class SaturationEqualizerFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SaturationEqualizer {

    override fun createFilter(): GmicFilter = SaturationEqualizer(
        black = value.settings[0].toFloat(),
        nearBlack = value.settings[1].toFloat(),
        darkGray = value.settings[2].toFloat(),
        midDarkGray = value.settings[3].toFloat(),
        middleGray = value.settings[4].toFloat(),
        midLightGray = value.settings[5].toFloat(),
        lightGray = value.settings[6].toFloat(),
        highlights = value.settings[7].toFloat(),
        white = value.settings[8].toFloat(),
        hue0 = value.settings[9].toFloat(),
        hue45 = value.settings[10].toFloat(),
        hue90 = value.settings[11].toFloat(),
        hue135 = value.settings[12].toFloat(),
        hue180 = value.settings[13].toFloat(),
        hue225 = value.settings[14].toFloat(),
        hue270 = value.settings[15].toFloat(),
        hue315 = value.settings[16].toFloat(),
        hue360 = value.settings[17].toFloat(),
        rotateHueBands = value.settings[18].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_saturation_equalizer,
            R.string.filter_help_gmic_filter_saturation_equalizer
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_black, -12, -128f..128f),
                gmicNumber(R.string.gmic_param_near_black, -6, -128f..128f),
                gmicNumber(R.string.gmic_param_dark_gray, 4, -128f..128f),
                gmicNumber(R.string.gmic_param_mid_dark_gray, 8, -128f..128f),
                gmicNumber(R.string.gmic_param_middle_gray, 12, -128f..128f),
                gmicNumber(R.string.gmic_param_mid_light_gray, 8, -128f..128f),
                gmicNumber(R.string.gmic_param_light_gray, 4, -128f..128f),
                gmicNumber(R.string.highlights, -2, -128f..128f),
                gmicNumber(R.string.gmic_param_white, -8, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_0, 8, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_45, 14, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_90, 6, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_135, -4, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_180, -8, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_225, 4, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_270, 10, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_315, 12, -128f..128f),
                gmicNumber(R.string.gmic_param_hue_360, 8, -128f..128f),
                gmicNumber(R.string.gmic_param_rotate_hue_bands, 0, -45f..45f)
            )
        }
    }
}