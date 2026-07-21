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
import com.t8rin.gmic.filters.Charcoal
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class CharcoalFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "65",
            "70",
            "170",
            "false",
            "true",
            "false",
            "50",
            "70",
            "-1",
            "-16777216",
            "false"
        )
    )
) : GMICFilterTransformation(), Filter.Charcoal {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Charcoal(
        granularity = value[0].toInt(),
        lowlightCrossover = value[1].toInt(),
        highlightCrossover = value[2].toInt(),
        boostContrast = value[3].toBoolean(),
        optimizeSize = value[4].toBoolean(),
        chalkHighlights = value[5].toBoolean(),
        minimumHighlight = value[6].toInt(),
        maximumHighlight = value[7].toInt(),
        backgroundColor = value[8].toInt(),
        foregroundColor = value[9].toInt(),
        invertColors = value[10].toBoolean()
    )

}
