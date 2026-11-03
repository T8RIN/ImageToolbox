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
import com.t8rin.gmic.filters.Cutout
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Cutout.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class CutoutFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Cutout {

    override fun createFilter(): GmicFilter = Cutout(
        levels = value.settings[0].toInt(),
        edgeSimplicity = value.settings[1].toFloat(),
        edgeFidelity = value.settings[2].toInt(),
        normalize = value.settings[3].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_cutout, R.string.filter_help_gmic_filter_cutout)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.levels, 4, 2f..32f),
                gmicNumber(R.string.gmic_param_edge_simplicity, 0.5f, 0f..3f),
                gmicInteger(R.string.gmic_param_edge_fidelity, 4, 0f..10f),
                gmicToggle(R.string.gmic_param_normalize, true)
            )
        }
    }
}