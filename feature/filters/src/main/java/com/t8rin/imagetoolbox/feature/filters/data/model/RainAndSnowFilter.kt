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
import com.t8rin.gmic.filters.RainAndSnow
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RainAndSnow.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class RainAndSnowFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.RainAndSnow {

    override fun createFilter(): GmicFilter = RainAndSnow(
        angle = value.settings[0].toFloat(),
        speed = value.settings[1].toFloat(),
        density = value.settings[2].toFloat(),
        radius = value.settings[3].toFloat(),
        gamma = value.settings[4].toFloat(),
        opacity = value.settings[5].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_rain_and_snow,
            R.string.filter_help_gmic_filter_rain_and_snow
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.angle, 65, -180f..180f),
                gmicNumber(R.string.speed, 10, 0f..50f),
                gmicNumber(R.string.density, 50, 0f..100f),
                gmicNumber(R.string.radius, 0.1f, 0f..3f),
                gmicNumber(R.string.gamma, 1, 0f..2f),
                gmicNumber(R.string.opacity, 1, 0f..1f)
            )
        }
    }
}