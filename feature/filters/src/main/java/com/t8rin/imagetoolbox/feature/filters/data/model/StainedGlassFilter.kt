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
import com.t8rin.gmic.filters.StainedGlass
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.StainedGlass.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class StainedGlassFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.StainedGlass {

    override fun createFilter(): GmicFilter = StainedGlass(
        edges = value.settings[0].toFloat(),
        shading = value.settings[1].toFloat(),
        thinSeparators = value.settings[2].toBoolean(),
        equalize = value.settings[3].toBoolean(),
        colors = value.settings[4].toFloat(),
        brightness = value.settings[5].toFloat(),
        contrast = value.settings[6].toFloat(),
        gamma = value.settings[7].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.texture_stained_glass, R.string.filter_help_texture_stained_glass)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.texture_edges, 40, 0f..100f),
                gmicNumber(R.string.texture_shading, 0.1f, 0f..0.5f),
                gmicToggle(R.string.gmic_param_thin_separators, false),
                gmicToggle(R.string.equalize, true),
                gmicNumber(R.string.color, 1, 0f..3f),
                gmicNumber(R.string.brightness, 0, -100f..100f),
                gmicNumber(R.string.contrast, 0, -100f..100f),
                gmicNumber(R.string.gamma, 0, -100f..100f)
            )
        }
    }
}