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
import com.t8rin.gmic.filters.Charcoal
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Charcoal.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class CharcoalFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Charcoal {

    override fun createFilter(): GmicFilter = Charcoal(
        granularity = value.settings[0].toInt(),
        lowlightCrossover = value.settings[1].toInt(),
        highlightCrossover = value.settings[2].toInt(),
        boostContrast = value.settings[3].toBoolean(),
        optimizeSize = value.settings[4].toBoolean(),
        chalkHighlights = value.settings[5].toBoolean(),
        minimumHighlight = value.settings[6].toInt(),
        maximumHighlight = value.settings[7].toInt(),
        backgroundColor = value.settings[8].toInt(),
        foregroundColor = value.settings[9].toInt(),
        invertColors = value.settings[10].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_charcoal, R.string.filter_help_gmic_filter_charcoal)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_granularity, 65, 0f..800f),
                gmicInteger(R.string.gmic_param_lowlight_crossover, 70, 0f..255f),
                gmicInteger(R.string.gmic_param_highlight_crossover, 170, 0f..255f),
                gmicToggle(R.string.contrast, false),
                gmicToggle(R.string.gmic_param_optimize_size, true),
                gmicToggle(R.string.gmic_param_chalk_highlights, false),
                gmicInteger(R.string.gmic_param_minimum_highlight, 50, 0f..255f),
                gmicInteger(R.string.gmic_param_maximum_highlight, 70, 0f..255f),
                gmicColor(R.string.background_color, -1),
                gmicColor(R.string.gmic_param_foreground_color, -16777216),
                gmicToggle(R.string.invert_colors, false)
            )
        }
    }
}