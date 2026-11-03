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
import com.t8rin.gmic.filters.DcpDehaze
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DcpDehaze.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class DcpDehazeFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.DcpDehaze {

    override fun createFilter(): GmicFilter = DcpDehaze(
        scale = value.settings[0].toInt(),
        strength = value.settings[1].toFloat(),
        min = value.settings[2].toFloat(),
        max = value.settings[3].toFloat(),
        brightness = value.settings[4].toFloat(),
        contrast = value.settings[5].toFloat(),
        gamma = value.settings[6].toFloat(),
        transmittanceMap = value.settings[7].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_dcp_dehaze, R.string.filter_help_gmic_filter_dcp_dehaze)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.scale, 5, 1f..20f),
                gmicNumber(R.string.strength, 1, 0f..2f),
                gmicNumber(R.string.min, 0.2f, 0f..1f),
                gmicNumber(R.string.max, 1, 0f..1f),
                gmicNumber(R.string.gmic_param_brightness, 0, -100f..100f),
                gmicNumber(R.string.contrast, 0, -100f..100f),
                gmicNumber(R.string.gamma, 0, -100f..100f),
                gmicToggle(R.string.gmic_param_transmittance_map, false)
            )
        }
    }
}