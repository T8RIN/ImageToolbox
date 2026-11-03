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
import com.t8rin.gmic.filters.Streak
import com.t8rin.gmic.filters.StreakPropagation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Streak.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class StreakFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Streak {

    override fun createFilter(): GmicFilter = Streak(
        maskColor = value.settings[0].toInt(),
        step = value.settings[1].toFloat(),
        angle = value.settings[2].toFloat(),
        propagation = StreakPropagation.valueOf(value.settings[3])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_streak, R.string.filter_help_gmic_filter_streak)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicColor(R.string.mask_color, -65536),
                gmicNumber(R.string.gmic_param_step, 0, 0f..30f),
                gmicNumber(R.string.angle, 0, 0f..360f),
                gmicSelection(
                    R.string.gmic_param_propagation,
                    "BidirectionalSmooth",
                    listOf("Backward", "Forward", "BidirectionalSharp", "BidirectionalSmooth")
                )
            )
        }
    }
}