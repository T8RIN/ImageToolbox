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
import com.t8rin.gmic.filters.SixtiesCinema
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SixtiesCinema.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class SixtiesCinemaFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SixtiesCinema {

    override fun createFilter(): GmicFilter = SixtiesCinema(
        grain = value.settings[0].toFloat(),
        noonForMidnight = value.settings[1].toBoolean(),
        geneKelly = value.settings[2].toBoolean(),
        rayHarryhausen = value.settings[3].toBoolean(),
        caryGrant = value.settings[4].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_sixties_cinema,
            R.string.filter_help_gmic_filter_sixties_cinema
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.grain, 10, 0f..25f),
                gmicToggle(R.string.gmic_param_noon_for_midnight, false),
                gmicToggle(R.string.gmic_param_gene_kelly, false),
                gmicToggle(R.string.gmic_param_ray_harryhausen, false),
                gmicToggle(R.string.gmic_param_cary_grant, false)
            )
        }
    }
}