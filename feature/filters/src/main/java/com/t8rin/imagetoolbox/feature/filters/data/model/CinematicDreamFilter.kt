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
import com.t8rin.gmic.filters.CinematicDream
import com.t8rin.gmic.model.GmicExecutionOptions
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CinematicDream.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class CinematicDreamFilter(
    override val value: Value
) : GMICFilterTransformation(GmicExecutionOptions(disableSmooth = false)), Filter.CinematicDream {

    override fun createFilter(): GmicFilter = CinematicDream(
        detailsStrength = value.settings[0].toFloat(),
        detailsScale = value.settings[1].toFloat(),
        bloomSmoothness = value.settings[2].toFloat(),
        highlight = value.settings[3].toInt(),
        localContrast = value.settings[4].toFloat(),
        shadowStrength = value.settings[5].toFloat(),
        shadowScale = value.settings[6].toFloat(),
        normalize = value.settings[7].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_cinematic_dream,
            R.string.filter_help_gmic_filter_cinematic_dream
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_details_strength, 75, 0f..400f),
                gmicNumber(R.string.gmic_param_details_scale, 45, 0f..255f),
                gmicNumber(R.string.softness, 55, 0f..255f),
                gmicInteger(R.string.highlights, 28, 0f..100f),
                gmicNumber(R.string.contrast, 18, 0f..100f),
                gmicNumber(R.string.gmic_param_shadow_strength, 0.55f, 0f..1f),
                gmicNumber(R.string.gmic_param_shadow_scale, 6, 0f..20f),
                gmicToggle(R.string.gmic_param_normalize, true)
            )
        }
    }
}