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
import com.t8rin.gmic.filters.PoincareDisk
import com.t8rin.gmic.filters.PoincareFilling
import com.t8rin.gmic.filters.PoincareTiling
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class PoincareDiskFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "5",
            "6",
            "100",
            "20",
            "0",
            "Polygonal",
            "true",
            "Image",
            "50",
            "50",
            "0",
            "0",
            "2",
            "-16777216",
            "-16777216",
            "-1"
        )
    )
) : GMICFilterTransformation(), Filter.PoincareDisk {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = PoincareDisk(
        pValue = value[0].toInt(),
        qValue = value[1].toInt(),
        size = value[2].toFloat(),
        iterations = value[3].toInt(),
        angle = value[4].toFloat(),
        tiling = PoincareTiling.valueOf(value[5]),
        antialiasing = value[6].toBoolean(),
        filling = PoincareFilling.valueOf(value[7]),
        shiftX = value[8].toFloat(),
        shiftY = value[9].toFloat(),
        zoom = value[10].toFloat(),
        imageAngle = value[11].toFloat(),
        outline = value[12].toInt(),
        outlineColor = value[13].toInt(),
        firstColor = value[14].toInt(),
        secondColor = value[15].toInt()
    )

}
