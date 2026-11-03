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
import com.t8rin.gmic.filters.StringLink
import com.t8rin.gmic.filters.Stringify
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Stringify.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class StringifyFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Stringify {

    override fun createFilter(): GmicFilter = Stringify(
        smoothness = value.settings[0].toInt(),
        levels = value.settings[1].toInt(),
        samplingRate = value.settings[2].toFloat(),
        link = StringLink.valueOf(value.settings[3]),
        offset = value.settings[4].toFloat(),
        threshold = value.settings[5].toInt(),
        opacity = value.settings[6].toFloat(),
        normalization = value.settings[7].toFloat(),
        fillHoles = value.settings[8].toBoolean(),
        backgroundColor = value.settings[9].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_stringify, R.string.filter_help_gmic_filter_stringify)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_smoothness, 2, 0f..10f),
                gmicInteger(R.string.levels, 32, 2f..64f),
                gmicNumber(R.string.gmic_param_sampling_rate, 20, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_link,
                    "ThreePoints",
                    listOf("TwoPoints", "ThreePoints", "FourPoints", "AllPoints")
                ),
                gmicNumber(R.string.offset, 100, 0f..100f),
                gmicInteger(R.string.threshold, 32, 0f..1024f),
                gmicNumber(R.string.opacity, 20, 0f..100f),
                gmicNumber(R.string.gmic_param_normalization, 25, 0f..100f),
                gmicToggle(R.string.gmic_param_fill_holes, false),
                gmicColor(R.string.background_color, -16777216)
            )
        }
    }
}