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
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.gmic.filters.PowerTwirl
import com.t8rin.gmic.filters.PowerTwirlMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PowerTwirl.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class PowerTwirlFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PowerTwirl {

    override fun createFilter(): GmicFilter = PowerTwirl(
        amplitude = value.settings[0].toFloat(),
        offset = value.settings[1].toFloat(),
        centerXPercent = value.settings[2].toFloat(),
        centerYPercent = value.settings[3].toFloat(),
        power = value.settings[4].toFloat(),
        boundary = GmicBoundary.valueOf(value.settings[5]),
        mode = PowerTwirlMode.valueOf(value.settings[6])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_power_twirl,
            R.string.filter_help_gmic_filter_power_twirl
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.amplitude, 1, -20f..20f),
                gmicNumber(R.string.offset, 0, -180f..180f),
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_power, 1, -20f..20f),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Mirror",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
                ),
                gmicSelection(R.string.gmic_param_mode, "Polar", listOf("Polar", "Cartesian"))
            )
        }
    }
}