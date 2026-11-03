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
import com.t8rin.gmic.filters.HighlightSynthesis
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HighlightSynthesis.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class HighlightSynthesisFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.HighlightSynthesis {

    override fun createFilter(): GmicFilter = HighlightSynthesis(
        red = value.settings[0].toFloat(),
        green = value.settings[1].toFloat(),
        blue = value.settings[2].toFloat(),
        rgbToSrgb = value.settings[3].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_highlight_synthesis,
            R.string.filter_help_gmic_filter_highlight_synthesis
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.color_red, 1.1f, 0f..2f),
                gmicNumber(R.string.color_green, 0.7f, 0f..2f),
                gmicNumber(R.string.color_blue, 1.15f, 0f..2f),
                gmicToggle(R.string.gmic_param_rgb_to_srgb, true)
            )
        }
    }
}