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
import com.t8rin.gmic.filters.Retinex
import com.t8rin.gmic.filters.RetinexColorSpace
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class RetinexFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("75", "16", "Hsv", "1", "1", "5", "15", "80", "250")
    )
) : GMICFilterTransformation(), Filter.Retinex {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Retinex(
        strength = value[0].toFloat(),
        valueOffset = value[1].toFloat(),
        colorSpace = RetinexColorSpace.valueOf(value[2]),
        minCut = value[3].toFloat(),
        maxCut = value[4].toFloat(),
        regularization = value[5].toFloat(),
        lowScale = value[6].toFloat(),
        middleScale = value[7].toFloat(),
        highScale = value[8].toFloat()
    )

}
