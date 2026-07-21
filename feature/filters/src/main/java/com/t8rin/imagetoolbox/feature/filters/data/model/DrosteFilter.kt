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
import com.t8rin.gmic.filters.Droste
import com.t8rin.gmic.filters.DrosteDrawingMode
import com.t8rin.gmic.filters.DrosteMirror
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class DrosteFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "20",
            "20",
            "80",
            "20",
            "80",
            "80",
            "20",
            "80",
            "1",
            "0",
            "0",
            "0",
            "1",
            "None",
            "Nearest",
            "Replace"
        )
    )
) : GMICFilterTransformation(), Filter.Droste {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Droste(
        upperLeftX = value[0].toFloat(),
        upperLeftY = value[1].toFloat(),
        upperRightX = value[2].toFloat(),
        upperRightY = value[3].toFloat(),
        lowerRightX = value[4].toFloat(),
        lowerRightY = value[5].toFloat(),
        lowerLeftX = value[6].toFloat(),
        lowerLeftY = value[7].toFloat(),
        iterations = value[8].toInt(),
        xShift = value[9].toFloat(),
        yShift = value[10].toFloat(),
        angle = value[11].toFloat(),
        zoom = value[12].toFloat(),
        mirror = DrosteMirror.valueOf(value[13]),
        boundary = GmicBoundary.valueOf(value[14]),
        drawingMode = DrosteDrawingMode.valueOf(value[15])
    )

}
