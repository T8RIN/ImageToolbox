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
import com.t8rin.gmic.filters.Doodle
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Doodle.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class DoodleFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Doodle {

    override fun createFilter(): GmicFilter = Doodle(
        precision = value.settings[0].toFloat(),
        smoothness = value.settings[1].toFloat(),
        coherence = value.settings[2].toFloat(),
        contourThreshold = value.settings[3].toFloat(),
        spacing = value.settings[4].toInt(),
        minimumStrokeLength = value.settings[5].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_doodle, R.string.filter_help_gmic_filter_doodle)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_precision, 30, 0f..100f),
                gmicNumber(R.string.gmic_param_smoothness, 2, 0f..10f),
                gmicNumber(R.string.gmic_param_coherence, 2, 0f..10f),
                gmicNumber(R.string.threshold, 1.5f, 0f..10f),
                gmicInteger(R.string.spacing, 2, 0f..20f),
                gmicNumber(R.string.gmic_param_minimum_stroke_length, 70, 0f..255f)
            )
        }
    }
}