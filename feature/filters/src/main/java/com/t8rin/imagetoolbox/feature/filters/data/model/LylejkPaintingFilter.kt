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
import com.t8rin.gmic.filters.LylejkPainting
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LylejkPainting.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class LylejkPaintingFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.LylejkPainting {

    override fun createFilter(): GmicFilter = LylejkPainting(
        iterations = value.settings[0].toInt(),
        abstraction = value.settings[1].toInt(),
        radius = value.settings[2].toInt(),
        canvas = value.settings[3].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_lylejk_painting,
            R.string.filter_help_gmic_filter_lylejk_painting
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.iterations, 10, 1f..20f),
                gmicInteger(R.string.gmic_param_abstraction, 2, 1f..20f),
                gmicInteger(R.string.radius, 4, 1f..30f),
                gmicNumber(R.string.gmic_param_canvas, 10, 0f..100f)
            )
        }
    }
}