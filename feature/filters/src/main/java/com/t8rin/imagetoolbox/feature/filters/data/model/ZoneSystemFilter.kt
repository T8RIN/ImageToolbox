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
import com.t8rin.gmic.filters.ZoneSystem
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ZoneSystem.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class ZoneSystemFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ZoneSystem {

    override fun createFilter(): GmicFilter = ZoneSystem(
        shadowsZone = value.settings[0].toInt(),
        highlightsZone = value.settings[1].toInt(),
        gamma = value.settings[2].toFloat(),
        contrast = value.settings[3].toFloat(),
        blackPoint = value.settings[4].toInt(),
        whitePoint = value.settings[5].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_zone_system,
            R.string.filter_help_gmic_filter_zone_system
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_shadows_zone, 2, 1f..5f),
                gmicInteger(R.string.gmic_param_highlights_zone, 9, 6f..10f),
                gmicNumber(R.string.gamma, 1.1f, 0f..5f),
                gmicNumber(R.string.contrast, 1.15f, 0f..4f),
                gmicInteger(R.string.gmic_param_black_point, 8, 0f..255f),
                gmicInteger(R.string.tag_white_point, 245, 0f..255f)
            )
        }
    }
}