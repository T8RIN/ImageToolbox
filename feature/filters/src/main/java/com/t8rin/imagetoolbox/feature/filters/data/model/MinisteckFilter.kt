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
import com.t8rin.gmic.filters.Ministeck
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Ministeck.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class MinisteckFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Ministeck {

    override fun createFilter(): GmicFilter = Ministeck(
        colors = value.settings[0].toInt(),
        resolution = value.settings[1].toInt(),
        pieceSize = value.settings[2].toInt(),
        pieceComplexity = value.settings[3].toInt(),
        reliefAmplitude = value.settings[4].toFloat(),
        reliefSize = value.settings[5].toFloat(),
        outline = value.settings[6].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_ministeck, R.string.filter_help_gmic_filter_ministeck)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.color, 8, 2f..24f),
                gmicInteger(R.string.resolution, 64, 16f..256f),
                gmicInteger(R.string.gmic_param_piece_size, 8, 1f..64f),
                gmicInteger(R.string.gmic_param_piece_complexity, 2, 1f..10f),
                gmicNumber(R.string.amplitude, 100, 0f..256f),
                gmicNumber(R.string.gmic_param_relief_size, 0.3f, 0f..1f),
                gmicToggle(R.string.gmic_param_outline, false)
            )
        }
    }
}