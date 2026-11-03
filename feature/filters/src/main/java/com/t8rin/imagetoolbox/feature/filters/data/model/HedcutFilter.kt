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
import com.t8rin.gmic.filters.Hedcut
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Hedcut.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class HedcutFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Hedcut {

    override fun createFilter(): GmicFilter = Hedcut(
        contrast = value.settings[0].toFloat(),
        luminanceLevel = value.settings[1].toFloat(),
        smoothing = value.settings[2].toFloat(),
        size = value.settings[3].toFloat(),
        step = value.settings[4].toFloat(),
        quality = value.settings[5].toBoolean(),
        forceGray = value.settings[6].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_hedcut, R.string.filter_help_gmic_filter_hedcut)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.contrast, 0.5f, 0f..1f),
                gmicNumber(R.string.gmic_param_luminance_level, 0.5f, 0f..1f),
                gmicNumber(R.string.gmic_param_smoothing, 0.5f, 0f..1f),
                gmicNumber(R.string.just_size, 0.0f, 0f..1f),
                gmicNumber(R.string.gmic_param_step, 0.5f, 0f..1f),
                gmicToggle(R.string.quality, false),
                gmicToggle(R.string.gmic_param_force_gray, true)
            )
        }
    }
}