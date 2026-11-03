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
import com.t8rin.gmic.filters.FreakyBlackAndWhite
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FreakyBlackAndWhite.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class FreakyBlackAndWhiteFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.FreakyBlackAndWhite {

    override fun createFilter(): GmicFilter = FreakyBlackAndWhite(
        strengthPercent = value.settings[0].toFloat(),
        oddnessPercent = value.settings[1].toFloat(),
        brightnessPercent = value.settings[2].toFloat(),
        contrastPercent = value.settings[3].toFloat(),
        gammaPercent = value.settings[4].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_freaky_black_and_white,
            R.string.filter_help_gmic_filter_freaky_black_and_white
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.strength, 90, 0f..100f),
                gmicNumber(R.string.gmic_param_oddness_percent, 20, 0f..100f),
                gmicNumber(R.string.brightness, 0, -100f..100f),
                gmicNumber(R.string.contrast, 0, -100f..100f),
                gmicNumber(R.string.gamma, 0, -100f..100f)
            )
        }
    }
}