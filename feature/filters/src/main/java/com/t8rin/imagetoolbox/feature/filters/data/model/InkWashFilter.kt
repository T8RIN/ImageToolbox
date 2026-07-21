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
import com.t8rin.gmic.filters.InkWash
import com.t8rin.gmic.filters.InkWashContrast
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class InkWashFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("0.14", "23", "false", "0.5", "0.54", "2.25", "None", "2", "6", "5", "20")
    )
) : GMICFilterTransformation(), Filter.InkWash {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = InkWash(
        size = value[0].toFloat(),
        amplitude = value[1].toFloat(),
        skipOtherSteps = value[2].toBoolean(),
        smootherSharpness = value[3].toFloat(),
        edgeProtection = value[4].toFloat(),
        softness = value[5].toFloat(),
        stretchContrast = InkWashContrast.valueOf(value[6]),
        localNormalizationAmplitude = value[7].toFloat(),
        localNormalizationSize = value[8].toFloat(),
        neighborhoodSmoothness = value[9].toFloat(),
        averageSmoothness = value[10].toFloat()
    )

}
