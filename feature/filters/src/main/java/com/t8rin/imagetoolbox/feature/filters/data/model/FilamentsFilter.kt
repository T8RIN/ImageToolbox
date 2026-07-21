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
import com.t8rin.gmic.filters.Filaments
import com.t8rin.gmic.filters.FilamentColorMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class FilamentsFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "50",
            "50",
            "75",
            "30",
            "10",
            "0",
            "true",
            "true",
            "true",
            "true",
            "true",
            "50",
            "WhiteOnBlack"
        )
    )
) : GMICFilterTransformation(), Filter.Filaments {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Filaments(
        density = value[0].toFloat(),
        length = value[1].toFloat(),
        contour = value[2].toFloat(),
        distortion = value[3].toFloat(),
        smoothness = value[4].toFloat(),
        rotation = value[5].toFloat(),
        localNormalization = value[6].toBoolean(),
        throwFromLeft = value[7].toBoolean(),
        throwFromRight = value[8].toBoolean(),
        throwFromAbove = value[9].toBoolean(),
        throwFromBelow = value[10].toBoolean(),
        opacity = value[11].toFloat(),
        colorMode = FilamentColorMode.valueOf(value[12])
    )

}
