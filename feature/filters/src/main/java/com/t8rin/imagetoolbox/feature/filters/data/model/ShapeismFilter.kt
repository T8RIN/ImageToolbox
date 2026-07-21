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
import com.t8rin.gmic.filters.Shapeism
import com.t8rin.gmic.filters.ShapeismAngle
import com.t8rin.gmic.filters.ShapeismShape
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class ShapeismFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Circle",
            "7",
            "0.38",
            "0",
            "true",
            "5",
            "32",
            "8",
            "Any",
            "0",
            "5",
            "0.5",
            "1",
            "-16777216"
        )
    )
) : GMICFilterTransformation(), Filter.Shapeism {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Shapeism(
        shape = ShapeismShape.valueOf(value[0]),
        branches = value[1].toInt(),
        thickness = value[2].toFloat(),
        angle = value[3].toFloat(),
        antialiasing = value[4].toBoolean(),
        scales = value[5].toInt(),
        maximalSize = value[6].toInt(),
        minimalSize = value[7].toInt(),
        allowedAngles = ShapeismAngle.valueOf(value[8]),
        spacing = value[9].toInt(),
        precision = value[10].toInt(),
        edges = value[11].toFloat(),
        smoothness = value[12].toFloat(),
        backgroundColor = value[13].toInt()
    )

}
