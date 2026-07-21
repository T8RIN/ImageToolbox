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
import com.t8rin.gmic.filters.CrtPhosphors
import com.t8rin.gmic.filters.CrtPhosphorType
import com.t8rin.gmic.filters.CrtPrecision
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class CrtPhosphorsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Stripes",
            "4",
            "Low",
            "0",
            "4",
            "50",
            "true",
            "false",
            "false",
            "false",
            "true"
        )
    )
) : GMICFilterTransformation(), Filter.CrtPhosphors {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = CrtPhosphors(
        type = CrtPhosphorType.valueOf(value[0]),
        upscaleFactor = value[1].toInt(),
        precision = CrtPrecision.valueOf(value[2]),
        smoothness = value[3].toFloat(),
        neighborhoodSize = value[4].toInt(),
        stride = value[5].toFloat(),
        adaptivePattern = value[6].toBoolean(),
        useLuma = value[7].toBoolean(),
        transposePattern = value[8].toBoolean(),
        averageOverPattern = value[9].toBoolean(),
        normalizeImage = value[10].toBoolean()
    )

}
