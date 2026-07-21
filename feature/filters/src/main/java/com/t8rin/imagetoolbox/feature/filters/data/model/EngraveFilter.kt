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
import com.t8rin.gmic.filters.Engrave
import com.t8rin.gmic.filters.EngraveAntialiasing
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class EngraveFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "0.5",
            "50",
            "0",
            "8",
            "40",
            "0",
            "0",
            "true",
            "10",
            "1",
            "0",
            "0",
            "0",
            "X1_5"
        )
    )
) : GMICFilterTransformation(), Filter.Engrave {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Engrave(
        radius = value[0].toFloat(),
        density = value[1].toFloat(),
        edges = value[2].toFloat(),
        coherence = value[3].toFloat(),
        threshold = value[4].toFloat(),
        minimalArea = value[5].toInt(),
        flatRegionsRemoval = value[6].toFloat(),
        addColorBackground = value[7].toBoolean(),
        quantization = value[8].toFloat(),
        shading = value[9].toInt(),
        hue = value[10].toFloat(),
        saturation = value[11].toFloat(),
        lightness = value[12].toFloat(),
        antialiasing = EngraveAntialiasing.valueOf(value[13])
    )

}
